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
package org.exoplatform.ide.codeassistant.framework.server.api;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistantStorage Feb 8, 2011 2:33:41 PM evgen $
 *
 */
public interface CodeAssistant
{

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} 
    * @throws CodeAssistantException
    */
   TypeInfo getClassByFQN(String fqn) throws CodeAssistantException;

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * (Search in project)
    * 
    * @param fqn the Full Qualified Name
    * @param location of file
    * @return {@link TypeInfo}
    * @throws CodeAssistantException
    */
   TypeInfo getClassByFQNFromProject(String fqn, String location) throws CodeAssistantException;

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} 
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> findFQNsByClassName(String className) throws CodeAssistantException;

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * (Search in project)
    * 
    * @param className
    * @param location of file
    * @return {@link ShortTypeInfo}
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> findFQNsByClassNameInProject(String className, String location)
      throws CodeAssistantException;

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
   List<ShortTypeInfo> findFQNsByPrefix(String prefix, String where) throws CodeAssistantException;

   /**
    * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name)<br>
    * Warning: <b>This method search only in class names!</b>
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
    */
   List<ShortTypeInfo> findFQNsByPrefixInProject(String prefix, String location) throws CodeAssistantException;

   /**
    * Find all classes or annotations or interfaces
    *   
    * @param type the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION) 
    * @param prefix optional parameter that matching first letter of type name
    * @return Returns set of FQNs matched to class type
    * @throws CodeAssistantException
    */
   public ShortTypeInfo[] findByType(String type, String prefix) throws CodeAssistantException;

   /**
    * Find JavaDoc for FQN
    * @param fqn of type
    * @return string JavaDoc
    * @throws CodeAssistantException
    */
   public String getClassDoc(String fqn) throws CodeAssistantException;

}