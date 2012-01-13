/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.client.model;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author eXo
 * @version $Id: $
 */

public class FolderModel extends org.exoplatform.ide.vfs.shared.Folder implements ItemContext
{
   private ItemList<Item> children = new ItemList<Item>();

   private ProjectModel project;

   private FolderModel parent;

   private boolean persisted;

   @SuppressWarnings("rawtypes")
   public FolderModel(String name, FolderModel parent)
   {
      super(null, name, FOLDER_MIME_TYPE, parent.createPath(name), parent.getId(), new Date().getTime(),
         new ArrayList<Property>(), new HashMap<String, Link>());
      this.parent = parent;
      this.persisted = false;
   }

   @SuppressWarnings("rawtypes")
   public FolderModel(String name, FolderModel parent, Map<String, Link> links)
   {
      super(null, name, FOLDER_MIME_TYPE, parent.createPath(name), parent.getId(), new Date().getTime(),
         new ArrayList<Property>(), links);
      this.parent = parent;
      this.persisted = false;
   }

   public FolderModel(JSONObject itemObject)
   {
      super();
      init(itemObject);
   }

   public FolderModel(Folder folder)
   {
      super(folder.getId(), folder.getName(), FOLDER_MIME_TYPE, folder.getPath(), folder.getParentId(), folder
         .getCreationDate(), folder.getProperties(), folder.getLinks());
      this.persisted = true;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public void init(JSONObject itemObject)
   {
      id = itemObject.get("id").isString().stringValue();
      name = itemObject.get("name").isString().stringValue();
      if (itemObject.get("mimeType").isString() != null)
         mimeType = itemObject.get("mimeType").isString().stringValue();
      path = itemObject.get("path").isString().stringValue();
      parentId =
         (itemObject.get("parentId").isNull() != null) ? null : itemObject.get("parentId").isString().stringValue();
      creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
      properties = (List)JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
      links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
      this.persisted = true;
   }

   public FolderModel()
   {
      super();
   }

   /**
    * @return the children
    */
   public ItemList<Item> getChildren()
   {
      return children;
   }

   /**
    * @param children the children to set
    */
   public void setChildren(ItemList<Item> children)
   {
      this.children = children;
   }

   @Override
   public ProjectModel getProject()
   {
      return project;
   }

   @Override
   public void setProject(ProjectModel proj)
   {
      this.project = proj;

   }

   @Override
   public FolderModel getParent()
   {
      return parent;
   }

   @Override
   public void setParent(FolderModel parent)
   {
      this.parent = parent;
   }

   @Override
   public boolean isPersisted()
   {
      return persisted;
   }
}
