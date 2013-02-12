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

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.ServiceException;

import org.exoplatform.ide.invite.Invite;
import org.exoplatform.ide.invite.InviteException;
import org.exoplatform.ide.invite.InviteService;
import org.exoplatform.ide.security.oauth.OAuthTokenProvider;
import org.exoplatform.services.security.ConversationState;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This REST service is used for getting user's Google Contacts.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GoogleContactsService.java Aug 20, 2012 4:44:58 PM azatsarynnyy $
 *
 */
@Path("/ide/googlecontacts")
public class GoogleContactsRestService
{
   @Inject
   private GoogleContactsClient client;

   @Inject
   private OAuthTokenProvider oauthTokenProvider;

   @Inject
   private InviteService inviteService;

   /**
    * Fetch all user's contacts.
    *
    * @return {@link List} of user's contacts
    * @throws ServiceException
    *    if any error in Google Contacts Service 
    * @throws IOException
    *    if any i/o errors occur
    */
   @GET
   @Path("/all")
   @Produces(MediaType.APPLICATION_JSON)
   public List<GoogleContact> getContactList() throws IOException, ServiceException
   {
      //getting filtered by workspace owner invites
      List<String> filteredByCurrentUser = new ArrayList<String>();
      try
      {
         if (ConversationState.getCurrent() == null)
         {
            throw new ServiceException("Error getting current user id.");
         }

         String currentId = ConversationState.getCurrent().getIdentity().getUserId();

         for (Invite invite : inviteService.getInvites(false))
         {
            if (invite.getFrom() != null && invite.getFrom().equals(currentId))
            {
               filteredByCurrentUser.add(invite.getEmail());
            }
         }
      }
      catch (InviteException e)
      {
         throw new ServiceException(e.getMessage(), e);
      }

      //getting google contacts
      List<GoogleContact> contactList = new ArrayList<GoogleContact>();
      outer:
      for (ContactEntry contactEntry : client.getAllContacts())
      {
         List<Email> contactEmailList = contactEntry.getEmailAddresses();
         // skip contacts without email
         if (contactEmailList.isEmpty())
         {
            continue;
         }

         List<String> emails = new ArrayList<String>();
         for (Email email : contactEmailList)
         {
            //check if contact is already invited, if true - than we don't displayed him from invite proposals
            if (filteredByCurrentUser.contains(email.getAddress()))
            {
               continue outer;
            }
            emails.add(email.getAddress());
         }

         GoogleContact contact = new GoogleContact();
         contact.setId(contactEntry.getSelfLink().getHref());
         contact.setName(contactEntry.getTitle().getPlainText());
         contact.setPhotoBase64(client.getContactPhotoAsBase64(contactEntry));
         contact.setEmailAddresses(emails);

         contactList.add(contact);
      }

      return contactList;
   }

   @GET
   @Path("/is-authenticate")
   @Produces(MediaType.APPLICATION_JSON)
   public Response isAuthenticate() throws Exception
   {
      final String userId = ConversationState.getCurrent().getIdentity().getUserId();
      if (oauthTokenProvider.getToken("google", userId) == null)
      {
         return Response.status(200).entity("{\"state\":\"invalid\"}").build();
      }

      String token = oauthTokenProvider.getToken("google", userId);

      URL tokenInfoUrl = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + token);
      HttpURLConnection connection = (HttpURLConnection)tokenInfoUrl.openConnection();

      if (connection.getResponseCode() == 200)
      {
         return Response.status(200).entity("{\"state\":\"valid\"}").build();
      }
      else
      {

         return Response.status(200).entity("{\"state\":\"invalid\"}").build();
      }
   }
}
