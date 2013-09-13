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
