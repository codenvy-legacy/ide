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
package com.codenvy.ide.factory.server;

import com.codenvy.api.factory.SimpleFactoryUrl;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.ide.commons.shared.ProjectType;
import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.User;
import com.codenvy.organization.model.Workspace;

import org.codenvy.mail.MailSenderClient;
import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.ide.git.server.*;
import org.exoplatform.ide.git.shared.*;
import org.exoplatform.ide.project.ProjectPrepare;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.impl.fs.LocalFSMountStrategy;
import org.exoplatform.ide.vfs.server.*;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Service for sharing Factory URL by e-mail messages.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryService.java Jun 25, 2013 10:50:00 PM azatsarynnyy $
 */
@Path("{ws-name}/factory")
public class FactoryService {
    private static final Log LOG = ExoLogger.getLogger(FactoryService.class);

    @Inject
    private MailSenderClient            mailSenderClient;
    @Inject
    private GitConnectionFactory        gitConnectionFactory;
    @Inject
    private VirtualFileSystemRegistry   vfsRegistry;
    @Inject
    private LocalPathResolver           localPathResolver;
    @Inject
    private UserManager                 userManager;
    @Inject
    private WorkspaceManager            workspaceManager;
    @Inject
    private LocalFSMountStrategy        mountStrategy;
    @Inject
    private GitRepositoryPrivacyChecker privacyChecker;

    @PathParam("ws-name")
    private String workspaceName;

    private static final Pattern PATTERN        = Pattern.compile("public static final String PROJECT_ID = .*");
    private static final Pattern PATTERN_NUMBER = Pattern.compile("public static final String PROJECT_NUMBER = .*");

    private static final String BRANCH_NOT_FOUND    = "Branch <b>%s</b> doesn't exist. Switching to default branch.";
    private static final String SWITCHING_TO_BRANCH = "Switching to <b>%s</b> branch.";
    private static final String COMMIT_NOT_FOUND    = "Commit <b>%s</b> doesn't exist. Switching to default branch.";

    private static final Pattern COMMIT_NOT_FOUND_PATTERN = Pattern.compile("(.*fatal: reference is not a tree:.*)|" +
                                                                            "(.*fatal: Cannot update paths and switch to branch 'temp'.*)");

    /**
     * Sends e-mail message to share Factory URL.
     *
     * @param recipient
     *         address to share Factory URL
     * @param message
     *         text message that includes Factory URL
     * @return the Response with the corresponded status
     */
    @POST
    @Path("share")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RolesAllowed({"workspace/developer"})
    public Response share(@FormParam("recipient") String recipient,
                          @FormParam("message") String message) {
        try {
            mailSenderClient.sendMail("Codenvy <noreply@codenvy.com>",
                                      recipient,
                                      null,
                                      "Check out my Codenvy project",
                                      "text/plain; charset=utf-8",
                                      URLDecoder.decode(message, "UTF-8"));
            return Response.ok().build();
        } catch (MessagingException | IOException e) {
            LOG.warn(e.getLocalizedMessage(), e);
            throw new WebApplicationException(e);
        }
    }

    /**
     * Clone specified git repository and perform converting it into IDE project.
     *
     * @param factoryUrl
     *         gitConnectionFactory object
     * @return cloned project
     * @throws VirtualFileSystemException
     * @throws GitException
     * @throws URISyntaxException
     * @throws IOException
     */
    @POST
    @Path("clone")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //@RolesAllowed({"workspace/developer"})
    public Item cloneProject(SimpleFactoryUrl factoryUrl,
                             @QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId) throws VirtualFileSystemException, GitException,
                                                                               URISyntaxException, IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        try {
            GitConnection gitConnection = getGitConnection(vfs, projectId);
            gitConnection.clone(new CloneRequest(factoryUrl.getVcsurl(), null));

            //check and set type of cloned repository
            setPrivateRepositoryPermissions(factoryUrl.getVcsurl(), vfs, projectId);

            if (factoryUrl.getCommitid() != null && !factoryUrl.getCommitid().trim().isEmpty()) {
                //Try to checkout to new branch "temp" with HEAD of setted commit ID
                gitConnection.branchCheckout(new BranchCheckoutRequest("temp", factoryUrl.getCommitid(), true));
            } else if (factoryUrl.getVcsbranch() != null && !factoryUrl.getVcsbranch().trim().isEmpty()) {
                //Try to checkout to specified branch. For first we need to list all cloned local branches to
                //find if specified branch already exist, if its true, we check if this this branch is active
                if (!branchCheckouted(gitConnection, factoryUrl)) {
                    publishWebsocketMessage(String.format(BRANCH_NOT_FOUND, factoryUrl.getVcsbranch()));
                }
            }
        } catch (GitException e) {
            if (COMMIT_NOT_FOUND_PATTERN.matcher(e.getLocalizedMessage()).find()) {
                publishWebsocketMessage(String.format(COMMIT_NOT_FOUND, factoryUrl.getCommitid()));
            } else {
                LOG.warn(e.getLocalizedMessage(), e);
                vfs.delete(projectId, null);
                throw e;
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            vfs.delete(projectId, null);
            throw e;
        } finally {
            //Finally if we found parameter vcsinfo we check that we should delete git repository after cloning.
            if (!factoryUrl.getVcsinfo()) {
                deleteRepository(vfs, projectId);
            }
        }

        return convertToProject(factoryUrl, vfsId, projectId);
    }

