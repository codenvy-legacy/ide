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

import com.codenvy.security.oauth.OAuthTokenProvider;
import com.codenvy.security.shared.Token;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.gdata.client.Query;
import com.google.gdata.client.Query.CustomParameter;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;

import org.apache.commons.codec.binary.Base64;
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
public class GoogleContactsClient {
    /** Service for access to the Google Contacts data API. */
    private ContactsService          service;

    private final OAuthTokenProvider oAuthTokenProvider;

    /**
     * Constructs new {@link GoogleContactsClient} instance.
     * 
     * @param oAuthTokenProvider
     * @throws IOException OAuth provider with Google account if any i/o errors occur
     */
    public GoogleContactsClient(OAuthTokenProvider oAuthTokenProvider) throws IOException {
        this.oAuthTokenProvider = oAuthTokenProvider;
        service = new ContactsService("exo.ide");
    }

    /**
     * Returns contact photo as string encoded in Base64.
     * 
     * @param contact Google Contact for getting photo
     * @return contact photo in binary format
     * @throws IOException if any i/o errors occur
     * @throws ServiceException if any error in Google Contacts Service
     */
    public String getContactPhotoAsBase64(ContactEntry contact) throws IOException, ServiceException {
        byte[] photo = getPhoto(contact);
        if (photo != null) {
            return Base64.encodeBase64String(photo);
        }
        return null;
    }

    /**
     * Returns all user's contacts from Google Contacts Service.
     * 
     * @return all user's contacts from Google Contacts Service
     * @throws IOException if any i/o errors occur
     * @throws ServiceException if any error in Google Contacts Service
     */
    public List<ContactEntry> getAllContacts() throws IOException, ServiceException {
        Credential credentials = new Credential(BearerToken.authorizationHeaderAccessMethod());
        Token token = oAuthTokenProvider.getToken("google", getUserId());
        credentials.setAccessToken(token != null ? token.getToken() : null);
        service.setOAuth2Credentials(credentials);
        ContactFeed feed;
        Query query = new Query(new URL("https://www.google.com/m8/feeds/contacts/default/full"));
        query.addCustomParameter(new CustomParameter("xoauth_requestor_id", getUserId()));
        DateTime startTime = new DateTime(0);
        query.setUpdatedMin(startTime);
        int maxResult = 200;
        query.setMaxResults(maxResult);
        List<ContactEntry> googleContacts = new ArrayList<ContactEntry>(maxResult);
        feed = service.query(query, ContactFeed.class);
        googleContacts.addAll(feed.getEntries());
        return googleContacts;
    }

    /**
     * Returns identifier of the user which is logged in.
     * 
     * @return user identifier
     */
    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    /**
     * Returns contact photo in binary format.
     * 
     * @param contactEntry Google Contact for getting photo
     * @return contact photo in binary format
     * @throws IOException if any i/o errors occur
     * @throws ServiceException if any error in Google Contacts Service
     */
    private byte[] getPhoto(ContactEntry contactEntry) {
        Link photoLink = contactEntry.getContactPhotoLink();

        if (photoLink == null || photoLink.getEtag() == null) {
            return null;
        }

        try {
            GDataRequest createLinkQueryRequest = service.createLinkQueryRequest(photoLink);
            createLinkQueryRequest.execute();

            InputStream responseStream = createLinkQueryRequest.getResponseStream();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (true) {
                int read = 0;
                if ((read = responseStream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                } else {
                    break;
                }
            }
            byte[] photo = out.toByteArray();

            createLinkQueryRequest.end();
            responseStream.close();
            out.close();
            return photo;
        } catch (Exception e) {
            return null;
        }

    }
}
