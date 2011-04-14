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
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by The eXo Platform SAS .
 * 
 * @author eXo
 * @version $Id: $
 */

public class Folder extends org.exoplatform.ide.vfs.shared.Folder implements ItemContext
{

   private ItemList<Item> children = new ItemList<Item>();
   
   private Project project;
 
   public Folder(String name, String path, String parentId)
   {
      super(null, name, FOLDER_MIME_TYPE, path, parentId, new Date().getTime(), 
    		new ArrayList<Property>(), new HashMap<String, Link>());
   }
   
   public Folder(String name, String path, String parentId,Map<String, Link> links)
   {
      super(null, name, FOLDER_MIME_TYPE, path, parentId, new Date().getTime(), 
         new ArrayList<Property>(), links);
   }


   public Folder(JSONObject itemObject)
   {
      super(itemObject.get("id").isString().stringValue(),
            itemObject.get("name").isString().stringValue(),
            itemObject.get("mimeType").isString().stringValue(),
            itemObject.get("path").isString().stringValue(),            
            itemObject.get("parentId").isString().stringValue(),
            (long)itemObject.get("creationDate").isNumber().doubleValue(),     
            JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties")),     
            JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links")));

   }
   

   public Folder()
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
   public Project getProject()
   {
      return project;
   }

   @Override
   public void setProject(Project proj)
   {
      this.project = proj;
      
   }

   

}
