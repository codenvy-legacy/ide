/**
 * Copyright (C) 2010 eXo Platform SAS.
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
 *
 */

package org.exoplatform.ide.vfs.webdav;

import java.io.InputStream;
import java.util.List;

import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Session;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.webdav.command.PropFindCommand;
import org.exoplatform.ide.vfs.webdav.command.PutCommand;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.webdav.Depth;
import org.exoplatform.services.jcr.webdav.PreconditionException;
import org.exoplatform.services.jcr.webdav.util.NodeTypeUtil;
import org.exoplatform.services.jcr.webdav.util.TextUtil;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ExtHttpHeaders;
import org.exoplatform.services.rest.ext.webdav.method.CHECKIN;
import org.exoplatform.services.rest.ext.webdav.method.CHECKOUT;
import org.exoplatform.services.rest.ext.webdav.method.COPY;
import org.exoplatform.services.rest.ext.webdav.method.LOCK;
import org.exoplatform.services.rest.ext.webdav.method.MKCOL;
import org.exoplatform.services.rest.ext.webdav.method.MOVE;
import org.exoplatform.services.rest.ext.webdav.method.OPTIONS;
import org.exoplatform.services.rest.ext.webdav.method.ORDERPATCH;
import org.exoplatform.services.rest.ext.webdav.method.PROPFIND;
import org.exoplatform.services.rest.ext.webdav.method.PROPPATCH;
import org.exoplatform.services.rest.ext.webdav.method.REPORT;
import org.exoplatform.services.rest.ext.webdav.method.SEARCH;
import org.exoplatform.services.rest.ext.webdav.method.UNCHECKOUT;
import org.exoplatform.services.rest.ext.webdav.method.UNLOCK;
import org.exoplatform.services.rest.ext.webdav.method.VERSIONCONTROL;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide-vfs-webdav")
public class WebDavVirtualFileSystemService extends WebDavServiceImpl
{

   /**
    * Logger.
    */
   private static Log log = ExoLogger.getLogger(WebDavVirtualFileSystemService.class);
   
