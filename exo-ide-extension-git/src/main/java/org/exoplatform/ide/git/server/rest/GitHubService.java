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
package org.exoplatform.ide.git.server.rest;

import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.git.server.github.GitHub;
import org.exoplatform.ide.git.server.github.GitHubException;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubCredentials;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.security.oauth.OAuthTokenProvider;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST service to get the list of repositories from GitHub (where sample projects are located).
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesService.java Aug 29, 2011 9:59:02 AM vereshchaka $
 */
@Path("ide/github")
public class GitHubService
{
   @Inject
   OAuthTokenProvider oauthTokenProvider;

   @Inject
   GitHub github;

   public GitHubService()
   {
   }

   protected GitHubService(GitHub github)
   {
      this.github = github;
   }

   @Path("list/user")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public GitHubRepository[] listRepositoriesByUser(
      @QueryParam("username") String userName) throws IOException, GitHubException, ParsingResponseException,
      InvalidArgumentException
   {
      return github.listRepositories(userName);
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(
      Map<String, String> credentials) throws IOException, GitHubException, ParsingResponseException,
      VirtualFileSystemException
   {
      github.login(new GitHubCredentials(credentials.get("login"), credentials.get("password")));
   }

   @Path("list")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public GitHubRepository[] listRepositories() throws IOException, GitHubException, ParsingResponseException,
      VirtualFileSystemException
   {
      return github.listRepositories();
   }

   @GET
   @Path("collaborators/{user}/{repository}")
   @Produces(MediaType.APPLICATION_JSON)
   public Collaborators collaborators(@PathParam("user") String user,
                                      @PathParam("repository") String repository) throws IOException,
      GitHubException, ParsingResponseException, VirtualFileSystemException
   {
      return github.getCollaborators(user, repository);
   }

   @GET
   @Path("token/{userid}")
   @Produces(MediaType.TEXT_PLAIN)
   public String getToken(
      @PathParam("userid") String userId) throws IOException, GitHubException, ParsingResponseException,
      VirtualFileSystemException
   {
      final String token = oauthTokenProvider.getToken("github", userId);

      if (token == null || token.isEmpty())
      {
         return null;
      }

      //need to check if token which stored is valid for requests, then if valid - we send it to user
      String tokenVerifyUrl = "https://api.github.com/?access_token=" + token;
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(tokenVerifyUrl).openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod("GET");
         http.setRequestProperty("Accept", "application/json");

         if (http.getResponseCode() == 401)
         {
            return null;
         }
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }

      return token;
   }

   @POST
   @Path("ssh/generate")
   public void updateSSHKey() throws VirtualFileSystemException, IOException, GitHubException, ParsingResponseException
   {
      github.generateGitHubSshKey();
   }
}
