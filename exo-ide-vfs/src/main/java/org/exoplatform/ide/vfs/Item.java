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
package org.exoplatform.ide.vfs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Item
{
   private String id;

   private Type type;

   private String path;

   private long creationDate;

   private long lastModificationDate;

   private boolean locked;

   private List<OutputProperty> properties;

   public Item(String id, Type type, String path, long creationDate, long lastModificationDate, boolean locked,
      List<OutputProperty> properties)
   {
      this.id = id;
      this.type = type;
      this.path = path;
      this.creationDate = creationDate;
      this.lastModificationDate = lastModificationDate;
      this.locked = locked;
      this.properties = properties;
   }

   public Item()
   {
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public Type getType()
   {
      return type;
   }

   public void setType(Type type)
   {
      this.type = type;
   }

   public String getPath()
   {
      return path;
   }

   public void setPath(String path)
   {
      this.path = path;
   }

   public long getCreationDate()
   {
      return creationDate;
   }

   public void setCreationDate(long creationDate)
   {
      this.creationDate = creationDate;
   }

   public long getLastModificationDate()
   {
      return lastModificationDate;
   }

   public void setLastModificationDate(long lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   public boolean isLocked()
   {
      return locked;
   }

   public void setLocked(boolean locked)
   {
      this.locked = locked;
   }

   public List<OutputProperty> getProperties()
   {
      if (properties == null)
         properties = new ArrayList<OutputProperty>();
      return properties;
   }
}
