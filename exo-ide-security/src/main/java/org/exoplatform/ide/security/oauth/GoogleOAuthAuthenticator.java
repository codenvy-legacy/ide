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
import com.google.api.client.http.HttpParser;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Authentication oauth service for google account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public final class GoogleOAuthAuthenticator extends BaseOAuthAuthenticator
{

   private static final List<String> SCOPE = Collections.singletonList("https://www.googleapis.com/auth/appengine.admin");

   public GoogleOAuthAuthenticator() throws IOException
   {
      this(new MemoryCredentialStore(), loadClientSecrets("client_secrets.json"));
   }

   protected GoogleOAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets)
   {
      this.flow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(), clientSecrets,
         SCOPE).setCredentialStore(credentialStore).build();
      List<String> redirectUris = clientSecrets.getDetails().getRedirectUris();
      if (redirectUris == null || redirectUris.isEmpty())
      {
         throw new RuntimeException("Redirect URI not found. ");
      }
      this.redirectUri = redirectUris.get(0);
   }

   @Override
   protected HttpParser getParser()
   {
      return new JsonHttpParser(flow.getJsonFactory());
   }

   @Override
   public User getUser(String accessToken) throws OAuthAuthenticationException
   {
      return new User(null);
   }

   @Override
   public final String getOAuthProvider()
   {
      return "google";
   }
}
