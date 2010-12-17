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
 * Representation of abstract item used to interaction with client via JSON.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Item
{
   /** Identifier of object. */
   private String id;

   /** Name of object. */
   private String name;

   /** Type of object. */
   private Type type;

   /** Path. */
   private String path;

   /** Creation date in long format. */
   private long creationDate;

   /** Date of last modification in long format. */
   private long lastModificationDate;

   /** Locking flag. */
   private boolean locked;

   private List<OutputProperty> properties;

   /**
    * @param id identifier of object
    * @param name the name of object
    * @param path path of object
    * @param creationDate creation date in long format
    * @param lastModificationDate date of last modification in long format
    * @param locked is object locked or not
    * @param properties other properties of object
    */
   public Item(String id, String name, Type type, String path, long creationDate, long lastModificationDate,
      boolean locked, List<OutputProperty> properties)
   {
      this.id = id;
      this.name = name;
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

   /**
    * @return identifier of object
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the identifier of object
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return name of object
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name of object
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return type of object
    */
   public Type getType()
   {
      return type;
   }

   /**
    * @param type the type of object
    */
   public void setType(Type type)
   {
      this.type = type;
   }

   /**
    * @return path
    */
   public String getPath()
   {
      return path;
   }

   /**
    * @param path the path
    */
   public void setPath(String path)
   {
      this.path = path;
   }

   /**
    * @return creation date
    */
   public long getCreationDate()
   {
      return creationDate;
   }

   /**
    * @param creationDate the creation date
    */
   public void setCreationDate(long creationDate)
   {
      this.creationDate = creationDate;
   }

   /**
    * @return date of last modification
    */
   public long getLastModificationDate()
   {
      return lastModificationDate;
   }

   /**
    * @param lastModificationDate the date of last modification
    */
   public void setLastModificationDate(long lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   /**
    * @return <code>true</code> if object locked and <code>false</code>
    *         otherwise
    */
   public boolean isLocked()
   {
      return locked;
   }

   /**
    * @param locked locking flag. Must be <code>true</code> if object locked and
    *           <code>false</code> otherwise
    */
   public void setLocked(boolean locked)
   {
      this.locked = locked;
   }

   /**
    * Other properties.
    * 
    * @return properties. If there is no properties then empty list returned,
    *         never <code>null</code>
    */
   public List<OutputProperty> getProperties()
   {
      if (properties == null)
         properties = new ArrayList<OutputProperty>();
      return properties;
   }
}
