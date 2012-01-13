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
package org.exoplatform.ide.codeassistant.framework.server.utils;

/**
 * Description of one source entry in groovy classpath.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 24, 2010 $
 * 
 */
public class GroovyClassPathEntry
{
   /**
    * Type of dependency. It may be "file"(file) or "dir" (directory).
    */
   private String kind;

   /**
    * Path to dependency.
    */
   private String path;

   /**
    * @return the kind
    */
   public String getKind()
   {
      return kind;
   }

   /**
    * @param kind the kind to set
    */
   public void setKind(String kind)
   {
      this.kind = kind;
   }

   /**
    * @return the path
    */
   public String getPath()
   {
      return path;
   }

   /**
    * @param path the path to set
    */
   public void setPath(String path)
   {
      this.path = path;
   }
}
