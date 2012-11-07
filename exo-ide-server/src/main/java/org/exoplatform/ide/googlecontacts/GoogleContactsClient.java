/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.gdata.client.Query;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import org.exoplatform.ide.security.oauth.OAuthTokenProvider;
import org.exoplatform.services.security.ConversationState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Client for Google Contacts Service.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GoogleContactsClient.java Aug 27, 2012 11:36:45 AM azatsarynnyy $
 */
public class GoogleContactsClient
{
   /**
    * Service for access to the Google Contacts data API.
    */
   private ContactsService service;

   /**
    * {@link GoogleContactsClient} instance.
    */
   private static GoogleContactsClient instance;

   /**
    * Read/write access to Contacts and Contact Groups.
    */
   private static final String SCOPE = "https://www.google.com/m8/feeds/";

   /**
    * Base URL for the feed.
    */
   private static final String DEFAULT_FEED = SCOPE + "contacts/";

   /**
    * The special value 'default' can be used to refer to the authenticated user.
    */
   private static final String USER_NAME = "default";

   /**
    * Constructs new {@link GoogleContactsClient} instance.
    *
    * @param oAuthTokenProvider
    * @throws IOException
    *    OAuth provider with Google account
    *    if any i/o errors occur
    */
   public GoogleContactsClient(OAuthTokenProvider oAuthTokenProvider) throws IOException
   {
      service = new ContactsService("exo.ide");
      Credential credentials = new Credential(BearerToken.authorizationHeaderAccessMethod());
      credentials.setAccessToken(oAuthTokenProvider.getToken("google", getUserId()));
      service.setOAuth2Credentials(credentials);
   }

   /**
    * Returns {@link GoogleContactsClient} instance.
    *
    * @return {@link GoogleContactsClient} instance
    * @throws IOException
    *    if any i/o errors occur
    */
   public static GoogleContactsClient getInstance(OAuthTokenProvider oAuthTokenProvider) throws IOException
   {
      if (instance == null)
      {
         instance = new GoogleContactsClient(oAuthTokenProvider);
      }
      return instance;
   }

   /**
    * Returns contact photo as string encoded in Base64.
    *
    * @param contact
    *    Google Contact for getting photo
    * @return contact photo in binary format
    * @throws IOException
    *    if any i/o errors occur
    * @throws ServiceException
    *    if any error in Google Contacts Service
    */
   public String getContactPhotoAsBase64(ContactEntry contact) throws IOException, ServiceException
   {
      byte[] photo = getPhoto(contact);
      if (photo != null)
      {
         return Base64.encode(photo);
      }
      return null;
   }

   /**
    * Returns all user's contacts from Google Contacts Service.
    *
    * @return all user's contacts from Google Contacts Service
    * @throws IOException
    *    if any i/o errors occur
    * @throws ServiceException
    *    if any error in Google Contacts Service
    */
   public List<ContactEntry> getAllContacts() throws IOException, ServiceException
   {
      ContactFeed feed;
      Query query = new Query(new URL(DEFAULT_FEED + USER_NAME + "/full"));
      List<ContactEntry> googleContacts = new ArrayList<ContactEntry>();

      // Loop used because the feed may not contain all of the user's contacts,
      // because there's a default limit on the number of results returned. Default limit is 25 entries.
      do
      {
         feed = service.query(query, ContactFeed.class);
         googleContacts.addAll(feed.getEntries());
         query.setStartIndex(feed.getEntries().size() + query.getStartIndex());
      }
      while (feed.getTotalResults() > query.getStartIndex());

      return googleContacts;
   }

   /**
    * Returns identifier of the user which is logged in.
    *
    * @return user identifier
    */
   private String getUserId()
   {
      return ConversationState.getCurrent().getIdentity().getUserId();
   }

   /**
    * Returns contact photo in binary format.
    *
    * @param contactEntry
    *    Google Contact for getting photo
    * @return contact photo in binary format
    * @throws IOException
    *    if any i/o errors occur
    * @throws ServiceException
    *    if any error in Google Contacts Service
    */
   private byte[] getPhoto(ContactEntry contactEntry) throws IOException, ServiceException
   {
      Link photoLink = contactEntry.getContactPhotoLink();

      if (photoLink == null || photoLink.getEtag() == null)
      {
         return null;
      }

      GDataRequest createLinkQueryRequest = service.createLinkQueryRequest(photoLink);
      createLinkQueryRequest.execute();

      InputStream responseStream = createLinkQueryRequest.getResponseStream();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      while (true)
      {
         int read = 0;
         if ((read = responseStream.read(buffer)) != -1)
         {
            out.write(buffer, 0, read);
         }
         else
         {
            break;
         }
      }
      byte[] photo = out.toByteArray();

      createLinkQueryRequest.end();
      responseStream.close();
      out.close();

      return photo;
   }
}