    /**
     * Returns true if checkout was successful, otherwise false;
     */
    private boolean branchCheckouted(GitConnection gitConnection, SimpleFactoryUrl factoryUrl) throws GitException {
        List<Branch> branches = gitConnection.branchList(new BranchListRequest(BranchListRequest.LIST_ALL));

        String branchName;
        for (Branch branch : branches) {
            branchName = branch.getDisplayName();
            if (branchName.contains("origin")) {
                String[] temp = branch.getDisplayName().split("/");
                branchName = temp[temp.length - 1];
            }

            if (branchName.equals(factoryUrl.getVcsbranch())) {
                gitConnection.branchCheckout(new BranchCheckoutRequest(branch.getDisplayName(), branch.getName(), branch.isRemote()));
                publishWebsocketMessage(String.format(SWITCHING_TO_BRANCH, factoryUrl.getVcsbranch()));
                return true;
            }
        }

        return false;
    }

    /**
     * Send message to socket to allow client make output to console.
     *
     * @param content
     *         message content
     */
    private void publishWebsocketMessage(String content) {
        ChannelBroadcastMessage message = new ChannelBroadcastMessage();
        message.setChannel("factory-events");
        message.setType(ChannelBroadcastMessage.Type.NONE);
        message.setBody(content);

        try {
            WSConnectionContext.sendMessage(message);
        } catch (Exception ex) {
            LOG.error("Failed to send message over WebSocket.", ex);
        }
    }

