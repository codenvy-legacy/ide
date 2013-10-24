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
import com.codenvy.commons.security.oauth.OAuthTokenProvider;
import com.codenvy.ide.commons.shared.ProjectType;
import com.codenvy.ide.factory.shared.FactorySpec10;

import org.apache.commons.io.IOUtils;
import org.codenvy.mail.MailSenderClient;
import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
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

    private final MailSenderClient          mailSenderClient;
    private final GitConnectionFactory      factory;
    private       VirtualFileSystemRegistry vfsRegistry;
    private       LocalPathResolver         localPathResolver;

    @QueryParam("vfsid")
    private String vfsId;

    @QueryParam("projectid")
    private String projectId;

    @Inject
    private OAuthTokenProvider oauthTokenProvider;

    private static final Pattern PATTERN          = Pattern.compile("public static final String PROJECT_ID = .*");
    private static final Pattern PATTERN_NUMBER   = Pattern.compile("public static final String PROJECT_NUMBER = .*");
    private static final String  TEMPORARY_BRANCH = "temp";

    /**
     * Constructs a new {@link FactoryService}.
     *
     * @param mailSenderClient
     *         client for sending messages over e-mail
     */
    public FactoryService(MailSenderClient mailSenderClient, VirtualFileSystemRegistry vfsRegistry,
                          LocalPathResolver localPathResolver, GitConnectionFactory factory) {
        this.mailSenderClient = mailSenderClient;
        this.vfsRegistry = vfsRegistry;
        this.localPathResolver = localPathResolver;
        this.factory = factory;
    }

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
    public Response share(@FormParam("recipient") String recipient, //
                          @FormParam("message") String message) {
        final String sender = "Codenvy <noreply@codenvy.com>";
        final String subject = "Check out my Codenvy project";
        final String mimeType = "text/plain; charset=utf-8";
        try {
            mailSenderClient.sendMail(sender, recipient, null, subject, mimeType, URLDecoder.decode(message, "UTF-8"));
            return Response.ok().build();
        } catch (MessagingException | IOException e) {
            throw new WebApplicationException(e);
        }
    }

    /**
     * Clone specified git repository and perform converting it into IDE project.
     *
     * @param factoryUrl
     *         factory object
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
    public Item cloneProject(SimpleFactoryUrl factoryUrl)
            throws VirtualFileSystemException, GitException,
                   URISyntaxException, IOException {
        GitConnection gitConnection = getGitConnection();
        try {
            gitConnection.clone(new CloneRequest(factoryUrl.getVcsurl(), null));
            if (factoryUrl.getCommitid() != null && !factoryUrl.getCommitid().trim().isEmpty()) {
                //Try to checkout to new branch "temp" with HEAD of setted commit ID
                gitConnection.branchCheckout(new BranchCheckoutRequest(TEMPORARY_BRANCH, factoryUrl.getCommitid(), true));
            } else if (factoryUrl.getVcsbranch() != null && !factoryUrl.getVcsbranch().trim().isEmpty()) {
                //Try to checkout to specified branch. For first we need to list all cloned local branches to
                //find if specified branch already exist, if its true, we check if this this branch is active
                List<Branch> branches = gitConnection.branchList(new BranchListRequest(null));
                for (Branch branch : branches) {
                    if (branch.getDisplayName().equals(factoryUrl.getVcsbranch())) {
                        gitConnection.branchCheckout(
                                new BranchCheckoutRequest(factoryUrl.getVcsbranch(), "origin/" + factoryUrl.getVcsbranch(), false));
                        break;
                    }
                }
            }
        } catch (GitException e) {
            if (e.getMessage().matches("(.*Ref .* can not be resolved.*)|(.*Missing unknown.*)")) {
                if (factoryUrl.getCommitid() != null && !factoryUrl.getCommitid().trim().isEmpty()) {
                    publishWebsocketMessage("Commit <b>" + factoryUrl.getCommitid() + "</b> doesn't exist. Switching to default branch.");
                } else if (factoryUrl.getVcsbranch() != null && !factoryUrl.getVcsbranch().trim().isEmpty()) {
                    publishWebsocketMessage("Branch <b>" + factoryUrl.getVcsbranch() + "</b> doesn't exist. Switching to default branch.");
                }
            } else {
                deleteRepository();
                throw e;
            }
        } finally {
            //Finally if we found parameter vcsinfo we check that we should delete git repository after cloning.
            if (!factoryUrl.getVcsinfo()) {
                deleteRepository();
            }
        }

        return convertToProject(factoryUrl);
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
     *         factory object
     * @return {@link ProjectModel} instance
     * @throws VirtualFileSystemException
     * @throws IOException
     */
    private Item convertToProject(SimpleFactoryUrl factoryUrl)
            throws VirtualFileSystemException, IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item itemToUpdate = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        try {
            Item item = vfs.getItemByPath(itemToUpdate.getPath() + "/.project", null, false, null);
            vfs.delete(item.getId(), null);
        } catch (ItemNotFoundException ignore) {
            // ignore
        }

        ProjectType projectType = ProjectType.fromValue(factoryUrl.getProjectattributes().get(FactorySpec10.PROJECT_TYPE));

        List<Property> props = new ArrayList<Property>();
        props.addAll(itemToUpdate.getProperties());
        props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
        props.add(new PropertyImpl("vfs:projectType", projectType.toString()));
        props.add(new PropertyImpl("codenow", factoryUrl.getVcsurl()));
        if (factoryUrl.getVcsinfo())
            props.add(new PropertyImpl("isGitRepository", "true"));
        itemToUpdate = vfs.updateItem(itemToUpdate.getId(), props, null);

        if (ProjectType.GOOGLE_MBS_ANDROID == projectType) {
            prepareAndroidProject(factoryUrl, vfs, itemToUpdate);
        }

        return itemToUpdate;
    }

    /**
     * Prepare Consts.java file for Android projects.
     *
     * @param factoryUrl
     *         factory instance
     * @param vfs
     *         virtual file system
     * @param item
     *         {@link ProjectModel} instance
     * @throws VirtualFileSystemException
     * @throws IOException
     */
    private void prepareAndroidProject(SimpleFactoryUrl factoryUrl, VirtualFileSystem vfs, Item item)
            throws VirtualFileSystemException, IOException {
        File constJava =
                (File)vfs.getItemByPath(item.getPath() + "/src/com/google/cloud/backend/android/Consts.java", null, false,
                                        PropertyFilter.NONE_FILTER);
        String content = IOUtils.toString(vfs.getContent(constJava.getId()).getStream());

        String[] actionParams = factoryUrl.getAction().replaceAll("'", "").split(";");
        String prjNum = null;
        String prjID = null;

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
        vfs.updateContent(constJava.getId(), MediaType.valueOf(constJava.getMimeType()), new ByteArrayInputStream(newContent.getBytes()),
                          null);
    }

    /**
     * Perform deleting git repository.
     *
     * @throws VirtualFileSystemException
     */
    protected void deleteRepository() throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        try {
            Item project = getGitProject();
            String path2gitFolder = project.getPath() + "/.git";
            Item gitItem = vfs.getItemByPath(path2gitFolder, null, false, PropertyFilter.NONE_FILTER);
            vfs.delete(gitItem.getId(), null);
        } catch (ItemNotFoundException e) {
            //ignore
        }
    }

    /**
     * Retrieve git connection instance.
     *
     * @return {@link GitConnection} instance
     * @throws GitException
     * @throws VirtualFileSystemException
     */
    protected GitConnection getGitConnection() throws GitException, VirtualFileSystemException {
        GitUser gituser = null;
        ConversationState user = ConversationState.getCurrent();
        if (user != null) {
            gituser = new GitUser(user.getIdentity().getUserId());
        }
        return factory.getConnection(resolveLocalPath(), gituser);
    }

    /**
     * Resolve path to project in file system.
     *
     * @return path to cloned project
     * @throws VirtualFileSystemException
     */
    protected String resolveLocalPath() throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (vfs == null) {
            throw new VirtualFileSystemException(
                    "Can't resolve path on the Local File System : Virtual file system not initialized");
        }
        Item gitProject = getGitProject();
        return localPathResolver.resolve(vfs, gitProject.getId());
    }

    /**
     * Get item of cloned project.
     *
     * @return {@link ProjectModel} instance
     * @throws VirtualFileSystemException
     */
    private Item getGitProject() throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item project = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        Item parent = vfs.getItem(project.getParentId(), false, PropertyFilter.ALL_FILTER);
        if (parent.getItemType().equals(ItemType.PROJECT)) // MultiModule project
            return parent;
        return project;
    }
}
