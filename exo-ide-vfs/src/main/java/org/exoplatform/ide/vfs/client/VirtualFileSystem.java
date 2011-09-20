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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPHeader;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Class provide communication with server side of VirtualFileSystem via REST.
 * 
 * @author vetal
 * 
 */
public class VirtualFileSystem
{
   /**
    * VFS instance
    */
   private static VirtualFileSystem instance;

   //private final HandlerManager eventBus;

   /**
    * Fully qualified URL to root folder of VFS
    */
   private final String workspaceURL;

   /**
    * @see org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo
    */
   private final VirtualFileSystemInfo info;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   /**
    * @param eventBus
    * @param workspaceURL
    */
   public VirtualFileSystem(String workspaceURL)
   {
      this(workspaceURL, null);
   }

   VirtualFileSystem(String workspaceURL, VirtualFileSystemInfo info)
   {
      instance = this;

      this.workspaceURL = workspaceURL;

      this.info = info;
   }

   /**
    * Set information about server virtual file system and its capabilities via REST. And initialize VFS instance.
    * 
    * @param callback the callback for HTTP request
    * @param loader the
    * @param eventBus
    * @param workspaceURL
    */
   public void init(AsyncRequestCallback<VirtualFileSystemInfo> callback) throws RequestException
   {
      //      VirtualFileSystem fs = new VirtualFileSystem(workspaceURL);
      //      fs.info = new VirtualFileSystemInfo();
      //      VFSInfoUnmarshaller unmarshaller = new VFSInfoUnmarshaller(fs.info);
      //      callback.setResult(fs.info);
      //      callback.setEventBus(eventBus);
      //      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, workspaceURL).send(callback);
   }

   //   /**
   //    * Set information about server virtual file system and its capabilities.
   //    * And initialize VFS instance.
   //    * 
   //    * @param info
   //    * @param eventBus
   //    * @param workspaceURL
   //    */
   //   public static void init(VirtualFileSystemInfo info, String workspaceURL)
   //   {
   //      VirtualFileSystem fs = new VirtualFileSystem(workspaceURL);
   //      fs.info = info;
   //   }

   /**
    * @return information about server virtual file system and its capabilities.
    */
   public VirtualFileSystemInfo getInfo()
   {
      return info;
   }

   /**
    * @return url to root folder of vfs
    */
   public String getURL()
   {
      return workspaceURL;
   }

   /**
    * Get folder content
    * 
    * @param path
    */
   public void getChildren(FolderModel folder, AsyncRequestCallback<List<Item>> callback) throws RequestException
   {
      //      ItemList<Item> items = new ItemList<Item>();
      //      folder.setChildren(items);

      //callback.setResult(items);
      //ChildrenUnmarshaller unmarshaller = new ChildrenUnmarshaller(callback.getResult());

      //callback.setEventBus(eventBus);
      //callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, folder.getLinkByRelation(Link.REL_CHILDREN).getHref()).send(
         callback);

   }

   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFolder(org.exoplatform.ide.vfs.shared.Folder parent, AsyncRequestCallback<FolderModel> callback)
      throws RequestException
   {

      String name = callback.getPayload().getName();
      String url = parent.getLinkByRelation(Link.REL_CREATE_FOLDER).getHref() + "?name=" + name;

      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Create new project
    * 
    * @param path
    */
   public void createProject(org.exoplatform.ide.vfs.shared.Folder parent, AsyncRequestCallback<ProjectModel> callback)
      throws RequestException
   {

      ProjectModel newProject = callback.getPayload();
      String url =
         parent.getLinkByRelation(Link.REL_CREATE_PROJECT).getHref() + "?name=" + newProject.getName()
            + "&type=" + newProject.getProjectType();
      AsyncRequest.build(RequestBuilder.POST, url)
         .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(newProject.getProperties()).toString())
         .header(HTTPHeader.CONTENT_TYPE, "application/json").send(callback);
   }

   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFile(org.exoplatform.ide.vfs.shared.Folder parent, AsyncRequestCallback<FileModel> callback)
      throws RequestException
   {

      FileModel newFile = callback.getPayload();
      String url = parent.getLinkByRelation(Link.REL_CREATE_FILE).getHref() + "?name=" + newFile.getName();

      AsyncRequest.build(RequestBuilder.POST, url).data(newFile.getContent())
         .header(HTTPHeader.CONTENT_TYPE, newFile.getMimeType()).send(callback);
   }

   /**
    * Get content of the file.
    * 
    * @param file
    */
   public void getContent(AsyncRequestCallback<FileModel> callback) throws RequestException
   {
      String url = callback.getPayload().getLinkByRelation(Link.REL_CONTENT).getHref();
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * Update content of file
    * 
    * @param file
    * @param callback
    * @param loader
    */
   public void updateContent(FileModel file, AsyncRequestCallback<FileModel> callback) throws RequestException
   {
      String url =
         file.getLinkByRelation(Link.REL_CONTENT).getHref()
            + ((file.isLocked()) ? "?lockToken=" + file.getLock().getLockToken() : "");
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.CONTENT_TYPE, file.getMimeType())
         .data(file.getContent()).send(callback);
   }

   /**
    * Delete item (file or folder)
    * 
    * @param item
    * @param callback
    * @param loader
    */
   public void delete(Item item, AsyncRequestCallback<String> callback) throws RequestException
   {
      String lockStr = "";
      if (item.getItemType() == ItemType.FILE && ((FileModel)item).isLocked())
         lockStr = "?lockToken=" + ((FileModel)item).getLock();

      String url = item.getLinkByRelation(Link.REL_DELETE).getHref() + lockStr;

      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Copy item (file or folder)
    * 
    * @param source the source item
    * @param destination id of destination file folder
    * @param callback
    * @param loader
    */
   public void copy(Item source, String destination, AsyncRequestCallback<StringBuilder> callback)
      throws RequestException
   {
      String url =
         source.getLinkByRelation(Link.REL_COPY).getHref() + (destination != null ? "?parentId=" + destination : "");
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Move item (file or folder)
    * 
    * @param source the source item
    * @param destination id of destination file folder
    * @param lockToken
    * @param callback
    * @param loader
    */
   public void move(Item source, String destination, String lockToken, AsyncRequestCallback<StringBuilder> callback)
      throws RequestException
   {
      String lockStr = "";
      if (source.getItemType() == ItemType.FILE && ((FileModel)source).isLocked())
         lockStr = "?lockToken=" + ((FileModel)source).getLock();
      String url = source.getLinkByRelation(Link.REL_MOVE).getHref() + "?parentId=" + destination + lockStr;
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   public void rename(Item item, String mediaType, String newname, String lockToken,
      AsyncRequestCallback<StringBuilder> callback) throws Exception
   {
      throw new Exception("Method \"rename\" is not implemented");
   }

   /**
    * Place lock on File item.
    * @param file to be locked
    * @param callback
    * @throws RequestException
    */
   public void lock(FileModel file, AsyncRequestCallback<LockToken> callback) throws RequestException
   {
      String url = file.getLinks().get("lock").getHref();
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Remove lock from file.
    * @param file to be unlocked
    * @param lockToken lock token
    * @param callback
    * @throws RequestException
    */
   public void unlock(FileModel file, String lockToken, AsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = file.getLinks().get("unlock").getHref() + "?lockToken=" + lockToken;
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Get item by location(href).
    * @param location of item
    * @param callback
    * @throws RequestException 
    */
   public void getItemByLocation(String location, AsyncRequestCallback<? extends Item> callback)
      throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, location).send(callback);
   }

}
