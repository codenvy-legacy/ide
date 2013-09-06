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

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.commons.shared.ProjectType;

import org.apache.commons.io.IOUtils;
import org.codenvy.mail.MailSenderClient;
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
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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

    private static final Pattern PATTERN = Pattern.compile("public static final String PROJECT_ID = .*");

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

    /**
     * Logs event generated during factory URL creation.
     *
     * @param vfsId
     * @param projectId
     * @param action
     * @param factoryUrl
     * @param idCommit
     * @param vcs
     * @param vcsUrl
     */
    @Path("log-factory-created")
    @GET
    public void logFactoryCreated(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                  @QueryParam("action") StringBuilder action,
                                  @QueryParam("factoryurl") StringBuilder factoryUrl,
                                  @QueryParam("idcommit") String idCommit, @QueryParam("vcs") String vcs,
                                  @QueryParam("vcsurl") String vcsUrl) throws VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        String workspace = EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString();
        Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        String user = ConversationState.getCurrent().getIdentity().getUserId();
        factoryUrl.append("&pname=").append(project.getName()).append("&wname=").append(workspace).append("&vcs=")
                  .append(vcs).append("&vcsurl=").append(vcsUrl).append("&idcommit=").append(idCommit)
                  .append("&action=").append(action);
        LOG.info("EVENT#factory-created# WS#" + workspace + "# USER#" + user + "# PROJECT#" + project.getName() +
                 "# TYPE#" + project.getProjectType() + "# REPO-URL#" + vcsUrl + "# FACTORY-URL#" + factoryUrl + "#");
    }

    @POST
    @Path("clone")
    @Produces(MediaType.APPLICATION_JSON)
    public Item cloneProject(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             @QueryParam("remoteuri") String remoteUri,
                             @QueryParam("idcommit") String idCommit,
                             @QueryParam("ptype") String projectType,
                             @QueryParam("action") String action)
            throws VirtualFileSystemException, GitException,
                   URISyntaxException, IOException {
        GitConnection gitConnection = getGitConnection(projectId, vfsId);
        CloneRequest cloneRequest = new CloneRequest(remoteUri, null);
        gitConnection.clone(cloneRequest);
        BranchCheckoutRequest checkoutRequest = new BranchCheckoutRequest();
        checkoutRequest.setName("temp");
        checkoutRequest.setCreateNew(true);
        checkoutRequest.setStartPoint(idCommit);
        gitConnection.branchCheckout(checkoutRequest);
        deleteRepository(vfsId, projectId);
        return convertToProject(vfsId, projectId, remoteUri, projectType, action);

    }

    private Item convertToProject(String vfsId, String projectId, String remoteUri, String projectType, String action)
            throws VirtualFileSystemException, IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item itemToUpdate = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        if (projectType != null && !projectType.isEmpty()) {
            List<Property> props = new ArrayList<Property>();
            props.addAll(itemToUpdate.getProperties());
            props.add(new PropertyImpl("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
            props.add(new PropertyImpl("vfs:projectType", projectType.toString()));
            props.add(new PropertyImpl("codenow", remoteUri));
            itemToUpdate = vfs.updateItem(itemToUpdate.getId(), props, null);
            if (ProjectType.GOOGLE_MBS_ANDROID.toString().equals(projectType)) {
                File constJava = (File)vfs
                        .getItemByPath(itemToUpdate.getPath() + "/src/com/google/cloud/backend/android/Consts.java", null, false,
                                       PropertyFilter.NONE_FILTER);
                String content = IOUtils.toString(vfs.getContent(constJava.getId()).getStream());
                if (action.contains("'projectID=")) {
                    action = action.substring("'projectID=".length());
                    action = action.substring(0, action.length() - 1);
                }
                String newContent = PATTERN.matcher(content).replaceFirst("public static final String PROJECT_ID = \"" + action + "\";");
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
