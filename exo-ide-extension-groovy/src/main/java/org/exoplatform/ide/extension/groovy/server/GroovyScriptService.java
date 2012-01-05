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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.groovy.ExtendedGroovyClassLoader;
import org.everrest.groovy.GroovyClassLoaderProvider;
import org.everrest.groovy.SourceFile;
import org.exoplatform.container.ConcurrentPicoContainer;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyClassPath;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.security.ConversationState;
import org.picocontainer.ComponentAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@Path("/ide/groovy/")
public class GroovyScriptService
{
   /** Resource live time. Resource will be expired after this if it is deployed by user under 'developer' role. */
   private static final int RESOURCE_LIVE_TIME = 15 * 60 * 1000; // 15 minutes

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   private ConcurrentPicoContainer restfulComponents;

   @Inject
   private GroovyClassLoaderProvider groovyClassLoaderProvider;

   public GroovyScriptService(@Named("RestfulContainerProvider") Provider<ConcurrentPicoContainer> restfulComponents)
   {
      this.restfulComponents = restfulComponents.get();
   }

   /**
    * Deploy Groovy script as REST service.
    *
    * @param fileId ID of groovy source file
    * @param vfsId ID of virtual file system
    * @param projectId ID of IDE project
    * @throws VirtualFileSystemException
    * @throws JsonException
    */
   @POST
   @Path("/deploy")
   @RolesAllowed({"administrators"})
   public void deploy( //
                       @QueryParam("id") String fileId, //
                       @QueryParam("vfsid") String vfsId, //
                       @QueryParam("projectid") String projectId //
   ) throws VirtualFileSystemException, JsonException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      Class<?> toDeploy = compile(vfs, projectId, fileId);
      restfulComponents.registerComponentImplementation(GroovyComponentKey.make(vfs.getInfo().getId(), fileId), toDeploy);
   }

   /**
    * Remove deployed REST service.
    *
    * @param fileId ID of groovy source file
    * @param vfsId ID of virtual file system
    * @throws VirtualFileSystemException
    */
   @POST
   @Path("/undeploy")
   @RolesAllowed({"administrators"})
   public void undeploy( //
                         @QueryParam("id") String fileId, //
                         @QueryParam("vfsid") String vfsId //
   ) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (null == restfulComponents.unregisterComponent(GroovyComponentKey.make(vfs.getInfo().getId(), fileId)))
      {
         throw new WebApplicationException( //
            Response //
               .status(Response.Status.BAD_REQUEST) //
               .entity("Can't unbind script " + fileId + ", not bound or has wrong mapping to the resource class. ") //
               .type(MediaType.TEXT_PLAIN).build());
      }
   }

   /**
    * Deploy Groovy script as REST service. There restriction in access to resources deployed in 'sandbox':
    * <ul>
    * <li>Resource visible for user which deploy it only. It is not visible for other users.</li>
    * <li>Resource will be 'undeployed' automatically after a certain time</li>
    * <li>Resource may be removed user which deploy it only.</li>
    * </ul>
    *
    * @param fileId ID of groovy source file
    * @param vfsId ID of virtual file system
    * @param projectId ID of IDE project
    * @throws VirtualFileSystemException
    * @throws JsonException
    * @see #undeployFromSandbox(String, String)
    */
   @POST
   @Path("/deploy-sandbox")
   @RolesAllowed({"developers"})
   public void deployInSandbox( //
                                @QueryParam("id") String fileId, //
                                @QueryParam("vfsid") String vfsId, //
                                @QueryParam("projectid") String projectId //
   ) throws VirtualFileSystemException, JsonException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      GroovyComponentKey key = GroovyComponentKey.make(vfs.getInfo().getId(), fileId);
      final String userId = ConversationState.getCurrent().getIdentity().getUserId();
      final long expired = System.currentTimeMillis() + RESOURCE_LIVE_TIME;
      key.setAttribute("ide.developer.id", userId);
      key.setAttribute("ide.expiration.date", expired);
      Class<?> toDeploy = compile(vfs, projectId, fileId);
      restfulComponents.registerComponentImplementation(key, toDeploy);
   }

   /**
    * Remove previously deployed REST service.
    *
    * @param fileId ID of groovy source file
    * @param vfsId ID of virtual file system
    * @throws VirtualFileSystemException
    */
   @POST
   @Path("/undeploy-sandbox")
   @RolesAllowed({"developers"})
   public void undeployFromSandbox( //
                                    @QueryParam("id") String fileId, //
                                    @QueryParam("vfsid") String vfsId //
   ) throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      ComponentAdapter component = restfulComponents.getComponentAdapter(GroovyComponentKey.make(vfs.getInfo().getId(), fileId));
      if (null == component)
      {
         throw new WebApplicationException( //
            Response //
               .status(Response.Status.BAD_REQUEST) //
               .entity("Can't unbind script " + fileId + ", not bound or has wrong mapping to the resource class. ") //
               .type(MediaType.TEXT_PLAIN).build());
      }
      if (!ConversationState.getCurrent().getIdentity().getUserId().equals(
         ((GroovyComponentKey)component.getComponentKey()).getAttribute("ide.developer.id")))
      {
         throw new WebApplicationException( //
            Response //
               .status(Response.Status.FORBIDDEN) //
               .entity("Access to not own resource forbidden. ") //
               .type(MediaType.TEXT_PLAIN).build());
      }
      restfulComponents.unregisterComponent(component.getComponentKey());
   }

   /**
    * Validate Groovy script. Try compile source in <code>inputStream</code> and if compilation passed we assume source
    * is OK. Do not check JAX-RS annotation or possibility to deploy specified source as JAX-RS resource.
    *
    * @param vfsId ID of virtual file system
    * @param name the name of source. This name has a display purpose. If source has error and may not be compiled then
    * this name used in error message. If name is <code>null</code> then Groovy engine will use automatically generated
    * name.
    * @param projectId ID of IDE project
    * @param inputStream source of Groovy script for validation
    * @throws VirtualFileSystemException
    * @throws JsonException
    */
   @POST
   @Path("/validate-script")
   public void validate( //
                         @QueryParam("vfsid") String vfsId, //
                         @QueryParam("name") String name, //
                         @QueryParam("projectid") String projectId, //
                         InputStream inputStream //
   ) throws VirtualFileSystemException, JsonException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      compile(vfs, projectId, inputStream, name);
   }

   /*
   * Get list of JAR files includes attributes from META-INF/MANIFEST.MF
   */
   @GET
   @Path("/jars")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getAvailableJarLibraries() throws IOException
   {
      return Response.ok().entity(JarsCollector.collect()).build();
   }

   private Class<?> compile(VirtualFileSystem vfs, String projectId, String fileId) throws VirtualFileSystemException,
      JsonException
   {
      ContentStream source = vfs.getContent(fileId);
      InputStream sourceStream = source.getStream();
      String fileName = source.getFileName();

      Class<?> result;
      try
      {
         result = compile(vfs, projectId, sourceStream, fileName);
      }
      finally
      {
         try
         {
            sourceStream.close();
         }
         catch (IOException ignored)
         {
         }
      }

      return result;
   }

   private Class<?> compile(VirtualFileSystem vfs, String projectId, InputStream source, String fileName)
      throws VirtualFileSystemException, JsonException
   {
      ExtendedGroovyClassLoader cl;
      SourceFile[] files;
      GroovyClassPath classPath;

      if (projectId != null && (classPath = GroovyClassPathHelper.getGroovyClassPath(projectId, vfs)) != null)
      {
         try
         {
            cl = groovyClassLoaderProvider.getGroovyClassLoader(GroovyClassPathHelper.getSourceFolders(classPath));
         }
         catch (MalformedURLException e)
         {
            // One or more items in class path may have invalid URL,
            // e.g. not supported protocol or not invalid string
            throw new RuntimeException(e.getMessage(), e);
         }

         files = GroovyClassPathHelper.getSourceFiles(classPath);
      }
      else
      {
         cl = groovyClassLoaderProvider.getGroovyClassLoader();
         files = new SourceFile[0];
      }

      Class<?> result = cl.parseClass(source, fileName, files);

      return result;
   }
}
