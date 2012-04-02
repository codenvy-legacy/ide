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
package org.exoplatform.ide.extension.samples.server.rest;

import org.exoplatform.ide.extension.samples.server.Github;
import org.exoplatform.ide.extension.samples.server.GithubException;
import org.exoplatform.ide.extension.samples.shared.GitHubCredentials;
import org.exoplatform.ide.extension.samples.shared.Repository;
import org.exoplatform.ide.extension.samples.shared.RepositoryExt;
import org.exoplatform.ide.helper.ParsingResponseException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
public class GithubService
{
   @Inject
   Github github;

   public GithubService()
   {
   }

   protected GithubService(Github github)
   {
      this.github = github;
   }

   @Path("list/user")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Repository[] listRepositoriesByUser(@QueryParam("username") String userName) throws IOException,
      GithubException, ParsingResponseException, InvalidArgumentException
   {
      return github.listRepositories(userName);
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws IOException, GithubException, ParsingResponseException,
      VirtualFileSystemException
   {
      github.login(new GitHubCredentials(credentials.get("login"), credentials.get("password")));
   }

   @Path("list")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public RepositoryExt[] listRepositories() throws IOException, GithubException, ParsingResponseException,
      VirtualFileSystemException
   {
      return github.listRepositories();
   }
}
