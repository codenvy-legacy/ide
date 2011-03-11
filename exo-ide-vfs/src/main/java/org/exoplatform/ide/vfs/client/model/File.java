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

public class File extends org.exoplatform.ide.vfs.shared.File implements ProjectContext
{

   private boolean newFile;

   private String content = null;

   //private boolean contentChanged = false;
   
   private HashSet<File> versionHistory = new HashSet<File>();
   
   private LockToken lockToken = null;
   
   private Project project;

   public File(String name, String path, String mimeType, String content)
   {
//      public File(String id, String name, String path, long creationDate, long lastModificationDate, String versionId,
//         String mimeType, long length, boolean locked, List<Property> properties, Map<String, Link> links)

	  super(null, name, path, new Date().getTime(), 
			new Date().getTime(), null /*versionId*/,
			mimeType, 0, false, new ArrayList<Property>(),
			new HashMap<String, Link>());
      this.newFile = true;
      this.content = content;
   }

//   public File(org.exoplatform.ide.vfs.shared.File persistedFile) 
//   {
//	   super(persistedFile.getId(), 
//	       persistedFile.getName(), 
//	       persistedFile.getPath(), 
//			 persistedFile.getCreationDate(), 
//			 persistedFile.getLastModificationDate(), 
//			 persistedFile.getVersionId(), 
//			 persistedFile.getMimeType(),
//			 persistedFile.getLength(), 
//			 persistedFile.isLocked(), 
//			 persistedFile.getProperties(), 
//			 persistedFile.getLinks());
//
//	   this.newFile = false;
//   }
   
   
   public File(JSONObject itemObject)
   {
      super(itemObject.get("id").isString().stringValue(),
            itemObject.get("name").isString().stringValue(),
            itemObject.get("path").isString().stringValue(),
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

   
}
