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

package org.exoplatform.ide.extension.java.shared.proj;

import java.util.List;

import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import com.google.gwt.json.client.JSONObject;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Project extends org.exoplatform.ide.vfs.shared.Project
{

   private ItemList<ProjectItem> children = new ItemList<ProjectItem>();

   public Project()
   {
   }

   public Project(JSONObject itemObject)
   {
      init(itemObject);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public void init(JSONObject itemObject)
   {
      // id = itemObject.get("id").isString().stringValue();
      // name = itemObject.get("name").isString().stringValue();
      // if (itemObject.get("mimeType").isString() != null)
      // mimeType = itemObject.get("mimeType").isString().stringValue();
      // path = itemObject.get("path").isString().stringValue();
      // parentId = (itemObject.get("parentId").isNull() != null) ? null : itemObject.get("parentId").isString().stringValue();
      // creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
      // properties = (List)JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
      // links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
      // this.persisted = true;
   }

}
