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
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;



/**
 * Created by The eXo Platform SAS .
 * 
 * @author eXo
 * @version $Id: $
 */

public class File extends org.exoplatform.ide.vfs.shared.File implements ItemContext
{

   private boolean newFile;

   private String content = null;

   //private boolean contentChanged = false;
   
   private HashSet<File> versionHistory = new HashSet<File>();
   
   private LockToken lockToken = null;
   
   private Project project;
   
   private Folder parent;

   public File(String name, String path, String mimeType, String content, Folder parent)
   {
	  super(null, name, path, parent.getId(), new Date().getTime(), 
			new Date().getTime(), null /*versionId*/,
			mimeType, 0, false, new ArrayList<Property>(),
			new HashMap<String, Link>());
      this.newFile = true;
      this.content = content;
      this.parent = parent;
   }

   public File() 
   {
      super();
   }
   
   
   public File(JSONObject itemObject)
   {
      super(itemObject.get("id").isString().stringValue(),
            itemObject.get("name").isString().stringValue(),
            itemObject.get("path").isString().stringValue(),
            itemObject.get("parentId").isString().stringValue(),
            (long)itemObject.get("creationDate").isNumber().doubleValue(),
            (long)itemObject.get("lastModificationDate").isNumber().doubleValue(),
            itemObject.get("versionId").isString().stringValue(),
            itemObject.get("mimeType").isString().stringValue(),
            (long)itemObject.get("length").isNumber().doubleValue(),
            (boolean)itemObject.get("locked").isBoolean().booleanValue(),                
            JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties")),     
            JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links")));
      
      this.newFile = false;
   }

   /**
    * @return the content
    */
   public String getContent()
   {
      return content;
   }

   /**
    * @param content the content to set
    */
   public void setContent(String content)
   {
      this.content = content;
   }

//   /**
//    * @return the contentChanged
//    */
//   public boolean isContentChanged()
//   {
//      return contentChanged;
//   }
//
//   /**
//    * @param contentChanged the contentChanged to set
//    */
//   public void setContentChanged(boolean contentChanged)
//   {
//      this.contentChanged = contentChanged;
//   }

   
   public HashSet<File> getVersionHistory()
   {
      return versionHistory;
   }


   public void setVersionHistory(HashSet<File> versionHistory)
   {
      this.versionHistory = versionHistory;
   }
   
   public void clearVersionHistory() {
      
      this.versionHistory = new HashSet<File>();   
   }


   public LockToken getLockToken()
   {
      return lockToken;
   }


   public void setLockToken(LockToken lockToken)
   {
      this.lockToken = lockToken;
   }

   public boolean isLocked() 
   {
      return lockToken == null;
   }


   public boolean isNewFile()
   {
      return newFile;
   }


   public void setNewFile(boolean newFile)
   {
      this.newFile = newFile;
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

   public final Folder getParent()
   {
      return parent;
   }

   
}
