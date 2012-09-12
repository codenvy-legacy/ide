/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.resources.model;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;

/**
 * 
 * Not intended to be extended by client code. Use File, Folder and Project as superclass.
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public class Resource implements ItemContext
{
   /** Id of object. */
   protected String id;

   /** Name of object. */
   protected String name;

   /** Type of object. */
   protected final String resourceType;

   /** Media type. */
   protected String mimeType;

   /** Path. */
   protected String path;

//   /** Parent ID. Must be <code>null</code> if item is root folder. */
//   protected String parentId;

   /** Creation date in long format. */
   protected long creationDate;

   /** Links. */
   protected JsonStringMap<Link> links;
   
   // item context
   protected Project project;

   protected Folder parent;

   protected boolean persisted;
   
   /**
    * @param id id of item
    * @param name name of item
    * @param resourceType type of item
    * @param mimeType the media type
    * @param path path of item
    * @param parentId id of parent folder. May be <code>null</code> if current item is root folder
    * @param creationDate creation date in long format
    * @param links hyper-links for retrieved or(and) manage item
    */
   public Resource(String id, String name, String resourceType, String mimeType, String path, Folder parent,
      long creationDate, JsonStringMap<Link> links)
   {
      this.id = id;
      this.name = name;
      this.resourceType = resourceType;
      this.mimeType = mimeType;
      this.path = path;
      this.parent = parent;
      this.creationDate = creationDate;
      this.links = links;
   }

   /**
    * 
    */
   public Resource(String itemType)
   {
      this.resourceType = itemType;
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
   public String getResourceType()
   {
      return resourceType;
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

//   /** @return id of parent folder and <code>null</code> if current item is root folder */
//   public String getParentId()
//   {
//      return parentId;
//   }
//
//   /** @param parentId id of parent folder and <code>null</code> if current item is root folder */
//   public void setParentId(String parentId)
//   {
//      this.parentId = parentId;
//   }

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
    * Links for retrieved or(and) manage item.
    *
    * @return links map. Never <code>null</code> but empty map instead
    */
   public JsonStringMap<Link> getLinks()
   {
      if (links == null)
      {
         links = JsonCollections.createStringMap();
      }
      return links;
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
      return "Resourece [id=" + id + ", name=" + name + ", type=" + resourceType + ']';
   }
   
   
   // item context

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public void setProject(Project proj)
   {
      this.project = proj;

   }

   @Override
   public final Folder getParent()
   {
      return parent;
   }

   @Override
   public void setParent(Folder parent)
   {
      this.parent = parent;
   }

   @Override
   public boolean isPersisted()
   {
      return persisted;
   }
   

}