   public WebDavVirtualFileSystemService(InitParams params, RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService) throws Exception
   {
      super(params, repositoryService, sessionProviderService);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @PROPFIND
   @Path("/{repoName}/{repoPath:.*}/")
   public Response propfind(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader, @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      if (log.isDebugEnabled())
      {
         log.debug("PROPFIND " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Session session = session(repoName, workspaceName(repoPath), null);
         String uri =
            uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
               .toString();
         Depth depth = new Depth(depthHeader);
         return new PropFindCommand().propfind(session, path(repoPath), body, depth.getIntValue(), uri);
      }
      catch (NoSuchWorkspaceException exc)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
      }
      catch (PreconditionException exc)
      {
         return Response.status(HTTPStatus.BAD_REQUEST).entity(exc.getMessage()).build();
      }
      catch (Exception exc)
      {
         log.error(exc.getMessage(), exc);
         return Response.serverError().entity(exc.getMessage()).build();
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   @MKCOL
   @Path("/{repoName}/{repoPath:.*}/")
   public Response mkcol(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.CONTENT_NODETYPE) String nodeTypeHeader,
      @HeaderParam(ExtHttpHeaders.CONTENT_MIXINTYPES) String mixinTypesHeader)
   {
      return super.mkcol(repoName, repoPath, lockTokenHeader, ifHeader, nodeTypeHeader, mixinTypesHeader);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @CHECKIN
   @Path("/{repoName}/{repoPath:.*}/")
   public Response checkin(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {
      return super.checkin(repoName, repoPath, lockTokenHeader, ifHeader);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @CHECKOUT
   @Path("/{repoName}/{repoPath:.*}/")
   public Response checkout(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {
      return super.checkout(repoName, repoPath, lockTokenHeader, ifHeader);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @COPY
   @Path("/{repoName}/{repoPath:.*}/")
   public Response copy(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.DESTINATION) String destinationHeader,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
      @HeaderParam(ExtHttpHeaders.OVERWRITE) String overwriteHeader, @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      return super.copy(repoName, repoPath, destinationHeader, lockTokenHeader, ifHeader, depthHeader, overwriteHeader,
         uriInfo, body);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @DELETE
   @Path("/{repoName}/{repoPath:.*}/")
   public Response delete(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {
      return super.delete(repoName, repoPath, lockTokenHeader, ifHeader);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @GET
   @Path("/{repoName}/{repoPath:.*}/")
   public Response get(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.RANGE) String rangeHeader,
      @HeaderParam(ExtHttpHeaders.IF_MODIFIED_SINCE) String ifModifiedSince, @QueryParam("version") String version,
      @Context UriInfo uriInfo)
   {
      return super.get(repoName, repoPath, rangeHeader, ifModifiedSince, version, uriInfo);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @HEAD
   @Path("/{repoName}/{repoPath:.*}/")
   public Response head(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @Context UriInfo uriInfo)
   {
      return super.head(repoName, repoPath, uriInfo);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @LOCK
   @Path("/{repoName}/{repoPath:.*}/")
   public Response lock(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader, HierarchicalProperty body)
   {
      return super.lock(repoName, repoPath, lockTokenHeader, ifHeader, depthHeader, body);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @UNLOCK
   @Path("/{repoName}/{repoPath:.*}/")
   public Response unlock(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {
      return super.unlock(repoName, repoPath, lockTokenHeader, ifHeader);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @MOVE
   @Path("/{repoName}/{repoPath:.*}/")
   public Response move(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.DESTINATION) String destinationHeader,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
      @HeaderParam(ExtHttpHeaders.OVERWRITE) String overwriteHeader, @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      return super.move(repoName, repoPath, destinationHeader, lockTokenHeader, ifHeader, depthHeader, overwriteHeader,
         uriInfo, body);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @OPTIONS
   @Path("/{repoName}/{path:.*}/")
   public Response options(@PathParam("path") String path)
   {
      return super.options(path);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @ORDERPATCH
   @Path("/{repoName}/{repoPath:.*}/")
   public Response order(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      return super.order(repoName, repoPath, lockTokenHeader, ifHeader, uriInfo, body);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @PROPPATCH
   @Path("/{repoName}/{repoPath:.*}/")
   public Response proppatch(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      return super.proppatch(repoName, repoPath, lockTokenHeader, ifHeader, uriInfo, body);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @PUT
   @Path("/{repoName}/{repoPath:.*}/")
   public Response put(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.FILE_NODETYPE) String fileNodeTypeHeader,
      @HeaderParam(ExtHttpHeaders.CONTENT_NODETYPE) String contentNodeTypeHeader,
      @HeaderParam(ExtHttpHeaders.CONTENT_MIXINTYPES) String mixinTypes,
      @HeaderParam(ExtHttpHeaders.CONTENT_TYPE) MediaType mediatype, InputStream inputStream)
   {
      if (log.isDebugEnabled())
      {
         log.debug("PUT " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         String mimeType = null;
         String encoding = null;

         if (mediatype == null)
         {
            MimeTypeResolver mimeTypeResolver = new MimeTypeResolver();
            mimeTypeResolver.setDefaultMimeType(defaultFileMimeType);
            mimeType = mimeTypeResolver.getMimeType(TextUtil.nameOnly(repoPath));
         }
         else
         {
            mimeType = mediatype.getType() + "/" + mediatype.getSubtype();
            encoding = mediatype.getParameters().get("charset");
         }

         List<String> tokens = lockTokens(lockTokenHeader, ifHeader);
         Session session = session(repoName, workspaceName(repoPath), tokens);

         String fileNodeType = NodeTypeUtil.getFileNodeType(fileNodeTypeHeader);
         if (fileNodeType == null)
         {
            fileNodeType = defaultFileNodeType;
         }

         String contentNodeType = NodeTypeUtil.getContentNodeType(contentNodeTypeHeader);
         NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
         NodeType nodeType = ntm.getNodeType(contentNodeType);
         NodeTypeUtil.checkContentResourceType(nodeType);

         return new PutCommand(nullResourceLocks).put(session, path(repoPath), inputStream, fileNodeType,
            contentNodeType, NodeTypeUtil.getMixinTypes(mixinTypes), mimeType, encoding, updatePolicyType,
            autoVersionType, tokens);

      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();

      }
      catch (NoSuchNodeTypeException exc)
      {
         log.error("NoSuchNodeTypeException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.BAD_REQUEST).entity(exc.getMessage()).build();
      }
      catch (Exception exc)
      {
         log.error(exc.getMessage(), exc);
         return Response.serverError().entity(exc.getMessage()).build();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @REPORT
   @Path("/{repoName}/{repoPath:.*}/")
   public Response report(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader, @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      return super.report(repoName, repoPath, depthHeader, uriInfo, body);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @SEARCH
   @Path("/{repoName}/{repoPath:.*}/")
   public Response search(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      return super.search(repoName, repoPath, uriInfo, body);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @UNCHECKOUT
   @Path("/{repoName}/{repoPath:.*}/")
   public Response uncheckout(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {
      return super.uncheckout(repoName, repoPath, lockTokenHeader, ifHeader);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @VERSIONCONTROL
   @Path("/{repoName}/{repoPath:.*}/")
   public Response versionControl(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {
      return super.versionControl(repoName, repoPath, lockTokenHeader, ifHeader);
   }
   
   
   
}
