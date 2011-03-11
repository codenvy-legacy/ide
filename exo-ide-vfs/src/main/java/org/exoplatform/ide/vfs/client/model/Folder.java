/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.client.model;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;



/**
 * Created by The eXo Platform SAS .
 * 
 * @author eXo
 * @version $Id: $
 */

public class Folder extends org.exoplatform.ide.vfs.shared.Folder implements ProjectContext
{

   private HashSet<Item> children = new HashSet<Item>();
   
   private Project project;
   
   //Folder(String id, String name, String mimeType, String path, long creationDate, List<Property> properties,
     // Map<String, Link> links)
   
   public Folder(String name, String path)
   {
      super(null, name, FOLDER_MIME_TYPE, path, new Date().getTime(), 
    		new ArrayList<Property>(), new HashMap<String, Link>());
   }
   
//   public Folder(org.exoplatform.ide.vfs.shared.Folder persistedFolder)
//   {
//      super(persistedFolder.getId(), persistedFolder.getName(), 
//         FOLDER_MIME_TYPE,
//    		persistedFolder.getPath(), persistedFolder.getCreationDate(), 
//    		persistedFolder.getProperties(), persistedFolder.getLinks());
//   }
   
   public Folder(JSONObject itemObject)
   {
      super(itemObject.get("id").isString().stringValue(),
            itemObject.get("name").isString().stringValue(),
            itemObject.get("mimeType").isString().stringValue(),
            itemObject.get("path").isString().stringValue(),
            (long)itemObject.get("creationDate").isNumber().doubleValue(),     
            JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties")),     
            JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links")));

   }


   /**
    * @return the children
    */
   public HashSet<Item> getChildren()
   {
      return children;
   }

   /**
    * @param children the children to set
    */
   public void setChildren(HashSet<Item> children)
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
