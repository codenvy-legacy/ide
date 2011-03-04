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
   public static String REL_ACL = "acl";
   public static String REL_SELF = "self";
   public static String REL_CURRENT = "current";
   public static String REL_SEARCH = "search";
   public static String REL_COPY = "copy";
   public static String REL_MOVE = "move";
   
   /** Id of object. */
   private String id;

   /** Name of object. */
   private String name;

   /** Type of object. */
   private ItemType itemType;

   /**  */
   private String mimeType;

   /** Path. */
   private String path;

   /** Creation date in long format. */
   private long creationDate;

   private List<Property> properties;

   private Map<String, Link> links;

   /**
    * @param id id of item
    * @param name the name of item
    * @param itemType type of item
    * @param iconHint hint of icon display item on client side
    * @param path path of item
    * @param creationDate creation date in long format
    * @param properties other properties of object
    * @param links hyper-links for retrieved or(and) manage item
    */
   public Item(String id, String name, ItemType itemType, String mimeType, String path, long creationDate,
      List<Property> properties, Map<String, Link> links)
   {
      this.id = id;
      this.name = name;
      this.itemType = itemType;
      this.mimeType = mimeType;
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
    * @return type of item
    */
   public ItemType getItemType()
   {
      return itemType;
   }

   /**
    * @param type the type of item
    */
   public void setItemType(ItemType type)
   {
      this.itemType = type;
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

   
   public final String getMimeType()
   {
      return mimeType;
   }

   public final void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
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
      return "Item [id=" + id + ", name=" + name + ", type=" + itemType + "]";
   }
}
