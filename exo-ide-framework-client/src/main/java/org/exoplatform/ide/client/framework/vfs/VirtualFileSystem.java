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

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.ClientRequestCallback;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlList;
import org.exoplatform.ide.client.framework.vfs.callback.ItemLockCallback;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback;


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
    * 
    * @param childrenReceivedCallback - the callback which the client has to implement
    */
   public abstract void getChildren(Folder folder, ChildrenReceivedCallback childrenReceivedCallback);

   /**
    * Create new folder
    * 
    * @param folder - the folder to create
    * @param folderCallback - the callback which the client has to implement
    */
   public abstract void createFolder(Folder folder, FolderCreateCallback folderCallback);

   /**
    * Get content of the file.
    * 
    * @param file - the file
    * @param fileCallback - the callback which the client has to implement
    */
   public abstract void getContent(File file, FileCallback fileCallback);
   
   /**
    * Save locked file content
    * 
    * @param file - the file to save
    * @param lockToken - lock token
    * @param fileCallback - the callback which the client has to implement
    */
   public abstract void saveContent(File file, String lockToken, FileContentSaveCallback fileCallback);

   /**
    * Delete file or folder
    * 
    * @param item - the item to delete
    * @param itemCallback - the callback which the client has to implement
    */
   public abstract void deleteItem(Item item, ClientRequestCallback itemCallback);
   
   /**
    * Move existed item to another location as path
    * 
    * @param item - item to move
    * @param destination - new item location
    * @param lockToken - lock token
    * @param moveCallback - the callback which the client has to implement
    */
   public abstract void move(Item item, String destination, String lockToken, MoveItemCallback moveCallback);

   /**
    * Copy item to another locations as path
    * 
    * @param item
    * @param destination   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)

    */
   public abstract void copy(Item item, String destination, CopyCallback copyCallback);
   
   /**
    * Get all live properties and such properties:
    * 
    * <p>D:lockdiscovery</p>
    * <p>D:isversioned</p>
    * 
    * @param item - item to get properties.
    * @param itemCallback - the callback which the client has to implement
    */
   public abstract void getPropertiesCallback(Item item, ItemPropertiesCallback itemCallback);
   
   /**
    * Get properties of file or folder
    * 
    * @param item - the item to get properties.
    * @param properties - the list of properties to get
    * @param itemCallback - the callback which the client has to implement
    */
   public abstract void getPropertiesCallback(Item item, List<QName> properties, ItemPropertiesCallback itemCallback);

   /**
    * Save properties of file or folder
    * 
    * @param item - the item
    * @param lockToken - the lock token
    * @param itemCallback - the callback which the client has to implement
    */
   public abstract void saveProperties(Item item, String lockToken, ItemPropertiesCallback itemCallback);

   /**
    * Search files.
    * 
    * @param folder - selected folder (start point for search)
    * @param text - text in file
    * @param mimeType - proposed mime type of file
    * @param path - path
    * @param searchCallback - the callback which the client has to implement
    */
   public abstract void search(Folder folder, String text, String mimeType, String path, ClientRequestCallback searchCallback);
   

   /**
    * Lock item.
    * 
    * @param item - item to lock
    * @param timeout - the timeout
    * @param userName - user name
    * @param itemCallback - the callback which the client has to implement
    */
   public abstract void lock(Item item, int timeout, String userName, ItemLockCallback itemCallback);
   
   /**
    * Unlock item
    * 
    * @param item
    * @param lockToken
    * @param itemCallback
    */
   public abstract void unlock(Item item, String lockToken, ItemUnlockCallback itemCallback);
   
   /**
    * Get item's versions history
    * 
    * @param item
    * @param versionsCallback
    */
   public abstract void getVersions(Item item, VersionsCallback versionsCallback);

   
   /**
    * Save ACL of item
    * 
    * @param item
    * @param acl
    * @param lockToken
    * @param aclCallback
    */
   public abstract void setACL(Item item, AccessControlList acl, String lockToken, ClientRequestCallback aclCallback);
   
}
