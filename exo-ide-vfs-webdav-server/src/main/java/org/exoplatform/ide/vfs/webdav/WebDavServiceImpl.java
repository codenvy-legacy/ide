/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.webdav;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
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
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.webdav.Depth;
import org.exoplatform.services.jcr.webdav.PreconditionException;
import org.exoplatform.services.jcr.webdav.Range;
import org.exoplatform.services.jcr.webdav.WebDavService;
import org.exoplatform.services.jcr.webdav.command.CopyCommand;
import org.exoplatform.services.jcr.webdav.command.DeleteCommand;
import org.exoplatform.services.jcr.webdav.command.GetCommand;
import org.exoplatform.services.jcr.webdav.command.HeadCommand;
import org.exoplatform.services.jcr.webdav.command.LockCommand;
import org.exoplatform.services.jcr.webdav.command.MkColCommand;
import org.exoplatform.services.jcr.webdav.command.MoveCommand;
import org.exoplatform.services.jcr.webdav.command.OrderPatchCommand;
import org.exoplatform.services.jcr.webdav.command.PropFindCommand;
import org.exoplatform.services.jcr.webdav.command.PropPatchCommand;
import org.exoplatform.services.jcr.webdav.command.PutCommand;
import org.exoplatform.services.jcr.webdav.command.SearchCommand;
import org.exoplatform.services.jcr.webdav.command.UnLockCommand;
import org.exoplatform.services.jcr.webdav.command.deltav.CheckInCommand;
import org.exoplatform.services.jcr.webdav.command.deltav.CheckOutCommand;
import org.exoplatform.services.jcr.webdav.command.deltav.ReportCommand;
import org.exoplatform.services.jcr.webdav.command.deltav.UnCheckOutCommand;
import org.exoplatform.services.jcr.webdav.command.deltav.VersionControlCommand;
import org.exoplatform.services.jcr.webdav.lock.NullResourceLocksHolder;
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
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * Created by The eXo Platform SARL .<br/>
 * 
 * @author Gennady Azarenkov
 * @version $Id: WebDavServiceImpl.java 2286 2010-04-16 13:55:00Z dkatayev $
 */

@Path("/jcr")
public class WebDavServiceImpl implements WebDavService, ResourceContainer
{

   /**
    * Default folder initialization node type.
    */
   public static final String INIT_PARAM_DEF_FOLDER_NODE_TYPE = "def-folder-node-type";

   /**
    * Default file initialization node type.
    */
   public static final String INIT_PARAM_DEF_FILE_NODE_TYPE = "def-file-node-type";

   /**
    * Default file initialization mime type.
    */
   public static final String INIT_PARAM_DEF_FILE_MIME_TYPE = "def-file-mimetype";

   /**
    * Initialization initialization "update-policy"-parameter value.
    */
   public static final String INIT_PARAM_UPDATE_POLICY = "update-policy";

   /**
    * Initialization "auto-version"-parameter value.
    */
   public static final String INIT_PARAM_AUTO_VERSION = "auto-version";

   public static final String INIT_PARAM_CACHE_CONTROL = "cache-control";

   private HashMap<MediaType, String> cacheControlMap = new HashMap<MediaType, String>();

   /**
    * Logger.
    */
   private static Log log = ExoLogger.getLogger("exo.jcr.component.webdav.WebDavServiceImpl");

   /**
    * Local Thread SessionProvider.
    */
   private final ThreadLocalSessionProviderService sessionProviderService;

   /**
    * Repository service.
    */
   private final RepositoryService repositoryService;

   /**
    * NullResourceLocksHolder.
    */
   protected final NullResourceLocksHolder nullResourceLocks;

   /**
    * Default folder node type.
    */
   private String defaultFolderNodeType = "nt:folder";

   /**
    * Default file node type.
    */
   protected String defaultFileNodeType = "nt:file";

   /**
    * Default file mime type.
    */
   protected String defaultFileMimeType = "application/octet-stream";

   /**
    * Update policy.
    */
   protected String updatePolicyType = "create-version";

   /**
    * Auto-version default value.
    */
   protected String autoVersionType = "checkout-checkin";

   /**
    * The list of allowed methods.
    */
   private static final String ALLOW;

