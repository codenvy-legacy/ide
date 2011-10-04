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
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @version $Id:$
 */
public class ProjectModel extends org.exoplatform.ide.vfs.shared.Project implements ItemContext
{
   private ItemList<Item> children = new ItemList<Item>();
   private FolderModel parent;
   private boolean persisted;

   @SuppressWarnings("rawtypes")
   public ProjectModel(String name, FolderModel parent, String type, List<Property> properties)
   {
      super(null, name, PROJECT_MIME_TYPE, parent.createPath(name), parent.getId(), new Date().getTime(), properties,
         new HashMap<String, Link>(), type);
      this.parent = parent;
      this.persisted = false;
   }

   public ProjectModel(Project project)
   {
      super(project.getId(), project.getName(), PROJECT_MIME_TYPE, project.getPath(), project.getParentId(), project
         .getCreationDate(), project.getProperties(), project.getLinks(), project.getProjectType());
   }

   public ProjectModel()
   {
      super();
   }
   
   public ProjectModel(JSONObject itemObject)
   {
      super();
      init(itemObject);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public void init(JSONObject itemObject)
   {
      id = itemObject.get("id").isString().stringValue();
      name = itemObject.get("name").isString().stringValue();
      mimeType = itemObject.get("mimeType").isString().stringValue();
      path = itemObject.get("path").isString().stringValue();
      parentId = itemObject.get("parentId").isString().stringValue();
      creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
      properties = (List)JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
      links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
      projectType = (itemObject.get("projectType") != null) ?  itemObject.get("projectType").isString().stringValue() : null;
      this.persisted = true;
   }

   @Override
   public ProjectModel getProject()
   {
      return this;
   }

   @Override
   public void setProject(ProjectModel proj)
   {
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
}
