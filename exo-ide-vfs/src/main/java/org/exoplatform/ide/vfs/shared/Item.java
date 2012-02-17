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
import java.util.Set;

/**
 * Representation of abstract item used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class Item
{
   /** Id of object. */
   protected String id;

   /** Name of object. */
   protected String name;

   /** Type of object. */
   protected final ItemType itemType;

   /** Media type. */
   protected String mimeType;

   /** Path. */
   protected String path;

   /** Parent ID. Must be <code>null</code> if item is root folder. */
   protected String parentId;

   /** Creation date in long format. */
   protected long creationDate;

   /** Properties. */
   @SuppressWarnings("rawtypes")
   protected List<Property> properties;

   /** Links. */
   protected Map<String, Link> links;

   /**
    * @param id id of item
    * @param name name of item
    * @param itemType type of item
    * @param mimeType the media type
    * @param path path of item
    * @param parentId id of parent folder. May be <code>null</code> if current item is root folder
    * @param creationDate creation date in long format
    * @param properties other properties of object
    * @param links hyper-links for retrieved or(and) manage item
    */
   @SuppressWarnings("rawtypes")
   public Item(String id, String name, ItemType itemType, String mimeType, String path, String parentId,
               long creationDate, List<Property> properties, Map<String, Link> links)
   {
      this.id = id;
      this.name = name;
      this.itemType = itemType;
      this.mimeType = mimeType;
      this.path = path;
      this.parentId = parentId;
      this.creationDate = creationDate;
      this.properties = properties;
      this.links = links;
   }

   public Item(ItemType itemType)
   {
      this.itemType = itemType;
   }

   /** @return id of object */
   public String getId()
   {
      return id;
   }

   /** @param id the id of object */
   public void setId(String id)
   {
      this.id = id;
   }

   /** @return name of object */
   public String getName()
   {
      return name;
   }

   /** @param name the name of object */
   public void setName(String name)
   {
      this.name = name;
   }

   /** @return type of item */
   public ItemType getItemType()
   {
      return itemType;
   }

   /** @return path */
   public String getPath()
   {
      return path;
   }

   /** @param path the path */
   public void setPath(String path)
   {
      this.path = path;
   }

   /** @return id of parent folder and <code>null</code> if current item is root folder */
   public String getParentId()
   {
      return parentId;
   }

   /** @param parentId id of parent folder and <code>null</code> if current item is root folder */
   public void setParentId(String parentId)
   {
      this.parentId = parentId;
   }

   /** @return creation date */
   public long getCreationDate()
   {
      return creationDate;
   }

   /** @param creationDate the creation date */
   public void setCreationDate(long creationDate)
   {
      this.creationDate = creationDate;
   }

   /** @return media type */
   public String getMimeType()
   {
      return mimeType;
   }

   /** @param mimeType media type */
   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }

   /**
    * Other properties.
    *
    * @return properties. If there is no properties then empty list returned, never <code>null</code>
    */
   @SuppressWarnings("rawtypes")
   public List<Property> getProperties()
   {
      if (properties == null)
      {
         properties = new ArrayList<Property>();
      }
      return properties;
   }

   /**
    * Get single property with specified name.
    *
    * @param name name of property
    * @return property or <code>null</code> if there is not property with specified name
    */
   @SuppressWarnings("rawtypes")
   public Property getProperty(String name)
   {
      for (Property p : getProperties())
      {
         if (p.getName().equals(name))
         {
            return p;
         }
      }
      return null;
   }

   /**
    * Check does item has property with specified name.
    *
    * @param name name of property
    * @return <code>true</code> if item has property <code>name</code> and <code>false</code> otherwise
    */
   public boolean hasProperty(String name)
   {
      return getProperty(name) != null;
   }

   /**
    * Get value of property <code>name</code>. It is shortcut for:
    * <pre>
    *    String name = ...
    *    Item item = ...
    *    Property property = item.getProperty(name);
    *    Object value;
    *    if (property != null)
    *       value = property.getValue().get(0);
    *    else
    *       value = null;
    * </pre>
    *
    * @param name property name
    * @return value of property with specified name or <code>null</code>
    */
   @SuppressWarnings("rawtypes")
   public Object getPropertyValue(String name)
   {
      Property p = getProperty(name);
      if (p != null)
      {
         return p.getValue().get(0);
      }
      return null;
   }

   /**
    * Get set of property values
    *
    * @param name property name
    * @return set of property values or <code>null</code> if property does not exists
    * @see #getPropertyValue(String)
    */
   @SuppressWarnings({"rawtypes", "unchecked"})
   public List getPropertyValues(String name)
   {
      Property p = getProperty(name);
      if (p != null)
      {
         List values = new ArrayList(p.getValue().size());
         for (Object v : p.getValue())
         {
            values.add(v);
         }
         return values;
      }
      return null;
   }

   /**
    * Links for retrieved or(and) manage item.
    *
    * @return links map. Never <code>null</code> but empty map instead
    */
   public Map<String, Link> getLinks()
   {
      if (links == null)
      {
         links = new HashMap<String, Link>();
      }
      return links;
   }

   /** @return set of relations */
   public Set<String> getLinkRelations()
   {
      return getLinks().keySet();
   }

   /**
    * @param rel relation string
    * @return corresponding hyperlink or null if no such relation found
    */
   public Link getLinkByRelation(String rel)
   {
      return getLinks().get(rel);
   }

   /** @see java.lang.Object#toString() */
   @Override
   public String toString()
   {
      return "Item [id=" + id + ", name=" + name + ", type=" + itemType + ']';
   }
}
