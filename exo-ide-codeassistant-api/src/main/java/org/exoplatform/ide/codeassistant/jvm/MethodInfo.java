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

import java.util.List;

/**
 * Represent information about class method. Can be transform to JSON. <code>
 *  {
 *     "generic": "public boolean java.lang.String.equals(java.lang.Object)",
 *     "exceptionTypes": [],
 *     "declaringClass": "java.lang.String",
 *     "name": "equals",
 *     "genericParameterTypes": "(java.lang.Object)",
 *     "modifiers": 1,
 *     "returnType": "boolean",
 *     "parameterTypes": "(Object)",
 *     "genericReturnType": "boolean"
 *   }
 * </code>
 * 
 */
public interface MethodInfo extends Member
{

   String getDeclaringClass();

   List<String> getExceptionTypes();

   /**
    * @return the parameterNames
    */
   List<String> getParameterNames();

   /**
    * @return the parameterTypes
    */
   List<String> getParameterTypes();

   String getReturnType();

   /**
    * @return the isConstructor
    */
   boolean isConstructor();

   /**
    * @param isConstructor
    *           the isConstructor to set
    */
   void setConstructor(boolean isConstructor);

   void setDeclaringClass(String declaringClass);

   void setExceptionTypes(List<String> exceptionTypes);

   /**
    * @param parameterNames
    *           the parameterNames to set
    */
   void setParameterNames(List<String> parameterNames);

   /**
    * @param parameterTypes
    *           the parameterTypes to set
    */
   void setParameterTypes(List<String> parameterTypes);

   void setReturnType(String returnType);

}