   static
   {
      StringBuffer sb = new StringBuffer();
      for (Method m : WebDavServiceImpl.class.getMethods())
      {
         for (Annotation a : m.getAnnotations())
         {
            javax.ws.rs.HttpMethod ma = null;
            if ((ma = a.annotationType().getAnnotation(javax.ws.rs.HttpMethod.class)) != null)
            {
               if (sb.length() > 0)
                  sb.append(", ");
               sb.append(ma.value());
            }
         }
      }

      ALLOW = sb.toString();

   }

   /**
    * Constructor.
    * 
    * @param params Initialization parameters
    * @param repositoryService repository service
    * @param sessionProviderService session provider service
    * @throws Exception {@link Exception}
    */
   public WebDavServiceImpl(InitParams params, RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService) throws Exception
   {
      this.sessionProviderService = sessionProviderService;
      this.repositoryService = repositoryService;
      this.nullResourceLocks = new NullResourceLocksHolder();

      ValueParam pDefFolderNodeType = params.getValueParam(INIT_PARAM_DEF_FOLDER_NODE_TYPE);
      if (pDefFolderNodeType != null)
      {
         defaultFolderNodeType = pDefFolderNodeType.getValue();
         log.info(INIT_PARAM_DEF_FOLDER_NODE_TYPE + " = " + defaultFolderNodeType);
      }

      ValueParam pDefFileNodeType = params.getValueParam(INIT_PARAM_DEF_FILE_NODE_TYPE);
      if (pDefFileNodeType != null)
      {
         defaultFileNodeType = pDefFileNodeType.getValue();
         log.info(INIT_PARAM_DEF_FILE_NODE_TYPE + " = " + defaultFileNodeType);
      }

      ValueParam pDefFileMimeType = params.getValueParam(INIT_PARAM_DEF_FILE_MIME_TYPE);
      if (pDefFileMimeType != null)
      {
         defaultFileMimeType = pDefFileMimeType.getValue();
         log.info(INIT_PARAM_DEF_FILE_MIME_TYPE + " = " + defaultFileMimeType);
      }

      ValueParam pUpdatePolicy = params.getValueParam(INIT_PARAM_UPDATE_POLICY);
      if (pUpdatePolicy != null)
      {
         updatePolicyType = pUpdatePolicy.getValue();
         log.info(INIT_PARAM_UPDATE_POLICY + " = " + updatePolicyType);
      }

      ValueParam pAutoVersion = params.getValueParam(INIT_PARAM_AUTO_VERSION);
      if (pAutoVersion != null)
      {
         autoVersionType = pAutoVersion.getValue();
         log.info(INIT_PARAM_AUTO_VERSION + " = " + autoVersionType);
      }

      ValueParam pCacheControl = params.getValueParam(INIT_PARAM_CACHE_CONTROL);
      if (pCacheControl != null)
      {
         String cacheControlConfigValue = pCacheControl.getValue();

         try
         {
            String[] elements = cacheControlConfigValue.split(";");
            for (String element : elements)
            {
               String cacheValue = element.split(":")[1];
               String keys = element.split(":")[0];
               for (String key : keys.split(","))
               {
                  MediaType mediaType = new MediaType(key.split("/")[0], key.split("/")[1]);
                  cacheControlMap.put(mediaType, cacheValue);
               }
            }
         }
         catch (Exception e)
         {
            log.warn("Invalid " + INIT_PARAM_CACHE_CONTROL + " parameter");
         }

      }

   }

