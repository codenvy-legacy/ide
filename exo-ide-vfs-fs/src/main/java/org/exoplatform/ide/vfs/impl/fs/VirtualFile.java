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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class VirtualFile
{
   private final String id;
   private final Path path;
   private final ItemType type;
//   private final java.io.File ioFile;
   private final long length;
   private final long lastModificationDate = 0; // TODO
   private final Map<String, List<String>> properties = Collections.emptyMap(); // TODO

   VirtualFile(String id, Path path, ItemType type, /*java.io.File ioFile*/long length)
   {
      this.id = id;
      this.path = path;
      this.type = type;
      this.length = length;
//      this.ioFile = ioFile;
   }

   String getId()
   {
      return id;
   }

   Path getPath()
   {
      return path;
   }

   ItemType getType()
   {
      return type;
   }

   String getMediaType()
   {
      return "text/plain"; // TODO
   }

   long getCreationDate()
   {
      // Creation date is not accessible over JDK API. May be done when switch to JDK7.
      return getLastModificationDate();
   }

   long getLastModificationDate()
   {
      return lastModificationDate;
   }

   long getLength()
   {
      return length/*ioFile.length()*/;
   }

   Map<String, List<String>> getProperties()
   {
      return properties;
   }

   String getFirstProperty(String name)
   {
      List<String> l = properties.get(name);
      return l == null || l.isEmpty() ? null : l.get(0);
   }
}
