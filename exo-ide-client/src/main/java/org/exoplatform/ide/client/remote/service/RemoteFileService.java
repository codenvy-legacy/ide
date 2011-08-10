/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.remote.service;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.client.framework.vfs.File;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Client-side service, which downloads the content of the specified resource.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RemoteFileService
{

   /**
    * Instance of this service.
    */
   private static RemoteFileService instance;

   /**
    * Gets RemoteFileService instance.
    * 
    * @return
    */
   public static RemoteFileService getInstance()
   {
      return instance;
   }

   /**
    * Context of server-side service.
    */
   private static final String REMOTE_FILE_SERVICE_CONTEXT = "/ide/remotefile/content";

   /**
    * Event Bus instance.
    */
   private HandlerManager eventBus;

   /**
    * Loader instance.
    */
   private Loader loader;

   /**
    * Context of the Rest Service.
    */
   private String restServiceContext;

   /**
    * Creates a new instance of RemoteFileService.
    * 
    * @param eventBus Event Bus
    * @param loader Loader
    * @param restServiceContext context of the Rest service
    */
   public RemoteFileService(HandlerManager eventBus, Loader loader, String restServiceContext)
   {
      instance = this;

      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
   }

   /**
    * Gets content of the remote file.
    * 
    * @param url file's url
    * @param callback callback to handle results
    */
   public void getRemoteFile(String url, AsyncRequestCallback<File> callback)
   {
      String requestURL = restServiceContext + REMOTE_FILE_SERVICE_CONTEXT + "?url=" + url;

      File file = new File(url);

      RemoteFileContentUnmarshaller unmarshaller = new RemoteFileContentUnmarshaller(file);

      callback.setResult(file);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      loader.setMessage("Loading remote file...");

      AsyncRequest.build(RequestBuilder.GET, requestURL, loader).header(HTTPHeader.CONNECTION, "close").send(callback);
   }

}
