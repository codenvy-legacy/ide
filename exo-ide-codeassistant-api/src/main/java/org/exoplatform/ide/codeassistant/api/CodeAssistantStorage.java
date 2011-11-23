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
package org.exoplatform.ide.codeassistant.api;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 22, 2011 11:58:42 AM evgen $
 *
 */
public interface CodeAssistantStorage
{
   public enum JavaType {
      CLASS, INTERFACE, ANNOTATION
   }

   public enum Where {
      FQN("fqn"), CLASSNAME("className");

      private final String where;

      Where(String where)
      {
         this.where = where;
      }

      public String getWhere()
      {
         return where;
      }
   }

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} or null if Class object not found.
    * @throws CodeAssistantException
    */
   TypeInfo getClassByFQN(String fqn) throws CodeAssistantException;

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} 
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> findFQNsByClassName(String className) throws CodeAssistantException;

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
    * @param where the string that indicate where find (must be "className" or "fqn"), if null search do for class names
    */
   List<ShortTypeInfo> findFQNsByPrefix(String prefix, Where where) throws CodeAssistantException;

   /**
    * Find all classes or annotations or interfaces
    *   
    * @param type the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION) 
    * @param prefix optional parameter that matching first letter of type name
    * @return Returns set of FQNs matched to class type
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> findByType(JavaType type, String prefix) throws CodeAssistantException;

   /**
    * Find JavaDoc for FQN
    * @param fqn of type
    * @return string JavaDoc
    * @throws CodeAssistantException
    */
   public String getClassDoc(String fqn) throws CodeAssistantException;
}
