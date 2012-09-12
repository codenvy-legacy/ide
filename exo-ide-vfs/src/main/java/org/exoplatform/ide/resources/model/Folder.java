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
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public class Folder extends Resource
{
   public static final String FOLDER_MIME_TYPE = "text/directory";

   public static final String TYPE = "folder";

   private JsonArray<Resource> children = JsonCollections.<Resource> createArray();

   /**
    * @param id
    * @param name
    * @param resourceType
    * @param mimeType
    * @param path
    * @param parent
    * @param creationDate
    * @param links
    */
   public Folder(String id, String name, String mimeType, String path, Folder parent,
      long creationDate, JsonStringMap<Link> links)
   {
      this(id, name, TYPE, mimeType, path, parent, creationDate, links);
   }
   
   protected Folder(String id, String name, String type, String mimeType, String path, Folder parent,
      long creationDate, JsonStringMap<Link> links)
   {
      super(id, name, type, mimeType, path, parent, creationDate, links);
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
      return this.path + "/" + childName;
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
      path = itemObject.get("path").isString().stringValue();
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

}
