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


/**
 * 
 * Member is reflects identifying information about a single member (a field or
 * a method) or a constructor.
 * 
 * @see FieldInfo
 * @see MethodInfo
 * 
 */
public interface Member
{

   /**
    * @return the modifiers
    */
   int getModifiers();

   /**
    * @return the name
    */
   String getName();

   /**
    * @param modifiers
    *           the modifiers to set
    */
   void setModifiers(int modifiers);

   /**
    * @param name
    *           the name to set
    */
   void setName(String name);

}