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

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.codeassistant.framework.server.utils.ClassPathFileNotFoundException;
import org.exoplatform.ide.codeassistant.framework.server.utils.DependentResources;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.jcr.ext.resource.jcr.Handler;
import org.exoplatform.services.jcr.ext.script.groovy.GroovyScript2RestLoader;
import org.exoplatform.services.jcr.ext.script.groovy.NodeScriptKey;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.ext.groovy.GroovyJaxrsPublisher;
import org.exoplatform.services.rest.ext.groovy.ResourceId;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.impl.ResourcePublicationException;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;

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

   /**
    * Resource live time. Resource will be expired after this if it is deployed
    * by user under 'developer' role.
    */
   int resourceLiveTime = 60 * 15 * 1000;

   public GroovyScriptService(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, Handler jcrUrlHandler, InitParams params)
   {
      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         jcrUrlHandler, params);
   }

   public GroovyScriptService(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, RegistryService registryService, GroovyJaxrsPublisher groovyPublisher,
      Handler jcrUrlHandler, InitParams params)
   {
      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         registryService, groovyPublisher, jcrUrlHandler, params);
   }

   public GroovyScriptService(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, RegistryService registryService, Handler jcrUrlHandler,
      InitParams params)
   {
      super(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         registryService, jcrUrlHandler, params);
   }

   /**
    * Validate groovy script.
    * 
    * @param location location of groovy script
    * @param inputStream script for validation
    * @return {@link Response}
    */
   @POST
   @Path("/validate-script")
   public Response validate(@Context UriInfo uriInfo, @HeaderParam("location") String location, InputStream inputStream)
   {
      //Get name from script location:
      String name =
         (location != null && location.length() > 0) ? location.substring(location.lastIndexOf("/") + 1) : "";
      //Get dependent resources from classpath file if exist:
      DependentResources dependentResources =
         GroovyScriptServiceUtil.getDependentResource(location, uriInfo.getBaseUri().toASCIIString(),
            repositoryService, sessionProviderService);
      if (dependentResources != null)
      {
         return super.validateScript(name, inputStream, dependentResources.getFolderSources(),
            dependentResources.getFileSources());
      }
      return super
         .validateScript(name, inputStream, Collections.<String> emptyList(), Collections.<String> emptyList());
   }

   /**
    * Get the groovy classpath location file, 
    * which is proper for pointed item's location.
    * The groovy classpath location is in response body,
    * if not found, then status 404.
    * 
    * @param uriInfo request URI information
    * @param location item's location
    * @return {@link Response}
    */
   @GET
   @Path("/classpath-location")
   @Produces("text/plain")
   public String getClassPathLocation(@Context UriInfo uriInfo, @HeaderParam("location") String location)
   {
      String baseUri = uriInfo.getBaseUri().toASCIIString();
      String[] jcrLocation = GroovyScriptServiceUtil.parseJcrLocation(baseUri, location);
      try
      {
         Session session =
            GroovyScriptServiceUtil.getSession(repositoryService, sessionProviderService, jcrLocation[0],
               jcrLocation[1] + "/" + jcrLocation[2]);
         Node rootNode = session.getRootNode();
         Node node = rootNode.getNode(jcrLocation[2]);
         Node classpathNode = findClassPathNode(node);
         if (classpathNode != null)
         {
            //Form the available href of the classpath file:
            String classpathLocation =
               baseUri + GroovyScriptServiceUtil.WEBDAV_CONTEXT + jcrLocation[0] + "/" + jcrLocation[1];
            classpathLocation +=
               (classpathNode.getPath().startsWith("/")) ? classpathNode.getPath() : "/" + classpathNode.getPath();
            return classpathLocation;
         }
         else
         {
            throw new ClassPathFileNotFoundException("Groovy classpath file not found.");
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
      catch (ClassPathFileNotFoundException e)
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
   private Node findClassPathNode(Node node) throws RepositoryException
   {
      if (node == null)
         return null;
      //Get all child node that end with ".groovyclasspath"
      NodeIterator nodeIterator = node.getNodes("*" + GROOVY_CLASSPATH);
      while (nodeIterator.hasNext())
      {
         Node childNode = nodeIterator.nextNode();
         //The first found groovy class path file will be returned:
         if (GROOVY_CLASSPATH.equals(childNode.getName()))
            return childNode;
      }
      try
      {
         //Go upper to find class path file:   
         Node parentNode = node.getParent();
         return findClassPathNode(parentNode);
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
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   @POST
   @Path("/deploy-sandbox")
   @RolesAllowed({"developers"})
   public Response deployInSandbox(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      @Context SecurityContext security, MultivaluedMap<String, String> properties)
   {
      return sandboxLoader(uriInfo, location, true, security, properties);
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   @POST
   @Path("/undeploy-sandbox")
   @RolesAllowed({"developers"})
   public Response undeployFromSandox(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      @Context SecurityContext security, MultivaluedMap<String, String> properties)
   {
      return sandboxLoader(uriInfo, location, false, security, properties);
   }

   /**
    * Deploy groovy script as REST service. 
    * 
    * @param uriInfo URI information
    * @param location location of groovy script to be deployed
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    * @throws IOException 
    */
   @POST
   @Path("/deploy")
   @RolesAllowed({"administrators"})
   public Response deploy(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      MultivaluedMap<String, String> properties)
   {
      String[] jcrLocation = GroovyScriptServiceUtil.parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);
      if (jcrLocation == null)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(location + " not found. ").type(MediaType.TEXT_PLAIN)
            .build();
      }
      //Get dependent resources from classpath file if exist:
      DependentResources dependentResources =
         GroovyScriptServiceUtil.getDependentResource(location, uriInfo.getBaseUri().toASCIIString(),
            repositoryService, sessionProviderService);
      if (dependentResources != null)
      {
         return super.load(jcrLocation[0], jcrLocation[1], jcrLocation[2], true, dependentResources.getFolderSources(),
            dependentResources.getFileSources(), properties);
      }
      return super.load(jcrLocation[0], jcrLocation[1], jcrLocation[2], true, Collections.<String> emptyList(),
         Collections.<String> emptyList(), properties);
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   @POST
   @Path("/undeploy")
   @RolesAllowed({"administrators"})
   public Response undeploy(@Context UriInfo uriInfo, @HeaderParam("location") String location,
      MultivaluedMap<String, String> properties)
   {
      String[] jcrLocation = GroovyScriptServiceUtil.parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);

      if (jcrLocation == null)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(location + " not found. ").type(MediaType.TEXT_PLAIN)
            .build();
      }

      return super.load(jcrLocation[0], jcrLocation[1], jcrLocation[2], false, Collections.<String> emptyList(),
         Collections.<String> emptyList(), properties);
   }

   /**
    * @param uriInfo URI information
    * @param location location of groovy script
    * @param state if true - deploy, false - undeploy
    * @param security security context
    * @param properties optional properties to be applied to loaded resource
    * @return {@link Response}
    */
   private Response sandboxLoader(UriInfo uriInfo, String location, boolean state, SecurityContext security,
      MultivaluedMap<String, String> properties)
   {
      String[] jcrLocation = GroovyScriptServiceUtil.parseJcrLocation(uriInfo.getBaseUri().toASCIIString(), location);
      if (jcrLocation == null)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(location + " not found. ").type(MediaType.TEXT_PLAIN)
            .build();
      }
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
         ses =
            sessionProviderService.getSessionProvider(null).getSession(jcrLocation[1],
               repositoryService.getRepository(jcrLocation[0]));
         Node script = ((Node)ses.getItem("/" + jcrLocation[2])).getNode("jcr:content");
         ResourceId key = new NodeScriptKey(jcrLocation[0], jcrLocation[1], script);

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
            return Response
               .status(Response.Status.BAD_REQUEST)
               .entity("Can't remove resource " + jcrLocation[2] + ", not bound or has wrong mapping to the resource. ")
               .type(MediaType.TEXT_PLAIN).build();
         }
         if (state)
         {
            if (properties == null)
            {
               properties = new MultivaluedMapImpl();
            }
            properties.putSingle(DEVELOPER_ID, userId);
            properties.putSingle(ResourceBinder.RESOURCE_EXPIRED,
               Long.toString(System.currentTimeMillis() + resourceLiveTime));

            //Get dependent resources from classpath file if exist:
            DependentResources dependentResources =
               GroovyScriptServiceUtil.getDependentResource(location, uriInfo.getBaseUri().toASCIIString(),
                  repositoryService, sessionProviderService);
            if (dependentResources != null)
            {
               return load(jcrLocation[0], jcrLocation[1], jcrLocation[2], true, dependentResources.getFolderSources(),
                  dependentResources.getFileSources(), properties);
            }

            return load(jcrLocation[0], jcrLocation[1], jcrLocation[2], true, Collections.<String> emptyList(),
               Collections.<String> emptyList(), properties);
            //  groovyPublisher.publishPerRequest(script.getProperty("jcr:data").getStream(), key, properties, createSourceFolders(dependentResources.getFolderSources()), createSourceFiles(dependentResources.getFileSources()));
         }

         return Response.status(Response.Status.NO_CONTENT).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + jcrLocation[2] + " does not exists";
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

   /*
    * Get list of JAR files include attributes from META-INF/MANIFEST.MF
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
         return createErrorResponse(e, HTTPStatus.INTERNAL_ERROR);
      }

   }

}