    /**
     * Perform converting cloned directory into project.
     *
     * @param factoryUrl
     *         gitConnectionFactory object
     * @return {@link ProjectModel} instance
     * @throws VirtualFileSystemException
     * @throws IOException
     */
    private Item convertToProject(SimpleFactoryUrl factoryUrl, String vfsId, String projectId)
            throws VirtualFileSystemException, IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item itemToUpdate = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);

        ProjectType projectType = ProjectType.DEFAULT;
        String encodedParamType = factoryUrl.getProjectattributes().get("ptype");

        if (encodedParamType != null && !encodedParamType.isEmpty()) {
            try {
                projectType = ProjectType.fromValue(URLDecoder.decode(encodedParamType, "UTF-8"));
            } catch (IllegalArgumentException e) {
                //if exception, our project type already setted to "default"
                LOG.error(e.getLocalizedMessage(), e);
            }
        }

        List<Property> props = new ArrayList<>();

        if (projectType == ProjectType.DEFAULT || projectType == ProjectType.MULTI_MODULE) {
            try {
                new ProjectPrepare(vfs).doPrepare(projectId);
                itemToUpdate = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
                projectType = ProjectType.fromValue(((Project)itemToUpdate).getProjectType());
            } catch (Exception e) {
                itemToUpdate = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
                props.addAll(itemToUpdate.getProperties());
                props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
                props.add(new PropertyImpl("vfs:projectType", projectType.toString()));
            }
        } else {
            props.addAll(itemToUpdate.getProperties());
            props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
            props.add(new PropertyImpl("vfs:projectType", projectType.toString()));
        }

        props.add(new PropertyImpl("codenow", factoryUrl.getVcsurl()));
        if (factoryUrl.getVcsinfo()) {
            props.add(new PropertyImpl("isGitRepository", "true"));
        }

        itemToUpdate = vfs.updateItem(itemToUpdate.getId(), props, null);

        if (factoryUrl.getVariables() != null && factoryUrl.getVariables().size() != 0) {
            try {
                java.io.File workspace = mountStrategy.getMountPath();
                java.nio.file.Path path = Paths.get(workspace.getAbsolutePath(), factoryUrl.getProjectattributes().get("pname"));

                new VariableReplacer(path).performReplacement(factoryUrl.getVariables());
            } catch (VirtualFileSystemException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }

        if (ProjectType.GOOGLE_MBS_ANDROID == projectType) {
            prepareAndroidProject(factoryUrl, vfs, itemToUpdate);
        }

        return itemToUpdate;
    }

    /**
     * Prepare Consts.java file for Android projects.
     * NOTE Deprecated since 2.9.3 and will be removed from IDE after 2.10.x.
     *
     * @param factoryUrl
     *         gitConnectionFactory instance
     * @param vfs
     *         virtual file system
     * @param item
     *         {@link ProjectModel} instance
     */
    @Deprecated
    private void prepareAndroidProject(SimpleFactoryUrl factoryUrl, VirtualFileSystem vfs, Item item) {
        final String path = item.getPath() + "/src/com/google/cloud/backend/android/Consts.java";

        try {
            final File constJava = (File)vfs.getItemByPath(path, null, false, PropertyFilter.NONE_FILTER);
            final String content = IoUtil.readStream(vfs.getContent(constJava.getId()).getStream());

            final String action =
                    (factoryUrl.getAction().toLowerCase().indexOf("%3d") > 0) ? URLDecoder.decode(factoryUrl.getAction(), "UTF-8")
                                                                              : factoryUrl.getAction();

            String[] actionParams = action.replaceAll("'", "").split(";");
            String prjNum = "";
            String prjID = "";

            for (String param : actionParams) {
                if (param.startsWith("projectNumber")) {
                    prjNum = param.split("=")[1];
                }
                if (param.startsWith("projectID")) {
                    prjID = param.split("=")[1];
                }
            }

            String newContent = PATTERN.matcher(content).replaceFirst("public static final String PROJECT_ID = \"" + prjID + "\";");
            newContent = PATTERN_NUMBER.matcher(newContent).replaceFirst("public static final String PROJECT_NUMBER = \"" + prjNum + "\";");
            vfs.updateContent(constJava.getId(), MediaType.valueOf(constJava.getMimeType()),
                              new ByteArrayInputStream(newContent.getBytes()),
                              null);
        } catch (VirtualFileSystemException e) {
            if (e instanceof ItemNotFoundException) {
                publishWebsocketMessage("File '" + path + "' doesn't exists.");
            } else {
                publishWebsocketMessage("Failed to update fields in '" + path + "'.");
            }
        } catch (IOException e) {
            publishWebsocketMessage("Failed to read file '" + path + "'.");
        }
    }

    /**
     * Perform deleting git repository.
     *
     * @throws VirtualFileSystemException
     */
    private void deleteRepository(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        try {
            Item project = getGitProject(vfs, projectId);
            String path2gitFolder = project.getPath() + "/.git";
            Item gitItem = vfs.getItemByPath(path2gitFolder, null, false, PropertyFilter.NONE_FILTER);
            vfs.delete(gitItem.getId(), null);
        } catch (ItemNotFoundException e) {
            LOG.warn(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Retrieve git connection instance.
     *
     * @return {@link GitConnection} instance
     * @throws GitException
     * @throws VirtualFileSystemException
     */
    private GitConnection getGitConnection(VirtualFileSystem vfs, String projectId)
            throws GitException, VirtualFileSystemException {
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
                    gituser = new GitUser(username, userState.getIdentity().getUserId());
                } else {
                    gituser = new GitUser(userState.getIdentity().getUserId());
                }
            }
        } catch (OrganizationServiceException e) {
            LOG.error("It is not possible to get user", e);
            throw new GitException("User not found");
        }
        return gitConnectionFactory.getConnection(localPathResolver.resolve(vfs, getGitProject(vfs, projectId).getId()), gituser);
    }

    /**
     * Get item of cloned project.
     *
     * @return {@link ProjectModel} instance
     * @throws VirtualFileSystemException
     */
    private Item getGitProject(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        Item project = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        Item parent = vfs.getItem(project.getParentId(), false, PropertyFilter.ALL_FILTER);
        if (parent.getItemType().equals(ItemType.PROJECT)) // MultiModule project
            return parent;
        return project;
    }

    private void setPrivateRepositoryPermissions(String vcsUrl, VirtualFileSystem vfs, String projectId) {
        try {
            Workspace workspace = workspaceManager.getWorkspaceByName(workspaceName);
            if (privacyChecker.isRepositoryPublic(vcsUrl)) {
                workspace.setAttribute("is_private", "false");
            } else {
                workspace.setAttribute("is_private", "true");
                AccessControlEntry ace =
                        new AccessControlEntryImpl(new PrincipalImpl("workspace/developer", Principal.Type.GROUP), Collections
                                .singleton(VirtualFileSystemInfo.BasicPermissions.ALL.value()));
                vfs.updateACL(projectId, Arrays.asList(ace), true, null);
            }
            workspaceManager.updateWorkspace(workspace);
        } catch (OrganizationServiceException e) {
            LOG.error(e.getLocalizedMessage(), e);
        } catch (VirtualFileSystemException e) {
            LOG.error("Can't set permissions for private project. " + e.getLocalizedMessage(), e);
        }
    }
}
