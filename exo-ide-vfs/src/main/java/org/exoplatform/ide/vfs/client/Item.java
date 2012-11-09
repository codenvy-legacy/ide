/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.client;

import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.List;
import java.util.Map;


public interface Item
{
   /** @return id of object */
   public String getId();

   /** @param id the id of object */
   public void setId(String id);

   /** @return name of object */
   public String getName();

   /** @param name the name of object */
   public void setName(String name);

   /** @return type of item */
   public ItemType getItemType();
   
   /** @ set type of item */
   public void setItemType(ItemType itemType);

   /** @return path */
   public String getPath();

   /** @param path the path */
   public void setPath(String path);

   /** @return id of parent folder and <code>null</code> if current item is root folder */
   public String getParentId();

   /** @param parentId id of parent folder and <code>null</code> if current item is root folder */
   public void setParentId(String parentId);

   /** @return creation date */
   public Long getCreationDate();

   /** @param creationDate the creation date */
   public void setCreationDate(Long creationDate);

   /** @return media type */
   public String getMimeType();

   /** @param mimeType media type */
   public void setMimeType(String mimeType);

   /**
    * Other properties.
    *
    * @return properties. If there is no properties then empty list returned, never <code>null</code>
    */
   public List<Property> getProperties();

   /**
    * Links for retrieved or(and) manage item.
    *
    * @return links map. Never <code>null</code> but empty map instead
    */
   public Map<String, Link> getLinks();

   /** @return set of relations */
   public List<String> getLinkRelations();
   
   public String getProjectType();

   public void setProjectType(String projectType);

}