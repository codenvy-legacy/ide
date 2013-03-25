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
package org.exoplatform.ide.git.server.github;

import com.codenvy.organization.InvitationService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.ContainerUtils;
import org.exoplatform.ide.commons.JsonHelper;
import org.exoplatform.ide.commons.JsonNameConventions;
import org.exoplatform.ide.commons.JsonParseException;
import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.security.oauth.OAuthTokenProvider;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Github.java Sep 5, 2011 12:08:04 PM vereshchaka $
 */
public class GitHub
{
   /** Predefined name of GitHub user. Use it to make possible for users to clone repositories with samples. */
   private final String myGitHubUser;

   private final SshKeyStore sshKeyStore;
   private final OAuthTokenProvider oauthTokenProvider;
   private final InvitationService invitationService;

   public GitHub(InitParams initParams,
                 OAuthTokenProvider oauthTokenProvider,
                 InvitationService invitationService,
                 SshKeyStore sshKeyStore)
   {
      this(ContainerUtils.readValueParam(initParams, "github-user"), oauthTokenProvider, invitationService, sshKeyStore);
   }

   public GitHub(String myGitHubUser,
                 OAuthTokenProvider oauthTokenProvider,
                 InvitationService invitationService,
                 SshKeyStore sshKeyStore)
   {
      this.myGitHubUser = myGitHubUser;
      this.oauthTokenProvider = oauthTokenProvider;
      this.sshKeyStore = sshKeyStore;
      this.invitationService = invitationService;
   }

   /**
    * Get the list of public repositories by user's name.
    *
    * @param user
    *    name of user
    * @return an array of repositories
    * @throws IOException
    *    if any i/o errors occurs
    * @throws GitHubException
    *    if GitHub server return unexpected or error status for request
    * @throws org.exoplatform.ide.commons.ParsingResponseException
    *    if any error occurs when parse response body
    */
   public GitHubRepository[] listRepositories(String user) throws IOException, GitHubException,
      ParsingResponseException
   {
      user = (user == null || user.isEmpty()) ? myGitHubUser : user;
      if (user == null)
      {
         throw new IllegalArgumentException("User's name must not be null.");
      }
      final String url = "https://api.github.com/users/" + user + "/repos";
      final String method = "GET";
      String response = doJsonRequest(url, method, 200);
      return parseJsonResponse(response, GitHubRepository[].class, null);
   }

   public Collaborators getCollaborators(String user, String repository) throws IOException, ParsingResponseException,
      GitHubException
   {
      final String url = "https://api.github.com/repos/" + user + '/' + repository + "/collaborators";
      final String method = "GET";
      String response = doJsonRequest(url, method, 200);
      // It seems that collaborators response does not contains all required fields.
      // Iterate over list and request more info about each user.
      final GitHubUserImpl[] collaborators = parseJsonResponse(response, GitHubUserImpl[].class, null);
      final String userId = getUserId();
      final Collaborators myCollaborators = new CollaboratorsImpl();
      for (GitHubUserImpl collaborator : collaborators)
      {
         response = doJsonRequest(collaborator.getUrl(), method, 200);
         GitHubUserImpl gitHubUser = parseJsonResponse(response, GitHubUserImpl.class, null);
         String email = gitHubUser.getEmail();
         if (!(email == null || email.isEmpty() || email.equals(userId) || isAlreadyInvited(email)))
         {
            myCollaborators.getCollaborators().add(gitHubUser);
         }
      }
      return myCollaborators;
   }

   private boolean isAlreadyInvited(String collaborator) throws GitHubException
   {
      /*try
      {
         String currentId = getUserId();
         for (Invite invite : inviteService.getInvites(false))
         {
            if (invite.getFrom() != null && invite.getFrom().equals(currentId) && invite.getEmail().equals(collaborator))
            {
               return true;
            }
         }
         return false;
      }
      catch (InviteException e)
      {
         throw new GitHubException(500, e.getMessage(), "text/plain");
      }*/
      // TODO : temporary, just to be able compile. Re-work it after update invitation mechanism.
      return false;
   }

