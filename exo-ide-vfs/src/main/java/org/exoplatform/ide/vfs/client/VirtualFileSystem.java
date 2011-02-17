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
package org.exoplatform.ide.vfs.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.callback.ChildrenReceivedCallback;
import org.exoplatform.ide.vfs.client.callback.FileContentCallback;
import org.exoplatform.ide.vfs.client.callback.FolderCreatedCallback;
import org.exoplatform.ide.vfs.client.callback.VfsInfoCallback;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

public class VirtualFileSystem
{
   public interface Messages
   {

      static final String GET_FILE_CONTENT = "Reading file content...";

      static final String GET_FOLDER_CONTENT = "Reading folder content...";

      static final String CREATE_FOLDER = "Creating folder...";

      static final String DELETE_FOLDER = "Deleting folder...";

      static final String DELETE_FILE = "Deleting file...";

      static final String SAVE_FILE_CONTENT = "Saving file...";

      static final String GET_PROPERTIES = "Reading properties...";

      static final String SAVE_PROPERTIES = "Saving properties...";

      static final String SEARCH = "Searching...";

      static final String MOVE_FILE = "Moving file...";

      static final String MOVE_FOLDER = "Moving folder...";

      static final String COPY_FILE = "Copying file...";

      static final String COPY_FOLDER = "Copying folder...";

      static final String LOCK = "Locking...";

   }

   private static VirtualFileSystem instance;

   private final HandlerManager eventBus;

   private String workspace;

   private Loader loader;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   public VirtualFileSystem(HandlerManager eventBus, Loader loader, String workspace)
   {
      instance = this;

      this.eventBus = eventBus;
      this.loader = loader;
      this.workspace = workspace;
   }

   public void getVFSInfo(VfsInfoCallback vfsInfoCallback)
   {
      String url = encodeURI(workspace + "/");

      VirtualFileSystemInfo virtualFileSystemInfo = new VirtualFileSystemInfo();

      VFSInfoUnmarshaller unmarshaller = new VFSInfoUnmarshaller(virtualFileSystemInfo);
      vfsInfoCallback.setVirtualFileSystemInfo(virtualFileSystemInfo);

//      String errorMessage = " Service is not deployed.<br>Resource not found.";
//      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, vfsInfoCallback);

      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * Get folder content
    * 
    * @param path
    */
   public void getChildren(String id, ChildrenReceivedCallback childrenCallback)
   {
      String url = encodeURI(workspace + "/children/" + id);

      ItemList<Item> items = new ItemList<Item>();
      childrenCallback.setItems(items);
      ChildrenUnmarshaller unmarshaller = new ChildrenUnmarshaller(id, items);

//      String errorMessage = "Service is not deployed.<br>Parent folder not found.";
//      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      loader.setMessage(Messages.GET_FOLDER_CONTENT);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, childrenCallback);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);

   }

   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFolder(String parentId, String name, FolderCreatedCallback folderCallback)
   {
      String url = workspace + "/folder" + parentId;
      if (url.endsWith("/"))
      {
         url = url.substring(0, url.length() - 1);
      }

      url += "?name=" + name;
      url = encodeURI(url);

      String newFolderID = parentId;
      if (!newFolderID.endsWith("/"))
         newFolderID += "/";
      newFolderID += name;

      folderCallback.setFolderId(newFolderID);

//      String errorMessage = "Service is not deployed.<br>Resource already exist.<br>Parent folder not found.";
//      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      loader.setMessage(Messages.COPY_FOLDER);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, folderCallback);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * Get content of the file.
    * 
    * @param file
    */
   public void getFileContent(String id, FileContentCallback fileContentCallback)
   {
      String url = workspace + "/content" + id;
      
      FileContentUnmarshaller unmarshaller = new FileContentUnmarshaller(fileContentCallback);
      
//      String errorMessage = " Service is not deployed.<br>Resource not found.";
//      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      loader.setMessage(Messages.GET_FILE_CONTENT);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, fileContentCallback);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
      
   }

   public void saveFileContent(String id, String mediaType, String content, String lockToken)
   {
      
   }

   public void copy(String source, String destination)
   {
   }

   public void move(String id, String parentId, String lockToken)
   {

   }

   public void delete(String id, String lockToken)
   {

   }

   public void rename(String id, String mediaType, String newname, String lockToken)
   {

   }

   public void lock(String id)
   {
   }

   public void unlock(String id, String lockToken)
   {

   }

   private ExceptionThrownEvent getErrorEvent(String message)
   {
      return new ExceptionThrownEvent(message);
   }
   
   /**
    * @param url
    * @return result of javaScript function <code>encodeURI(url)</code>
    */
   public static native String encodeURI(String url) /*-{
       return encodeURI(url);
     }-*/;
   
}
