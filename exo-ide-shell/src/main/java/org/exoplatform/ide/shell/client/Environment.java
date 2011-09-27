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
package org.exoplatform.ide.shell.client;

import org.exoplatform.ide.vfs.client.model.FolderModel;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.storage.client.Storage;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 26, 2011 evgen $
 *
 */
public class Environment
{
   private Storage storage;

   private static Environment instance;

   private Map<String, String> storageMap;

   private FolderModel currentFolder;

   /**
    * 
    */
   protected Environment()
   {
      if (Storage.isSessionStorageSupported())
      {
         storage = Storage.getSessionStorageIfSupported();
      }
      else
      {
         storageMap = new HashMap<String, String>();
      }
      instance = this;
   }

   /**
    * @return instance of Enviroment class
    */
   public static final Environment get()
   {
      if (instance == null)
      {
         instance = new Environment();
      }
      return instance;
   }

   public void saveValue(String key, String value)
   {
      if (storage != null)
      {
         storage.setItem(key, value);
      }
      else
      {
         storageMap.put(key, value);
      }
   }

   public String getValue(String key)
   {
      if (storage != null)
      {
         return storage.getItem(key);
      }
      else
      {
         return storageMap.get(key);
      }
   }

   /**
    * @return the currentFolder
    */
   public FolderModel getCurrentFolder()
   {
      return currentFolder;
   }

   /**
    * @param currentFolder the currentFolder to set
    */
   public void setCurrentFolder(FolderModel currentFolder)
   {
      this.currentFolder = currentFolder;
   }

}
