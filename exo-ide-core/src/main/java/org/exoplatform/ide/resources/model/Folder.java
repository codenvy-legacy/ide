/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero GeneralLicense
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU GeneralLicense for more details.
 *
 * You should have received a copy of the GNU GeneralLicense
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.resources.model;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.resources.marshal.JSONDeserializer;

/**
 * Represents the folder containing {@link Resource}s. 
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class Folder extends Resource
{
   public static final String FOLDER_MIME_TYPE = "text/directory";

   public static final String TYPE = "folder";

   private JsonArray<Resource> children = JsonCollections.<Resource> createArray();

   /**
    * 
    * @param id
    * @param name
    * @param resourceType
    * @param mimeType
    * @param path
    * @param parent
    * @param creationDate
    * @param links
    */
   public Folder(String id, String name, String mimeType,// String path, 
      Folder parent, long creationDate, JsonStringMap<Link> links)
   {
      this(id, name, TYPE, mimeType, parent, creationDate, links);
   }

   /**
    * Full protected constructor used for sub-classing 
    * 
    * @param id
    * @param name
    * @param type
    * @param mimeType
    * @param parent
    * @param creationDate
    * @param links
    */
   protected Folder(String id, String name, String type, String mimeType,//String path, 
      Folder parent, long creationDate, JsonStringMap<Link> links)
   {
      super(id, name, type, mimeType, parent, creationDate, links);
      this.persisted = false;
   }

   /** Empty instance of Folder. */
   public Folder()
   {
      this(TYPE);
      mimeType = FOLDER_MIME_TYPE;
   }

   /** For extending classes */
   protected Folder(String itemType)
   {
      super(itemType);
   }

   public String createPath(String childName)
   {
      return getPath() + "/" + childName;
   }

   public Folder(JSONObject itemObject)
   {
      this();
      init(itemObject);
   }

   public void init(JSONObject itemObject)
   {
      id = itemObject.get("id").isString().stringValue();
      name = itemObject.get("name").isString().stringValue();
      if (itemObject.get("mimeType").isString() != null)
      {
         mimeType = itemObject.get("mimeType").isString().stringValue();
      }
      //path = itemObject.get("path").isString().stringValue();
      // no longer exists
      //      parentId =
      //         (itemObject.get("parentId").isNull() != null) ? null : itemObject.get("parentId").isString().stringValue();
      creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
      links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
      this.persisted = true;
   }

   /**
    * @return the children
    */
   public JsonArray<Resource> getChildren()
   {
      return children;
   }

   /**
    * @param children the children to set
    */
   public void setChildren(JsonArray<Resource> children)
   {
      this.children = children;
   }

   /**
    * Recursively looks for the Resource
    * 
    * @param id
    * @return resource or null if not found
    */
   public Resource findResourceById(String id)
   {
      for (int i = 0; i < children.size(); i++)
      {
         Resource child = children.get(i);
         if (child.getId().equals(id))
         {
            return child;
         }

         if (child instanceof Folder)
         {
            Resource resourceById = ((Folder)child).findResourceById(id);
            if (resourceById != null)
            {
               return resourceById;
            }
         }
      }
      return null;
   }

   /**
    * Recursively looks for the Resource
    * 
    * @param name
    * @param type
    * @return resource or null if not found
    */
   public Resource findResourceByName(String name, String type)
   {
      for (int i = 0; i < children.size(); i++)
      {
         Resource child = children.get(i);
         if (child.getId().equals(id) && child.getResourceType().equals(type))
         {
            return child;
         }

         if (child instanceof Folder)
         {
            Resource resourceById = ((Folder)child).findResourceByName(id, type);
            if (resourceById != null)
            {
               return resourceById;
            }
         }
      }
      return null;
   }

   /**
    * internal add to list
    * 
    * @param resource
    */
   void addChild(Resource resource)
   {
      children.add(resource);
   }

   /**
    * Internal remove from list
    * 
    * @param resource
    */
   void removeChild(Resource resource)
   {
      children.remove(resource);
      resource.setParent(null);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public boolean isFile()
   {
      return false;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public boolean isFolder()
   {
      return true;
   }
}
