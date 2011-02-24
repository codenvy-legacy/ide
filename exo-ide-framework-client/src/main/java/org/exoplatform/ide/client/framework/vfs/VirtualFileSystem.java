/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.vfs;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlList;
import org.exoplatform.ide.client.framework.vfs.callback.ItemLockCallback;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback;

import java.util.List;


public abstract class VirtualFileSystem
{

   private static VirtualFileSystem instance;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   protected VirtualFileSystem()
   {
      instance = this;
   }

   /**
    * Get folder content
    * 
    * @param folder - folder which children will be received.
    * @param callback - the callback which the client has to implement
    */
   public abstract void getChildren(Folder folder, AsyncRequestCallback<Folder> callback);

   /**
    * Create new folder
    * 
    * @param folder - the folder to create
    * @param callback - the callback which the client has to implement
    */
   public abstract void createFolder(Folder folder, FolderCreateCallback callback);

   /**
    * Get content of the file.
    * 
    * @param file - the file
    * @param callback - the callback which the client has to implement
    */
   public abstract void getContent(File file, FileCallback callback);
   
   /**
    * Save locked file content
    * 
    * @param file - the file to save
    * @param lockToken - lock token
    * @param callback - the callback which the client has to implement
    */
   public abstract void saveContent(File file, String lockToken, FileContentSaveCallback callback);

   /**
    * Delete file or folder
    * 
    * @param item - the item to delete
    * @param callback - the callback which the client has to implement
    */
   public abstract void deleteItem(Item item, AsyncRequestCallback<Item> callback);
   
   /**
    * Move existed item to another location as path
    * 
    * @param item - item to move
    * @param destination - new item location
    * @param lockToken - lock token
    * @param callback - the callback which the client has to implement
    */
   public abstract void move(Item item, String destination, String lockToken, MoveItemCallback callback);

   /**
    * Copy item to another locations as path
    * 
    * @param item
    * @param destination
    * @param callback
    */
   public abstract void copy(Item item, String destination, CopyCallback callback);
   
   /**
    * Get all live properties and such properties:
    * 
    * <p>D:lockdiscovery</p>
    * <p>D:isversioned</p>
    * 
    * @param item - item to get properties.
    * @param callback - the callback which the client has to implement
    */
   public abstract void getProperties(Item item, ItemPropertiesCallback callback);
   
   /**
    * Get properties of file or folder
    * 
    * @param item - the item to get properties.
    * @param properties - the list of properties to get
    * @param callback - the callback which the client has to implement
    */
   public abstract void getProperties(Item item, List<QName> properties, ItemPropertiesCallback callback);

   /**
    * Save properties of file or folder
    * 
    * @param item - the item
    * @param lockToken - the lock token
    * @param callback - the callback which the client has to implement
    */
   public abstract void saveProperties(Item item, String lockToken, ItemPropertiesCallback callback);

   /**
    * Search files.
    * 
    * @param folder - selected folder (start point for search)
    * @param text - text in file
    * @param mimeType - proposed mime type of file
    * @param path - path
    * @param callback - the callback which the client has to implement
    */
   public abstract void search(Folder folder, String text, String mimeType, String path, AsyncRequestCallback<Folder> callback);
   

   /**
    * Lock item.
    * 
    * @param item - item to lock
    * @param timeout - the timeout
    * @param userName - user name
    * @param callback - the callback which the client has to implement
    */
   public abstract void lock(Item item, int timeout, String userName, ItemLockCallback callback);
   
   /**
    * Unlock item
    * 
    * @param item
    * @param lockToken
    * @param callback
    */
   public abstract void unlock(Item item, String lockToken, AsyncRequestCallback<Item> callback);
   
   /**
    * Get item's versions history
    * 
    * @param item
    * @param callback
    */
   public abstract void getVersions(Item item, VersionsCallback versionsCallback);

   
   /**
    * Save ACL of item
    * 
    * @param item
    * @param acl
    * @param lockToken
    * @param callback
    */
   public abstract void setACL(Item item, AccessControlList acl, String lockToken, AsyncRequestCallback<Item> callback);
   
}
