/*
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
 */
package org.exoplatform.ide.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.resource.spi.ResourceAllocationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.AccessControlEntry;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.security.IdentityConstants;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 28, 2010 $
 *
 */
@Path("/ide/permission")
public class PermissionsService
{
   private RepositoryService repositoryService;

   private ThreadLocalSessionProviderService sessionProviderService;

   private final String WEBDAV_CONTEXT = "jcr";

   private static Log log = ExoLogger.getLogger(PermissionsService.class);

   /**
    * @param repositoryService
    * @param sessionProviderService
    */
   public PermissionsService(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
   }

   @POST
   @Path("/set/")
   @Consumes("application/json")
   public Response setPermission(@Context UriInfo uriInfo, @HeaderParam("Item-Href") String href,
      UserPermissionsStorage permissions)
   {
      String url = uriInfo.getBaseUri().toASCIIString() + "/" + WEBDAV_CONTEXT + "/";
      if (!href.startsWith(url))
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(href).build();
      }

      href = href.substring(url.length());

      if (href.length() > 0 && href.charAt(0) == '/')
      {
         href = href.substring(1);
      }

      String repoName = href.substring(0, href.indexOf("/"));

      href = href.substring(1);
      href = href.substring(href.indexOf("/") + 1);

      try
      {
         Session session = getSession(repoName, href);
         NodeImpl node = (NodeImpl)session.getItem(href.substring(href.indexOf("/")));
         NodeImpl content = (NodeImpl)node.getNode("jcr:content");
         if (content.getACL().getOwner().equals(session.getUserID()))
         {
            //         node.clearACL();
            if (node.canAddMixin("exo:privilegeable"))
            {
               node.addMixin("exo:privilegeable");
            }
            if (node.canAddMixin("exo:owneable"))
            {
               node.addMixin("exo:owneable");
            }
            //            node.clearACL();
            if (!node.getACL().getOwner().equals(session.getUserID()))
            {
               node.getACL().setOwner(session.getUserID());
            }
            //set all permissions to owner
            node.setPermission(session.getUserID(), PermissionType.ALL);

            //remove permissions for ANY 
            node.removePermission(IdentityConstants.ANY);

            for (UserPermissions p : permissions.getUserPermissions())
            {
               node.setPermission(p.getUserID(), p.getPermissions());
            }
            //            node.setPermissions(permissionMap);
            node.save();
            session.save();
         }
         else
         {
            return Response.status(HTTPStatus.FORBIDDEN)
               .entity("Can't change permissions, User " + session.getUserID() + " not owner for " + node.getName())
               .build();
         }
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

      return Response.ok().build();
   }

   @GET
   @Path("/get/")
   @Produces("application/json")
   public UserPermissionsStorage getPermissions(@Context UriInfo uriInfo, @HeaderParam("Item-Href") String href)
      throws ResourceAllocationException
   {
      String url = uriInfo.getBaseUri().toASCIIString() + "/" + WEBDAV_CONTEXT + "/";
      if (!href.startsWith(url))
      {
         throw new ResourceAllocationException("Resource " + href + " not found");
      }

      href = href.substring(url.length());

      if (href.length() > 0 && href.charAt(0) == '/')
      {
         href = href.substring(1);
      }

      String repoName = href.substring(0, href.indexOf("/"));

      href = href.substring(1);
      href = href.substring(href.indexOf("/") + 1);

      try
      {
         Session session = getSession(repoName, href);
         NodeImpl node = (NodeImpl)session.getItem(href.substring(href.indexOf("/")));
         NodeImpl content = (NodeImpl)node.getNode("jcr:content");
         if (content.getACL().getOwner().equals(session.getUserID()))
         {
            //         node.clearACL();
            if (node.canAddMixin("exo:privilegeable"))
            {
               node.addMixin("exo:privilegeable");
            }
            if (node.canAddMixin("exo:owneable"))
            {
               node.addMixin("exo:owneable");
            }
         }
         Map<String, java.util.List<String>> permissions = new LinkedHashMap<String, java.util.List<String>>();

         for (AccessControlEntry ace : node.getACL().getPermissionEntries())
         {
            if (permissions.containsKey(ace.getIdentity()))
            {
               permissions.get(ace.getIdentity()).add(ace.getPermission());
            }
            else
            {
               List<String> per = new ArrayList<String>();
               per.add(ace.getPermission());
               permissions.put(ace.getIdentity(), per);
            }
         }

         List<UserPermissions> userPermissionList = new ArrayList<UserPermissions>();
         for (String key : permissions.keySet())
         {
            UserPermissions up = new UserPermissions();
            up.setUserID(key);
            String[] per = new String[permissions.get(key).size()];
            per = permissions.get(key).toArray(per);
            up.setPermissions(per);
            userPermissionList.add(up);
         }
         UserPermissionsStorage stor = new UserPermissionsStorage();
         stor.setUserPermissions(userPermissionList);
         return stor;
      }
      catch (PathNotFoundException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         //            return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
      }
      catch (Exception exc)
      {
         log.error(exc.getMessage(), exc);
         //            return Response.serverError().entity(exc.getMessage()).build();
      }
      return new UserPermissionsStorage();

   }

   /**
    * Return JCR session for selected repository and workspace.
    * 
    * @param repoName
    * @param repoPath
    * @return
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   private Session getSession(String repoName, String repoPath) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository repo = this.repositoryService.getRepository(repoName);
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      if (sp == null)
         throw new RepositoryException("SessionProvider is not properly set. Make the application calls"
            + "SessionProviderService.setSessionProvider(..) somewhere before ("
            + "for instance in Servlet Filter for WEB application)");

      return sp.getSession(workspaceName(repoPath), repo);
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
    * Extracts workspace name from repository path.
    * 
    * @param repoPath repository path
    * @return workspace name
    */
   protected String workspaceName(String repoPath)
   {
      return repoPath.split("/")[0];
   }

}
