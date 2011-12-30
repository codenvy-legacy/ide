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

import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.List;

/**
 * 
 */
public interface CodeAssistantStorage
{
   /**
    * Find all annotations by prefix.
    * 
    * @param prefix
    *           matching first letter of type name if it set to null service
    *           MUST return all founded annotations
    * @return {@link List<ShortTypeInfo>}
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException;

   /**
    * Find all classes by prefix.
    * 
    * @param prefix
    *           matching first letter of type name if it set to null service
    *           MUST return all founded annotations
    * @return {@link List<ShortTypeInfo>}
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException;

   /**
    * Find JavaDoc for FQN
    * 
    * @param fqn
    *           of type
    * @return string Java doc
    * @throws CodeAssistantException
    *            if Java doc not found
    */
   String getClassJavaDoc(String fqn) throws CodeAssistantException;

   /**
    * Find all interfaces by prefix.
    * 
    * @param prefix
    *           matching first letter of type name if it set to null service
    *           MUST return all founded annotations
    * @return {@link List<ShortTypeInfo>}
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> getIntefaces(String prefix) throws CodeAssistantException;

   /**
    * Find JavaDoc for Java Class member FQN
    * 
    * @param fqn
    *           of type
    * @return string Java doc
    * @throws CodeAssistantException
    *            if Java doc not found
    */
   String getMemberJavaDoc(String fqn) throws CodeAssistantException;

   /**
    * Returns the Class object associated with the class or interface with the
    * given string Full Qualified Name.
    * 
    * @param fqn
    *           the Full Qualified Name
    * @return {@link TypeInfo} or null if Class object not found.
    * @throws CodeAssistantException
    */
   TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException;

   /**
    * sets of {@link ShortTypeInfo} matched to prefix (means FQN begin on
    * {fqnPrefix} ) Example : if prefix = "java.util.c" set must content: {
    * java.util.Comparator<T> java.util.Calendar java.util.Collection<E>
    * java.util.Collections java.util.ConcurrentModificationException
    * java.util.Currency java.util.concurrent java.util.concurrent.atomic
    * java.util.concurrent.locks }
    * 
    * @param fqnPrefix
    *           the string for matching FQNs
    * @return {@link List<ShortTypeInfo>}
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix) throws CodeAssistantException;

   /**
    * 
    * Return sets of {@link ShortTypeInfo} object associated with the class or
    * interface matched to name. (means Class simple name begin on {namePrefix})
    * Example: if name == "Node" result can content information about: -
    * javax.xml.soap.Node - com.google.gwt.xml.client.Node - org.w3c.dom.Node -
    * org.w3c.dom.traversal.NodeFilter - org.w3c.dom.traversal.NodeIterator
    * 
    * @param namePrefix
    *           the string for matching FQNs
    * @return {@link List<ShortTypeInfo>}
    * @throws CodeAssistantException
    */
   List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix) throws CodeAssistantException;
}
