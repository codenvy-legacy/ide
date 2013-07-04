/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.googlecontacts;

import com.codenvy.commons.security.oauth.OAuthTokenProvider;
import com.codenvy.commons.security.shared.Token;
import com.codenvy.organization.invite.InviteService;
import com.codenvy.organization.model.Invitation;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.ServiceException;

import org.exoplatform.services.security.ConversationState;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This REST service is used for getting user's Google Contacts.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GoogleContactsService.java Aug 20, 2012 4:44:58 PM azatsarynnyy $
 */
@Path("{ws-name}/googlecontacts")
public class GoogleContactsRestService {
    @Inject
    private GoogleContactsClient contactsClient;

    @Inject
    private OAuthTokenProvider   oauthTokenProvider;

    @Inject
    private InviteService        inviteService;

    /**
     * Fetch all user's contacts.
     * 
     * @return {@link List} of user's contacts
     * @throws ServiceException if any error in Google Contacts Service
     * @throws IOException if any i/o errors occur
     */
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GoogleContact> getContactList(@Context SecurityContext sctx) throws IOException, ServiceException {
        // list of invited users based on current user as sender invitation
        List<String> invitedUsers = new ArrayList<String>();

        // list of google contacts filtered by invited users if they appears in google contacts book
        List<GoogleContact> contactList = new ArrayList<GoogleContact>();

        for (Invitation invitation : inviteService.invitations(sctx)) {
            invitedUsers.add(invitation.getRecipient());
        }

        for (ContactEntry contactListEntry : contactsClient.getAllContacts()) {
            if (contactListEntry.hasEmailAddresses()) {
                for (Email entriesEmail : contactListEntry.getEmailAddresses()) {
                    // if user is already invited then we hide it from contacts list
                    if (invitedUsers.contains(entriesEmail.getAddress())) {
                        continue;
                    }

                    GoogleContact contact = new GoogleContact();
                    contact.setId(contactListEntry.getSelfLink().getHref());
                    contact.setName(contactListEntry.getTitle().getPlainText());
                    contact.setPhotoBase64(contactsClient.getContactPhotoAsBase64(contactListEntry));
                    contact.setEmailAddresses(contactListEntry.getEmailAddresses());

                    contactList.add(contact);
                }
            }
        }

        return contactList;
    }

    @GET
    @Path("/is-authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public String isAuthenticate() throws Exception {
        Token token = oauthTokenProvider.getToken("google", ConversationState.getCurrent().getIdentity().getUserId());
        return token != null ? token.getScope() : null;
    }
}
