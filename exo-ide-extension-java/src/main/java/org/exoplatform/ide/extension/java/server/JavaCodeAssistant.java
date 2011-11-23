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

import org.exoplatform.ide.codeassistant.api.CodeAssistant;
import org.exoplatform.ide.codeassistant.api.CodeAssistantException;
import org.exoplatform.ide.codeassistant.api.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.JavaType;
import org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.Where;
import org.exoplatform.ide.codeassistant.api.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.api.TypeInfo;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 22, 2011 4:53:15 PM evgen $
 *
 */
public class JavaCodeAssistant extends CodeAssistant
{

   /**
    * Default Maven 'sourceDirectory' value
    */
   private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

   private VirtualFileSystemRegistry vfsRegistry;

   /**
    * @param storage
    */
   public JavaCodeAssistant(CodeAssistantStorage storage, VirtualFileSystemRegistry vfsRegistry)
   {
      super(storage);
      this.vfsRegistry = vfsRegistry;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findClassByFQN(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   protected TypeInfo findClassByFQN(String fqn, String projectId, String vfsId)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByClassNameInProject(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   protected List<ShortTypeInfo> findFQNsByClassNameInProject(String className, String projectId, String vfsId)
      throws CodeAssistantException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByPrefixInProject(java.lang.String, org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.Where, java.lang.String, java.lang.String)
    */
   @Override
   protected List<ShortTypeInfo> findFQNsByPrefixInProject(String prefix, Where where, String projectId, String vfsId)
      throws CodeAssistantException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findByTypeInProject(org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.JavaType, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   protected List<ShortTypeInfo> findByTypeInProject(JavaType type, String prefix, String projectId, String vfsId)
      throws CodeAssistantException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#getJavaDocFromProject(java.lang.String)
    */
   @Override
   protected String getJavaDocFromProject(String fqn, String projectId, String vfsId) throws CodeAssistantException
   {
      throw new CodeAssistantException(404, "Not found");
   }

   /**
    * @throws VirtualFileSystemException 
    * @throws PermissionDeniedException 
    * @throws ItemNotFoundException 
    * @throws CodeAssistantException 
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findClassesInPackage(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> findClassesInPackage(String fileId, String projectId, String vfsId)
      throws ItemNotFoundException, PermissionDeniedException, VirtualFileSystemException, CodeAssistantException
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
   private List<ShortTypeInfo> findClassesInPackage(File file, Project project, VirtualFileSystem vfs)
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
