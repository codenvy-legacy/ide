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
package org.exoplatform.ide.extension.java.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorageClient;
import org.exoplatform.ide.codeassistant.jvm.bean.TypesListBean;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
import org.exoplatform.ide.extension.maven.server.BuilderClient;
import org.exoplatform.ide.extension.maven.server.BuilderException;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.extension.maven.shared.BuildStatus.Status;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * Service provide Autocomplete of source code is also known as code completion feature. In a source code editor autocomplete is
 * greatly simplified by the regular structure of the programming languages. At current moment implemented the search class FQN,
 * by Simple Class Name and a prefix (the lead characters in the name of the package or class).
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RestCodeAssistantJava Mar 30, 2011 10:40:38 AM evgen $
 * 
 */
@Path("/ide/code-assistant/java")
public class RestCodeAssistantJava
{

   @Inject
   private JavaCodeAssistant codeAssistant;

   @Inject
   private BuilderClient builderClient;

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @Inject
   private CodeAssistantStorageClient storageClient;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RestCodeAssistantJava.class);

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo}
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("/class-description")
   @Produces(MediaType.APPLICATION_JSON)
   public TypeInfo getClassByFQN(@QueryParam("fqn") String fqn, @QueryParam("projectid") String projectId,
      @QueryParam("vfsid") String vfsId) throws CodeAssistantException, VirtualFileSystemException
   {
      TypeInfo info = codeAssistant.getClassByFQN(fqn, projectId, vfsId);

      if (info != null)
         return info;

      if (LOG.isDebugEnabled())
         LOG.error("Class info for " + fqn + " not found");
      return null;
   }

   /**
    * Returns the class objects associated with the class or interface with the given simple name prefix.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo}
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("/classes-by-prefix")
   @Produces(MediaType.APPLICATION_JSON)
   public List<TypeInfo> getTypesByNamePrefix(@QueryParam("prefix") String namePrefix,
      @QueryParam("projectid") String projectId, @QueryParam("vfsid") String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      List<TypeInfo> infos = codeAssistant.getTypeInfoByNamePrefix(namePrefix, projectId, vfsId);

      if (infos != null)
         return infos;

      if (LOG.isDebugEnabled())
         LOG.error("Class with name prefix '" + namePrefix + "' not found");
      return null;
   }

   /**
    * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name) Example : if prefix = "java.util.c"
    * set must content: { java.util.Comparator<T> java.util.Calendar java.util.Collection<E> java.util.Collections
    * java.util.ConcurrentModificationException java.util.Currency java.util.concurrent java.util.concurrent.atomic
    * java.util.concurrent.locks }
    * 
    * @param prefix the string for matching FQNs
    * @param where the string that indicate where find (must be "className" or "fqn")
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("/find-by-prefix/{prefix}")
   @Produces(MediaType.APPLICATION_JSON)
   public TypesList findFQNsByPrefix(@PathParam("prefix") String prefix, @QueryParam("where") String where,
      @QueryParam("projectid") String projectId, @QueryParam("vfsid") String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      if (projectId == null)
         throw new InvalidArgumentException("'projectid' parameter is null.");

      if ("className".equalsIgnoreCase(where))
      {
         return new TypesListBean(codeAssistant.getTypesByNamePrefix(prefix, projectId, vfsId));
      }

      return new TypesListBean(codeAssistant.getTypesByFqnPrefix(prefix, projectId, vfsId));

   }

   /**
    * Find all classes or annotations or interfaces
    * 
    * @param type the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION)
    * @param prefix optional parameter that matching first letter of type name
    * @return Returns set of FQNs matched to class type
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("/find-by-type/{type}")
   @Produces(MediaType.APPLICATION_JSON)
   public TypesList findByType(@PathParam("type") String type, @QueryParam("prefix") String prefix,
      @QueryParam("projectid") String projectId, @QueryParam("vfsid") String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      if (projectId == null)
         throw new InvalidArgumentException("'projectid' parameter is null.");
      return new TypesListBean(codeAssistant.getByType(JavaType.valueOf(type.toUpperCase()), prefix, projectId, vfsId));
   }

   @GET
   @Path("/class-doc")
   @Produces(MediaType.TEXT_HTML)
   public String getClassDoc(@QueryParam("fqn") String fqn, @QueryParam("projectid") String projectId,
      @QueryParam("vfsid") String vfsId, @QueryParam("isclass") @DefaultValue("true") boolean isClass)
      throws CodeAssistantException, VirtualFileSystemException
   {

      if (projectId == null)
         throw new InvalidArgumentException("'projectid' parameter is null.");
      if (isClass)
      {
         String classJavaDoc = codeAssistant.getClassJavaDoc(fqn, projectId, vfsId);
         if (classJavaDoc == null)
            classJavaDoc = "JavaDoc not found";
         return "<html><head></head><body style=\"font-family: monospace;font-size: 12px;\">" + classJavaDoc
            + "</body></html>";
      }
      else
      {
         String memberJavaDoc = codeAssistant.getMemberJavaDoc(fqn, projectId, vfsId);
         if (memberJavaDoc == null)
            memberJavaDoc = "JavaDoc not found";
         return "<html><head></head><body style=\"font-family: monospace;font-size: 12px;\">" + memberJavaDoc
            + "</body></html>";
      }
   }

   /**
    * Find all classes in package
    *
    * @param fileId current file id (editing class)
    * @param vfsId id of virtual file system
    * @param projectId
    * @return
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("/find-in-package")
   @Produces(MediaType.APPLICATION_JSON)
   public TypesList findClassesInPackage(@QueryParam("fileid") String fileId, @QueryParam("vfsid") String vfsId,
      @QueryParam("projectid") String projectId) throws CodeAssistantException, VirtualFileSystemException
   {
      if (projectId == null)
         throw new InvalidArgumentException("'projectid' parameter is null.");
      return new TypesListBean(codeAssistant.getClassesFromProject(fileId, projectId, vfsId));
   }

   /**
    * Get List of Type info by array of FQNs
    * 
    * @param vfsId
    * @param projectId
    * @param fqns for types
    * @return List of types info
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    */
   @POST
   @Path("/types-by-fqns")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public List<TypeInfo> getTypesDescriptionsList(@QueryParam("vfsid") String vfsId,
      @QueryParam("projectid") String projectId, String[] fqns) throws CodeAssistantException,
      VirtualFileSystemException
   {
      List<TypeInfo> types = new ArrayList<TypeInfo>();
      for (String fqn : fqns)
      {
         types.add(codeAssistant.getClassByFQN(fqn, projectId, vfsId));
      }

      return types;
   }

   /**
    * Get list of package names
    * 
    * @param vfsId
    * @param projectId
    * @param packagePrefix
    * @return
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("/find-packages")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getPackages(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
      @QueryParam("package") String packagePrefix) throws CodeAssistantException, VirtualFileSystemException
   {
      if (projectId == null)
         throw new InvalidArgumentException("'projectid' parameter is null.");
      return codeAssistant.getPackagesByPrefix(packagePrefix, projectId, vfsId);
   }

   /**
    * Get list of all package names in project
    * 
    * @param vfsId
    * @param projectId
    * @param packagePrefix
    * @return
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    */
   @GET
   @Path("/get-packages")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getAllPackages(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      if (projectId == null)
         throw new InvalidArgumentException("'projectid' parameter is null.");
      return codeAssistant.getAllPackages(projectId, vfsId);
   }

   /**
    * Get list of all package names in project
    * 
    * @param vfsId
    * @param projectId
    * @param packagePrefix
    * @return
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException
    * @throws BuilderException 
    * @throws IOException 
    * @throws JsonException 
    */
   @GET
   @Path("/update-dependencies")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> updateDependency(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId)
      throws CodeAssistantException, VirtualFileSystemException, IOException, BuilderException, JsonException
   {
      final VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      Item item = vfs.getItem(projectId, PropertyFilter.ALL_FILTER);
      Project project = null; 
      if (item.getItemType().equals(ItemType.PROJECT))
         project = (Project)item;
      else 
      {
         LOG.warn("Getting item not a project ");
         throw new CodeAssistantException(500, "Getting item not a project");
      }
      String buildId = builderClient.dependenciesList(vfs, projectId);
      String dependencies = null;
      BuildStatus buildStatus = waitBuildTaskFinish(buildId);
      if (Status.SUCCESSFUL == buildStatus.getStatus())
      {
         if (buildStatus.getDownloadUrl() != null && !buildStatus.getDownloadUrl().isEmpty())
         {
            dependencies = makeRequest(buildStatus.getDownloadUrl());
            if (project.hasProperty("exoide:classpath") && project.getPropertyValue("exoide:classpath").equals(dependencies))
            {
               return codeAssistant.getAllPackages(project, vfs);
            }
            List<Property> properties =
               Arrays.<Property> asList(new PropertyImpl("exoide:classpath", dependencies), new PropertyImpl(
                  "exoide:build_error", (String)null));
            vfs.updateItem(projectId, properties, null);
         }
      }
      else
      {
         LOG.warn("Build failed, exit code: " + buildStatus.getExitCode() + ", message: " + buildStatus.getError());
         throw new BuilderException(buildStatus.getExitCode(), buildStatus.getError(), "text/plain");
      }
      buildId = builderClient.dependenciesCopy(vfs, projectId, null);
      buildStatus = waitBuildTaskFinish(buildId);
      if (Status.FAILED == buildStatus.getStatus())
      {
         LOG.warn("Build failed, exit code: " + buildStatus.getExitCode() + ", message: " + buildStatus.getError());
         throw new BuilderException(buildStatus.getExitCode(), buildStatus.getError(), "text/plain");
      }
      String statusUrl = storageClient.updateTypeIndex(dependencies, buildStatus.getDownloadUrl());
      waitStorageTaskFinish(statusUrl);
      buildId = builderClient.dependenciesCopy(vfs, projectId, "sources");
      buildStatus = waitBuildTaskFinish(buildId);
      if (Status.FAILED == buildStatus.getStatus())
      {
         LOG.warn("Build failed, exit code: " + buildStatus.getExitCode() + ", message: " + buildStatus.getError());
         throw new BuilderException(buildStatus.getExitCode(), buildStatus.getError(), "text/plain");
      }
      statusUrl = storageClient.updateDockIndex(dependencies, buildStatus.getDownloadUrl());
      try
      {
         waitStorageTaskFinish(statusUrl);
      }
      catch (Exception e)//Ignore exception in case add javadoc
      {
         LOG.debug("Adding sources artifact fail : " + statusUrl, e );
      }

      return codeAssistant.getAllPackages(project, vfs);
   }

   /**
    * @param dependencies
    * @param buildStatus  
    * @return
    */
   private String makeRequest(String requestUrl)
   {
      HttpURLConnection http = null;
      String response = null;
      try
      {
         URL url = new URL(requestUrl);
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
         {
            LOG.error("Can't download dependency list from: " + requestUrl);
         }
         InputStream data = http.getInputStream();
         response = readBody(data, http.getContentLength());
      }
      catch (MalformedURLException e)
      {
         LOG.error("Invalid URL", e);
      }
      catch (IOException e)
      {
         LOG.error("Error", e);
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
      return response;
   }

   /**
    * @param projectId
    * @param vfs
    * @param buildId 
    * @throws IOException
    * @throws BuilderException
    * @throws VirtualFileSystemException
    * @throws UnsupportedEncodingException
    * @throws MalformedURLException
    * @throws JsonException 
    */
   private BuildStatus waitBuildTaskFinish(String buildId) throws IOException, BuilderException,
      VirtualFileSystemException, UnsupportedEncodingException, MalformedURLException, JsonException
   {
      String status;
      BuildStatusBean buildStatus;
      final int sleepTime = 2000;
      JsonParser parser = new JsonParser();
      boolean isDone = false;
      do
      {
         try
         {
            Thread.sleep(sleepTime);
         }
         catch (InterruptedException ignored)
         {
         }
         status = builderClient.status(buildId);
         parser.parse(new ByteArrayInputStream(status.getBytes("UTF-8")));
         buildStatus = ObjectBuilder.createObject(BuildStatusBean.class, parser.getJsonObject());
         if (Status.IN_PROGRESS != buildStatus.getStatus())
         {
            isDone = true;
         }
      }
      while (!isDone);
      return buildStatus;
   }

   /**
    * @param projectId
    * @param vfs
    * @param buildId 
    * @throws IOException
    * @throws BuilderException
    * @throws VirtualFileSystemException
    * @throws UnsupportedEncodingException
    * @throws MalformedURLException
    * @throws JsonException 
    */
   private BuildStatus waitStorageTaskFinish(String buildId) throws IOException, BuilderException,
      VirtualFileSystemException, UnsupportedEncodingException, MalformedURLException, JsonException
   {
      String status;
      BuildStatusBean buildStatus;
      final int sleepTime = 2000;
      JsonParser parser = new JsonParser();
      boolean isDone = false;
      do
      {
         try
         {
            Thread.sleep(sleepTime);
         }
         catch (InterruptedException ignored)
         {
         }
         status = storageClient.status(buildId);
         parser.parse(new ByteArrayInputStream(status.getBytes("UTF-8")));
         buildStatus = ObjectBuilder.createObject(BuildStatusBean.class, parser.getJsonObject());
         if (Status.IN_PROGRESS != buildStatus.getStatus())
         {
            isDone = true;
         }
      }
      while (!isDone);
      return buildStatus;
   }

   private String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         int off = 0;
         int i;
         while ((i = input.read(b, off, contentLength - off)) > 0)
         {
            off += i;
         }
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int i;
         while ((i = input.read(buf)) != -1)
         {
            bout.write(buf, 0, i);
         }
         body = bout.toString();
      }
      return body;
   }

}
