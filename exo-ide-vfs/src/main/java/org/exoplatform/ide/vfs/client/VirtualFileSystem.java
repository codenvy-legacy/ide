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
import com.google.gwt.http.client.URL;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.copy.MimeType;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.HashMap;
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
   private VirtualFileSystemInfo info;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   /**
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
      this.info = callback.getPayload();
      AsyncRequest.build(RequestBuilder.GET, workspaceURL).send(callback);
   }

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
   public void getChildren(Folder folder, AsyncRequestCallback<List<Item>> callback) throws RequestException
   {
      String param = "propertyFilter=" + PropertyFilter.ALL;
      AsyncRequest.build(RequestBuilder.GET, folder.getLinkByRelation(Link.REL_CHILDREN).getHref() + "?" + param).send(
         callback);
   }

   /**
    * Create new folder.
    * 
    * @param path
    */
   public void createFolder(org.exoplatform.ide.vfs.shared.Folder parent, AsyncRequestCallback<FolderModel> callback)
      throws RequestException
   {
      String name = callback.getPayload().getName();
      String url = parent.getLinkByRelation(Link.REL_CREATE_FOLDER).getHref();
      String urlString = URL.decode(url).replace("[name]", name);
      urlString = URL.encode(urlString);      
      AsyncRequest.build(RequestBuilder.POST, urlString).send(callback);
   }

   /**
    * Create new project.
    * 
    * @param parent parent folder of the new project
    * @param callback callback
    * @throws RequestException
    */
   public void createProject(org.exoplatform.ide.vfs.shared.Folder parent, AsyncRequestCallback<ProjectModel> callback)
      throws RequestException
   {
      ProjectModel newProject = callback.getPayload();
      String url = parent.getLinkByRelation(Link.REL_CREATE_PROJECT).getHref();
      url = URL.decode(url).replace("[name]", newProject.getName());
      url = url.replace("[type]", newProject.getProjectType());
      url = URL.encode(url);
      AsyncRequest.build(RequestBuilder.POST, url)
         .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(newProject.getProperties()).toString())
         .header(HTTPHeader.CONTENT_TYPE, "application/json").send(callback);
   }

   /**
    * Create new file.
    * 
    * @param parent parent folder of the new file 
    * @param callback callback/
    * @throws RequestException
    */
   public void createFile(org.exoplatform.ide.vfs.shared.Folder parent, AsyncRequestCallback<FileModel> callback)
      throws RequestException
   {
      FileModel newFile = callback.getPayload();
      String url = parent.getLinkByRelation(Link.REL_CREATE_FILE).getHref();
      url = URL.decode(url).replace("[name]", newFile.getName());
      url = URL.encode(url);
      AsyncRequest.build(RequestBuilder.POST, url).data(newFile.getContent())
         .header(HTTPHeader.CONTENT_TYPE, newFile.getMimeType()).send(callback);
   }

   /**
    * Get content of the file.
    * 
    * @param callback callback
    * @throws RequestException
    */
   public void getContent(AsyncRequestCallback<FileModel> callback) throws RequestException
   {
      String url = callback.getPayload().getLinkByRelation(Link.REL_CONTENT).getHref();
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * Update content of the file.
    * 
    * @param file
    * @param callback
    * @throws RequestException
    */
   public void updateContent(FileModel file, AsyncRequestCallback<FileModel> callback) throws RequestException
   {
      //TODO check with lock
      String url = file.getLinkByRelation(Link.REL_CONTENT).getHref();
      url += (file.isLocked()) ? "?lockToken=" + file.getLock().getLockToken() : "";
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
      //TODO check with lock
      String url = item.getLinkByRelation(Link.REL_DELETE).getHref();
      if (ItemType.FILE == item.getItemType() && ((FileModel)item).isLocked())
      {
         url = URL.decode(url).replace("[lockToken]", ((FileModel)item).getLock().getLockToken());
      }
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Copy item (file or folder)
    * 
    * @param source item to copy
    * @param destination id of the destination folder
    * @param callback callback
    * @throws RequestException
    */
   public void copy(Item source, String destination, AsyncRequestCallback<StringBuilder> callback)
      throws RequestException
   {
      String url = source.getLinkByRelation(Link.REL_COPY).getHref();
      if (destination != null)
      {
         url = URL.decode(url).replace("[parentId]", destination);
      }
      url = URL.encode(url);
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Move item (file or folder)
    * 
    * @param source the source item
    * @param destination id of destination file folder
    * @param lockToken
    * @param callback
    * @throws RequestException
    */
   public void move(Item source, String destination, String lockToken, AsyncRequestCallback<ItemWrapper> callback)
      throws RequestException
   {
      //TODO check with locks
      String url = source.getLinkByRelation(Link.REL_MOVE).getHref();
      url = URL.decode(url).replace("[parentId]", destination);
      if (ItemType.FILE == source.getItemType() && ((FileModel)source).isLocked())
      {
         url = url.replace("[lockToken]", ((FileModel)source).getLock().getLockToken());
      }
      url = URL.encode(url);
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * @param item item to rename
    * @param mediaType media type to change (may be <code>null</code> in case of just renaming operation)
    * @param newname new name of the item (may be <code>null</code> in case of just changing media type)
    * @param lockToken lock token
    * @param callback  callback user has to implement to handle response
    * @throws RequestException
    */
   public void rename(Item item, String mediaType, String newname, String lockToken,
      AsyncRequestCallback<ItemWrapper> callback) throws RequestException
   {
      String url = item.getLinkByRelation(Link.REL_RENAME).getHref();
      url = URL.decode(url);
      url =
         (mediaType != null && !mediaType.isEmpty()) ? url.replace("[mediaType]", mediaType) : url.replace(
            "mediaType=[mediaType]", "");
      url =
         (newname != null && !newname.isEmpty()) ? url.replace("[newname]", newname) : url.replace("newname=[newname]",
            "");

      if (ItemType.FILE == item.getItemType() && ((FileModel)item).isLocked())
      {
         url = url.replace("[lockToken]", ((FileModel)item).getLock().getLockToken());
      }
      
      url = url.replace("?&", "?");
      url = url.replaceAll("&&", "&");
      url = URL.encode(url);
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Place lock on File item.
    * @param file to be locked
    * @param callback
    * @throws RequestException
    */
   public void lock(FileModel file, AsyncRequestCallback<LockToken> callback) throws RequestException
   {
      String url = file.getLinkByRelation(Link.REL_LOCK).getHref();
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
      String url = file.getLinkByRelation(Link.REL_UNLOCK).getHref();
      url = URL.decode(url).replace("[lockToken]", lockToken);
      AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

   /**
    * Get item by path.
    * Path MUST not start with "\"
    * @param path to item
    * @param callback
    * @throws RequestException
    */
   public void getItemByPath(String path, AsyncRequestCallback<ItemWrapper> callback) throws RequestException
   {
      if(path.startsWith("/"))
         path = path.substring(1);
      String url = info.getUrlTemplates().get((Link.REL_ITEM_BY_PATH)).getHref();
      url = URL.decode(url).replace("[path]", path);
      url = URL.encode(url);
      AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).send(callback);
   }

   /**
    * Get item by id.
    * @param id Id of the Item
    * @param callback
    * @throws RequestException
    */
   public void getItemById(String id, AsyncRequestCallback<ItemWrapper> callback) throws RequestException
   {
      String url = info.getUrlTemplates().get((Link.REL_ITEM)).getHref();
      url = URL.decode(url).replace("[id]", id);
      AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).send(callback);
   }

   /**
    * @param query
    * @param maxItems
    * @param skipCount
    * @param callback
    * @throws RequestException
    */
   public void search(HashMap<String, String> query, int maxItems, int skipCount,
      AsyncRequestCallback<List<Item>> callback) throws RequestException
   {
      String url = info.getUrlTemplates().get(Link.REL_SEARCH_FORM).getHref();
      url = URL.decode(url).replace("[maxItems]", String.valueOf(maxItems));
      url = url.replace("[skipCount]", String.valueOf(skipCount));
      url = url.replace("[propertyFilter]", PropertyFilter.ALL);
      String data = "";
      for (String key : query.keySet())
      {
         String value = query.get(key);
         data += (value != null && !value.isEmpty()) ? key + "=" + value + "&" : "";
      }
      
      url = URL.encode(url);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED).data(data).send(callback);
   }

   /**
    * Update item's properties.
    * 
    * @param item item to update with properties
    * @param lockToken lock token
    * @param callback callback user has to implement to handle response
    * @throws RequestException
    */
   public void updateItem(Item item, String lockToken, AsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = item.getLinkByRelation(Link.REL_SELF).getHref();
      url += (lockToken != null) ? "?lockToken=" + lockToken : "";
      AsyncRequest.build(RequestBuilder.POST, url)
         .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(item.getProperties()).toString())
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }
   
}
