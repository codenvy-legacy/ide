/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.git.server.rest;

import com.codenvy.api.vfs.server.ContentStream;
import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.ItemNotFoundException;
import com.codenvy.api.vfs.server.exceptions.PermissionDeniedException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Folder;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.ItemList;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitConnection;
import com.codenvy.ide.ext.git.server.GitConnectionFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.InfoPage;
import com.codenvy.ide.ext.git.server.LogPage;
import com.codenvy.ide.ext.git.shared.AddRequest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.BranchCheckoutRequest;
import com.codenvy.ide.ext.git.shared.BranchCreateRequest;
import com.codenvy.ide.ext.git.shared.BranchDeleteRequest;
import com.codenvy.ide.ext.git.shared.BranchListRequest;
import com.codenvy.ide.ext.git.shared.CloneRequest;
import com.codenvy.ide.ext.git.shared.CommitRequest;
import com.codenvy.ide.ext.git.shared.Commiters;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.FetchRequest;
import com.codenvy.ide.ext.git.shared.InitRequest;
import com.codenvy.ide.ext.git.shared.LogRequest;
import com.codenvy.ide.ext.git.shared.MergeRequest;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.MoveRequest;
import com.codenvy.ide.ext.git.shared.PullRequest;
import com.codenvy.ide.ext.git.shared.PushRequest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.ext.git.shared.RemoteAddRequest;
import com.codenvy.ide.ext.git.shared.RemoteListRequest;
import com.codenvy.ide.ext.git.shared.RemoteUpdateRequest;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.ext.git.shared.RmRequest;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.ext.git.shared.Tag;
import com.codenvy.ide.ext.git.shared.TagCreateRequest;
import com.codenvy.ide.ext.git.shared.TagDeleteRequest;
import com.codenvy.ide.ext.git.shared.TagListRequest;
import com.codenvy.ide.maven.tools.MavenUtils;
import com.codenvy.ide.Constants;
import com.codenvy.vfs.impl.fs.GitUrlResolver;
import com.codenvy.vfs.impl.fs.LocalPathResolver;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @author andrew00x */
@Path("git/{ws-id}")
public class GitService {
    private static final Logger LOG = LoggerFactory.getLogger(GitService.class);
    @Inject
    private LocalPathResolver         localPathResolver;
    @Inject
    private GitUrlResolver            gitUrlResolver;
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;
    @Inject
    private GitConnectionFactory      gitConnectionFactory;

    @PathParam("ws-id")
    private String vfsId;
    @QueryParam("projectid")
    private String projectId;

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void add(AddRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.add(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("branch-checkout")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void branchCheckout(BranchCheckoutRequest request)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.branchCheckout(request);
            determineProjectType();
        } finally {
            gitConnection.close();
        }
    }

    @Path("branch-create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Branch branchCreate(BranchCreateRequest request)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return gitConnection.branchCreate(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("branch-delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void branchDelete(BranchDeleteRequest request)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.branchDelete(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("branch-rename")
    @POST
    public void branchRename(@QueryParam("oldName") String oldName,
                             @QueryParam("newName") String newName)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.branchRename(oldName, newName);
        } finally {
            gitConnection.close();
        }
    }

