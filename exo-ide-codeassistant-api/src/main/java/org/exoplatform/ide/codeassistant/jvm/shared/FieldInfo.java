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
 * Represent information about class field. Can be transform to JSON. Example of
 * JSON: <code>
 * {
 * "declaringClass": "java.lang.String",
 * "name": "CASE_INSENSITIVE_ORDER",
 * "modifiers": 25,
 * "type": "Comparator"
 * }
 * </code>
 * 
 * 
 */
public interface FieldInfo extends Member
{

   String getDeclaringClass();

   String getType();

   void setDeclaringClass(String declaringClass);

   void setType(String type);

}