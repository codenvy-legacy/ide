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

package org.exoplatform.ideall.client.model.data;

import org.exoplatform.gwt.commons.rest.AsyncRequest;
import org.exoplatform.gwt.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwt.commons.rest.HTTPHeader;
import org.exoplatform.gwt.commons.rest.HTTPMethod;
import org.exoplatform.gwt.commons.rest.HTTPStatus;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.data.event.FolderContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FolderCreatedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemDeletedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.data.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.data.event.SearchResultReceivedEvent;
import org.exoplatform.ideall.client.model.data.marshal.FileContentMarshaller;
import org.exoplatform.ideall.client.model.data.marshal.FileContentSavingResultUnmarshaller;
import org.exoplatform.ideall.client.model.data.marshal.FileContentUnmarshaller;
import org.exoplatform.ideall.client.model.data.marshal.FolderContentUnmarshaller;
import org.exoplatform.ideall.client.model.data.marshal.ItemPropertiesMarshaller;
import org.exoplatform.ideall.client.model.data.marshal.ItemPropertiesSavingResultUnmarshaller;
import org.exoplatform.ideall.client.model.data.marshal.ItemPropertiesUnmarshaller;
import org.exoplatform.ideall.client.model.data.marshal.SearchRequestMarshaller;
import org.exoplatform.ideall.client.model.data.marshal.SearchResultUnmarshaller;
import org.exoplatform.ideall.client.model.util.TextUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DataServiceImpl extends DataService
{

   public static final String CONTEXT = "/jcr";

   private HandlerManager eventBus;

   public DataServiceImpl(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
   }
   
   private String getURL(String path) {
      String url = Configuration.getInstance().getContext() + CONTEXT + path;
      url = TextUtils.javaScriptEncodeURI(url);
      return url;
   }

   @Override
   public void getFileContent(File file)
   {
//      String url = Configuration.getInstance().getContext() + CONTEXT + file.getPath();
//      url = TextUtils.javaScriptEncodeURI(url);
      String url = getURL(file.getPath());

      FileContentUnmarshaller unmarshaller = new FileContentUnmarshaller(file);
      FileContentReceivedEvent event = new FileContentReceivedEvent(file);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.GET)
         .send(callback);
   }

   @Override
   public void getFolderContent(String path)
   {
      String url = getURL(path);
      if (!url.endsWith("/"))
      {
         url += "/";
      }
      
      System.out.println("folder content URL [" + url + "]");
      
//      String url = Configuration.getInstance().getContext() + CONTEXT + path;
//      if (!url.endsWith("/"))
//      {
//         url += "/";
//      }
//      url = TextUtils.javaScriptEncodeURI(url);

      Folder folder = new Folder(path);
      FolderContentReceivedEvent event = new FolderContentReceivedEvent(folder);
      FolderContentUnmarshaller unmarshaller = new FolderContentUnmarshaller(folder);
      
      int []acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, acceptStatus);
      AsyncRequest.build(RequestBuilder.GET, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND)
         .header(HTTPHeader.DEPTH, "1")
         .send(callback);
   }

   @Override
   public void createFolder(String path)
   {
      String url = getURL(path);
//      String url = Configuration.getInstance().getContext() + CONTEXT + path;
//      url = TextUtils.javaScriptEncodeURI(url);

      FolderCreatedEvent event = new FolderCreatedEvent(path);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MKCOL)
         .header(HTTPHeader.CONTENT_LENGTH, "0")
         .send(callback);
   }

   @Override
   public void deleteItem(Item item)
   {
      String url = getURL(item.getPath());
      
//      String url = Configuration.getInstance().getContext() + CONTEXT + item.getPath();
//      url = TextUtils.javaScriptEncodeURI(url);

      ItemDeletedEvent event = new ItemDeletedEvent(item);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
         .header(HTTPHeader.CONTENT_LENGTH, "0")
         .send(callback);
   }

   @Override
   public void saveFileContent(File file, String path)
   {
      String url = getURL(path);
//      String url = Configuration.getInstance().getContext() + CONTEXT + path;
//      url = TextUtils.javaScriptEncodeURI(url);

      boolean isNewFile = file.isNewFile();
      boolean isSaveAs = !file.getPath().equals(path);

      FileContentMarshaller marshaller = new FileContentMarshaller(file);
      FileContentSavedEvent event = new FileContentSavedEvent(file, path, isNewFile, isSaveAs);
      FileContentSavingResultUnmarshaller unmarshaller = new FileContentSavingResultUnmarshaller(file);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);

      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
         .header(HTTPHeader.CONTENT_TYPE, file.getContentType())
         .header(HTTPHeader.CONTENT_NODETYPE, file.getJcrContentNodeType())
         .data(marshaller)
         .send(callback);
   }

   @Override
   public void getProperties(Item item)
   {
//      String url = Configuration.getInstance().getContext() + CONTEXT + item.getPath();
//      url = TextUtils.javaScriptEncodeURI(url);
      String url = getURL(item.getPath());

      ItemPropertiesUnmarshaller unmarshaller = new ItemPropertiesUnmarshaller(item);
      ItemPropertiesReceivedEvent event = new ItemPropertiesReceivedEvent(item);

      int []acceptStatus = new int[]{HTTPStatus.MULTISTATUS};
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, acceptStatus);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPFIND)
         .header(HTTPHeader.DEPTH, "infinity")
         .send(callback);
   }

   @Override
   public void saveProperties(Item item)
   {
//      String url = Configuration.getInstance().getContext() + CONTEXT + item.getPath();
//      url = TextUtils.javaScriptEncodeURI(url);
      String url = getURL(item.getPath());

      ItemPropertiesMarshaller marshaller = new ItemPropertiesMarshaller(item);
      ItemPropertiesSavedEvent event = new ItemPropertiesSavedEvent(item);
      ItemPropertiesSavingResultUnmarshaller unmarshaller = new ItemPropertiesSavingResultUnmarshaller(item);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);

      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PROPPATCH)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml; charset=UTF-8")
         .data(marshaller)
         .send(callback);
   }

   @Override
   public void search(String content, String path)
   {
//      String url = Configuration.getInstance().getContext() + CONTEXT + path;
      String url = getURL(path);

      SearchRequestMarshaller requestMarshaller = new SearchRequestMarshaller(content);
      Folder folder = new Folder(path);
      SearchResultUnmarshaller unmarshaller = new SearchResultUnmarshaller(folder);
      SearchResultReceivedEvent event = new SearchResultReceivedEvent(folder);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.SEARCH)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml")
         .data(requestMarshaller)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ideall.client.model.data.DataService#advancedSearch(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void search(String path, String contentText, String name, String contentType, String searchPath)
   {
      //String url = Configuration.getInstance().getContext() + CONTEXT + folderPath;
      String url = getURL(path);
    
      SearchRequestMarshaller requestMarshaller =
         new SearchRequestMarshaller(contentText, name, contentType, searchPath);
      Folder folder = new Folder(path);
      SearchResultUnmarshaller unmarshaller = new SearchResultUnmarshaller(folder);
      SearchResultReceivedEvent event = new SearchResultReceivedEvent(folder);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.SEARCH)
         .header(HTTPHeader.CONTENT_TYPE, "text/xml")
         .data(requestMarshaller)
         .send(callback);
   }

   @Override
   public void move(Item item, String destination)
   {
//      String url = Configuration.getInstance().getContext() + CONTEXT + item.getPath();
//      url = TextUtils.javaScriptEncodeURI(url);
      String url = getURL(item.getPath());

      String host = GWT.getModuleBaseURL();

      String destinationURL = host.substring(0, host.indexOf("//") + 2);
      host = host.substring(host.indexOf("//") + 2);
      destinationURL += host.substring(0, host.indexOf("/"));
      destinationURL += Configuration.getInstance().getContext() + CONTEXT + TextUtils.javaScriptEncodeURI(destination);

      MoveCompleteEvent event = new MoveCompleteEvent(item, destination);

      if (item instanceof Folder)
      {
         if (!url.endsWith("/"))
         {
            url += "/";
         }

         if (!destinationURL.endsWith("/"))
         {
            destinationURL += "/";
         }

         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
         AsyncRequest.build(RequestBuilder.POST, url)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE)
            .header(HTTPHeader.DESTINATION, destinationURL)
            .header(HTTPHeader.CONTENT_LENGTH, "0")
            .send(callback);
      }
      else
      {
         AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
         AsyncRequest.build(RequestBuilder.POST, url)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.MOVE)
            .header(HTTPHeader.DESTINATION, destinationURL)
            .header(HTTPHeader.CONTENT_LENGTH, "0")
            .send(callback);
      }

   }

}
