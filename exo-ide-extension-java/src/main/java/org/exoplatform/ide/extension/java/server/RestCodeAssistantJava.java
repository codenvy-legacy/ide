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

import org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant;
import org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistantException;
import org.exoplatform.ide.codeassistant.framework.server.api.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.api.TypeInfo;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * Service provide Autocomplete of source code is also known as code completion feature. 
 * In a source code editor autocomplete is greatly simplified by the regular structure 
 * of the programming languages. 
 * At current moment implemented the search class FQN,
 * by Simple Class Name and a prefix (the lead characters in the name of the package or class).
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RestCodeAssistantJava Mar 30, 2011 10:40:38 AM evgen $
 *
 */
@Path("/ide/code-assistant/java")
public class RestCodeAssistantJava
{

   /**
    * Default Maven 'sourceDirectory' value
    */
   private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

   private final CodeAssistant codeAssistantStorage;

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RestCodeAssistantJava.class);

   public RestCodeAssistantJava(CodeAssistant codeAssistantStorage)
   {
      this.codeAssistantStorage = codeAssistantStorage;
   }

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} 
    * @throws CodeAssistantException
    */
   @GET
   @Path("/class-description")
   @Produces(MediaType.APPLICATION_JSON)
   public TypeInfo getClassByFQN(@Context UriInfo uriInfo, @QueryParam("fqn") String fqn,
      @HeaderParam("location") String location) throws CodeAssistantException
   {
      TypeInfo info = codeAssistantStorage.getClassByFQN(fqn);

      if (info != null)
         return info;

      if (LOG.isDebugEnabled())
         LOG.error("Class info for " + fqn + " not found");
      return null;
   }

   /**
    * Returns set of FQNs matched to Class name (means FQN end on {className})
    * Example :
    * if className = "String"
    * set must content
    * {
    *  java.lang.String
    *  java.lang.StringBuilder
    *  java.lang.StringBuffer
    *  java.lang.StringIndexOutOfBoundsException
    *  java.util.StringTokenizer
    *  ....
    * }
    * @param className the string for matching FQNs 
    * @return
    * @throws Exception 
    * */
   @GET
   @Path("/find")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> findFQNsByClassName(@Context UriInfo uriInfo, @QueryParam("class") String className,
      @HeaderParam("location") String location) throws CodeAssistantException
   {
      List<ShortTypeInfo> info = codeAssistantStorage.findFQNsByClassName(className);
      return info;
   }

   /**
    * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name)
    * Example :
    * if prefix = "java.util.c"
    * set must content:
    *  {
    *   java.util.Comparator<T>
    *   java.util.Calendar
    *   java.util.Collection<E>
    *   java.util.Collections
    *   java.util.ConcurrentModificationException
    *   java.util.Currency
    *   java.util.concurrent
    *   java.util.concurrent.atomic
    *   java.util.concurrent.locks
    *  }
    * 
    * @param prefix the string for matching FQNs
    * @param where the string that indicate where find (must be "className" or "fqn")
    */
   @GET
   @Path("/find-by-prefix/{prefix}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> findFQNsByPrefix(@Context UriInfo uriInfo, @PathParam("prefix") String prefix,
      @QueryParam("where") String where, @HeaderParam("location") String location) throws CodeAssistantException
   {
      List<ShortTypeInfo> info = codeAssistantStorage.findFQNsByPrefix(prefix, where);
      return info;
   }

   /**
    * Find all classes or annotations or interfaces
    *   
    * @param type the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION) 
    * @param prefix optional parameter that matching first letter of type name
    * @return Returns set of FQNs matched to class type
    * @throws CodeAssistantException
    */
   @GET
   @Path("/find-by-type/{type}")
   @Produces(MediaType.APPLICATION_JSON)
   public ShortTypeInfo[] findByType(@PathParam("type") String type, @QueryParam("prefix") String prefix)
      throws CodeAssistantException
   {
      return codeAssistantStorage.findByType(type, prefix);
   }

   @GET
   @Path("/class-doc")
   @Produces(MediaType.TEXT_HTML)
   public String getClassDoc(@QueryParam("fqn") String fqn) throws CodeAssistantException
   {
      return "<html><head></head><body style=\"font-family: monospace;font-size: 12px;\">"
         + codeAssistantStorage.getClassDoc(fqn) + "</body></html>";
   }

   /**
    * Find all classes in project
    * @param uriInfo
    * @param location
    * @return set of FQNs matched to project at file location 
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException 
    * @throws PermissionDeniedException 
    * @throws ItemNotFoundException 
    */
   @GET
   @Path("/find-in-package")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> findClassesInPackage(@QueryParam("fileid") String fileId,
      @QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      List<ShortTypeInfo> classNames = null;
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      Item item = vfs.getItem(fileId, PropertyFilter.ALL_FILTER);
      if (item.getItemType() != ItemType.FILE)
         throw new InvalidArgumentException("Unable find Classes. Item " + item.getName() + " is not a file. ");

      Item p = vfs.getItem(projectId, PropertyFilter.ALL_FILTER);

      Project project = null;
      if (p instanceof Project)
         project = (Project)p;
      else
         throw new InvalidArgumentException("Unable find Classes. Item " + p.getName() + " is not a project. ");

      classNames = findClassesInPackage((File)item, project, vfs);

      return classNames;
   }

   /**
    * Return word until first point like "ClassName" on file name "ClassName.java"
    * @param fileName
    * @return
    */
   private String getClassNameOnFileName(String fileName)
   {
      if (fileName != null)
         return fileName.substring(0, fileName.indexOf("."));

      return null;
   }

   /**
    * Return possible FQN like "org.exoplatform.example.ClassName" on file path "/org/exoplatform/example/ClassName.java"
    * @param fileName
    * @return
    */
   private String getFQNByFilePath(File file, Project project)
   {
      String sourceFolderPath = (String)project.getPropertyValue("sourceFolder");
      if (sourceFolderPath == null)
      {
         sourceFolderPath = DEFAULT_SOURCE_FOLDER;
      }
      String fqn = file.getPath().substring((project.getPath() + "/" + sourceFolderPath).length() + 1);
      // remove file extension from path like ".java" from path "org/exoplatform/example/ClassName.java"
      if (fqn.matches(".*[.][^/]*$"))
         fqn = fqn.substring(0, fqn.lastIndexOf("."));
      // replace "/" on "."
      fqn = fqn.replaceAll("/", ".");

      return fqn;
   }

   /**
    * Find classes in package 
    * @param fileId
    * @param vfsId 
    * @return
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException  
    * TODO move this method to Java project code assistant
    */
   public List<ShortTypeInfo> findClassesInPackage(File file, Project project, VirtualFileSystem vfs)
      throws CodeAssistantException, VirtualFileSystemException
   {
      List<ShortTypeInfo> classes = new ArrayList<ShortTypeInfo>();
      ItemList<Item> children = vfs.getChildren(file.getParentId(), -1, 0, PropertyFilter.ALL_FILTER);
      for (Item i : children.getItems())
      {
         if (i.getName().endsWith(".java"))
         {
            if (file.getId().equals(i.getId()) || ItemType.FILE != i.getItemType())
               continue;
            classes.add(new ShortTypeInfo(0, getClassNameOnFileName(i.getName()), getFQNByFilePath((File)i, project),
               "CLASS"));
         }
      }
      return classes;
   }

}