    @Path("branch-list")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public GenericEntity<List<Branch>> branchList(BranchListRequest request)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return new GenericEntity<List<Branch>>(gitConnection.branchList(request)) {
            };
        } finally {
            gitConnection.close();
        }
    }

    @Path("clone")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RepoInfo clone(final CloneRequest request)
            throws URISyntaxException, GitException, VirtualFileSystemException {
        long start = System.currentTimeMillis();
        // On-the-fly resolving of repository's working directory.
        request.setWorkingDir(resolveLocalPathByPath(request.getWorkingDir()));
        LOG.info("Repository clone from '" + request.getRemoteUri() + "' to '" + request.getWorkingDir() + "' started");
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.clone(request);
            setGitRepositoryProp(projectId);
            determineProjectType();
            return DtoFactory.getInstance().createDto(RepoInfo.class).withRemoteUri(request.getRemoteUri());
        } finally {
            long end = System.currentTimeMillis();
            long seconds = (end - start) / 1000;
            LOG.info("Repository clone from '" + request.getRemoteUri() + "' to '" + request.getWorkingDir()
                     + "' finished. Process took " + seconds + " seconds (" + seconds / 60 + " minutes)");
            gitConnection.close();
        }
    }

    private void setGitRepositoryProp(String projectId) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
        Item project = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        String value = null;
        for (Property property : project.getProperties()) {
            if ("isGitRepository".equals(property.getName())) {
                value = (property.getValue() != null && property.getValue().size() > 0) ? property.getValue().get(0) : null;
                break;
            }
        }

        if (value == null || !value.equals("true")) {
            Property isGitRepositoryProperty = DtoFactory.getInstance().createDto(Property.class).withName("isGitRepository")
                                                         .withValue(new ArrayList<>(Arrays.asList("true")));
            List<Property> propertiesList = new ArrayList<>(1);
            propertiesList.add(isGitRepositoryProperty);
            vfs.updateItem(projectId, propertiesList, null);
        }
    }

    /**
     * Try to determine project's type by it's structure.
     *
     * @throws VirtualFileSystemException
     */
    private void determineProjectType() throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);

        //Сheck whether ".codenvy" folder and "project" file are already exists
        ItemList folders = vfs.getChildren(projectId, -1, 0, "folder", false, PropertyFilter.ALL_FILTER);
        for (Item folder : folders.getItems()) {
            if (".codenvy".equals(folder.getName())) {
                ItemList files = vfs.getChildren(folder.getId(), -1, 0, "file", false, PropertyFilter.NONE_FILTER);
                for (Item file : files.getItems()) {
                    if ("project".equals(file.getName())) {
                        return;
                    }
                }
            }
        }
        ItemList files = vfs.getChildren(projectId, -1, 0, "file", false, PropertyFilter.NONE_FILTER);
        boolean foundMavenProject = false;
        for (Item file : files.getItems()) {
            if ("pom.xml".equals(file.getName())) {
                boolean isMultiModule = isMultiModule(vfs.getContent(file.getId()));
                String propertyFileContent = "";
                if (isMultiModule) {
                    processMultiModuleMavenProject(vfs, projectId);
                    propertyFileContent =
                            "{\"type\":\"maven_multi_module\",\"properties\":[{\"name\":\"builder.name\",\"value\":[\"maven\"]}]}";
                } else {
                    propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
                }
                foundMavenProject = true;
                Folder codenvyFolder = vfs.createFolder(projectId, ".codenvy");
                vfs.createFile(codenvyFolder.getId(), "project", MediaType.APPLICATION_JSON_TYPE,
                               new ByteArrayInputStream(propertyFileContent.getBytes()));
                break;
            }
        }
        // We didn't found a maven project, turn it as nameless project
        if (!foundMavenProject) {
            String propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
            Folder codenvyFolder = vfs.createFolder(projectId, ".codenvy");
            vfs.createFile(codenvyFolder.getId(), "project", MediaType.APPLICATION_JSON_TYPE,
                           new ByteArrayInputStream(propertyFileContent.getBytes()));

        }

    }

    /**
     * @param vfs
     *         virtual file system
     * @param projectId
     *         id of the multimodule project
     * @throws ItemNotFoundException
     * @throws InvalidArgumentException
     * @throws PermissionDeniedException
     * @throws VirtualFileSystemException
     */
    private void processMultiModuleMavenProject(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        ItemList folders = vfs.getChildren(projectId, -1, 0, "folder", false, PropertyFilter.ALL_FILTER);
        findPom(vfs, folders);
    }

    /**
     * Recursively find pom.xml in the project's structure.
     *
     * @param vfs
     *         virtual file system
     * @param folders
     *         folders to look for pom.xml
     * @throws ItemNotFoundException
     * @throws InvalidArgumentException
     * @throws PermissionDeniedException
     * @throws VirtualFileSystemException
     */
    private void findPom(VirtualFileSystem vfs, ItemList folders) throws VirtualFileSystemException {
        if (folders.getItems().isEmpty()) {
            return;
        }

        for (Item folder : folders.getItems()) {
            ItemList files = vfs.getChildren(folder.getId(), -1, 0, "file", false, PropertyFilter.NONE_FILTER);
            boolean found = false;
            for (Item file : files.getItems()) {
                if ("pom.xml".equals(file.getName())) {
                    List<Property> propertiesList = new ArrayList<>();
                    propertiesList.add(DtoFactory.getInstance().createDto(Property.class).withName("isGitRepository")
                                                 .withValue(new ArrayList<>(Arrays.asList("true"))));
                    vfs.updateItem(folder.getId(), propertiesList, null);
                    found = true;

                    final String propertyFileContent = "{\"type\":\"" + Constants.NAMELESS_ID + "\"}";
                    Folder codenvyFolder = vfs.createFolder(folder.getId(), ".codenvy");
                    vfs.createFile(codenvyFolder.getId(), "project", MediaType.APPLICATION_JSON_TYPE,
                                   new ByteArrayInputStream(propertyFileContent.getBytes()));
                    break;
                }
            }
            if (!found) {
                findPom(vfs, vfs.getChildren(folder.getId(), -1, 0, "folder", false, PropertyFilter.ALL_FILTER));
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
            LOG.error("Can't read pom.xml to determine project's type.", e);
        }
        return false;
    }

    @Path("commit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Revision commit(CommitRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        Revision revision = gitConnection.commit(request);
        try {
            if (revision.isFake()) {
                Status status = status(false);

                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    ((InfoPage)status).writeTo(bos);
                    revision.setMessage(new String(bos.toByteArray()));
                } catch (IOException e) {
                    LOG.error("Cant write to revision", e);
                    throw new GitException("Cant execute status");
                }
            }
        } finally {
            gitConnection.close();
        }
        return revision;
    }

    @Path("diff")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public InfoPage diff(DiffRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return gitConnection.diff(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("fetch")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void fetch(FetchRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.fetch(request);
            determineProjectType();
        } finally {
            gitConnection.close();
        }
    }

    @Path("init")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void init(final InitRequest request) throws GitException, VirtualFileSystemException {
        request.setWorkingDir(resolveLocalPath(projectId));
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.init(request);
            setGitRepositoryProp(projectId);
        } finally {
            gitConnection.close();
        }
    }

    @Path("log")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public LogPage log(LogRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return gitConnection.log(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("merge")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public MergeResult merge(MergeRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return gitConnection.merge(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("mv")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void mv(MoveRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.mv(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("pull")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void pull(PullRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.pull(request);
            determineProjectType();
        } finally {
            gitConnection.close();
        }
    }

    @Path("push")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void push(PushRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.push(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("remote-add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void remoteAdd(RemoteAddRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.remoteAdd(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("remote-delete/{name}")
    @POST
    public void remoteDelete(@PathParam("name") String name)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.remoteDelete(name);
        } finally {
            gitConnection.close();
        }
    }

    @Path("remote-list")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public GenericEntity<List<Remote>> remoteList(RemoteListRequest request)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return new GenericEntity<List<Remote>>(gitConnection.remoteList(request)) {
            };
        } finally {
            gitConnection.close();
        }
    }

    @Path("remote-update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void remoteUpdate(RemoteUpdateRequest request)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.remoteUpdate(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("reset")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void reset(ResetRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.reset(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("rm")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void rm(RmRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.rm(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("status")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Status status(@QueryParam("short") boolean shortFormat)
            throws GitException, VirtualFileSystemException {
        if (!isGitRepository()) {
            throw new GitException("Not a git repository.");
        }
        GitConnection gitConnection = getGitConnection();
        try {
            return gitConnection.status(shortFormat);
        } finally {
            gitConnection.close();
        }
    }

    @Path("tag-create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Tag tagCreate(TagCreateRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return gitConnection.tagCreate(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("tag-delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void tagDelete(TagDeleteRequest request) throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.tagDelete(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("tag-list")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public GenericEntity<List<Tag>> tagList(TagListRequest request)
            throws GitException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            return new GenericEntity<List<Tag>>(gitConnection.tagList(request)) {
            };
        } finally {
            gitConnection.close();
        }
    }

    @Path("read-only-url")
    @GET
    public String readOnlyGitUrl(@Context UriInfo uriInfo) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
        return gitUrlResolver.resolve(uriInfo, vfs, projectId);
    }

    @GET
    @Path("commiters")
    public Commiters getCommiters(@Context UriInfo uriInfo)
            throws VirtualFileSystemException, GitException {
        GitConnection gitConnection = getGitConnection();
        try {
            return DtoFactory.getInstance().createDto(Commiters.class).withCommiters(gitConnection.getCommiters());
        } finally {
            gitConnection.close();
        }
    }

    @GET
    @Path("delete-repository")
    public void deleteRepository(@Context UriInfo uriInfo) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
        Item project = getGitProject(vfs, projectId);
        String path2gitFolder = project.getPath() + "/.git";
        Item gitItem = vfs.getItemByPath(path2gitFolder, null, false, PropertyFilter.NONE_FILTER);
        vfs.delete(gitItem.getId(), null);
        List<Property> properties = project.getProperties();
        List<Property> propertiesNew = new ArrayList<Property>(properties.size() - 1);
        for (Property property : properties) {
            if (property.getName().equalsIgnoreCase("isGitRepository")) {
                property.setValue(null);
            }
            propertiesNew.add(property);
        }
        vfs.updateItem(project.getId(), propertiesNew, null);
    }

    // TODO: this is temporary method
    private Item getGitProjectByPath(VirtualFileSystem vfs, String projectPath) throws VirtualFileSystemException {
        Item project = vfs.getItemByPath(projectPath, null, false, PropertyFilter.ALL_FILTER);
        Item parent = vfs.getItem(project.getParentId(), false, PropertyFilter.ALL_FILTER);
//        if (parent.getItemType().equals(ItemType.PROJECT)) // MultiModule project
//            return parent;
        return project;
    }

    private Item getGitProject(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        Item project = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        Item parent = vfs.getItem(project.getParentId(), false, PropertyFilter.ALL_FILTER);
//        if (parent.getItemType().equals(ItemType.PROJECT)) // MultiModule project
//            return parent;
        return project;
    }

    protected boolean isGitRepository() throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
        Item project = getGitProject(vfs, projectId);

        String value = null;
        for (Property property : project.getProperties()) {
            if ("isGitRepository".equals(property.getName())) {
                value = (property.getValue() != null && property.getValue().size() > 0) ? property.getValue().get(0) : null;
                break;
            }
        }

        return value != null && value.equals("true");
    }

    // TODO: this is temporary method
    protected String resolveLocalPathByPath(String folderPath) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
        if (vfs == null) {
            throw new VirtualFileSystemException(
                    "Can't resolve path on the Local File System : Virtual file system not initialized");
        }
        Item gitProject = getGitProjectByPath(vfs, folderPath);

        projectId = gitProject.getId();

        final MountPoint mountPoint = vfs.getMountPoint();
        final VirtualFile virtualFile = mountPoint.getVirtualFileById(gitProject.getId());
        return localPathResolver.resolve(virtualFile);
    }

    protected String resolveLocalPath(String projectId) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
        if (vfs == null) {
            throw new VirtualFileSystemException(
                    "Can't resolve path on the Local File System : Virtual file system not initialized");
        }
        Item gitProject = getGitProject(vfs, projectId);
        final MountPoint mountPoint = vfs.getMountPoint();
        final VirtualFile virtualFile = mountPoint.getVirtualFileById(gitProject.getId());
        return localPathResolver.resolve(virtualFile);
    }

    protected GitConnection getGitConnection() throws GitException, VirtualFileSystemException {
        return gitConnectionFactory.getConnection(resolveLocalPath(projectId));
    }
}
