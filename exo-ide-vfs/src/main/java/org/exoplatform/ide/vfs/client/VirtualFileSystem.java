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

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.client.model.File;
import org.exoplatform.ide.vfs.client.model.Folder;
import org.exoplatform.ide.vfs.client.model.Project;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

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
      ItemList<Item> items = new ItemList<Item>();
      folder.setChildren(items);
      
      callback.setResult(items);
      ChildrenUnmarshaller unmarshaller = new ChildrenUnmarshaller(folder);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      
      AsyncRequest.build(RequestBuilder.GET, folder.getLinkByRelation(Folder.REL_CHILDREN).getHref(), loader).send(callback);

   }

   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFolder(Folder parent, String name, AsyncRequestCallback<Folder> callback, Loader loader)
   {
      
      String url = parent.getLinkByRelation(Folder.REL_CREATE_FOLDER).getHref()+"?name="+name;

      Folder newFolder = new Folder();
      
      callback.setResult(newFolder);
      callback.setPayload(new ItemUnmarshaller(newFolder));

      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }
   
   
   /**
    * Create new project
    * 
    * @param path
    */
   public void createProject(Folder parent, String name, String type, 
         List<Property> properties, AsyncRequestCallback<Project> callback, 
         Loader loader)
   {
      
      String url = parent.getLinkByRelation(Folder.REL_CREATE_PROJECT).getHref()+
      "?name="+name+"&type="+type;

      Project newProject = new Project();
      
      callback.setResult(newProject);
      callback.setPayload(new ItemUnmarshaller(newProject));

      AsyncRequest.build(RequestBuilder.POST, url, loader).
      data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(properties).toString()).
      send(callback);
   }
   
   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFile(File newFile,
         AsyncRequestCallback<File> callback, Loader loader)
   {
      
 //     System.out.println("createFile "+
 //           newFile.getParent().getLinkByRelation(Folder.REL_CREATE_FILE)+" "+newFile.getContent());
      
      String url = newFile.getParent().getLinkByRelation(Folder.REL_CREATE_FILE).getHref()+
      "?name="+newFile.getName();
      
       File file = new File();
      
      callback.setResult(file);
      callback.setPayload(new ItemUnmarshaller(file));
      
 
      AsyncRequest.build(RequestBuilder.POST, url, loader).data(newFile.getContent()).
      header(HTTPHeader.CONTENT_TYPE, newFile.getMimeType()).send(callback);
   }

   /**
    * Get content of the file.
    * 
    * @param file
    */
   public void getContent(File file, AsyncRequestCallback<String> callback, Loader loader)
   {
      //System.out.println("getContent "+ file.getPath());
      
      String url = file.getLinkByRelation(File.REL_CONTENT).getHref();
      
      callback.setResult(file.getContent());
      callback.setPayload(new FileContentUnmarshaller(callback));
     
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
      
   }

   public void updateContent(File file, AsyncRequestCallback<String> callback, Loader loader)
   {

      String url = file.getLinkByRelation(File.REL_CONTENT).getHref()+
      //"?mediaType="+file.getMimeType()+
      ((file.isLocked())?"?lockToken="+file.getLockToken():"");

      AsyncRequest.build(RequestBuilder.POST, url, loader).
      header(HTTPHeader.CONTENT_TYPE, file.getMimeType()).
      data(file.getContent()).send(callback);
   }
   
   public void delete(Item item, AsyncRequestCallback<String> callback, Loader loader)
   {
      String lockStr = "";
      if(item.getItemType() == ItemType.FILE && ((File)item).isLocked())
         lockStr = "?lockToken="+((File)item).getLockToken();

      String url = item.getLinkByRelation(Item.REL_DELETE).getHref()+lockStr;

      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   public void copy(String source, String destination, Loader loader)
   {
   }

   public void move(String id, String parentId, String lockToken, Loader loader)
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
   
}
