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
import org.exoplatform.ide.resources.marshal.JSONDeserializer;

/**
 * This is a derivative of {@link Resource}, that adds File-specific properties and methods to provide
 * an access to files stored on VFS.
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
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

   /** content if retrieved */
   private String content = null;

   private boolean contentChanged = false;

   private JsonArray<File> versionHistory = JsonCollections.<File> createArray();

   private Lock lock = null;

   /** Empty instance of file. */
   protected File()
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

   /**
    * Init from JSONObject
    * @param itemObject
    */
   public void init(JSONObject itemObject)
   {
      id = itemObject.get("id").isString().stringValue();
      name = itemObject.get("name").isString().stringValue();
      mimeType = itemObject.get("mimeType").isString().stringValue();
      //path = itemObject.get("path").isString().stringValue();
      //parentId = itemObject.get("parentId").isString().stringValue();
      creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
      links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
      versionId = itemObject.get("versionId").isString().stringValue();
      length = (long)itemObject.get("length").isNumber().doubleValue();
      lastModificationDate = (long)itemObject.get("lastModificationDate").isNumber().doubleValue();
      locked = itemObject.get("locked").isBoolean().booleanValue();
      this.contentChanged = false;
      fixMimeType();
   }

   /**
    * @return the content if already retrieved
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

   /**
    * @return the history
    */
   public JsonArray<File> getVersionHistory()
   {
      return versionHistory;
   }

   /**
    * @param versionHistory set history
    */
   public void setVersionHistory(JsonArray<File> versionHistory)
   {
      this.versionHistory = versionHistory;
   }

   /**
    * Clear history
    */
   public void clearVersionHistory()
   {
      this.versionHistory = JsonCollections.createArray();
   }

   /**
    * @return lock object
    */
   public Lock getLock()
   {
      return lock;
   }

   /**
    * @param set lock object
    */
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

   /**
    * @return true if phantom file representing the version
    */
   public boolean isVersion()
   {
      return versionId == null ? false : !versionId.equals("0");
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public boolean isFile()
   {
      return true;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public boolean isFolder()
   {
      return false;
   }
}
