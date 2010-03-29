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

package org.exoplatform.ideall.client.model.vfs.webdav;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.ChildrenReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.CopyCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FolderCreatedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.SearchResultReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.CopyRequestMarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.CopyResponseUnmarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.FileContentMarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.FileContentSavingResultUnmarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.FileContentUnmarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.FolderContentUnmarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.ItemPropertiesMarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.ItemPropertiesSavingResultUnmarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.ItemPropertiesUnmarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.SearchRequestMarshaller;
import org.exoplatform.ideall.client.model.vfs.webdav.marshal.SearchResultUnmarshaller;

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

   }

   public static final String CONTEXT = "/jcr";

   private HandlerManager eventBus;

   public WebDavVirtualFileSystem(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
   }

   //   private String getURL(String path)
   //   {
   //      String url = Configuration.getInstance().getContext() + CONTEXT + path;
   //      url = TextUtils.javaScriptEncodeURI(url);
   //      return url;
   //   }
   
   public static native String javaScriptEncodeURI(String text) /*-{
      return encodeURI(text);
   }-*/;

   @Override
   public void getFileContent(File file)
   {
      //      String url = Configuration.getInstance().getContext() + CONTEXT + file.getPath();
      //      url = TextUtils.javaScriptEncodeURI(url);
      String url = javaScriptEncodeURI(file.getHref());

      FileContentUnmarshaller unmarshaller = new FileContentUnmarshaller(file);
      FileContentReceivedEvent event = new FileContentReceivedEvent(file);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);

      Loader.getInstance().setMessage(Messages.GET_FILE_CONTENT);

      AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.GET).send(
         callback);
   }

   @Override
   public void getChildren(Folder folder)
   {
      String url = javaScriptEncodeURI(folder.getHref());
      //      if (!url.endsWith("/"))
      //      {
      //         url += "/";
      //      }

      System.out.println("here!!!!!!!!!!");

      ChildrenReceivedEvent event = new ChildrenReceivedEvent(folder);
      FolderContentUnmarshaller unmarshaller = new FolderContentUnmarshaller(folder);

      int[] acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, acceptStatus);

      Loader.getInstance().setMessage(Messages.GET_FOLDER_CONTENT);

      AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND)
         .header(HTTPHeader.DEPTH, "1").send(callback);
   }

   @Override
   public void createFolder(Folder folder)
   {
      System.out.println("create folder " + folder.getHref());
      
      String url = javaScriptEncodeURI(folder.getHref());

      FolderCreatedEvent event = new FolderCreatedEvent(folder);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);

      Loader.getInstance().setMessage(Messages.CREATE_FOLDER);

      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MKCOL).header(
         HTTPHeader.CONTENT_LENGTH, "0").send(callback);
   }

   @Override
   public void deleteItem(Item item)
   {
      String url = javaScriptEncodeURI(item.getHref());
      ItemDeletedEvent event = new ItemDeletedEvent(item);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);

      if (item instanceof File)
      {
         Loader.getInstance().setMessage(Messages.DELETE_FILE);
      }
      else
      {
         Loader.getInstance().setMessage(Messages.DELETE_FOLDER);
      }
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE).header(
         HTTPHeader.CONTENT_LENGTH, "0").send(callback);
   }

   @Override
   public void saveFileContent(File file)
   {
      String url = javaScriptEncodeURI(file.getHref());
      boolean isNewFile = file.isNewFile();

      FileContentMarshaller marshaller = new FileContentMarshaller(file);
      FileContentSavedEvent event = new FileContentSavedEvent(file, isNewFile);
      FileContentSavingResultUnmarshaller unmarshaller = new FileContentSavingResultUnmarshaller(file);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);

      Loader.getInstance().setMessage(Messages.SAVE_FILE_CONTENT);

      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT).header(
         HTTPHeader.CONTENT_TYPE, file.getContentType()).header(HTTPHeader.CONTENT_NODETYPE,
         file.getJcrContentNodeType()).data(marshaller).send(callback);
   }

   @Override
   public void getProperties(Item item)
   {
      String url = javaScriptEncodeURI(item.getHref());

      ItemPropertiesUnmarshaller unmarshaller = new ItemPropertiesUnmarshaller(eventBus, item);
      ItemPropertiesReceivedEvent event = new ItemPropertiesReceivedEvent(item);

      int[] acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, acceptStatus);

      Loader.getInstance().setMessage(Messages.GET_PROPERTIES);

      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND)
         .header(HTTPHeader.DEPTH, "infinity").send(callback);
   }

   @Override
   public void saveProperties(Item item)
   {
      String url = javaScriptEncodeURI(item.getHref());

      ItemPropertiesMarshaller marshaller = new ItemPropertiesMarshaller(item);
      ItemPropertiesSavedEvent event = new ItemPropertiesSavedEvent(item);
      ItemPropertiesSavingResultUnmarshaller unmarshaller = new ItemPropertiesSavingResultUnmarshaller(item);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);

      Loader.getInstance().setMessage(Messages.SAVE_PROPERTIES);

      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPPATCH)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8").data(marshaller).send(callback);
   }

   @Override
   public void search(Folder folder, String text, String mimeType, String path)
   {
      String url = javaScriptEncodeURI(folder.getHref());
      SearchRequestMarshaller requestMarshaller = new SearchRequestMarshaller(text, mimeType, path);

      SearchResultUnmarshaller unmarshaller = new SearchResultUnmarshaller(eventBus, folder);
      SearchResultReceivedEvent event = new SearchResultReceivedEvent(folder);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);

      Loader.getInstance().setMessage(Messages.SEARCH);

      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.SEARCH).header(
         HTTPHeader.CONTENT_TYPE, "text/xml").data(requestMarshaller).send(callback);
   }

   @Override
   public void move(Item item, String destination)
   {
      String url = javaScriptEncodeURI(item.getHref());
//      String host = GWT.getModuleBaseURL();
//
//      String destinationURL = host.substring(0, host.indexOf("//") + 2);
//      host = host.substring(host.indexOf("//") + 2);
//      destinationURL += host.substring(0, host.indexOf("/"));
      //destinationURL += Configuration.getInstance().getContext() + CONTEXT + TextUtils.javaScriptEncodeURI(destination);

      MoveCompleteEvent event = new MoveCompleteEvent(item, destination);

      if (item instanceof File)
      {
         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);

         Loader.getInstance().setMessage(Messages.MOVE_FILE);

         AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE)
            .header(HTTPHeader.DESTINATION, destination).header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
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

         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);

         Loader.getInstance().setMessage(Messages.MOVE_FOLDER);

         AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE)
            .header(HTTPHeader.DESTINATION, destination).header(HTTPHeader.CONTENT_LENGTH, "0").send(callback);
      }

   }

   @Override
   public void copy(Item item, String destination)
   {
      String url = javaScriptEncodeURI(item.getHref());
//      String host = GWT.getModuleBaseURL();

      String destinationURL = destination;
      //host.substring(0, host.indexOf("//") + 2);
//      host = host.substring(host.indexOf("//") + 2);
//      destinationURL += host.substring(0, host.indexOf("/"));
      //destinationURL += Configuration.getInstance().getContext() + CONTEXT + TextUtils.javaScriptEncodeURI(destination);

      CopyCompleteEvent event = new CopyCompleteEvent(item, destination);

      int[] acceptStatus = new int[]{HTTPStatus.CREATED};
      CopyResponseUnmarshaller unmarshaller = new CopyResponseUnmarshaller();
      CopyRequestMarshaller marshaller = new CopyRequestMarshaller();

      if (item instanceof File)
      {
         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, acceptStatus);

         Loader.getInstance().setMessage(Messages.COPY_FILE);

         AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.COPY)
            .header(HTTPHeader.DESTINATION, destinationURL).header(HTTPHeader.CONTENT_LENGTH, "0").data(marshaller)
            .send(callback);
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

         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, acceptStatus);

         Loader.getInstance().setMessage(Messages.COPY_FOLDER);

         AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.COPY)
            .header(HTTPHeader.DESTINATION, destinationURL).header(HTTPHeader.CONTENT_LENGTH, "0").data(marshaller)
            .send(callback);
      }
   }

}
