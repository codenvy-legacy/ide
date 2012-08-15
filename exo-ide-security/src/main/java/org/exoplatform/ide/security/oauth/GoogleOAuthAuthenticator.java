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
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * OAuth authentication for google account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public class GoogleOAuthAuthenticator extends BaseOAuthAuthenticator
{
   private static final List<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/appengine.admin",
      "https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/userinfo.email");

   public GoogleOAuthAuthenticator() throws IOException
   {
      this(new MemoryCredentialStore(), loadClientSecrets("google_client_secrets.json"));
   }

   public GoogleOAuthAuthenticator(CredentialStore credentialStore) throws IOException
   {
      this(credentialStore, loadClientSecrets("google_client_secrets.json"));
   }

   protected GoogleOAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets)
   {
      super(
         new GoogleAuthorizationCodeFlow.Builder(
            new NetHttpTransport(),
            new JacksonFactory(),
            clientSecrets, SCOPE)
            .setCredentialStore(credentialStore).build(),
         new HashSet<String>(clientSecrets.getDetails().getRedirectUris()));
   }

   @Override
   public User getUser(String accessToken) throws OAuthAuthenticationException
   {
      return getUser("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken, GoogleUser.class);
   }

   @Override
   public final String getOAuthProvider()
   {
      return "google";
   }
}
