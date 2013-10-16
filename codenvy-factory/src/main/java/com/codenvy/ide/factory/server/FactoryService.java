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

import com.codenvy.commons.security.oauth.OAuthTokenProvider;
import com.codenvy.ide.commons.shared.ProjectType;

import org.apache.commons.io.IOUtils;
import org.codenvy.mail.MailSenderClient;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
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
import javax.ws.rs.*;
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
    private       VirtualFileSystemRegistry vfsRegistry;
    private       LocalPathResolver         localPathResolver;

    @Inject
    private OAuthTokenProvider oauthTokenProvider;

    private static final Pattern PATTERN        = Pattern.compile("public static final String PROJECT_ID = .*");
    private static final Pattern PATTERN_NUMBER = Pattern.compile("public static final String PROJECT_NUMBER = .*");

    /**
     * Constructs a new {@link FactoryService}.
     *
     * @param mailSenderClient
     *         client for sending messages over e-mail
     */
    public FactoryService(MailSenderClient mailSenderClient, VirtualFileSystemRegistry vfsRegistry,
                          LocalPathResolver localPathResolver) {
        this.mailSenderClient = mailSenderClient;
        this.vfsRegistry = vfsRegistry;
        this.localPathResolver = localPathResolver;
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
    public Response share(@QueryParam("recipient") String recipient, //
                          @QueryParam("message") String message) {
        final String sender = "Codenvy <noreply@codenvy.com>";
        final String subject = "Check out my Codenvy project";
        final String mimeType = "text/html; charset=utf-8";
        try {
            mailSenderClient.sendMail(sender, recipient, null, subject, mimeType, message);
            return Response.ok().build();
        } catch (MessagingException | IOException e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("clone")
    @Produces(MediaType.APPLICATION_JSON)
    public Item cloneProject(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             @QueryParam("remoteuri") String remoteUri,
                             @QueryParam("idcommit") String idCommit,
                             @QueryParam("ptype") String projectType,
                             @QueryParam("action") String action,
                             @QueryParam("keepvcsinfo") boolean keepVcsInfo,
                             @QueryParam("gitbranch") String gitBranch)
            throws VirtualFileSystemException, GitException,
                   URISyntaxException, IOException {
        GitConnection gitConnection = null;

        try {
            gitConnection = getGitConnection(projectId, vfsId);
            CloneRequest cloneRequest = new CloneRequest(remoteUri, null);
            gitConnection.clone(cloneRequest);
            BranchCheckoutRequest checkoutRequest = new BranchCheckoutRequest();
            if (idCommit != null && !idCommit.isEmpty()) {
                checkoutRequest.setName("temp");
                checkoutRequest.setCreateNew(true);
                checkoutRequest.setStartPoint(idCommit);
            } else if (gitBranch != null && !gitBranch.isEmpty()) {
                //by default master branch already exist
                checkoutRequest.setCreateNew(!(gitBranch.equals("master") || gitBranch.equals("origin/master")));
                checkoutRequest.setName(gitBranch);
                checkoutRequest.setStartPoint("origin/" + gitBranch);
            } else {
                checkoutRequest.setName("master");
                checkoutRequest.setCreateNew(false);
                checkoutRequest.setStartPoint("origin/master");
            }
            gitConnection.branchCheckout(checkoutRequest);
            if (!keepVcsInfo)
                deleteRepository(vfsId, projectId);
        } catch (JGitInternalException e) {
            //if commit id doesn't exist, jgit produce exception like "Missing unknown <hashOfCommit>"
            if (e.getMessage().contains("Missing unknown") && gitConnection != null) {
                publishWebsocketMessage("Commit <b>" + idCommit + "</b> doesn't exist. Switching to default branch.");

                //trying to switch to head of default branch
                BranchCheckoutRequest checkoutRequest = new BranchCheckoutRequest();
                checkoutRequest.setName("master");
                checkoutRequest.setCreateNew(false);
                checkoutRequest.setStartPoint("origin/master");
                gitConnection.branchCheckout(checkoutRequest);
                if (!keepVcsInfo)
                    deleteRepository(vfsId, projectId);
            } else {
                deleteRepository(vfsId, projectId);
                throw new GitException(e);
            }
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().matches("Ref [a-zA-Z0-9-_]+ can not be resolved")) {
                throw new IllegalArgumentException(e);
            } else {
                publishWebsocketMessage("Branch <b>" + gitBranch + "</b> doesn't exist. Switching to default branch.");
            }
        }

        return convertToProject(vfsId, projectId, remoteUri, projectType, action, keepVcsInfo);
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

    private Item convertToProject(String vfsId, String projectId, String remoteUri, String projectType, String action, boolean keepVcsInfo)
            throws VirtualFileSystemException, IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item itemToUpdate = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        try {
            Item item = vfs.getItemByPath(itemToUpdate.getPath() + "/.project", null, false, null);
            vfs.delete(item.getId(), null);
        } catch (ItemNotFoundException ignore) {
            // ignore
        }
        if (projectType != null && !projectType.isEmpty()) {
            List<Property> props = new ArrayList<Property>();
            props.addAll(itemToUpdate.getProperties());
            props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
            props.add(new PropertyImpl("vfs:projectType", projectType));
            props.add(new PropertyImpl("codenow", remoteUri));
            if (keepVcsInfo)
                props.add(new PropertyImpl("isGitRepository", "true"));
            itemToUpdate = vfs.updateItem(itemToUpdate.getId(), props, null);
            if (ProjectType.GOOGLE_MBS_ANDROID.toString().equals(projectType)) {
                File constJava = (File)vfs
                        .getItemByPath(itemToUpdate.getPath() + "/src/com/google/cloud/backend/android/Consts.java", null, false,
                                       PropertyFilter.NONE_FILTER);
                String content = IOUtils.toString(vfs.getContent(constJava.getId()).getStream());

                String[] actionParams = action.replaceAll("'", "").split(";");
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
                newContent =
                        PATTERN_NUMBER.matcher(newContent).replaceFirst("public static final String PROJECT_NUMBER = \"" + prjNum + "\";");
                vfs.updateContent(constJava.getId(), MediaType.valueOf(constJava.getMimeType()),
                                  new ByteArrayInputStream(newContent.getBytes()), null);
            }
        }
        return itemToUpdate;
    }

    protected void deleteRepository(String vfsId, String projectId) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item project = getGitProject(vfs, projectId);
        String path2gitFolder = project.getPath() + "/.git";
        Item gitItem = vfs.getItemByPath(path2gitFolder, null, false, PropertyFilter.NONE_FILTER);
        vfs.delete(gitItem.getId(), null);
    }

    protected GitConnection getGitConnection(String projectId, String vfsId)
            throws GitException, VirtualFileSystemException {
        GitUser gituser = null;
        ConversationState user = ConversationState.getCurrent();
        if (user != null) {
            gituser = new GitUser(user.getIdentity().getUserId());
        }
        return GitConnectionFactory.getInstance().getConnection(resolveLocalPath(projectId, vfsId), gituser);
    }

    protected String resolveLocalPath(String projectId, String vfsId) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (vfs == null) {
            throw new VirtualFileSystemException(
                    "Can't resolve path on the Local File System : Virtual file system not initialized");
        }
        Item gitProject = getGitProject(vfs, projectId);
        return localPathResolver.resolve(vfs, gitProject.getId());
    }

    private Item getGitProject(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        Item project = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        Item parent = vfs.getItem(project.getParentId(), false, PropertyFilter.ALL_FILTER);
        if (parent.getItemType().equals(ItemType.PROJECT)) // MultiModule project
            return parent;
        return project;
    }
}
