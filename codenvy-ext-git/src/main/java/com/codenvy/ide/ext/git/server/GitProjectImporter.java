package com.codenvy.ide.ext.git.server;

import com.codenvy.api.project.server.AbstractVirtualFileEntry;
import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.ProjectImporter;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.commons.lang.NameGenerator;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.Constants;
import com.codenvy.ide.ext.git.server.nativegit.NativeGitConnectionFactory;
import com.codenvy.ide.ext.git.shared.CloneRequest;
import com.codenvy.vfs.impl.fs.LocalPathResolver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitProjectImporter implements ProjectImporter {

    private final NativeGitConnectionFactory nativeGitConnectionFactory;
    private final LocalPathResolver          localPathResolver;

    @Inject
    public GitProjectImporter(NativeGitConnectionFactory nativeGitConnectionFactory, LocalPathResolver localPathResolver) {
        this.nativeGitConnectionFactory = nativeGitConnectionFactory;
        this.localPathResolver = localPathResolver;
    }

    @Override
    public String getId() {
        return "git";
    }

    @Override
    public void importSources(FolderEntry baseFolder, String location) throws IOException {
        try {
            if (!baseFolder.isFolder()) {
                throw new IOException("Project cannot be imported into \"" + baseFolder.getName() + "\". It is not a folder.");
            }

            FolderEntry tempFolderToClone = null;

            if (!isFolderEmpty(baseFolder)) {
                tempFolderToClone = baseFolder.createFolder(NameGenerator.generate("temp-", 15));
            }

            String fullPathToClonedProject = localPathResolver.resolve(tempFolderToClone != null ? tempFolderToClone.getVirtualFile()
                                                                                                 : baseFolder.getVirtualFile());

            GitConnection gitConnection = nativeGitConnectionFactory.getConnection(fullPathToClonedProject);
            gitConnection.clone(DtoFactory.getInstance().createDto(CloneRequest.class)
                                          .withWorkingDir(fullPathToClonedProject)
                                          .withRemoteName("origin")
                                          .withRemoteUri(location));

            if (tempFolderToClone != null) {
                for (AbstractVirtualFileEntry virtualFileEntry : tempFolderToClone.getChildren()) {
                    virtualFileEntry.moveTo(baseFolder.getPath());
                }
                tempFolderToClone.remove();
            }

            if (!baseFolder.isProjectFolder()) {
                String propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
                FolderEntry projectMetaFolder = baseFolder.createFolder(".codenvy");
                projectMetaFolder.createFile("project", propertyFileContent.getBytes(), MediaType.APPLICATION_JSON_TYPE.getType());
            }
        } catch (VirtualFileSystemException | GitException | URISyntaxException e) {
            throw new IOException("Selected project cannot be imported.", e);
        }
    }

    private boolean isFolderEmpty(FolderEntry folder) {
        return folder.getChildren().size() == 0;
    }
}
