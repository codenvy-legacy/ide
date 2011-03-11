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
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.client.model.Folder;
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

   private String workspaceURL;
   
   private VirtualFileSystemInfo info;

   //private Loader loader;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   private VirtualFileSystem(HandlerManager eventBus, String workspaceURL)
   {
      instance = this;

      this.eventBus = eventBus;
      this.workspaceURL = workspaceURL;     
   }

   public static void init(AsyncRequestCallback<VirtualFileSystemInfo> callback, Loader loader,
         HandlerManager eventBus, String workspaceURL)
   {
      
      VirtualFileSystem fs = new VirtualFileSystem(eventBus, workspaceURL);

      fs.info = new VirtualFileSystemInfo();

      VFSInfoUnmarshaller unmarshaller = new VFSInfoUnmarshaller(fs.info);
     
      callback.setResult(fs.info);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, workspaceURL, loader).send(callback);
   }
   
   public VirtualFileSystemInfo getInfo()
   {
      return info;
   }
   
   public String getURL() 
   {
      return workspaceURL;
   }

   /**
    * Get folder content
    * 
    * @param path
    */
   public void getChildren(Folder folder, AsyncRequestCallback<ItemList<Item>> callback, Loader loader)
   {
     // String url = encodeURI(workspace + "/children/" + id);
      
      //String url = encodeURI(workspaceURL + "/children/" + id);

      ItemList<Item> items = new ItemList<Item>();
      callback.setResult(items);
      ChildrenUnmarshaller unmarshaller = new ChildrenUnmarshaller(folder);

//      String errorMessage = "Service is not deployed.<br>Parent folder not found.";
//      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

//      loader.setMessage(Messages.GET_FOLDER_CONTENT);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      
      System.out.println("getChildren "+folder.getLinkByRelation(Folder.REL_CHILDREN).getHref());
      
      AsyncRequest.build(RequestBuilder.GET, folder.getLinkByRelation(Folder.REL_CHILDREN).getHref(), loader).send(callback);

   }

   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFolder(String parentId, String name, AsyncRequestCallback<String> callback, Loader loader)
   {
      String url = workspaceURL + "/folder" + parentId;
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

      callback.setResult(newFolderID);

//      String errorMessage = "Service is not deployed.<br>Resource already exist.<br>Parent folder not found.";
//      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      loader.setMessage(Messages.COPY_FOLDER);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * Get content of the file.
    * 
    * @param file
    */
   public void getFileContent(String id, AsyncRequestCallback<String> callback, Loader loader)
   {
      String url = workspaceURL + "/content" + id;
      
      FileContentUnmarshaller unmarshaller = new FileContentUnmarshaller(callback);
      
//      String errorMessage = " Service is not deployed.<br>Resource not found.";
//      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      loader.setMessage(Messages.GET_FILE_CONTENT);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
      
   }

   public void saveFileContent(String id, String mediaType, String content, String lockToken, Loader loader)
   {
      
   }

   public void copy(String source, String destination, Loader loader)
   {
   }

   public void move(String id, String parentId, String lockToken, Loader loader)
   {

   }

   public void delete(String id, String lockToken)
   {

   }

   public void rename(String id, String mediaType, String newname, String lockToken, Loader loader)
   {

   }

   public void lock(String id, Loader loader)
   {
   }

   public void unlock(String id, String lockToken, Loader loader)
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
