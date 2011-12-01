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
package org.exoplatform.ide.codeassistant.jvm;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for JVM based programming languages(Java, Groovy) codeassitant.
 * Class contains some basic methods for using {@link CodeAssistantStorage}.
 * All abstract methods is languages and project specific(Java, Groovy). 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistantStorage Feb 8, 2011 2:33:41 PM evgen $
 *
 */
public abstract class CodeAssistant
{

   protected CodeAssistantStorage storage;

   /**
    * @param storage
    */
   public CodeAssistant(CodeAssistantStorage storage)
   {
      this.storage = storage;
   }

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @param projectId of current project
    * @param vfsId Id of VirtualFileSystem
    * @return {@link TypeInfo}
    * @throws CodeAssistantException
    */
   public TypeInfo getClassByFQN(String fqn, String projectId, String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      TypeInfo clazz = storage.getTypeByFqn(fqn);
      if (clazz != null)
         return clazz;
      else
         return getClassByFqnFromProject(fqn, projectId, vfsId);
   }

   /**
    * Search for Java type in project. 
    * 
    * @param fqn the Full Qualified Name
    * @param projectId of current project
    * @param vfsId Id of VirtualFileSystem
    * @return {@link TypeInfo} of null if JavaType not found.
    */
   protected abstract TypeInfo getClassByFqnFromProject(String fqn, String projectId, String vfsId)
      throws VirtualFileSystemException, CodeAssistantException;

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * @param className Name of the class, interface, enum or annotation
    * @param projectId Id of current project
    * @param vfsId If of the VitrualFileSystem
    * @return list of {@link ShortTypeInfo}
    * @throws CodeAssistantException
    * @throws VirtualFileSystemException 
    */
   public List<ShortTypeInfo> getTypesByNamePrefix(String className, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();
      List<ShortTypeInfo> list = storage.getTypesByNamePrefix(className);
      if (list != null)
         result.addAll(list);

      list = getTypesByNamePrefixFromProject(className, projectId, vfsId);
      if (list != null)
         result.addAll(list);
      return result;
   }

   /**
    * Search for Java type in project 
   @param className Name of the class, interface, enum or annotation
    * @param projectId Id of current project
    * @param vfsId If of the VitrualFileSystem
    * @return list of {@link ShortTypeInfo}
    * @throws CodeAssistantException
    */
   protected abstract List<ShortTypeInfo> getTypesByNamePrefixFromProject(String className, String projectId,
      String vfsId) throws CodeAssistantException, VirtualFileSystemException;

   /**
    * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name)<br>
    * <br>
    * Example :
    * if prefix = "java.util.c"
    * set must content:
    * <pre>
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
    * </pre>
    * @param prefix the string for matching FQNs
    * @param where the string that indicate where find (must be "className" or "fqn")
    * @param projectId Id of the project
    * @param vfsId Id of the VirtualFileSystem
    * @return
    * @throws CodeAssistantException
    */
   public List<ShortTypeInfo> getTypesByFqnPrefix(String prefix, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();
      List<ShortTypeInfo> list = storage.getTypesByFqnPrefix(prefix);
      if (list != null)
         result.addAll(list);

      list = getTypesByFqnPrefixInProject(prefix, projectId, vfsId);
      if (list != null)
         result.addAll(list);
      return result;
   }

   /**
    * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name)<br>
    * @param prefix the string for matching FQNs
    * @param where Where find FQN or CLASSNAME
    * @param projectId Id of the project
    * @param vfsId Id of the VirtualFileSystem
    * @return
    * @throws CodeAssistantException
    */
   protected abstract List<ShortTypeInfo> getTypesByFqnPrefixInProject(String prefix, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException;

   /**
    * Find all classes or annotations or interfaces
    *   
    * @param type the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION) 
    * @param prefix optional parameter that matching first letter of type name
    * @return Returns list of FQNs matched to class type
    * @throws CodeAssistantException
    */
   public List<ShortTypeInfo> getByType(JavaType type, String prefix, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException
   {
      List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();
      switch (type)
      {
         case INTERFACE :
            List<ShortTypeInfo> intefaces = storage.getIntefaces(prefix);
            if (intefaces != null)
               result.addAll(intefaces);
            break;
         case ANNOTATION :
            List<ShortTypeInfo> annotations = storage.getAnnotations(prefix);
            if (annotations != null)
               result.addAll(annotations);
            break;
         case CLASS :
         case ENUM :
            List<ShortTypeInfo> classes = storage.getClasses(prefix);
            if (classes != null)
               result.addAll(classes);
            break;
         default :
            break;
      }
      List<ShortTypeInfo> tmp = getByTypeFromProject(type, prefix, projectId, vfsId);
      if (tmp != null)
         result.addAll(tmp);
      return result;
   }

   /**
    * Find all classes or annotations or interfaces in project
    *   
    * @param type the enum, that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION) 
    * @param prefix optional parameter that matching first letter of type name
    * @param projectId Id of the project
    * @param vfsId Id of the VirtualFileSystem
    * @return Returns list of FQNs matched to class type
    * @throws CodeAssistantException
    */
   protected abstract List<ShortTypeInfo> getByTypeFromProject(JavaType type, String prefix, String projectId,
      String vfsId) throws CodeAssistantException, VirtualFileSystemException;

   /**
    * Find JavaDoc for FQN
    * @param fqn of type
    * @return string JavaDoc
    * @throws CodeAssistantException
    */
   public String getClassJavaDoc(String fqn, String projectId, String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      try
      {
         return storage.getClassJavaDoc(fqn);
      }
      catch (CodeAssistantException e)
      {
         //java doc not found, try search in project
         return getClassJavaDocFromProject(fqn, projectId, vfsId);
      }
   }

   /**
    * Find JavaDoc for Java Class member FQN
    * @param fqn of type
    * @return string JavaDoc
    * @throws CodeAssistantException
    */
   public String getMemberJavaDoc(String fqn, String projectId, String vfsId) throws CodeAssistantException,
      VirtualFileSystemException
   {
      try
      {
         return storage.getMemberJavaDoc(fqn);
      }
      catch (CodeAssistantException e)
      {
         //java doc not found, try search in project
         return getMemberJavaDocFromProject(fqn, projectId, vfsId);
      }
   }

   /**
    * Find JavaDoc for FQN
    * @param fqn of type
    * @return string JavaDoc
    * @throws CodeAssistantException if Java doc not found.
    */
   protected abstract String getClassJavaDocFromProject(String fqn, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException;

   /**
    * Find JavaDoc for Java Class member FQN
    * @param fqn of type
    * @return string JavaDoc
    * @throws CodeAssistantException if Java doc not found.
    */
   protected abstract String getMemberJavaDocFromProject(String fqn, String projectId, String vfsId)
      throws CodeAssistantException, VirtualFileSystemException;

   /**
    * Find all nested Java types for fileId file
    * @param fileId Id of the file
    * @param projectId Id of project
    * @param vfsId {@link VirtualFileSystem} Id
    * @return List of nested Java types for file
    * @throws VirtualFileSystemException
    * @throws CodeAssistantException
    */
   public abstract List<ShortTypeInfo> getClassesFromProject(String fileId, String projectId, String vfsId)
      throws VirtualFileSystemException, CodeAssistantException;
}