   /**
    * Get the array of the extended repositories of the authorized user.
    *
    * @return array of the repositories
    * @throws IOException
    *    if any i/o errors occurs
    * @throws GitHubException
    *    if GitHub server return unexpected or error status for request
    * @throws org.exoplatform.ide.commons.ParsingResponseException
    *    if any error occurs when parse response body
    */
   public GitHubRepository[] listRepositories() throws IOException, GitHubException, ParsingResponseException
   {
      String oauthToken = oauthTokenProvider.getToken("github", getUserId());
      if (oauthToken == null || oauthToken.isEmpty())
      {
         throw new GitHubException(401, "Authentication required.\n", "text/plain");
      }
      final String url = "https://api.github.com/user/repos?access_token=" + oauthToken;
      final String method = "GET";
      final String response = doJsonRequest(url, method, 200);
      return parseJsonResponse(response, GitHubRepository[].class, null);
   }

   public void generateGitHubSshKey() throws IOException, SshKeyStoreException, GitHubException, ParsingResponseException
   {
      String oauthToken = oauthTokenProvider.getToken("github", getUserId());
      if (oauthToken == null || oauthToken.isEmpty())
      {
         throw new GitHubException(401, "Authentication required.\n", "text/plain");
      }
      final String url = "https://api.github.com/user/keys?access_token=" + oauthToken;

      sshKeyStore.removeKeys("github.com");
      sshKeyStore.genKeyPair("github.com", null, null);
      SshKey sshKey = sshKeyStore.getPublicKey("github.com");

      String keyContent = new String(sshKey.getBytes());

      Map<String, String> params = new HashMap<String, String>(2);
      params.put("title", keyContent.split("\\s")[2]);
      params.put("key", keyContent);

      String jsonRequest = JsonHelper.toJson(params);

      doJsonRequest(url, "POST", 200, jsonRequest);
   }

   /**
    * Do json request (without authorization!)
    *
    * @param url
    *    the request url
    * @param method
    *    the request method
    * @param success
    *    expected success code of request
    * @return response
    * @throws IOException
    * @throws GitHubException
    */
   private String doJsonRequest(String url, String method, int success) throws IOException, GitHubException
   {
      return doJsonRequest(url, method, success, null);
   }

   /**
    * Do json request (without authorization!)
    *
    * @param url
    *    the request url
    * @param method
    *    the request method
    * @param success
    *    expected success code of request
    * @param postData
    *    post data represented by json string
    * @return response
    * @throws IOException
    * @throws GitHubException
    */
   private String doJsonRequest(String url, String method, int success, String postData) throws IOException, GitHubException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(url).openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod(method);
         http.setRequestProperty("Accept", "application/json");
         if (postData != null && !postData.isEmpty())
         {
            http.setRequestProperty("Content-Type", "application/json");
            http.setDoOutput(true);

            BufferedWriter writer = null;
            try
            {
               writer = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
               writer.write(postData);
            }
            finally
            {
               if (writer != null)
               {
                  writer.close();
               }
            }
         }

         if (http.getResponseCode() != success)
         {
            throw fault(http);
         }

         InputStream input = http.getInputStream();
         String result;
         try
         {
            result = readBody(input, http.getContentLength());
         }
         finally
         {
            input.close();
         }
         return result;
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
   }

   private <O> O parseJsonResponse(String json, Class<O> clazz, Type type) throws ParsingResponseException
   {
      try
      {
         return JsonHelper.fromJson(json, clazz, type, JsonNameConventions.CAMEL_UNDERSCORE);
      }
      catch (JsonParseException e)
      {
         throw new ParsingResponseException(e.getMessage(), e);
      }
   }

   private GitHubException fault(HttpURLConnection http) throws IOException
   {
      InputStream errorStream = null;
      try
      {
         int responseCode = http.getResponseCode();
         errorStream = http.getErrorStream();
         if (errorStream == null)
         {
            return new GitHubException(responseCode, null, null);
         }

         int length = http.getContentLength();
         String body = readBody(errorStream, length);

         if (body != null)
         {
            return new GitHubException(responseCode, body, http.getContentType());
         }

         return new GitHubException(responseCode, null, null);
      }
      finally
      {
         if (errorStream != null)
         {
            errorStream.close();
         }
      }
   }

   private static String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         int off = 0;
         int i;
         while ((i = input.read(b, off, contentLength - off)) > 0)
         {
            off += i;
         }
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int point;
         while ((point = input.read(buf)) != -1)
         {
            bout.write(buf, 0, point);
         }
         body = bout.toString();
      }
      return body;
   }

   private String getUserId()
   {
      return ConversationState.getCurrent().getIdentity().getUserId();
   }
}
