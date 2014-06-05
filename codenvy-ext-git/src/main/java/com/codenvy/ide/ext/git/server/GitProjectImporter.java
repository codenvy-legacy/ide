/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.server;

import com.codenvy.api.core.ApiException;
import com.codenvy.api.core.UnauthorizedException;
import com.codenvy.api.project.server.AbstractVirtualFileEntry;
import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ProjectImporter;
import com.codenvy.api.project.server.ProjectManager;
import com.codenvy.api.vfs.server.ContentStream;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.Constants;
import com.codenvy.ide.ext.git.server.nativegit.NativeGitConnectionFactory;
import com.codenvy.ide.ext.git.shared.BranchCheckoutRequest;
import com.codenvy.ide.ext.git.shared.CloneRequest;
import com.codenvy.ide.ext.git.shared.FetchRequest;
import com.codenvy.ide.ext.git.shared.InitRequest;
import com.codenvy.ide.ext.git.shared.RemoteAddRequest;
import com.codenvy.ide.maven.tools.MavenUtils;
import com.codenvy.vfs.impl.fs.LocalPathResolver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.maven.model.Model;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitProjectImporter implements ProjectImporter {

    private final NativeGitConnectionFactory nativeGitConnectionFactory;
    private final LocalPathResolver          localPathResolver;
    private final ProjectManager             projectManager;

    private static final String DEFAULT_REMOTE = "origin";

    @Inject
    public GitProjectImporter(NativeGitConnectionFactory nativeGitConnectionFactory, LocalPathResolver localPathResolver,
                              ProjectManager projectManager) {
        this.nativeGitConnectionFactory = nativeGitConnectionFactory;
        this.localPathResolver = localPathResolver;
        this.projectManager = projectManager;
    }

    @Override
    public String getId() {
        return "git";
    }


    @Override
    public String getDescription() {
        return "Add possibility to import project from GIT repository";
    }

    @Override
    public void importSources(FolderEntry baseFolder, String location) throws IOException, ApiException {
        try {
            if (!baseFolder.isFolder()) {
                throw new IOException("Project cannot be imported into \"" + baseFolder.getName() + "\". It is not a folder.");
            }

            String fullPathToClonedProject = localPathResolver.resolve(baseFolder.getVirtualFile());
            GitConnection gitConnection = nativeGitConnectionFactory.getConnection(fullPathToClonedProject);

            if (!isFolderEmpty(baseFolder)) {
                gitConnection = gitConnection.init(DtoFactory.getInstance().createDto(InitRequest.class)
                                                             .withWorkingDir(fullPathToClonedProject)
                                                             .withInitCommit(false)
                                                             .withBare(false));
                gitConnection.remoteAdd(DtoFactory.getInstance().createDto(RemoteAddRequest.class)
                                                  .withName(DEFAULT_REMOTE)
                                                  .withUrl(location));
                gitConnection.fetch(DtoFactory.getInstance().createDto(FetchRequest.class)
                                              .withRemote(DEFAULT_REMOTE)
                                              .withRefSpec(Collections.singletonList("refs/heads/master:refs/remotes/origin/master")));
                gitConnection.branchCheckout(DtoFactory.getInstance().createDto(BranchCheckoutRequest.class)
                                                       .withName("master"));
            } else {
                gitConnection.clone(DtoFactory.getInstance().createDto(CloneRequest.class)
                                              .withWorkingDir(fullPathToClonedProject)
                                              .withRemoteName(DEFAULT_REMOTE)
                                              .withRemoteUri(location));
            }

            if (!baseFolder.isProjectFolder()) {
                String propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
                FolderEntry projectMetaFolder = baseFolder.createFolder(".codenvy");
                projectMetaFolder.createFile("project", propertyFileContent.getBytes(), MediaType.APPLICATION_JSON_TYPE.getType());
            } else {
                determineProjectType(baseFolder); //Temporary solution
            }

        } catch (NotAuthorizedException e) {

            throw new UnauthorizedException("User is not authorize to call this action. " +
                                            "Try go to main menu Window->Preference->SSH Key and generate new keys pair");
        } catch (VirtualFileSystemException | GitException | URISyntaxException e) {
            throw new IOException("Selected project cannot be imported.", e);
        }
    }


    private boolean isFolderEmpty(FolderEntry folder) {
        return folder.getChildren().size() == 0;
    }

    /**
     * Try to determine project's type by it's structure.
     *
     * @throws VirtualFileSystemException
     */
    private void determineProjectType(FolderEntry folderEntry) throws VirtualFileSystemException, IOException {
        Project project = projectManager.getProject(EnvironmentContext.getCurrent().getWorkspaceId(), folderEntry.getPath());
        if (project.getDescription().getProjectType().getId() != Constants.NAMELESS_ID)
            return;
        for (AbstractVirtualFileEntry file : folderEntry.getChildren()) {
            if ("pom.xml".equals(file.getName())) {
                boolean isMultiModule = isMultiModule(file.getVirtualFile().getContent());
                String propertyFileContent = "";
                if (isMultiModule) {
                    processMultiModuleMavenProject(folderEntry);
                    propertyFileContent =
                            "{\"type\":\"maven_multi_module\",\"properties\":[{\"name\":\"builder.name\",\"value\":[\"maven\"]}]}";
                } else {
                    propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
                }
                if (folderEntry.getChild(".codenvy") != null) {
                    folderEntry.getChild(".codenvy").remove();
                }
                FolderEntry projectMetaFolder = folderEntry.createFolder(".codenvy");
                projectMetaFolder.createFile("project", propertyFileContent.getBytes(), MediaType.APPLICATION_JSON_TYPE.getType());

                break;
            }
        }
    }

    /**
     */
    private void processMultiModuleMavenProject(FolderEntry folderEntry) throws VirtualFileSystemException {
        findPom(folderEntry.getChildFolders());
    }

    /**
     * Recursively find pom.xml in the project's structure.
     *
     * @param folders
     *         folders to look for pom.xml
     * @throws com.codenvy.api.vfs.server.exceptions.ItemNotFoundException
     * @throws com.codenvy.api.vfs.server.exceptions.InvalidArgumentException
     * @throws com.codenvy.api.vfs.server.exceptions.PermissionDeniedException
     * @throws VirtualFileSystemException
     */
    private void findPom(List<FolderEntry> folders) throws VirtualFileSystemException {
        if (folders == null || folders.isEmpty()) {
            return;
        }
        for (FolderEntry folder : folders) {
            List<FileEntry> files = folder.getChildFiles();
            boolean found = false;
            for (FileEntry file : files) {
                if ("pom.xml".equals(file.getName())) {
                    found = true;
                    String propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
                    FolderEntry projectMetaFolder = folder.createFolder(".codenvy");
                    projectMetaFolder.createFile("project", propertyFileContent.getBytes(), MediaType.APPLICATION_JSON_TYPE.getType());
                    break;
                }
            }
            if (!found) {
                findPom(folder.getChildFolders());
            }
        }
    }


    /**
     * Checks whether project is multi-module by analyzing packaging in pom.xml.
     * Must be {@code &lt;packaging&gt;pom&lt;/packaging&gt;}.
     *
     * @param pomContent
     *         content of the pom.xml file
     * @return {@code true} if project is multi-module
     */
    private boolean isMultiModule(ContentStream pomContent) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(pomContent.getStream()))) {
            final Model pom = MavenUtils.readModel(reader);
            return (pom.getModules().size() > 0);
        } catch (IOException e) {
//            LOG.error("Can't read pom.xml to determine project's type.", e);
        }
        return false;
    }


}
