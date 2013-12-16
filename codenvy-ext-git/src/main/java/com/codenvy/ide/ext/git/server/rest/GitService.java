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
import com.codenvy.api.vfs.server.GitUrlResolver;
import com.codenvy.api.vfs.server.LocalPathResolver;
import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemImpl;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.ItemNotFoundException;
import com.codenvy.api.vfs.server.exceptions.LocalPathResolveException;
import com.codenvy.api.vfs.server.exceptions.PermissionDeniedException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.ItemType;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.ItemList;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.commons.MavenUtils;
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
import com.codenvy.ide.ext.git.shared.GitUser;
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
import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.User;

import org.apache.maven.model.Model;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitService.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@Path("{ws-name}/git")
public class GitService {
    private static final Log LOG = ExoLogger.getLogger(GitService.class);
    
    @Inject
    private LocalPathResolver localPathResolver;

    @Inject
    private GitUrlResolver gitUrlResolver;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Inject
    private UserManager userManager;

    @Inject
    private GitConnectionFactory gitConnectionFactory;

    @QueryParam("vfsid")
    private String vfsId;

    @QueryParam("projectid")
    private String projectId;

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void add(AddRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public void branchCheckout(BranchCheckoutRequest request) throws GitException, LocalPathResolveException,
                                                                     VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.branchCheckout(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("branch-create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Branch branchCreate(BranchCreateRequest request) throws GitException, LocalPathResolveException,
                                                                   VirtualFileSystemException {
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
    public void branchDelete(BranchDeleteRequest request) throws GitException, LocalPathResolveException,
                                                                 VirtualFileSystemException {
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
                             @QueryParam("newName") String newName) throws GitException, VirtualFileSystemException {
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
    public GenericEntity<List<Branch>> branchList(BranchListRequest request) throws GitException,
                                                                                    LocalPathResolveException,
                                                                                    VirtualFileSystemException {
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
    public RepoInfo clone(final CloneRequest request) throws URISyntaxException, GitException,
                                                             LocalPathResolveException, VirtualFileSystemException {
        long start = System.currentTimeMillis();
        // On-the-fly resolving of repository's working directory.
        request.setWorkingDir(resolveLocalPath(request.getWorkingDir()));
        LOG.info("Repository clone from '" + request.getRemoteUri() + "' to '" + request.getWorkingDir() + "' started");
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.clone(request);
            setGitRepositoryProp(projectId);
            determineProjectType();
            addToIndex();
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
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item project = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        String value = null;
        for (Property property : project.getProperties()){
            if ("isGitRepository".equals(property.getName())){
                value = (property.getValue() != null && property.getValue().size() > 0) ? property.getValue().get(0) : null;
                break;
            }
        }
        
        if (value == null || !value.equals("true")) {
            Property isGitRepositoryProperty = DtoFactory.getInstance().createDto(Property.class).withName("isGitRepository").withValue(new ArrayList<String>(Arrays.asList("true")));
            List<Property> propertiesList = new ArrayList<Property>(1);
            propertiesList.add(isGitRepositoryProperty);
            vfs.updateItem(projectId, propertiesList, null);
        }
    }
    
    private void determineProjectType() throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        ItemList files = vfs.getChildren(projectId, -1, 0, "file", false, PropertyFilter.NONE_FILTER);
        for (Item file : files.getItems()){
            if ("pom.xml".equals(file.getName())){
                boolean isMultiModule = isMultiModule(vfs.getContent(file.getId()));
                List<Property> propertiesList = new ArrayList<Property>();
                if (isMultiModule){
                    Property projectTypeProperty = DtoFactory.getInstance().createDto(Property.class).withName("vfs:projectType").withValue(new ArrayList<String>(Arrays.asList("Multiple Module Project")));
                    propertiesList.add(projectTypeProperty);
                    propertiesList.add(DtoFactory.getInstance().createDto(Property.class).withName("nature.primary").withValue(new ArrayList<String>(Arrays.asList("java"))));
                    propertiesList.add(DtoFactory.getInstance().createDto(Property.class).withName("vfs:mimeType").withValue(new ArrayList<String>(Arrays.asList("text/vnd.ideproject+directory"))));
                    propertiesList.add(DtoFactory.getInstance().createDto(Property.class).withName("builder_name").withValue(new ArrayList<String>(Arrays.asList("maven"))));
                    processMultiModuleMavenProject(vfs, projectId);
                } else {
                    Property projectTypeProperty = DtoFactory.getInstance().createDto(Property.class).withName("vfs:projectType").withValue(new ArrayList<String>(Arrays.asList("undefined")));
                    propertiesList.add(projectTypeProperty);
                }
                vfs.updateItem(projectId, propertiesList, null);
                break;
            }
        }
    }
    
    private void processMultiModuleMavenProject(VirtualFileSystem vfs, String parentProjectId) throws ItemNotFoundException,
                                                                                              InvalidArgumentException,
                                                                                              PermissionDeniedException,
                                                                                              VirtualFileSystemException {
        ItemList folders = vfs.getChildren(projectId, -1, 0, "folder", false, PropertyFilter.NONE_FILTER);
        for (Item folder : folders.getItems()) {
            ItemList files = vfs.getChildren(folder.getId(), -1, 0, "file", false, PropertyFilter.NONE_FILTER);
            for (Item file : files.getItems()) {
                if ("pom.xml".equals(file.getName())) {
                    List<Property> propertiesList = new ArrayList<Property>();
                    Property projectTypeProperty =
                                                   DtoFactory.getInstance().createDto(Property.class).withName("vfs:projectType")
                                                             .withValue(new ArrayList<String>(Arrays.asList("undefined")));
                    propertiesList.add(projectTypeProperty);
                    propertiesList.add(DtoFactory.getInstance().createDto(Property.class).withName("vfs:mimeType")
                                                 .withValue(new ArrayList<String>(Arrays.asList("text/vnd.ideproject+directory"))));
                    propertiesList.add(DtoFactory.getInstance().createDto(Property.class).withName("isGitRepository")
                                       .withValue(new ArrayList<String>(Arrays.asList("true"))));
                    vfs.updateItem(folder.getId(), propertiesList, null);
                    break;
                }
            }
        }
    }
    
    private boolean isMultiModule(ContentStream pomContent){
        Model pomModel;
        try {
            pomModel = MavenUtils.readPom(pomContent.getStream());
            return (pomModel.getPackaging() != null && pomModel.getPackaging().equals("pom"));
        } catch (IOException e) {
            LOG.error("Can't read pom.xml to determine project's type.", e);
        }
        return false;
    }

    private void addToIndex() throws VirtualFileSystemException {
      /* TODO VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (vfs instanceof LocalFileSystem) {
            ((LocalFileSystem)vfs).addToIndex(projectId);
        }*/
    }

    @Path("commit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Revision commit(CommitRequest request) throws GitException, LocalPathResolveException,
                                                         VirtualFileSystemException {
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
    public InfoPage diff(DiffRequest request)
            throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public void fetch(FetchRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.fetch(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("init")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void init(final InitRequest request) throws GitException, LocalPathResolveException,
                                                       VirtualFileSystemException {
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
    public LogPage log(LogRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public MergeResult merge(MergeRequest request) throws GitException, LocalPathResolveException,
                                                          VirtualFileSystemException {
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
    public void mv(MoveRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public void pull(PullRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.pull(request);
            addToIndex();
        } finally {
            gitConnection.close();
        }
    }

    @Path("push")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void push(PushRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public void remoteAdd(RemoteAddRequest request) throws GitException, LocalPathResolveException,
                                                           VirtualFileSystemException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.remoteAdd(request);
        } finally {
            gitConnection.close();
        }
    }

    @Path("remote-delete/{name}")
    @POST
    public void remoteDelete(@PathParam("name") String name) throws GitException, LocalPathResolveException,
                                                                    VirtualFileSystemException {
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
    public GenericEntity<List<Remote>> remoteList(RemoteListRequest request) throws GitException,
                                                                                    LocalPathResolveException,
                                                                                    VirtualFileSystemException {
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
    public void remoteUpdate(RemoteUpdateRequest request) throws GitException, LocalPathResolveException,
                                                                 VirtualFileSystemException {
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
    public void reset(ResetRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public void rm(RmRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public Status status(@QueryParam("short") boolean shortFormat) throws GitException,
                                                                          LocalPathResolveException,
                                                                          VirtualFileSystemException {
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
    public Tag tagCreate(TagCreateRequest request)
            throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public void tagDelete(TagDeleteRequest request)
            throws GitException, LocalPathResolveException, VirtualFileSystemException {
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
    public GenericEntity<List<Tag>> tagList(TagListRequest request) throws GitException, LocalPathResolveException,
                                                                           VirtualFileSystemException {
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
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return gitUrlResolver.resolve(uriInfo, vfs, projectId);
    }

    @GET
    @Path("commiters")
    public Commiters getCommiters(@Context UriInfo uriInfo) throws VirtualFileSystemException, GitException {
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
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
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

    private Item getGitProject(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        Item project = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        Item parent = vfs.getItem(project.getParentId(), false, PropertyFilter.ALL_FILTER);
        if (parent.getItemType().equals(ItemType.PROJECT)) // MultiModule project
            return parent;
        return project;
    }


    protected boolean isGitRepository() throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item project = getGitProject(vfs, projectId);

        String value = null;
        for (Property property : project.getProperties()){
            if ("isGitRepository".equals(property.getName())){
                value = (property.getValue() != null && property.getValue().size() > 0) ? property.getValue().get(0) : null;
                break;
            }
        }
        
        return value != null && value.equals("true");
    }

    protected String resolveLocalPath(String projId) throws LocalPathResolveException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (vfs == null) {
            throw new VirtualFileSystemException(
                    "Can't resolve path on the Local File System : Virtual file system not initialized");
        }
        Item gitProject = getGitProject(vfs, projectId);
        final MountPoint mountPoint = ((VirtualFileSystemImpl)vfs).getMountPoint();
        final VirtualFile virtualFile = mountPoint.getVirtualFileById(gitProject.getId());
        return localPathResolver.resolve(virtualFile);
    }

    protected GitConnection getGitConnection()
            throws GitException, LocalPathResolveException, VirtualFileSystemException {
        GitUser gituser = null;
        ConversationState userState = ConversationState.getCurrent();
        try {
            if (userState != null) {
                User user = userManager.getUserByAlias(userState.getIdentity().getUserId());
                String firstName = user.getProfile().getAttribute("firstName");
                String lastName = user.getProfile().getAttribute("lastName");
                String username = "";
                if (firstName != null && firstName.length() != 0) {
                    username += firstName.concat(" ");
                }
                if (lastName != null && lastName.length() != 0) {
                    username += lastName;
                }
                if (username.length() != 0) {
                    gituser = DtoFactory.getInstance().createDto(GitUser.class).withName(username).withEmail(userState.getIdentity().getUserId());
                } else {
                    gituser = DtoFactory.getInstance().createDto(GitUser.class).withName(userState.getIdentity().getUserId());
                }
            }
        } catch (OrganizationServiceException e) {
            LOG.error("It is not possible to get user", e);
            throw new GitException("User not found");
        }
        return gitConnectionFactory.getConnection(resolveLocalPath(projectId), gituser);
    }
}
