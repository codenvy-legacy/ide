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
package org.exoplatform.ide.codeassistant.jvm.shared;

import java.util.List;

/**
 *
 */
public interface TypeInfo extends ShortTypeInfo
{

   /**
    * @return the fields
    */
   List<FieldInfo> getFields();

   /**
    * @return the interfaces
    */
   List<String> getInterfaces();

   /**
    * @return the methods
    */
   List<MethodInfo> getMethods();

   /**
    * @return the superClass
    */
   String getSuperClass();

   /**
    * @param fields
    *           the fields to set
    */
   void setFields(List<FieldInfo> fields);

   /**
    * @param interfaces
    *           the interfaces to set
    */
   void setInterfaces(List<String> interfaces);

   /**
    * @param methods
    *           the methods to set
    */
   void setMethods(List<MethodInfo> methods);

   /**
    * @param superClass
    *           the superClass to set
    */
   void setSuperClass(String superClass);

}