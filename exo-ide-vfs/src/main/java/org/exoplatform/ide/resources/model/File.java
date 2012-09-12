/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
public class File extends Resource
{
   public static final String TYPE = "file";

   /** Id of version of file. */
   protected String versionId;

   /** Content length. */
   protected long length = -1;

   /** Date of last modification in long format. */
   protected long lastModificationDate;

   /** Locking flag. */
   protected boolean locked;

   // ======================

   private String content = null;

   private boolean contentChanged = false;

   private JsonArray<File> versionHistory = JsonCollections.<File> createArray();

   private Lock lock = null;

   /**
    * Instance of file with specified attributes.
    * 
    * @param id
    * @param name
    * @param mimeType
    * @param path
    * @param parentId
    * @param creationDate
    * @param links
    * @param versionId
    * @param length
    * @param locked
    * @param lastModificationDate
    */
   public File(String id, String name, String mimeType, String path, Folder parent, long creationDate,
      JsonStringMap<Link> links, String versionId, long length, boolean locked, long lastModificationDate)
   {
      this(id, name, TYPE, mimeType, path, parent, creationDate, links, versionId, length, locked,
         lastModificationDate);
   }

   protected File(String id, String name, String type, String mimeType, String path, Folder parent,
      long creationDate, JsonStringMap<Link> links, String versionId, long length, boolean locked,
      long lastModificationDate)
   {
      super(id, name, type, mimeType, path, parent, creationDate, links);
      this.lastModificationDate = lastModificationDate;
      this.locked = locked;
      this.versionId = versionId;
      this.length = length;
   }

   /** Empty instance of file. */
   public File()
   {
      super(TYPE);
   }

   /** For extending classes */
   protected File(String itemType)
   {
      super(itemType);
   }

   public File(JSONObject itemObject)
   {
      this();
      init(itemObject);
   }

   /** @return version id */
   public String getVersionId()
   {
      return versionId;
   }

   /** @param versionId the version id */
   public void setVersionId(String versionId)
   {
      this.versionId = versionId;
   }

   /** @return content length */
   public long getLength()
   {
      return length;
   }

   /** @param length the content length */
   public void setLength(long length)
   {
      this.length = length;
   }

   /** @return date of last modification */
   public long getLastModificationDate()
   {
      return lastModificationDate;
   }

   /** @param lastModificationDate the date of last modification */
   public void setLastModificationDate(long lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   /** @return <code>true</code> if object locked and <code>false</code> otherwise */
   public boolean isLocked()
   {
      return locked;
   }

   /** @param locked locking flag. Must be <code>true</code> if object locked and <code>false</code> otherwise */
   public void setLocked(boolean locked)
   {
      this.locked = locked;
   }

   // ===

   private void fixMimeType()
   {
      // Firefox adds ";charset=utf-8" to mime-type. Lets clear it.
      if (mimeType != null)
      {
         int index = mimeType.indexOf(';');
         if (index > 0)
         {
            mimeType = mimeType.substring(0, index);
         }
      }
   }

   public void init(JSONObject itemObject)
   {
      id = itemObject.get("id").isString().stringValue();
      name = itemObject.get("name").isString().stringValue();
      mimeType = itemObject.get("mimeType").isString().stringValue();
      path = itemObject.get("path").isString().stringValue();
      //parentId = itemObject.get("parentId").isString().stringValue();
      creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
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

   public JsonArray<File> getVersionHistory()
   {
      return versionHistory;
   }

   public void setVersionHistory(JsonArray<File> versionHistory)
   {
      this.versionHistory = versionHistory;
   }

   public void clearVersionHistory()
   {
      this.versionHistory = JsonCollections.createArray();
   }

   public Lock getLock()
   {
      return lock;
   }

   public void setLock(Lock lock)
   {
      this.lock = lock;
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

   public boolean isVersion()
   {
      return versionId == null ? false : !versionId.equals("0");
   }
}
