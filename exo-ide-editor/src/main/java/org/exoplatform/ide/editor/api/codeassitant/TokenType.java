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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:45:20 PM evgen $
 *
 */
public enum TokenType {
   CLASS, METHOD, FIELD, ANNOTATION, INTERFACE, ARRAY, ENUM, CONSTRUCTOR, KEYWORD, TEMPLATE, VARIABLE, FUNCTION, 
   /** Property type for JSON */
   PROPERTY,
   
   /**
    * HTML or XML tag.
    */
   TAG,
   
   /**
    * HTML or XML attribute; 
    */
   ATTRIBUTE,
   CDATA,

   /** Property type for JavaScript */
   BLOCK, 
   
   /** Property type for Groovy code */
   GROOVY_TAG, PACKAGE, IMPORT, PARAMETER, TYPE,
   
   /** Property type for Java code */
   JSP_TAG,
   
   /** Property type for Ruby code **/
   ROOT, MODULE, LOCAL_VARIABLE, GLOBAL_VARIABLE, CLASS_VARIABLE, INSTANCE_VARIABLE, CONSTANT,
   
   /** Propperty type for Php code **/
   PHP_TAG, CLASS_CONSTANT, NAMESPACE;
}
