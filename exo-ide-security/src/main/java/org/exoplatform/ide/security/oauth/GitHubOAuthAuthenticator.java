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
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import org.exoplatform.ide.security.shared.User;

import java.util.Collections;
import java.util.HashSet;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * OAuth authentication  for github account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public class GitHubOAuthAuthenticator extends BaseOAuthAuthenticator
{
   protected GitHubOAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets)
   {
      super(
         new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
            new NetHttpTransport(),
            new JacksonFactory(),
            new GenericUrl(clientSecrets.getDetails().getTokenUri()),
            new ClientParametersAuthentication(
               clientSecrets.getDetails().getClientId(),
               clientSecrets.getDetails().getClientSecret()),
            clientSecrets.getDetails().getClientId(),
            clientSecrets.getDetails().getAuthUri())
            .setScopes(Collections.<String>emptyList())
            .setCredentialStore(credentialStore).build(),
         new HashSet<String>(clientSecrets.getDetails().getRedirectUris()));
   }

   @Override
   public User getUser(String accessToken) throws OAuthAuthenticationException
   {
      GitHubUser user = getJson("https://api.github.com/user?access_token=" + accessToken, GitHubUser.class);
      final String email = user.getEmail();
      if (email == null || email.isEmpty())
      {
         throw new OAuthAuthenticationException("Cloud-IDE tries to use your GitHub public e-mail as a user identifier but it is not set. "  +
            "Please fill the e-mail in your public GitHub profile and return back here.");
      }
      try
      {
         new InternetAddress(email).validate();
      }
      catch (AddressException e)
      {
         throw new OAuthAuthenticationException(e.getMessage());
      }
      return user;
   }

   @Override
   public final String getOAuthProvider()
   {
      return "github";
   }
}
