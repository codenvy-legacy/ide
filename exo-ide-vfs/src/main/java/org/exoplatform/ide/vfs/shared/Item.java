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
package org.exoplatform.ide.vfs.shared;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of abstract item used to interaction with client via JSON.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Item
{
   /** Id of object. */
   private String id;

   /** Name of object. */
   private String name;

   /** Type of object. */
   private Type type;

   /** Path. */
   private String path;

   /** Creation date in long format. */
   private long creationDate;

   private List<Property> properties;

   private Map<String, Link> links;

   /**
    * @param id id of object
    * @param name the name of object
    * @param path path of object
    * @param creationDate creation date in long format
    * @param properties other properties of object
    * @param links hyperlinks for retrieved or(and) manage item
    */
   public Item(String id, String name, Type type, String path, long creationDate, List<Property> properties,
      Map<String, Link> links)
   {
      this.id = id;
      this.name = name;
      this.type = type;
      this.path = path;
      this.creationDate = creationDate;
      this.properties = properties;
      this.links = links;
   }

   public Item()
   {
   }

   /**
    * @return id of object
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id of object
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
    * Other properties.
    * 
    * @return properties. If there is no properties then empty list returned,
    *         never <code>null</code>
    */
   public List<Property> getProperties()
   {
      if (properties == null)
         properties = new ArrayList<Property>();
      return properties;
   }

   /**
    * Links for retrieved or(and) manage item.
    * 
    * @return links map. Never <code>null</code> but empty map instead
    */
   public Map<String, Link> getLinks()
   {
      if (links == null)
         links = new HashMap<String, Link>();
      return links;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "Item [id=" + id + ", name=" + name + ", type=" + type + "]";
   }
}