   /**
    * {@inheritDoc}
    */
   @CHECKIN
   @Path("/{repoName}/{repoPath:.*}/")
   public Response checkin(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {

      if (log.isDebugEnabled())
      {
         log.debug("CHECKIN " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      Session session;
      try
      {
         session = session(repoName, workspaceName(repoPath), lockTokens(lockTokenHeader, ifHeader));
         return new CheckInCommand().checkIn(session, path(repoPath));
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
   @CHECKOUT
   @Path("/{repoName}/{repoPath:.*}/")
   public Response checkout(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {

      if (log.isDebugEnabled())
      {
         log.debug("CHECKOUT " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      Session session;
      try
      {
         session = session(repoName, workspaceName(repoPath), lockTokens(lockTokenHeader, ifHeader));
         return new CheckOutCommand().checkout(session, path(repoPath));
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
   @COPY
   @Path("/{repoName}/{repoPath:.*}/")
   public Response copy(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.DESTINATION) String destinationHeader,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
      @HeaderParam(ExtHttpHeaders.OVERWRITE) String overwriteHeader, @Context UriInfo uriInfo, HierarchicalProperty body)
   {

      if (log.isDebugEnabled())
      {
         log.debug("COPY " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         String serverURI = uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).build().toString();

         destinationHeader = TextUtil.unescape(destinationHeader, '%');

         if (!destinationHeader.startsWith(serverURI))
         {
            return Response.status(HTTPStatus.BAD_GATEWAY).entity("Bad Gateway").build();
         }

         String srcWorkspace = workspaceName(repoPath);
         String srcNodePath = path(repoPath);

         String destPath = destinationHeader.substring(serverURI.length() + 1);
         String destWorkspace = workspaceName(destPath);
         String destNodePath = path(destPath);

         List<String> lockTokens = lockTokens(lockTokenHeader, ifHeader);

         Depth depth = new Depth(depthHeader);

         boolean overwrite = overwriteHeader != null && overwriteHeader.equalsIgnoreCase("T");

         if (overwrite)
         {
            delete(repoName, destPath, lockTokenHeader, ifHeader);
         }
         else
         {
            Session session = session(repoName, workspaceName(repoPath), null);

            if (session.getRootNode().hasNode(TextUtil.relativizePath(repoPath)))
            {
               return Response.status(HTTPStatus.PRECON_FAILED).entity("Not Found").build();
            }

         }

         if (depth.getStringValue().equalsIgnoreCase("infinity"))
         {

            if (srcWorkspace.equals(destWorkspace))
            {
               Session session = session(repoName, destWorkspace, lockTokens);
               return new CopyCommand().copy(session, srcNodePath, destNodePath);
            }

            Session destSession = session(repoName, destWorkspace, lockTokens);
            return new CopyCommand().copy(destSession, srcWorkspace, srcNodePath, destNodePath);

         }
         else if (depth.getIntValue() == 0)
         {

            int nodeNameStart = srcNodePath.lastIndexOf('/') + 1;
            String nodeName = srcNodePath.substring(nodeNameStart);

            Session session = session(repoName, destWorkspace, lockTokens);

            return new MkColCommand(nullResourceLocks).mkCol(session, destNodePath + "/" + nodeName,
               defaultFolderNodeType, null, lockTokens);

         }
         else
         {
            return Response.status(HTTPStatus.BAD_REQUEST).entity("Bad Request").build();
         }

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
   @DELETE
   @Path("/{repoName}/{repoPath:.*}/")
   public Response delete(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {

      if (log.isDebugEnabled())
      {
         log.debug("DELETE " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Session session = session(repoName, workspaceName(repoPath), lockTokens(lockTokenHeader, ifHeader));
         if (lockTokenHeader != null)
         {
            lockTokenHeader = lockTokenHeader.substring(1, lockTokenHeader.length() - 1);
         }
         return new DeleteCommand().delete(session, path(repoPath), lockTokenHeader);
      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @GET
   @Path("/{repoName}/{repoPath:.*}/")
   public Response get(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.RANGE) String rangeHeader,
      @HeaderParam(ExtHttpHeaders.IF_MODIFIED_SINCE) String ifModifiedSince, @QueryParam("version") String version,
      @Context UriInfo uriInfo)
   {
      if (log.isDebugEnabled())
      {
         log.debug("GET " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Session session = session(repoName, workspaceName(repoPath), null);

         ArrayList<Range> ranges = new ArrayList<Range>();

         if (rangeHeader != null)
         {
            if (log.isDebugEnabled())
            {
               log.debug(rangeHeader);
            }

            if (rangeHeader.startsWith("bytes="))
            {
               String rangeString = rangeHeader.substring(rangeHeader.indexOf("=") + 1);

               String[] tokens = rangeString.split(",");
               for (String token : tokens)
               {
                  Range range = new Range();
                  token = token.trim();
                  int dash = token.indexOf("-");
                  if (dash == -1)
                  {
                     return Response.status(HTTPStatus.REQUESTED_RANGE_NOT_SATISFIABLE).entity(
                        "Requested Range Not Satisfiable").build();
                  }
                  else if (dash == 0)
                  {
                     range.setStart(Long.parseLong(token));
                     range.setEnd(-1L);
                  }
                  else if (dash > 0)
                  {
                     range.setStart(Long.parseLong(token.substring(0, dash)));
                     if (dash < token.length() - 1)
                        range.setEnd(Long.parseLong(token.substring(dash + 1, token.length())));
                     else
                        range.setEnd(-1L);
                  }
                  ranges.add(range);
               }
            }
         }
         String uri =
            uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
               .toString();
         return new GetCommand().get(session, path(repoPath), version, uri, ranges, ifModifiedSince, cacheControlMap);

      }
      catch (PathNotFoundException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @HEAD
   @Path("/{repoName}/{repoPath:.*}/")
   public Response head(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @Context UriInfo uriInfo)
   {

      if (log.isDebugEnabled())
      {
         log.debug("HEAD " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Session session = session(repoName, workspaceName(repoPath), null);
         String uri =
            uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
               .toString();
         return new HeadCommand().head(session, path(repoPath), uri);
      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @LOCK
   @Path("/{repoName}/{repoPath:.*}/")
   public Response lock(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader, HierarchicalProperty body)
   {

      if (log.isDebugEnabled())
      {
         log.debug("LOCK " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Session session = session(repoName, workspaceName(repoPath), lockTokens(lockTokenHeader, ifHeader));
         return new LockCommand(nullResourceLocks).lock(session, path(repoPath), body, new Depth(depthHeader), "86400");

      }
      catch (PreconditionException exc)
      {
         log.error("PreconditionException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.PRECON_FAILED).entity(exc.getMessage()).build();

      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();

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
   @UNLOCK
   @Path("/{repoName}/{repoPath:.*}/")
   public Response unlock(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {

      if (log.isDebugEnabled())
      {
         log.debug("UNLOCK " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      Session session;
      List<String> tokens = lockTokens(lockTokenHeader, ifHeader);
      try
      {
         session = session(repoName, workspaceName(repoPath), tokens);
         return new UnLockCommand(nullResourceLocks).unLock(session, path(repoPath), tokens);

      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @MKCOL
   @Path("/{repoName}/{repoPath:.*}/")
   public Response mkcol(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.CONTENT_NODETYPE) String nodeTypeHeader,
      @HeaderParam(ExtHttpHeaders.CONTENT_MIXINTYPES) String mixinTypesHeader)
   {
      if (log.isDebugEnabled())
      {
         log.debug("MKCOL " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         List<String> tokens = lockTokens(lockTokenHeader, ifHeader);
         Session session = session(repoName, workspaceName(repoPath), tokens);
         String nodeType = NodeTypeUtil.getFileNodeType(nodeTypeHeader);
         if (nodeType == null)
         {
            nodeType = defaultFolderNodeType;
         }

         return new MkColCommand(nullResourceLocks).mkCol(session, path(repoPath), nodeType, NodeTypeUtil
            .getMixinTypes(mixinTypesHeader), tokens);
      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @MOVE
   @Path("/{repoName}/{repoPath:.*}/")
   public Response move(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.DESTINATION) String destinationHeader,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader,
      @HeaderParam(ExtHttpHeaders.OVERWRITE) String overwriteHeader, @Context UriInfo uriInfo, HierarchicalProperty body)
   {

      if (log.isDebugEnabled())
      {
         log.debug("MOVE " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         String serverURI = uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).build().toString();

         destinationHeader = TextUtil.unescape(destinationHeader, '%');

         if (!destinationHeader.startsWith(serverURI))
         {
            return Response.status(HTTPStatus.BAD_GATEWAY).entity("Bad Gateway").build();
         }

         String destPath = destinationHeader.substring(serverURI.length() + 1);
         String destWorkspace = workspaceName(destPath);
         String destNodePath = path(destPath);

         String srcWorkspace = workspaceName(repoPath);
         String srcNodePath = path(repoPath);

         List<String> lockTokens = lockTokens(lockTokenHeader, ifHeader);

         Depth depth = new Depth(depthHeader);

         boolean overwrite = overwriteHeader != null && overwriteHeader.equalsIgnoreCase("T");

         if (overwrite)
         {
            delete(repoName, destPath, lockTokenHeader, ifHeader);
         }
         else
         {
            Session session = session(repoName, workspaceName(repoPath), null);
            String uri =
               uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
                  .toString();
            Response prpfind = new PropFindCommand().propfind(session, destNodePath, body, depth.getIntValue(), uri);
            if (prpfind.getStatus() != HTTPStatus.NOT_FOUND)
            {
               return Response.status(HTTPStatus.PRECON_FAILED).entity("Preconditions Failed").build();
            }
         }

         if (depth.getStringValue().equalsIgnoreCase("Infinity"))
         {
            if (srcWorkspace.equals(destWorkspace))
            {
               Session session = session(repoName, srcWorkspace, lockTokens);
               return new MoveCommand().move(session, srcNodePath, destNodePath);
            }

            Session srcSession = session(repoName, srcWorkspace, lockTokens);
            Session destSession = session(repoName, destWorkspace, lockTokens);
            return new MoveCommand().move(srcSession, destSession, srcNodePath, destNodePath);
         }
         else
         {
            return Response.status(HTTPStatus.BAD_REQUEST).entity("Bad Request").build();
         }

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
   @OPTIONS
   @Path("/{repoName}/{path:.*}/")
   public Response options(@PathParam("path") String path)
   {

      if (log.isDebugEnabled())
      {
         log.debug("OPTIONS " + path);
      }

      String DASL_VALUE =
         "<DAV:basicsearch>" + "<exo:sql xmlns:exo=\"http://exoplatform.com/jcr\"/>"
            + "<exo:xpath xmlns:exo=\"http://exoplatform.com/jcr\"/>";

      return Response.ok().header(ExtHttpHeaders.ALLOW, /* allowCommands */ALLOW).header(ExtHttpHeaders.DAV,
         "1, 2, ordered-collections").header(ExtHttpHeaders.DASL, DASL_VALUE).header(ExtHttpHeaders.MSAUTHORVIA, "DAV")
         .build();
   }

   /**
    * {@inheritDoc}
    */
   @ORDERPATCH
   @Path("/{repoName}/{repoPath:.*}/")
   public Response order(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @Context UriInfo uriInfo, HierarchicalProperty body)
   {

      if (log.isDebugEnabled())
      {
         log.debug("ORDERPATCH " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         List<String> lockTokens = lockTokens(lockTokenHeader, ifHeader);
         Session session = session(repoName, workspaceName(repoPath), lockTokens);
         String uri =
            uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
               .toString();
         return new OrderPatchCommand().orderPatch(session, path(repoPath), body, uri);
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
   @PROPPATCH
   @Path("/{repoName}/{repoPath:.*}/")
   public Response proppatch(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader,
      @Context UriInfo uriInfo, HierarchicalProperty body)
   {
      if (log.isDebugEnabled())
      {
         log.debug("PROPPATCH " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         List<String> lockTokens = lockTokens(lockTokenHeader, ifHeader);
         Session session = session(repoName, workspaceName(repoPath), lockTokens);
         String uri =
            uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
               .toString();
         return new PropPatchCommand(nullResourceLocks).propPatch(session, path(repoPath), body, lockTokens, uri);
      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspace. " + exc.getMessage());
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @REPORT
   @Path("/{repoName}/{repoPath:.*}/")
   public Response report(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.DEPTH) String depthHeader, @Context UriInfo uriInfo, HierarchicalProperty body)
   {

      if (log.isDebugEnabled())
      {
         log.debug("REPORT " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Depth depth = new Depth(depthHeader);
         Session session = session(repoName, workspaceName(repoPath), null);
         String uri =
            uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
               .toString();
         return new ReportCommand().report(session, path(repoPath), body, depth, uri);
      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @SEARCH
   @Path("/{repoName}/{repoPath:.*}/")
   public Response search(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @Context UriInfo uriInfo, HierarchicalProperty body)
   {

      if (log.isDebugEnabled())
      {
         log.debug("SEARCH " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Session session = session(repoName, workspaceName(repoPath), null);
         String uri =
            uriInfo.getBaseUriBuilder().path(getClass()).path(repoName).path(workspaceName(repoPath)).build()
               .toString();
         return new SearchCommand().search(session, body, uri);

      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
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
   @UNCHECKOUT
   @Path("/{repoName}/{repoPath:.*}/")
   public Response uncheckout(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {

      if (log.isDebugEnabled())
      {
         log.debug("UNCHECKOUT " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      try
      {
         Session session = session(repoName, workspaceName(repoPath), lockTokens(lockTokenHeader, ifHeader));
         return new UnCheckOutCommand().uncheckout(session, path(repoPath));

      }
      catch (NoSuchWorkspaceException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();

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
   @VERSIONCONTROL
   @Path("/{repoName}/{repoPath:.*}/")
   public Response versionControl(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.LOCKTOKEN) String lockTokenHeader, @HeaderParam(ExtHttpHeaders.IF) String ifHeader)
   {

      if (log.isDebugEnabled())
      {
         log.debug("VERSION-CONTROL " + repoName + "/" + repoPath);
      }

      repoPath = normalizePath(repoPath);

      Session session;
      try
      {
         session = session(repoName, workspaceName(repoPath), lockTokens(lockTokenHeader, ifHeader));
      }
      catch (Exception exc)
      {
         log.error(exc.getMessage(), exc);
         return Response.serverError().entity(exc.getMessage()).build();
      }
      return new VersionControlCommand().versionControl(session, path(repoPath));
   }

   /**
    * Gives access to the current session.
    * 
    * @param repoName repository name
    * @param wsName workspace name
    * @param lockTokens Lock tokens
    * @return current session
    * @throws Exception {@link Exception}
    */
   protected Session session(String repoName, String wsName, List<String> lockTokens) throws Exception
   {
      ManageableRepository repo = this.repositoryService.getRepository(repoName);
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      if (sp == null)
         throw new RepositoryException("SessionProvider is not properly set. Make the application calls"
            + "SessionProviderService.setSessionProvider(..) somewhere before ("
            + "for instance in Servlet Filter for WEB application)");

      Session session = sp.getSession(wsName, repo);
      if (lockTokens != null)
      {
         String[] presentLockTokens = session.getLockTokens();
         ArrayList<String> presentLockTokensList = new ArrayList<String>();
         for (int i = 0; i < presentLockTokens.length; i++)
         {
            presentLockTokensList.add(presentLockTokens[i]);
         }

         for (int i = 0; i < lockTokens.size(); i++)
         {
            String lockToken = lockTokens.get(i);
            if (!presentLockTokensList.contains(lockToken))
            {
               session.addLockToken(lockToken);
            }
         }
      }
      return session;
   }

   /**
    * Extracts workspace name from repository path.
    * 
    * @param repoPath repository path
    * @return workspace name
    */
   protected String workspaceName(String repoPath)
   {
      return repoPath.split("/")[0];
   }

   /**
    * Normalizes path.
    * 
    * @param repoPath repository path
    * @return normalized path.
    */
   protected String normalizePath(String repoPath)
   {
      if (repoPath.length() > 0 && repoPath.endsWith("/"))
      {
         return repoPath.substring(0, repoPath.length() - 1);
      }

      String[] pathElements = repoPath.split("/");
      StringBuffer escapedPath = new StringBuffer();
      for (String element : pathElements)
      {
         try
         {
            if (element.contains("'"))
            {
               element = element.replaceAll("'", URLEncoder.encode("'", "UTF-8"));
            }
            escapedPath.append(element + "/");
         }
         catch (Exception e)
         {
            log.warn(e.getMessage());
         }
      }

      return escapedPath.toString().substring(0, escapedPath.length() - 1);
   }

   /**
    * Extracts path from repository path.
    * 
    * @param repoPath repository path
    * @return path
    */
   protected String path(String repoPath)
   {
      String path = repoPath.substring(workspaceName(repoPath).length());

      if (!"".equals(path))
      {
         return path;
      }

      return "/";
   }

   /**
    * Creates the list of Lock tokens from Lock-Token and If headers.
    * 
    * @param lockTokenHeader Lock-Token HTTP header
    * @param ifHeader If HTTP header
    * @return the list of lock tokens
    */
   protected List<String> lockTokens(String lockTokenHeader, String ifHeader)
   {
      ArrayList<String> lockTokens = new ArrayList<String>();

      if (lockTokenHeader != null)
      {
         lockTokenHeader = lockTokenHeader.substring(1, lockTokenHeader.length() - 1);

         if (lockTokenHeader.contains("opaquelocktoken"))
         {
            lockTokenHeader = lockTokenHeader.split(":")[1];
         }

         lockTokens.add(lockTokenHeader);
      }

      if (ifHeader != null)
      {
         String headerLockToken = ifHeader.substring(ifHeader.indexOf("("));
         headerLockToken = headerLockToken.substring(2, headerLockToken.length() - 2);
         lockTokens.add(headerLockToken);
      }

      return lockTokens;
   }

}
