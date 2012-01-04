/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage.extractors;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QDoxJavaDocExtractor
{

   /**
    * <p>
    * Method gets InputStream of jar file, and return Map of javaDocs.
    * </p>
    * <ul>
    * <li>Key of a result map is fqn of member (class, field, constructor,
    * method, etc).</li>
    * <li>Value of a result map is javaDoc comment for member with this fqn.</li>
    * </ul>
    * <p>
    * Member's fqn has next format:
    * </p>
    * <ul>
    * <li>If this member is <b>class</b>, then fqn equals full qualified name of
    * class. For Example: java.util.HashMap, java.io.InputStream, ....</li>
    * <li>If this member is <b>inner class</b>, then fqn contains full qualified
    * name of declared class and name of inner class, separated by $. For
    * example: java.util.HashMap$KeySet</li>
    * <li>If this member is <b>field</b>, then fqn contains full qualified name
    * of declared class and field name, separated by #. For example:
    * java.lang.Integer#MAX_VALUE, etc.</li>
    * <li>If this member is <b>method</b>, then fqn contains full qualified name
    * of declared class and method description, separated by #. Method
    * description contains from method name, and list of parameter types in
    * brackets. For example:
    * java.util.HashMap#put(java.lang.Object,java.lang.Object),
    * java.util.HashMap#clear(), etc.</li>
    * <li>If this member is <b>constructor</b>, then it's contains like
    * <b>method</b> but without method name. For example:
    * java.lang.HashMap(int,float), java.lang.HashMap(int)
    * </ul>
    * 
    * TODO update with default format definition
    * <a>http://www.w3.org/TR/REC-xml-names/#NT-QName</a>
    * <p>
    * <ul>
    * <li><b>SIMPLE_NAME</b> = [a-zA-Z][a-zA-Z0-9_]*</li>
    * <li><b>PACKAGE</b> = SIMPLE_NAME(.SIMPLE_NAME)*</li>
    * <li><b>CLASS_NAME</b> = SIMPLE_NAME | SIMPLE_NAME$SIMPLE_NAME</li>
    * <li><b>CLASS_FQN</b> = (PACKAGE.)*CLASS_NAME</li>
    * <li><b>FIELD_FQN</b> = (PACKAGE.)*CLASS_NAME#SIMPLE_NAME</li>
    * <li><b>METHOD_FQN</b> = (PACKAGE.)*CLASS_NAME#SIMPLE_NAME(PARAMS)</li>
    * <li><b>CONSTRUCTOR_FQN</b> = (PACKAGE.)*CLASS_NAME(PARAMS)</li>
    * <li><b>PARAMS</b> = empty string | PARAM(,PARAM)*</li>
    * <li><b>PARAM</b> = SIMPLE_PARAM | OBJECT_PARAM</li>
    * <li><b>SIMPLE_PARAM</b> = void | byte | char | boolean | short | int |
    * long | float | double</li>
    * <li><b>OBJECT_PARAM</b> = CLASS_FQN | CLASS_FQN < GENERIC > | GENERIC</li>
    * <li><b>GENERIC</b> = GENERIC_BASE | GENERIC_BASE extends CLASS_FQN</li>
    * <li><b>GENERIC_BASE</b> = SIMPLE_NAME | ?</li>
    * </ul>
    * </p>
    * 
    * <p>
    * If member not contains java doc, then result will not contains it's member
    * fqn.
    * </p>
    * 
    * @param sourceZipStream
    *           stream of jar file with sources
    * @return map with key - member fqn, and value - javaDoc comment
    */
   public static Map<String, String> extract(InputStream sourceZipStream)
   {
      // TODO to be continued...
      return new HashMap<String, String>();
   }

}
