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
package org.exoplatform.ide.security.oauth;

import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import org.exoplatform.ide.security.shared.User;

import java.util.Collections;
import java.util.HashSet;

/**
 * OAuth authentication for google account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public class GoogleOAuthAuthenticator extends OAuthAuthenticator
{

   protected GoogleOAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets)
   {
      super(new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(), clientSecrets,
         Collections.<String>emptyList()).setCredentialStore(credentialStore).setApprovalPrompt("auto").setAccessType
         ("online").build(), new HashSet<String>(clientSecrets.getDetails().getRedirectUris()));
   }

   @Override
   public User getUser(String accessToken) throws OAuthAuthenticationException
   {
      return getJson("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken, GoogleUser.class);
   }

   @Override
   public final String getOAuthProvider()
   {
      return "google";
   }
}
