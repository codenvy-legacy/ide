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

import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.InfoPage;
import org.exoplatform.ide.git.shared.AddRequest;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.BranchCreateRequest;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.CloneRequest;
import org.exoplatform.ide.git.shared.CommitRequest;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.FetchRequest;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;
import org.exoplatform.ide.git.shared.LogRequest;
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;
import org.exoplatform.ide.git.shared.MoveRequest;
import org.exoplatform.ide.git.shared.PullRequest;
import org.exoplatform.ide.git.shared.PushRequest;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.RmRequest;
import org.exoplatform.ide.git.shared.StatusRequest;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagCreateRequest;
import org.exoplatform.ide.git.shared.TagDeleteRequest;
import org.exoplatform.ide.git.shared.TagListRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitService.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@Path("ide/git")
public class GitService
{
   private static class InfoPageWrapper implements StreamingOutput
   {
      private final InfoPage infoPage;

      public InfoPageWrapper(InfoPage infoPage)
      {
         this.infoPage = infoPage;
      }

      /**
       * @see javax.ws.rs.core.StreamingOutput#write(java.io.OutputStream)
       */
      @Override
      public void write(OutputStream output) throws IOException, WebApplicationException
      {
         infoPage.writeTo(output);
      }
   }

   @Path("add")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void add(@QueryParam("workdir") String workDir, AddRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.add(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("branch-checkout")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void branchCheckout(@QueryParam("workdir") String workDir, BranchCheckoutRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.branchCheckout(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("branch-create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Branch branchCreate(@QueryParam("workdir") String workDir, BranchCreateRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         return gitConnection.branchCreate(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("branch-delete")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void branchDelete(@QueryParam("workdir") String workDir, BranchDeleteRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.branchDelete(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("branch-list")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public List<Branch> branchList(@QueryParam("workdir") String workDir, BranchListRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         return gitConnection.branchList(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("clone")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void clone(@QueryParam("workdir") String workDir, @Context SecurityContext sctx, CloneRequest request)
      throws URISyntaxException, GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         if (request.getUser() == null)
         {
            Principal principal = sctx.getUserPrincipal();
            if (principal != null)
               request.setUser(new GitUser(principal.getName()));
         }

         gitConnection.clone(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("commit")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Revision commit(@QueryParam("workdir") String workDir, CommitRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         return gitConnection.commit(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("diff")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public StreamingOutput diff(@QueryParam("workdir") String workDir, DiffRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         InfoPage diffPage = gitConnection.diff(request);
         return new InfoPageWrapper(diffPage);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("fetch")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void fetch(@QueryParam("workdir") String workDir, FetchRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.fetch(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("init")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void init(@QueryParam("workdir") String workDir, @Context SecurityContext sctx, InitRequest request)
      throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         if (request.getUser() == null)
         {
            Principal principal = sctx.getUserPrincipal();
            if (principal != null)
               request.setUser(new GitUser(principal.getName()));
         }

         gitConnection.init(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("log")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public StreamingOutput log(@QueryParam("workdir") String workDir, LogRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         InfoPage logPage = gitConnection.log(request);
         return new InfoPageWrapper(logPage);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("merge")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public MergeResult merge(@QueryParam("workdir") String workDir, MergeRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         return gitConnection.merge(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("mv")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void mv(@QueryParam("workdir") String workDir, MoveRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.mv(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("pull")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void pull(@QueryParam("workdir") String workDir, PullRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.pull(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("push")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void push(@QueryParam("workdir") String workDir, PushRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.push(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("reset")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void reset(@QueryParam("workdir") String workDir, ResetRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.reset(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("rm")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void rm(@QueryParam("workdir") String workDir, RmRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.rm(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("status")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public StreamingOutput status(@QueryParam("workdir") String workDir, StatusRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         InfoPage statusPage = gitConnection.status(request);
         return new InfoPageWrapper(statusPage);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("tag-create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Tag tagCreate(@QueryParam("workdir") String workDir, TagCreateRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         return gitConnection.tagCreate(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("tag-delete")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void tagDelete(@QueryParam("workdir") String workDir, TagDeleteRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         gitConnection.tagDelete(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("tag-list")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public List<Tag> tagList(@QueryParam("workdir") String workDir, TagListRequest request) throws GitException
   {
      GitConnection gitConnection = getGitConnection(workDir);
      try
      {
         return gitConnection.tagList(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   protected GitConnection getGitConnection(String workDir) throws GitException
   {
      return GitConnectionFactory.getIntance().getConnection(workDir);
   }
}
