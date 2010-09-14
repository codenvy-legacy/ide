/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

package org.exoplatform.ide.client.module.vfs.webdav;

import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.LockToken;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.ChildrenReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.CopyCompleteEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FolderCreatedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ide.client.module.vfs.api.event.SearchResultReceivedEvent;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.CopyRequestMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.CopyResponseUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FileContentMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FileContentSavingResultUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.FolderContentUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemPropertiesMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemPropertiesSavingResultUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.ItemPropertiesUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.LockItemMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.LockItemUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.MoveResponseUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.SearchRequestMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.SearchResultUnmarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.UnlockItemMarshaller;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.UnlockItemUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

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

   @Override
   public void getContent(File file)
   {
      String url = javaScriptEncodeURI(file.getHref());

      FileContentUnmarshaller unmarshaller = new FileContentUnmarshaller(file);
      FileContentReceivedEvent event = new FileContentReceivedEvent(file);

      String errorMessage = " Service is not deployed.<br>Resource not found.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);

      loader.setMessage(Messages.GET_FILE_CONTENT);

      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.GET)
         .send(callback);
   }

   @Override
   public void getChildren(Folder folder)
   {
      String url = javaScriptEncodeURI(folder.getHref());

      ChildrenReceivedEvent event = new ChildrenReceivedEvent(folder);
      FolderContentUnmarshaller unmarshaller = new FolderContentUnmarshaller(folder, images);

      String errorMessage = "Service is not deployed.<br>Parent folder not found.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      int[] acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent, acceptStatus);

      loader.setMessage(Messages.GET_FOLDER_CONTENT);

      AsyncRequest.build(RequestBuilder.GET, url, loader)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND).header(HTTPHeader.DEPTH, "1").send(callback);
   }

   @Override
   public void createFolder(Folder folder)
   {
      String url = javaScriptEncodeURI(folder.getHref());
      FolderCreatedEvent event = new FolderCreatedEvent(folder);

      String errorMessage = "Service is not deployed.<br>Resource already exist.<br>Parent folder not found.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);

      loader.setMessage(Messages.CREATE_FOLDER);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MKCOL)
         .header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
   }

   @Override
   public void deleteItem(Item item)
   {
      String url = javaScriptEncodeURI(item.getHref());
      ItemDeletedEvent event = new ItemDeletedEvent(item);

      String errorMessage = "Service is not deployed.<br>Resource not found.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);

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
   public void saveContent(File file)
   {
      saveContent(file, null);
      
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem#saveContent(org.exoplatform.ide.client.module.vfs.api.File, org.exoplatform.ide.client.module.vfs.api.LockToken)
    */
   @Override
   public void saveContent(File file, LockToken lockToken)
   {
      String url = javaScriptEncodeURI(file.getHref());
      boolean isNewFile = file.isNewFile();

      FileContentMarshaller marshaller = new FileContentMarshaller(file);
      FileContentSavedEvent event = new FileContentSavedEvent(file, isNewFile);
      FileContentSavingResultUnmarshaller unmarshaller = new FileContentSavingResultUnmarshaller(file);

      String errorMessage = "Service is not deployed.<br>Resource not found.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);

      loader.setMessage(Messages.SAVE_FILE_CONTENT);
      if (lockToken != null)
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
            .header(HTTPHeader.CONTENT_TYPE, file.getContentType() + "; " + DEFAULT_CHARSET)
            .header(HTTPHeader.CONTENT_NODETYPE, file.getJcrContentNodeType())
            .header(HTTPHeader.LOCKTOKEN, "<" + lockToken.getLockToken() + ">")
            .data(marshaller)
            .send(callback);
         
//         AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
//            .header(HTTPHeader.CONTENT_TYPE, file.getContentType())
//            .header(HTTPHeader.CONTENT_NODETYPE, file.getJcrContentNodeType())
//            .header(HTTPHeader.LOCKTOKEN, "<" + lockToken.getLockToken() + ">").data(marshaller).send(callback);
      }
      else
      {
         AsyncRequest.build(RequestBuilder.POST, url, loader)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
         .header(HTTPHeader.CONTENT_TYPE, file.getContentType() + "; " + DEFAULT_CHARSET)
         .header(HTTPHeader.CONTENT_NODETYPE, file.getJcrContentNodeType())
         .data(marshaller)
         .send(callback);
         
//         AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
//            .header(HTTPHeader.CONTENT_TYPE, file.getContentType())
//            .header(HTTPHeader.CONTENT_NODETYPE, file.getJcrContentNodeType()).data(marshaller).send(callback);
      }
   }

   @Override
   public void getProperties(Item item)
   {
      String url = javaScriptEncodeURI(item.getHref());

      ItemPropertiesUnmarshaller unmarshaller = new ItemPropertiesUnmarshaller(item, images);
      ItemPropertiesReceivedEvent event = new ItemPropertiesReceivedEvent(item);

      String errorMessage = "Service is not deployed.<br>Resource not found.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      int[] acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent, acceptStatus);

      loader.setMessage(Messages.GET_PROPERTIES);

      AsyncRequest.build(RequestBuilder.POST, url, loader)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND).header(HTTPHeader.DEPTH, "0").send(callback);
   }

   @Override
   public void saveProperties(Item item)
   {
      String url = javaScriptEncodeURI(item.getHref());

      ItemPropertiesMarshaller marshaller = new ItemPropertiesMarshaller(item);
      ItemPropertiesSavedEvent event = new ItemPropertiesSavedEvent(item);
      ItemPropertiesSavingResultUnmarshaller unmarshaller = new ItemPropertiesSavingResultUnmarshaller(item);

      String errorMessage = "Service is not deployed.<br>Resource not found.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);

      loader.setMessage(Messages.SAVE_PROPERTIES);

      AsyncRequest.build(RequestBuilder.POST, url, loader)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPPATCH)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8").data(marshaller).send(callback);
   }

   @Override
   public void search(Folder folder, String text, String mimeType, String path)
   {
      String url = javaScriptEncodeURI(folder.getHref());
      SearchRequestMarshaller requestMarshaller = new SearchRequestMarshaller(text, mimeType, path);

      SearchResultUnmarshaller unmarshaller = new SearchResultUnmarshaller(restContext, eventBus, folder, images);
      SearchResultReceivedEvent event = new SearchResultReceivedEvent(folder);

      String errorMessage = "Service is not deployed.<br>Search path does not exist.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);

      loader.setMessage(Messages.SEARCH);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.SEARCH)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8").data(requestMarshaller).send(callback);
   }

   @Override
   public void move(Item item, String destination)
   {
      String url = javaScriptEncodeURI(item.getHref());
      MoveCompleteEvent event = new MoveCompleteEvent(item, item.getHref());
      MoveResponseUnmarshaller unmarshaller = new MoveResponseUnmarshaller(item, destination);

      String errorMessage =
         "Service is not deployed.<br>Destination path does not exist<br>Folder already has item with same name.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      if (item instanceof File)
      {
         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);

         loader.setMessage(Messages.MOVE_FILE);

         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE).header(HTTPHeader.DESTINATION, destination)
            .header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
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

         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);

         loader.setMessage(Messages.MOVE_FOLDER);

         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE).header(HTTPHeader.DESTINATION, destination)
            .header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
      }

   }

   @Override
   public void copy(Item item, String destination)
   {
      String url = javaScriptEncodeURI(item.getHref());

      String destinationURL = destination;

      CopyCompleteEvent event = new CopyCompleteEvent(item, destination);

      int[] acceptStatus = new int[]{HTTPStatus.CREATED};
      CopyResponseUnmarshaller unmarshaller = new CopyResponseUnmarshaller();
      CopyRequestMarshaller marshaller = new CopyRequestMarshaller();

      String errorMessage =
         "Service is not deployed.<br>Destination path does not exist.<br>Folder already has item with same name.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      if (item instanceof File)
      {
         AsyncRequestCallback callback =
            new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent, acceptStatus);

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

         AsyncRequestCallback callback =
            new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent, acceptStatus);

         loader.setMessage(Messages.COPY_FOLDER);

         AsyncRequest.build(RequestBuilder.POST, url, loader)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.COPY).header(HTTPHeader.DESTINATION, destinationURL)
            .header(HTTPHeader.CONTENT_LENGTH, "0").data(marshaller).send(callback);
      }
   }

   @Override
   public void lock(Item item, int timeout, String userName)
   {
      String url = javaScriptEncodeURI(item.getHref());
      LockToken lockToken = new LockToken();

      ItemLockedEvent event = new ItemLockedEvent(item, lockToken);

      int[] acceptStatus = new int[]{HTTPStatus.OK};

      LockItemMarshaller marshaller = new LockItemMarshaller(userName);
      LockItemUnmarshaller unmarshaller = new LockItemUnmarshaller(lockToken);

      String errorMessage = "Service is not deployed.<br />Lock was not enforceable on this resource.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent, acceptStatus);

      loader.setMessage(Messages.LOCK);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.LOCK)
         .header(HTTPHeader.TIMEOUT, "Infinite, Second-" + timeout).data(marshaller).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem#unlock(org.exoplatform.ide.client.module.vfs.api.Item, org.exoplatform.ide.client.module.vfs.api.LockToken)
    */
   @Override
   public void unlock(Item item, LockToken lockToken)
   {
      String url = javaScriptEncodeURI(item.getHref());

      UnlockItemMarshaller marshaller = new UnlockItemMarshaller();
      UnlockItemUnmarshaller unmarshaller = new UnlockItemUnmarshaller();

      ItemUnlockedEvent event = new ItemUnlockedEvent(item);

      int[] acceptStatus = new int[]{HTTPStatus.NO_CONTENT};

      String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = getErrorEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent, acceptStatus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.UNLOCK)
         .header(HTTPHeader.LOCKTOKEN, "<" + lockToken.getLockToken() + ">").data(marshaller).send(callback);

   }

}
