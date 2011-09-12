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
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.FileModel;

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
    * Loader instance.
    */
   private Loader loader;

   /**
    * Creates a new instance of RemoteFileService.
    * 
    * @param eventBus Event Bus
    * @param loader Loader
    * @param restServiceContext context of the Rest service
    */
   public RemoteFileService(Loader loader)
   {
      instance = this;
      this.loader = loader;
   }

   /**
    * Gets content of the remote file.
    * 
    * @param url file's url
    * @param callback callback to handle results
    */
   public void getRemoteFileContent(FileModel file, String url, AsyncRequestCallback<FileModel> callback)
   {
      callback.setResult(file);
      callback.setResult(file);
      Unmarshallable unmarshaller = new RemoteFileContentUnmarshaller(file);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
