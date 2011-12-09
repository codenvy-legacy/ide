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
package org.exoplatform.ide.extension.groovy.server;

import org.everrest.core.ObjectFactory;
import org.everrest.core.ResourceBinder;
import org.everrest.core.ResourcePublicationException;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.impl.ResourceBinderImpl;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.core.resource.AbstractResourceDescriptor;
import org.everrest.groovy.ResourceId;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.codeassistant.framework.server.utils.DependentResources;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import org.exoplatform.ide.groovy.GroovyScript2RestLoader;
import org.exoplatform.ide.groovy.NodeScriptKey;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.resource.jcr.Handler;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide/groovy/")
public class GroovyScriptService extends GroovyScript2RestLoader
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(GroovyScriptService.class);

   public static final String DEVELOPER_ID = "ide.developer.id";

   public static final String GROOVY_CLASSPATH = ".groovyclasspath";

   private VirtualFileSystemRegistry vfsRegistry;

   /**
    * Resource live time. Resource will be expired after this if it is deployed
    * by user under 'developer' role.
    */
   int resourceLiveTime = 60 * 15 * 1000;

   public GroovyScriptService(VirtualFileSystemRegistry vfsRegistry, ResourceBinder binder,
      GroovyScriptInstantiator groovyScriptInstantiator, RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService, ConfigurationManager configurationManager,
      Handler jcrUrlHandler, InitParams params)
   {
      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         jcrUrlHandler, params);
      this.vfsRegistry = vfsRegistry;

      // XXX Temporary solution.
      ProviderBinder.getInstance().addMethodInvokerFilter(new DevelopmentResourceMethodFilter());
   }

   /**
    * Deploy groovy script as REST service. 
    * 
    * @param uriInfo URI information
    * @param location location of groovy script to be deployed
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws VirtualFileSystemException 
    * @throws JsonException 
    */
   @POST
   @Path("/deploy")
   @RolesAllowed({"administrators"})
   public Response deploy(@QueryParam("id") String id, @QueryParam("vfsid") String vfsid,
      @QueryParam("projectid") String projectid, MultivaluedMap<String, String> properties)
      throws VirtualFileSystemException, JsonException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      String path = vfs.getItem(id, PropertyFilter.NONE_FILTER).getPath().substring(1);
      if (projectid != null)
      {
         DependentResources dependentResources = getDependentResources(projectid, vfs);
         if (dependentResources != null)
         {
            return super.load(getCurrentRepository(), vfsid, path, true, dependentResources.getFolderSources(),
               dependentResources.getFileSources(), properties);
         }
      }
      return super.load(getCurrentRepository(), vfsid, path, true, null, null, properties);
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws VirtualFileSystemException 
    */
   @POST
   @Path("/undeploy")
   @RolesAllowed({"administrators"})
   public Response undeploy(@QueryParam("id") String id, @QueryParam("vfsid") String vfsid,
      MultivaluedMap<String, String> properties) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      String path = vfs.getItem(id, PropertyFilter.NONE_FILTER).getPath().substring(1);
      return super.load(getCurrentRepository(), vfsid, path, false, null, null, properties);
   }

   /**
    * Validate groovy script.
    * 
    * @param location location of groovy script
    * @param inputStream script for validation
    * @return {@link Response}
    * @throws VirtualFileSystemException
    * @throws JsonException 
    */
   @POST
   @Path("/validate-script")
   public Response validate(@QueryParam("vfsid") String vfsid, @QueryParam("name") String name,
      @QueryParam("projectid") String projectid, InputStream inputStream) throws VirtualFileSystemException,
      JsonException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      if (projectid != null)
      {
         DependentResources dependentResources = getDependentResources(projectid, vfs);
         if (dependentResources != null)
         {
            return super.validateScript(name, inputStream, dependentResources.getFolderSources(),
               dependentResources.getFileSources());
         }
      }
      return super.validateScript(name, inputStream, (List<String>)null, (List<String>)null);
   }

   private DependentResources getDependentResources(String projectid, VirtualFileSystem vfs)
      throws VirtualFileSystemException, JsonException
   {
      Folder project = (Folder)vfs.getItem(projectid, PropertyFilter.NONE_FILTER);
      try
      {
         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(vfs.getContent(project.createPath(GROOVY_CLASSPATH), null).getStream());
         JsonValue jsonValue = jsonParser.getJsonObject();
         GroovyClassPath classPath = ObjectBuilder.createObject(GroovyClassPath.class, jsonValue);
         return new DependentResources(getCurrentRepository(), classPath);
      }
      catch (ItemNotFoundException e)
      {
         return null;
      }
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws VirtualFileSystemException 
    */
   @POST
   @Path("/deploy-sandbox")
   @RolesAllowed({"developers"})
   public Response deployInSandbox(@QueryParam("vfsid") String vfsid, @QueryParam("id") String id,
      @Context SecurityContext security, MultivaluedMap<String, String> properties) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      File script = (File)vfs.getItem(id, PropertyFilter.NONE_FILTER);
      return sandboxDeploy(script.getPath(), vfsid, true, security, properties);
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws VirtualFileSystemException 
    */
   @POST
   @Path("/undeploy-sandbox")
   @RolesAllowed({"developers"})
   public Response undeployFromSandox(@QueryParam("vfsid") String vfsid, @QueryParam("id") String id,
      @Context SecurityContext security, MultivaluedMap<String, String> properties) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      File script = (File)vfs.getItem(id, PropertyFilter.NONE_FILTER);
      return sandboxDeploy(script.getPath(), vfsid, false, security, properties);
   }

   /**
    * @param path location of groovy script
    * @param state if true - deploy, false - undeploy
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws RepositoryException 
    */
   private Response sandboxDeploy(String path, String vfsid, boolean state, SecurityContext security,
      MultivaluedMap<String, String> properties)
   {
      path = path.startsWith("/") ? path.substring(1) : path;
      String userId = null;
      Principal principal = security.getUserPrincipal();
      if (principal != null)
      {
         userId = principal.getName();
      }
      if (userId == null)
      {
         // Should not happen
         return Response.status(Response.Status.FORBIDDEN).entity("User principal not found.")
            .type(MediaType.TEXT_PLAIN).build();
      }
      try
      {
         String repository = getCurrentRepository();
         ResourceId key = new NodeScriptKey(getCurrentRepository(), vfsid, "/" + path + "/jcr:content");
         ObjectFactory<AbstractResourceDescriptor> resource = groovyPublisher.getResource(key);
         if (resource != null)
         {
            String developer = resource.getObjectModel().getProperties().getFirst(DEVELOPER_ID);
            if (!userId.equals(developer))
            {
               return Response.status(Response.Status.FORBIDDEN).entity("Access to not own resource forbidden. ")
                  .type(MediaType.TEXT_PLAIN).build();
            }
            groovyPublisher.unpublishResource(key);
         }
         else if (!state)
         {
            return Response.status(Response.Status.BAD_REQUEST)
               .entity("Can't remove resource " + path + ", not bound or has wrong mapping to the resource. ")
               .type(MediaType.TEXT_PLAIN).build();
         }
         if (state)
         {
            if (properties == null)
            {
               properties = new MultivaluedMapImpl();
            }
            properties.putSingle(DEVELOPER_ID, userId);
            properties.putSingle(ResourceBinderImpl.RESOURCE_EXPIRED,
               Long.toString(System.currentTimeMillis() + resourceLiveTime));

            //Get dependent resources from classpath file if exist:
            DependentResources dependentResources =
               GroovyScriptServiceUtil.getDependentResource(path, repositoryService);
            if (dependentResources != null)
            {
               return load(repository, vfsid, path, true, dependentResources.getFolderSources(),
                  dependentResources.getFileSources(), properties);
            }

            return load(repository, vfsid, path, true, null, null, properties);
         }

         return Response.status(Response.Status.NO_CONTENT).build();
      }
      catch (ResourcePublicationException e)
      {
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
   }

   /*
    * Get list of JAR files includes attributes from META-INF/MANIFEST.MF
    */
   @GET
   @Path("/jars")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getAvailableJarLibraries()
   {
      try
      {
         JarsCollector collector = new JarsCollector();
         List<Jar> jarList = new ArrayList<Jar>(collector.getJars().values());
         return Response.ok().entity(jarList).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(500).entity(e.getMessage()).type("text/plain").build();
      }
   }

   // Temporary method. Remove after refactoring of GroovyScript2RestLoader.
   private String getCurrentRepository()
   {
      try
      {
         return repositoryService.getCurrentRepository().getConfiguration().getName();
      }
      catch (RepositoryException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
   }
}
