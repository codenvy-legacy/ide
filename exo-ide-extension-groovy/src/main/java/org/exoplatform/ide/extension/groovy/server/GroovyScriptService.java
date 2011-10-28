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
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.resource.jcr.Handler;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
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
   }

   //   public GroovyScriptService(VirtualFileSystemRegistry vfsRegistry, ResourceBinder binder,
   //      GroovyScriptInstantiator groovyScriptInstantiator, RepositoryService repositoryService,
   //      ThreadLocalSessionProviderService sessionProviderService, ConfigurationManager configurationManager,
   //      RegistryService registryService, GroovyResourcePublisher groovyPublisher, Handler jcrUrlHandler, InitParams params)
   //   {
   //      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
   //         registryService, groovyPublisher, jcrUrlHandler, params);
   //      this.vfsRegistry = vfsRegistry;
   //   }
   //
   //   public GroovyScriptService(VirtualFileSystemRegistry vfsRegistry, ResourceBinder binder,
   //      GroovyScriptInstantiator groovyScriptInstantiator, RepositoryService repositoryService,
   //      ThreadLocalSessionProviderService sessionProviderService, ConfigurationManager configurationManager,
   //      RegistryService registryService, Handler jcrUrlHandler, InitParams params)
   //   {
   //      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
   //         registryService, jcrUrlHandler, params);
   //      this.vfsRegistry = vfsRegistry;
   //   }

   /**
    * Validate groovy script.
    * 
    * @param location location of groovy script
    * @param inputStream script for validation
    * @return {@link Response}
    * @throws RepositoryConfigurationException 
    * @throws RepositoryException 
    * @throws JsonException 
    */
   @POST
   @Path("/validate-script")
   public Response validate(@QueryParam("vfsid") String vfsid,
                            @QueryParam("name") String name,
                            @QueryParam("projectid") String projectid, 
                            InputStream inputStream) throws VirtualFileSystemException,
      RepositoryException, RepositoryConfigurationException, JsonException
   {
      if (vfsid == null || vfsid.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Id of File System may not be null or empty");
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      if (projectid != null)
      {
         DependentResources dependentResources = getDependentResources(projectid, vfs);
         if (dependentResources != null)
         {
            return super.validateScript(name, inputStream, dependentResources.getFolderSources(),
               dependentResources.getFileSources());
         }
         else
         {
            return super.validateScript(name, inputStream, Collections.<String> emptyList(),
               Collections.<String> emptyList());
         }
      }
      return super
         .validateScript(name, inputStream, Collections.<String> emptyList(), Collections.<String> emptyList());
   }

   /**
    * @param projectid
    * @param inputStream
    * @param vfs
    * @param name
    * @param classpath
    * @return 
    * @throws ItemNotFoundException
    * @throws InvalidArgumentException
    * @throws PermissionDeniedException
    * @throws VirtualFileSystemException
    * @throws JsonException
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   private DependentResources getDependentResources(String projectid, VirtualFileSystem vfs)
      throws org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException, JsonException, RepositoryException,
      RepositoryConfigurationException
   {
      List<Item> chldrs = vfs.getChildren(projectid, -1, 0, PropertyFilter.NONE_FILTER).getItems();
      for (int i = 0; i < chldrs.size(); i++)
      {
         Item item = chldrs.get(i);
         if (GROOVY_CLASSPATH.equals(item.getName()))
         {
            JsonParser jsonParser = new JsonParser();
            jsonParser.parse(vfs.getContent(item.getId()).getStream());
            JsonValue jsonValue = jsonParser.getJsonObject();
            GroovyClassPath classPath = ObjectBuilder.createObject(GroovyClassPath.class, jsonValue);
            String repositoryName =
               (repositoryService.getCurrentRepository() != null) ? repositoryService.getCurrentRepository()
                  .getConfiguration().getName() : repositoryService.getDefaultRepository().getConfiguration().getName();
            return new DependentResources(repositoryName, classPath);
         }
      }
      return null;
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws VirtualFileSystemException 
    * @throws RepositoryException 
    */
   @POST
   @Path("/deploy-sandbox")
   @RolesAllowed({"developers"})
   public Response deployInSandbox(@QueryParam("vfsid") String vfsid, @QueryParam("id") String id,
      @Context SecurityContext security, MultivaluedMap<String, String> properties) throws VirtualFileSystemException
   {
      if (vfsid == null || vfsid.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Id of File System may not be null or empty");
      if (id == null || id.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Item id may not be null or empty");

      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      File script = (File)vfs.getItem(id, PropertyFilter.NONE_FILTER);
      return sandboxLoader(script.getPath(), vfsid, true, security, properties);
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
      if (vfsid == null || vfsid.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Id of File System may not be null or empty");
      if (id == null || id.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Item id may not be null or empty");

      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      File script = (File)vfs.getItem(id, PropertyFilter.NONE_FILTER);
      return sandboxLoader(script.getPath(), vfsid, false, security, properties);
   }

   /**
    * Deploy groovy script as REST service. 
    * 
    * @param uriInfo URI information
    * @param location location of groovy script to be deployed
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws RepositoryException 
    * @throws VirtualFileSystemException 
    * @throws RepositoryConfigurationException 
    * @throws JsonException 
    * @throws IOException 
    */
   @POST
   @Path("/deploy")
   @RolesAllowed({"administrators"})
   public Response deploy(@QueryParam("id") String id, @QueryParam("vfsid") String vfsid,
      @QueryParam("projectid") String projectid, MultivaluedMap<String, String> properties) throws RepositoryException,
      VirtualFileSystemException, JsonException, RepositoryConfigurationException
   {
      if (vfsid == null || vfsid.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Id of File System may not be null or empty");
      if (id == null || id.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Item id may not be null or empty");

      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      File script = (File)vfs.getItem(id, PropertyFilter.NONE_FILTER);
      DependentResources dependentResources = getDependentResources(projectid, vfs);
      //TODO method of getting current repository and workspace
      String repositoryName = repositoryService.getCurrentRepository().getConfiguration().getName();
      if (dependentResources != null)
      {
         return super.load(repositoryName, vfsid, script.getPath(), true, dependentResources.getFolderSources(),
            dependentResources.getFileSources(), properties);
      }
      return super.load(repositoryName, vfsid, script.getPath(), true, Collections.<String> emptyList(),
         Collections.<String> emptyList(), properties);
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws RepositoryException 
    * @throws VirtualFileSystemException 
    * @throws PermissionDeniedException 
    * @throws ItemNotFoundException 
    */
   @POST
   @Path("/undeploy")
   @RolesAllowed({"administrators"})
   public Response undeploy(@QueryParam("id") String id, @QueryParam("vfsid") String vfsid,
      MultivaluedMap<String, String> properties) throws RepositoryException, ItemNotFoundException,
      PermissionDeniedException, VirtualFileSystemException
   {
      //Get name from script location:
      if (vfsid == null || vfsid.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Id of File System may not be null or empty");
      if (id == null || id.isEmpty())
         throw new VirtualFileSystemException("Can't validate script. Item id may not be null or empty");

      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null);
      File script = (File)vfs.getItem(id, PropertyFilter.NONE_FILTER);
      //TODO method of getting current repository and workspace
      String repository = repositoryService.getCurrentRepository().getConfiguration().getName();
      return super.load(repository, vfsid, script.getPath().replaceFirst("/", ""), false,
         Collections.<String> emptyList(), Collections.<String> emptyList(), properties);
   }

   /**
    * @param location location of groovy script
    * @param state if true - deploy, false - undeploy
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws RepositoryException 
    */
   private Response sandboxLoader(String location, String vfsid, boolean state, SecurityContext security,
      MultivaluedMap<String, String> properties)
   {
      location = location.startsWith("/") ? location.substring(1) : location;
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

      Session ses = null;
      try
      {
         //TODO method of getting current repository and workspace
         String repository = repositoryService.getCurrentRepository().getConfiguration().getName();
         //         String workspace = RepositoryDiscoveryService.getEntryPoint();
         //         ses = repositoryService.getCurrentRepository().login(workspace);
         //         Node script = ((Node)ses.getItem("/" + location)).getNode("jcr:content");
         ResourceId key = new NodeScriptKey(repository, vfsid, location);
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
               .entity("Can't remove resource " + location + ", not bound or has wrong mapping to the resource. ")
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
               GroovyScriptServiceUtil.getDependentResource(location, repositoryService);
            if (dependentResources != null)
            {
               return load(repository, vfsid, location, true, dependentResources.getFolderSources(),
                  dependentResources.getFileSources(), properties);
            }

            return load(repository, vfsid, location, true, Collections.<String> emptyList(),
               Collections.<String> emptyList(), properties);
            //  groovyPublisher.publishPerRequest(script.getProperty("jcr:data").getStream(), key, properties, createSourceFolders(dependentResources.getFolderSources()), createSourceFiles(dependentResources.getFileSources()));
         }

         return Response.status(Response.Status.NO_CONTENT).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + location + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
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
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
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

}
