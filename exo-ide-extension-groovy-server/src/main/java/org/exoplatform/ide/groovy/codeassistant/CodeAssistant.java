/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy.codeassistant;

import org.exoplatform.ide.groovy.codeassistant.bean.ShortTypeInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;

import java.util.List;

/**
 * Service provide Autocomplete of source code is also known as code completion feature. 
 * In a source code editor autocomplete is greatly simplified by the regular structure 
 * of the programming languages. 
 * At current moment implemented the search class FQN,
 * by Simple Class Name and a prefix (the lead characters in the name of the package or class).
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface CodeAssistant
{

   /**
    * Returns the Class object associated with the class or interface with the given string name.
    * 
    * @param fqn the Full Qualified Name
    * @return {@link TypeInfo} 
    * @throws ClassNotFoundException
    */
   TypeInfo getClassByFQN(String fqn) throws CodeAssistantException;
   
   /**
    * Returns set of FQNs matched to prefix (means FQN begin on {prefix})
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
    * 
    */
   ShortTypeInfo[] findFQNsByPrefix(String prefix) throws CodeAssistantException;
   
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
    */
   ShortTypeInfo[] findFQNsByClassName(String className) throws CodeAssistantException;

}
