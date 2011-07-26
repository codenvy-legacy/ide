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

   private boolean persisted;

   private String content = null;

   //private boolean contentChanged = false;
   
   private HashSet<File> versionHistory = new HashSet<File>();
   
   private LockToken lockToken = null;
   
   private Project project;
   
   private org.exoplatform.ide.vfs.shared.Folder parent;

   @SuppressWarnings("unchecked")
   public File(String name, String mimeType, String content, 
         org.exoplatform.ide.vfs.shared.Folder parent)
   {
	  super(null, name, parent.createPath(name), parent.getId(), new Date().getTime(), 
			new Date().getTime(), null /*versionId*/,
			mimeType, 0, false, new ArrayList<Property>(),
			new HashMap<String, Link>());
      this.persisted = false;
      this.content = content;
      this.parent = parent;
      
      fixMimeType();
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
     
      fixMimeType();
      
      this.persisted = true;
   }
   
   private void fixMimeType()
   {
      // Firefox adds ";charset=utf-8" to mimetype
      // lets clear it
      int index = mimeType.indexOf(';'); 
      if(index > 0)
        mimeType = mimeType.substring(0, index);
   }
   
   public void init(JSONObject itemObject)
   {
      super.init(itemObject);
      setLength((long)itemObject.get("length").isNumber().doubleValue());
      this.persisted = true;
      fixMimeType();
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
      return lockToken != null;
   }


//   public boolean isNewFile()
//   {
//      return newFile;
//   }
//
//
//   public void setNewFile(boolean newFile)
//   {
//      this.newFile = newFile;
//   }


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
   public final org.exoplatform.ide.vfs.shared.Folder getParent()
   {
      return parent;
   }

   @Override
   public void setParent(org.exoplatform.ide.vfs.shared.Folder parent)
   {
      this.parent = parent;
   }

   @Override
   public boolean isPersisted()
   {
      // TODO Auto-generated method stub
      return persisted;
   }

   
}
