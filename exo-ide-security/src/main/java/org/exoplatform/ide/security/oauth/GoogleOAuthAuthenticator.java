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
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Authentication oauth service for google account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public class GoogleOAuthAuthenticator extends BaseOAuthAuthenticator
{
   private static final List<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/appengine.admin",
      "https://www.googleapis.com/auth/userinfo.profile");

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
   protected HttpParser getParser()
   {
      return new JsonHttpParser(flow.getJsonFactory());
   }

   @Override
   public User getUser(String accessToken) throws OAuthAuthenticationException
   {
      HttpURLConnection urlConnection = null;
      InputStream urlInputStream = null;

      try
      {
         URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
         urlConnection = (HttpURLConnection)url.openConnection();
         urlInputStream = urlConnection.getInputStream();

         JsonParser parser = new JsonParser();
         parser.parse(urlInputStream);
         JsonValue jsonValue = parser.getJsonObject();

         return ObjectBuilder.createObject(GoogleUser.class, jsonValue);
      }
      catch (JsonException e)
      {
         throw new OAuthAuthenticationException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new OAuthAuthenticationException(e.getMessage(), e);
      }
      finally
      {
         if (urlInputStream != null)
         {
            try
            {
               urlInputStream.close();
            }
            catch (IOException ignored)
            {
            }
         }

         if (urlConnection != null)
         {
            urlConnection.disconnect();
         }
      }

   }

   @Override
   public final String getOAuthProvider()
   {
      return "google";
   }
}
