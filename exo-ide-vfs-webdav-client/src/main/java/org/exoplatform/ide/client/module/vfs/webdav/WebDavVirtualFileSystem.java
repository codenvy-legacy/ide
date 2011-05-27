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
package org.exoplatform.ide.client.module.vfs.webdav;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.CopyCallback;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.LockToken;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.framework.vfs.VersionsCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlList;
import org.exoplatform.ide.client.framework.vfs.callback.ItemLockCallback;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.CopyRequestMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.CopyResponseUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FileContentMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FileContentSavingResultUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FolderContentUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemPropertiesMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemPropertiesSavingResultUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemPropertiesUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemSetACLMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemSetACLUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemVersionsMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemVersionsUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.LockItemMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.LockItemUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.MoveResponseUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.PropFindRequestMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.SearchRequestMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.SearchResultUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.UnlockItemMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.UnlockItemUnmarshaller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class WebDavVirtualFileSystem extends VirtualFileSystem
{

   private Map<String, String> images;

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

   public static final String CONTEXT = "/jcr";

   public static final String DEFAULT_CHARSET = "charset=UTF-8";

   private HandlerManager eventBus;

   private Loader loader;

   private String restContext;

   public WebDavVirtualFileSystem(HandlerManager eventbus, Loader loader, Map<String, String> images, String restContext)
   {
      this.eventBus = eventbus;
      this.loader = loader;
      this.images = images;
      this.restContext = restContext;
   }

   public static native String javaScriptEncodeURI(String text) /*-{
                                                                return encodeURI(text);
                                                                }-*/;

   private ExceptionThrownEvent getErrorEvent(String message)
   {
      return new ExceptionThrownEvent(message);
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#getContent(org.exoplatform.ide.client.framework.vfs.File, org.exoplatform.ide.client.framework.vfs.FileCallback)
    */
   @Override
   public void getContent(File file, FileCallback callback)
   {
      String url = javaScriptEncodeURI(file.getHref());

      FileContentUnmarshaller unmarshaller = new FileContentUnmarshaller(file);
      callback.setResult(file);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      loader.setMessage(Messages.GET_FILE_CONTENT);

      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.GET)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#getChildren(org.exoplatform.ide.client.framework.vfs.Folder, org.exoplatform.ide.client.framework.vfs.ChildrenReceivedCallback)
    */
   @Override
   public void getChildren(Folder folder, AsyncRequestCallback<Folder> callback)
   {
      String href = folder.getHref();
      if (!href.endsWith("/"))
      {
         new Exception("Href must ends with \"/\"").printStackTrace();
      }

      String url = javaScriptEncodeURI(href);

      callback.setResult(folder);
      List<QName> propeties = new ArrayList<QName>();
      propeties.add(ItemProperty.LOCKDISCOVERY);
      propeties.add(ItemProperty.ISVERSIONED);
      
      PropFindRequestMarshaller marshaller = new PropFindRequestMarshaller(propeties);
      FolderContentUnmarshaller unmarshaller = new FolderContentUnmarshaller(folder, images);

      int[] acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);

      loader.setMessage(Messages.GET_FOLDER_CONTENT);

      AsyncRequest.build(RequestBuilder.POST, url, loader)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND).header(HTTPHeader.DEPTH, "1")
         .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8")
         .data(marshaller).send(callback);
   }

   @Override
   public void createFolder(Folder folder,  AsyncRequestCallback<Folder> callback)
   {
      String url = javaScriptEncodeURI(folder.getHref());
      callback.setResult(folder);

      callback.setEventBus(eventBus);

      loader.setMessage(Messages.CREATE_FOLDER);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MKCOL)
         .header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
   }

   @Override
   public void deleteItem(Item item, AsyncRequestCallback<Item> callback)
   {
      String url = javaScriptEncodeURI(item.getHref());

      callback.setResult(item);
      callback.setEventBus(eventBus);

      if (item instanceof File)
      {
         loader.setMessage(Messages.DELETE_FILE);
      }
      else
      {
         loader.setMessage(Messages.DELETE_FOLDER);
      }
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
         .header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
   }

   @Override
   public void saveContent(File file, String lockToken, FileContentSaveCallback callback)
   {
      String url = javaScriptEncodeURI(file.getHref());
      final boolean isNewFile = file.isNewFile();

      FileContentSaveCallback.FileData fileData = callback.new FileData(file, isNewFile);
      callback.setResult(fileData);
      
      FileContentMarshaller marshaller = new FileContentMarshaller(file);
      FileContentSavingResultUnmarshaller unmarshaller = new FileContentSavingResultUnmarshaller(file);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      loader.setMessage(Messages.SAVE_FILE_CONTENT);
      if (lockToken != null)
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
            .header(HTTPHeader.CONTENT_TYPE, file.getContentType() + "; " + DEFAULT_CHARSET)
            .header(HTTPHeader.CONTENT_NODETYPE, file.getJcrContentNodeType())
            .header(HTTPHeader.LOCKTOKEN, "<" + lockToken + ">").data(marshaller).send(callback);
      }
      else
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
            .header(HTTPHeader.CONTENT_TYPE, file.getContentType() + "; " + DEFAULT_CHARSET)
            .header(HTTPHeader.CONTENT_NODETYPE, file.getJcrContentNodeType()).data(marshaller).send(callback);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#getProperties(org.exoplatform.ide.client.framework.vfs.Item, org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback)
    */
   @Override
   public void getProperties(Item item, ItemPropertiesCallback callback)
   {
      List<QName> propeties = new ArrayList<QName>();
      
      propeties.add(ItemProperty.LOCKDISCOVERY);
      propeties.add(ItemProperty.ISVERSIONED);
      
      getProperties(item, propeties, callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#getProperties(org.exoplatform.ide.client.framework.vfs.Item, java.util.List, org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback)
    */
   @Override
   public void getProperties(Item item, List<QName> properties, ItemPropertiesCallback callback)
   {
      String url = javaScriptEncodeURI(item.getHref());

      PropFindRequestMarshaller marshaller = new PropFindRequestMarshaller(properties);

      ItemPropertiesUnmarshaller unmarshaller = new ItemPropertiesUnmarshaller(item, images);

      callback.setResult(item);

      int[] acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);

      loader.setMessage(Messages.GET_PROPERTIES);

      AsyncRequest.build(RequestBuilder.POST, url, loader)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8")
         .header(HTTPHeader.DEPTH, "0").data(marshaller)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#saveProperties(org.exoplatform.ide.client.framework.vfs.Item, org.exoplatform.ide.client.framework.vfs.LockToken)
    */
   @Override
   public void saveProperties(Item item, String lockToken, ItemPropertiesCallback callback)
   {
      String url = javaScriptEncodeURI(item.getHref());

      ItemPropertiesMarshaller marshaller = new ItemPropertiesMarshaller(item);
      callback.setResult(item);
      ItemPropertiesSavingResultUnmarshaller unmarshaller = new ItemPropertiesSavingResultUnmarshaller(item);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      loader.setMessage(Messages.SAVE_PROPERTIES);

      if (lockToken != null)
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPPATCH)
            .header(HTTPHeader.LOCKTOKEN, "<" + lockToken + ">")
            .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8").data(marshaller).send(callback);
      }
      else
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPPATCH)
            .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8").data(marshaller).send(callback);
      }

   }

   public void search(Folder folder, String text, String mimeType, String path, AsyncRequestCallback<Folder> callback)
   {
      String url = javaScriptEncodeURI(folder.getHref());
      SearchRequestMarshaller requestMarshaller = new SearchRequestMarshaller(text, mimeType, path);

      SearchResultUnmarshaller unmarshaller = new SearchResultUnmarshaller(restContext, eventBus, folder, images);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      loader.setMessage(Messages.SEARCH);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.SEARCH)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8").data(requestMarshaller).send(callback);
   }

   public void move(Item item, String destination, String lockToken, MoveItemCallback callback)
   {
      String url = javaScriptEncodeURI(item.getHref());
      callback.setResult(callback.new MoveItemData(item, item.getHref()));
      MoveResponseUnmarshaller unmarshaller = new MoveResponseUnmarshaller(item, destination);
      
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      if (item instanceof File)
      {
         
         loader.setMessage(Messages.MOVE_FILE);
         if (lockToken != null)
         {
            AsyncRequest.build(RequestBuilder.POST, url, loader)
               .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE).header(HTTPHeader.DESTINATION, destination)
               .header(HTTPHeader.CONTENT_LENGTH, "0").header(HTTPHeader.LOCKTOKEN, "<" + lockToken + ">")
               .send(callback);
         }
         else
         {

            AsyncRequest.build(RequestBuilder.POST, url, loader)
               .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE).header(HTTPHeader.DESTINATION, destination)
               .header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
         }
      }
      else
      {
         if (!url.endsWith("/"))
         {
            url += "/";
         }

         if (!destination.endsWith("/"))
         {
            destination += "/";
         }

         loader.setMessage(Messages.MOVE_FOLDER);

         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE).header(HTTPHeader.DESTINATION, destination)
            .header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
      }
   }

   @Override
   public void copy(Item item, String destination, CopyCallback callback)
   {
      String url = javaScriptEncodeURI(item.getHref());

      String destinationURL = destination;

      callback.setResult(callback.new CopyItemData(item, destination));

      int[] acceptStatus = new int[]{HTTPStatus.CREATED};
      CopyResponseUnmarshaller unmarshaller = new CopyResponseUnmarshaller();
      CopyRequestMarshaller marshaller = new CopyRequestMarshaller();
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);

      if (item instanceof File)
      {
         loader.setMessage(Messages.COPY_FILE);

         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.COPY).header(HTTPHeader.DESTINATION, destinationURL)
            .header(HTTPHeader.CONTENT_LENGTH, "0").data(marshaller).send(callback);
      }
      else
      {
         if (!url.endsWith("/"))
         {
            url += "/";
         }

         if (!destinationURL.endsWith("/"))
         {
            destinationURL += "/";
         }

         loader.setMessage(Messages.COPY_FOLDER);

         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.COPY).header(HTTPHeader.DESTINATION, destinationURL)
            .header(HTTPHeader.CONTENT_LENGTH, "0").data(marshaller).send(callback);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#lock(org.exoplatform.ide.client.framework.vfs.Item, int, java.lang.String, org.exoplatform.ide.client.framework.vfs.callback.ItemLockCallback)
    */
   @Override
   public void lock(Item item, int timeout, String userName, ItemLockCallback callback)
   {
      String url = javaScriptEncodeURI(item.getHref());
      
      LockToken lockToken = new LockToken();

      int[] acceptStatus = new int[]{HTTPStatus.OK};

      LockItemMarshaller marshaller = new LockItemMarshaller(userName);
      LockItemUnmarshaller unmarshaller = new LockItemUnmarshaller(lockToken);

      callback.setResult(callback.new ItemLockData(item, lockToken));

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);

      loader.setMessage(Messages.LOCK);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.LOCK)
         .header(HTTPHeader.TIMEOUT, "Infinite, Second-" + timeout).data(marshaller).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#unlock(org.exoplatform.ide.client.framework.vfs.Item, java.lang.String, org.exoplatform.ide.client.framework.vfs.ItemUnlockCallback)
    */
   @Override
   public void unlock(Item item, String lockToken, AsyncRequestCallback<Item> callback)
   {
      String url = javaScriptEncodeURI(item.getHref());

      UnlockItemMarshaller marshaller = new UnlockItemMarshaller();
      UnlockItemUnmarshaller unmarshaller = new UnlockItemUnmarshaller();

      callback.setResult(item);

      int[] acceptStatus = new int[]{HTTPStatus.NO_CONTENT, 1223}; //1223 for IE

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.UNLOCK)
         .header(HTTPHeader.LOCKTOKEN, "<" + lockToken + ">").data(marshaller).send(callback);

   }

   @Override
   public void getVersions(Item item, VersionsCallback callback)
   {
      String url = javaScriptEncodeURI(item.getHref());

      List<Version> versions = new ArrayList<Version>();

      ItemVersionsMarshaller marshaller = new ItemVersionsMarshaller();
      ItemVersionsUnmarshaller unmarshaller = new ItemVersionsUnmarshaller(item, versions, images);

      callback.setResult(callback.new VersionsData(item, versions));

      int[] acceptStatus = new int[]{HTTPStatus.MULTISTATUS};

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.REPORT)
         .data(marshaller).send(callback);

   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.VirtualFileSystem#setACL(org.exoplatform.ide.client.framework.vfs.Item, org.exoplatform.ide.client.framework.vfs.acl.AccessControlList, java.lang.String, org.exoplatform.gwtframework.commons.rest.ClientRequestCallback)
    */
   @Override
   public void setACL(Item item, AccessControlList acl, String lockToken, AsyncRequestCallback<Item> callback)
   {
      String url = javaScriptEncodeURI(item.getHref());

      ItemSetACLMarshaller marshaller = new ItemSetACLMarshaller(acl);
      ItemSetACLUnmarshaller unmarshaller = new ItemSetACLUnmarshaller();

      int[] acceptStatus = new int[]{HTTPStatus.OK};

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);

      if (lockToken != null)
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.ACL)
            .header(HTTPHeader.LOCKTOKEN, "<" + lockToken + ">").data(marshaller).send(callback);
      }
      else
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.ACL)
            .data(marshaller).send(callback);
      }
   }

}
