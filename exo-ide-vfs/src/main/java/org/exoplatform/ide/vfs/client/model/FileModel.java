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
import org.exoplatform.ide.vfs.shared.Lock;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author eXo
 * @version $Id: $
 */
public class FileModel extends org.exoplatform.ide.vfs.shared.File implements ItemContext
{
   private boolean persisted;

   private String content = null;

   private boolean contentChanged = false;

   private HashSet<FileModel> versionHistory = new HashSet<FileModel>();

   private Lock lock = null;

   private ProjectModel project;

   private FolderModel parent;

   @SuppressWarnings("rawtypes")
   public FileModel(String name, String mimeType, String content, FolderModel parent)
   {
      super(null, name, parent.createPath(name), parent.getId(), new Date().getTime(), new Date().getTime(),
         null /*versionId*/, mimeType, 0, false, new ArrayList<Property>(), new HashMap<String, Link>());
      this.persisted = false;
      this.content = content;
      this.parent = parent;

      fixMimeType();
   }

   public FileModel()
   {
      super();
   }

   public FileModel(JSONObject itemObject)
   {
      super();
      init(itemObject);
   }

   public FileModel(org.exoplatform.ide.vfs.shared.File file)
   {
      super(file.getId(), file.getName(), file.getPath(), file.getParentId(), file.getCreationDate(), file
         .getLastModificationDate(), file.getVersionId(), file.getMimeType(), file.getLength(), file.isLocked(), file
         .getProperties(), file.getLinks());
      fixMimeType();
      this.persisted = true;
   }

   private void fixMimeType()
   {
      // Firefox adds ";charset=utf-8" to mime-type. Lets clear it.
      if (mimeType != null)
      {
         int index = mimeType.indexOf(';');
         if (index > 0)
            mimeType = mimeType.substring(0, index);
      }
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
      versionId = itemObject.get("versionId").isString().stringValue();
      length = (long)itemObject.get("length").isNumber().doubleValue();
      lastModificationDate = (long)itemObject.get("lastModificationDate").isNumber().doubleValue();
      locked = itemObject.get("locked").isBoolean().booleanValue();
      this.persisted = true;
      this.contentChanged = false;
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

   public HashSet<FileModel> getVersionHistory()
   {
      return versionHistory;
   }

   public void setVersionHistory(HashSet<FileModel> versionHistory)
   {
      this.versionHistory = versionHistory;
   }

   public void clearVersionHistory()
   {

      this.versionHistory = new HashSet<FileModel>();
   }

   public Lock getLock()
   {
      return lock;
   }

   public void setLock(Lock lock)
   {
      this.lock = lock;
   }

   public boolean isLocked()
   {
      return lock != null;
   }

   /**
    * @return the contentChanged
    */
   public boolean isContentChanged()
   {
      return contentChanged;
   }

   /**
    * @param contentChanged the contentChanged to set
    */
   public void setContentChanged(boolean contentChanged)
   {
      this.contentChanged = contentChanged;
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
   public final FolderModel getParent()
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

   public boolean isVersion()
   {
      return versionId == null ? false : !versionId.equals("current");
   }
}
