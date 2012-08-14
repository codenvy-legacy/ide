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

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpParser;
import com.google.api.client.http.UrlEncodedParser;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Authentication oauth service for github account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public final class GitHubOAuthAuthenticator extends BaseOAuthAuthenticator
{
   private static final List<String> SCOPE = Collections.singletonList("user,repo,delete_repo");

//   private final String GITHUB_ACCESS_TOKEN_URI = "https://github.com/login/oauth/access_token";
//   private final String GITHUB_AUTHORIZE_SERVICE = "https://github.com/login/oauth/authorize";

   public GitHubOAuthAuthenticator() throws IOException
   {
      this(new MemoryCredentialStore(), loadClientSecrets("client_github_secrets.json"));
   }

   protected GitHubOAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets)
   {
      this.flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
         new ApacheHttpTransport(),
         new JacksonFactory(),
         new GenericUrl("https://github.com/login/oauth/access_token"),
         new ClientParametersAuthentication(clientSecrets.getDetails().getClientId(),
            clientSecrets.getDetails().getClientSecret()),
         clientSecrets.getDetails().getClientId(),
         "https://github.com/login/oauth/authorize").setScopes(SCOPE).setCredentialStore(credentialStore).build();

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
      return new UrlEncodedParser();
   }

   @Override
   public User getUser(String accessToken) throws OAuthAuthenticationException
   {
      HttpURLConnection urlConnection= null;

      try
      {
         URL url = new URL("https://api.github.com/user?access_token=" + accessToken);
         urlConnection = (HttpURLConnection)url.openConnection();
         JsonParser parser = new JsonParser();
         parser.parse(urlConnection.getInputStream());
         JsonValue jsonValue = parser.getJsonObject();
         return ObjectBuilder.createObject(GitHubUser.class, jsonValue);
      }
      catch (JsonException e)
      {
         throw new OAuthAuthenticationException(e.getMessage(), e);
      }
      catch (MalformedURLException e)
      {
         throw new OAuthAuthenticationException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new OAuthAuthenticationException(e.getMessage(), e);
      }
      finally
      {
         if (urlConnection != null)
         {
            urlConnection.disconnect();
         }
      }
   }

   @Override
   public final String getOAuthProvider()
   {
      return "github";
   }
}
