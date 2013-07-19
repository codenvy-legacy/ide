/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.factory.server;

import org.codenvy.mail.MailSenderClient;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.security.ConversationState;

import javax.mail.MessagingException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for sharing Factory URL by e-mail messages.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryService.java Jun 25, 2013 10:50:00 PM azatsarynnyy $
 */
@Path("{ws-name}/factory")
public class FactoryService {

    private final MailSenderClient          mailSenderClient;
    private       VirtualFileSystemRegistry vfsRegistry;
    private       LocalPathResolver         localPathResolver;

    /**
     * Constructs a new {@link FactoryService}.
     *
     * @param mailSenderClient
     *         client for sending messages over e-mail
     */
    public FactoryService(MailSenderClient mailSenderClient, VirtualFileSystemRegistry vfsRegistry, LocalPathResolver localPathResolver) {
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
    public void cloneProject(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                             @QueryParam("remoteuri") String remoteUri, @QueryParam("idcommit") String idCommit) throws VirtualFileSystemException, GitException,
                                                                               URISyntaxException {
        GitConnection gitConnection = getGitConnection(projectId, vfsId);
        CloneRequest cloneRequest = new CloneRequest(remoteUri, null);
        gitConnection.clone(cloneRequest);
        BranchCheckoutRequest checkoutRequest = new BranchCheckoutRequest();
        checkoutRequest.setName("temp");
        checkoutRequest.setCreateNew(true);
        checkoutRequest.setStartPoint(idCommit);
        gitConnection.branchCheckout(checkoutRequest);
        deleteRepository(vfsId,projectId);

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
            throw new VirtualFileSystemException("Can't resolve path on the Local File System : Virtual file system not initialized");
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
