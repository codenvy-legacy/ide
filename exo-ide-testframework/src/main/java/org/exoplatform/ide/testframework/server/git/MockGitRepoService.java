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
package org.exoplatform.ide.testframework.server.git;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("ide/git-repo")
public class MockGitRepoService
{

   private static MockGitRepoService instance;

   public static MockGitRepoService getInstance()
   {
      return instance;
   }

   public MockGitRepoService()
   {
      instance = this;
   }

   private List<String> gitDirectories = new ArrayList<String>();

   @GET
   @Path("workdir")
   @Produces(MediaType.TEXT_PLAIN)
   public String getWorkDir(@Context UriInfo uriInfo, @HeaderParam("location") String location) throws Exception
   {
      return location + ".git";
   }

   /**
    * @param dir
    */
   public void addGitDirectory(String dir)
   {
      gitDirectories.add(dir);
   }

   @POST
   @Path("reset")
   public void resetMockGitService()
   {
      gitDirectories = new ArrayList<String>();
   }

}
