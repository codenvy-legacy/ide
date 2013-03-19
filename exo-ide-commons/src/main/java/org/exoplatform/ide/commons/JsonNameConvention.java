/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.commons;

/**
 * Abstraction to provide name transformation between JSON and Java names. It helps correct translate JSON names to
 * correct name of Java fields or methods, e.g translate Java camel-case name to lowercase JSON names with '-' or '_'
 * separator. Pass implementation of this interface to methods of {@link JsonHelper} to get required behaviour whe
 * serialize or deserialize objects to|from JSON.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see JsonNameConventions
 * @see NameConventionJsonParser
 * @see NameConventionJsonWriter
 */
public interface JsonNameConvention
{
   /**
    * Translate Java field name to JSON name, e.g. 'userName' -> 'user_name'
    *
    * @param javaName
    *    Java field name
    * @return JSON name
    */
   String toJsonName(String javaName);

   /**
    * Translate JSON name to Java field name, e.g. 'user_name' -> 'userName'
    *
    * @param jsonName
    *    JSON name
    * @return Java field name
    */
   String toJavaName(String jsonName);
}
