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
package org.exoplatform.ide.discovery;

import org.exoplatform.ide.Utils;
import org.exoplatform.ide.download.NodeTypeUtil;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide/discovery")
public class RepositoryDiscoveryService
{

   private final static String WEBDAV_CONTEXT = "jcr";

   public static final String WEBDAV_SCHEME = "jcr-webdav";

   public static final String DEF_WS = "dev-monit";

   /** See {@link SessionProviderService} */
   private ThreadLocalSessionProviderService sessionProviderService;

   private String entryPoint;

   private boolean discoverable;

   /**
    * To disable cache control.
    */
   private static final CacheControl noCache;

   static
   {
      noCache = new CacheControl();
      noCache.setNoCache(true);
      noCache.setNoStore(true);
   }

   private RepositoryService repositoryService;

   public RepositoryDiscoveryService(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService, String entryPoint, boolean discoverable)
   {
      this.repositoryService = repositoryService;

      if (entryPoint != null)
         this.entryPoint = entryPoint;
      else
         this.entryPoint = DEF_WS;

      this.sessionProviderService = sessionProviderService;
      this.discoverable = discoverable;
   }

   public final static String getWebDavConetxt()
   {
      return WEBDAV_CONTEXT;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/entrypoints/")
   public List<EntryPoint> getEntryPoints(@Context UriInfo uriInfo)
   {
      List<String> entryPoints = new ArrayList<String>();

      for (RepositoryEntry repositoryEntry : repositoryService.getConfig().getRepositoryConfigurations())
      {
         String repositoryName = repositoryEntry.getName();
         for (WorkspaceEntry workspaceEntry : repositoryEntry.getWorkspaceEntries())
         {
            String workspaceName = workspaceEntry.getName();

            String href =
               uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, repositoryName, workspaceName, "/").build()
                  .toString();
            entryPoints.add(href);
         }
      }

      List<EntryPoint> entryPointList = new ArrayList<EntryPoint>();
      for (int i = 0; i < entryPoints.size(); i++)
      {
         entryPointList.add(new EntryPoint(WEBDAV_SCHEME, entryPoints.get(i)));
      }

      return entryPointList;
   }

   @GET
   @Path("/defaultEntrypoint/")
   public String getDefaultEntryPoint(@Context UriInfo uriInfo) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository repository = repositoryService.getCurrentRepository();
      if (repository == null)
         repository = repositoryService.getDefaultRepository();

      String href =
         uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, repository.getConfiguration().getName(), entryPoint, "/")
            .build().toString();
      return href;
   }

   @GET
   @Path("/isdiscoverable/")
   public String isDiscoverable()
   {
      return "" + discoverable;
   }

   /**
    * Method search item location
    * @param uriInfo
    * @param location Start point to search
    * @param name Name of item
    * @return location of item
    */
   @GET
   @Path("/find/location")
   public String getFileLocation(@Context UriInfo uriInfo, @QueryParam("location") String location,
      @QueryParam("name") String name)
   {
      String baseUri = uriInfo.getBaseUri().toASCIIString();
      String[] jcrLocation = Utils.parseJcrLocation(baseUri, location);
      try
      {
         Session session =
            Utils.getSession(repositoryService, sessionProviderService, jcrLocation[0], jcrLocation[1] + "/"
               + jcrLocation[2]);
         Node rootNode = session.getRootNode();
         Node node = rootNode.getNode(jcrLocation[2]);
         Node itemNode = findNode(node, name);
         if (itemNode != null)
         {
            String itemLocation = baseUri + Utils.WEBDAV_CONTEXT + jcrLocation[0] + "/" + jcrLocation[1];
            itemLocation += (itemNode.getPath().startsWith("/")) ? itemNode.getPath() : "/" + itemNode.getPath();
            return itemLocation;
         }
         else
         {
            throw new FileNotFoundException("Item " + name + " not found.");
         }
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 500));
      }
      catch (RepositoryConfigurationException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 500));
      }
      catch (FileNotFoundException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 404));
      }
   }

   /**
    * Find file and return content
    * @param uriInfo
    * @param location Start point to search
    * @param name File name
    * @return file content
    */
   @GET
   @Path("/find/content")
   public String getFileContent(@Context UriInfo uriInfo, @QueryParam("location") String location,
      @QueryParam("name") String name)
   {
      String baseUri = uriInfo.getBaseUri().toASCIIString();
      String[] jcrLocation = Utils.parseJcrLocation(baseUri, location);
      try
      {
         Session session =
            Utils.getSession(repositoryService, sessionProviderService, jcrLocation[0], jcrLocation[1] + "/"
               + jcrLocation[2]);
         Node rootNode = session.getRootNode();
         Node node = rootNode.getNode(jcrLocation[2]);
         Node itemNode = findNode(node, name);
         if (itemNode != null)
         {
            return itemNode.getNode(NodeTypeUtil.JCR_CONTENT).getProperty(NodeTypeUtil.JCR_DATA).getString();
         }
         else
         {
            throw new FileNotFoundException("File " + name + " not found.");
         }
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 500));
      }
      catch (RepositoryConfigurationException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 500));
      }
      catch (FileNotFoundException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 404));
      }
   }

   /**
    * Find class path file's node by name step by step going upper in node hierarchy.
    * 
    * @param node node, in what child nodes to find class path file
    * @return {@link Node} found jcr node
    * @throws RepositoryException
    */
   private Node findNode(Node node, String name) throws RepositoryException
   {
      if (node == null)
         return null;
      //Get all child node that end with name
      NodeIterator nodeIterator = node.getNodes("*" + name);
      while (nodeIterator.hasNext())
      {
         Node childNode = nodeIterator.nextNode();
         if (name.equals(childNode.getName()))
            return childNode;
      }
      try
      {
         //Go upper to find item path file:   
         Node parentNode = node.getParent();
         return findNode(parentNode, name);
      }
      catch (ItemNotFoundException e)
      {
         return null;
      }
      catch (AccessDeniedException e)
      {
         return null;
      }
   }

   /**
    * Create response to send with error message.
    * 
    * @param t thrown exception
    * @param status http status
    * @return {@link Response} response with error
    */
   protected Response createErrorResponse(Throwable t, int status)
   {
      return Response.status(status).entity(t.getMessage()).type("text/plain").build();
   }

}
