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
import org.exoplatform.ide.git.server.StatusPage;
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
import org.exoplatform.ide.git.shared.Log;
import org.exoplatform.ide.git.shared.LogRequest;
import org.exoplatform.ide.git.shared.MergeRequest;
import org.exoplatform.ide.git.shared.MergeResult;
import org.exoplatform.ide.git.shared.MoveRequest;
import org.exoplatform.ide.git.shared.PullRequest;
import org.exoplatform.ide.git.shared.PushRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.git.shared.RemoteListRequest;
import org.exoplatform.ide.git.shared.RemoteUpdateRequest;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.RmRequest;
import org.exoplatform.ide.git.shared.Status;
import org.exoplatform.ide.git.shared.StatusRequest;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagCreateRequest;
import org.exoplatform.ide.git.shared.TagDeleteRequest;
import org.exoplatform.ide.git.shared.TagListRequest;
import org.exoplatform.ide.vfs.server.GitUrlResolver;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.GitUrlResolveException;
import org.exoplatform.ide.vfs.server.exceptions.LocalPathResolveException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.security.ConversationState;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitService.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@Path("ide/git")
public class GitService
{

   @Inject
   private LocalPathResolver localPathResolver;

   @Inject
   private GitUrlResolver gitUrlResolver;

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @QueryParam("vfsid")
   private String vfsId;

   @QueryParam("projectid")
   private String projectId;

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
   public void add(AddRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void branchCheckout(BranchCheckoutRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public Branch branchCreate(BranchCreateRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void branchDelete(BranchDeleteRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public List<Branch> branchList(BranchListRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void clone(CloneRequest request) throws URISyntaxException, GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
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
   public Revision commit(CommitRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   @Produces(MediaType.TEXT_PLAIN)
   public StreamingOutput diff(DiffRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void fetch(FetchRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void init(InitRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
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
   @Produces(MediaType.TEXT_PLAIN)
   public StreamingOutput log(LogRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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

   @Path("log")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Log jsonLog(LogRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         return gitConnection.log(request);
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
   public MergeResult merge(MergeRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void mv(MoveRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void pull(PullRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void push(PushRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         gitConnection.push(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("remote-add")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void remoteAdd(RemoteAddRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         gitConnection.remoteAdd(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("remote-delete/{name}")
   @POST
   public void remoteDelete(@PathParam("name") String name) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         gitConnection.remoteDelete(name);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("remote-list")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public List<Remote> remoteList(RemoteListRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         return gitConnection.remoteList(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("remote-update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void remoteUpdate(RemoteUpdateRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         gitConnection.remoteUpdate(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("reset")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void reset(ResetRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void rm(RmRequest request) throws GitException, LocalPathResolveException, VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   @Produces(MediaType.TEXT_PLAIN)
   public StreamingOutput status(StatusRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         StatusPage statusPage = gitConnection.status(request);
         return new InfoPageWrapper(statusPage);
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
   public Status jsonStatus(StatusRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         return gitConnection.status(request);
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
   public Tag tagCreate(TagCreateRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public void tagDelete(TagDeleteRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
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
   public List<Tag> tagList(TagListRequest request) throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitConnection gitConnection = getGitConnection();
      try
      {
         return gitConnection.tagList(request);
      }
      finally
      {
         gitConnection.close();
      }
   }

   @Path("read-only-url")
   @GET
   public String readOnlyGitUrl(@Context UriInfo uriInfo) throws VirtualFileSystemException 
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      return gitUrlResolver.resolve(uriInfo, vfs, projectId);
   }

   protected GitConnection getGitConnection() throws GitException, LocalPathResolveException,
      VirtualFileSystemException
   {
      GitUser gituser = null;
      ConversationState user = ConversationState.getCurrent();
      if (user != null)
         gituser = new GitUser(user.getIdentity().getUserId());
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException(
            "Can't resolve path on the Local File System : Virtual file system not initialized");
      return GitConnectionFactory.getInstance().getConnection(localPathResolver.resolve(vfs, projectId), gituser);
   }
}
