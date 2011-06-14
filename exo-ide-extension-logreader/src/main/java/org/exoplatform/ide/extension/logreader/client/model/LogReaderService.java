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
package org.exoplatform.ide.extension.logreader.client.model;

import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.logreader.client.model.marshal.LogReaderUnmarshaller;

/**
 * This service provides access to information stored in the logs created on current tenant.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderService
{

   private String restContext;

   private Loader loader;

   private static LogReaderService instance;

   public static LogReaderService get()
   {
      return instance;
   }

   /**
    * @param restContext
    * @param loader
    */
   public LogReaderService(String restContext, Loader loader)
   {
      super();
      this.restContext = restContext;
      this.loader = loader;
      instance = this;
   }

   /**
    * @param timeStamp the date from which message should be added to response. 
    *            This is not mandatory query parameter, by default its set to 0 which means that start time is not limited;
    * @param limit number of messages what should be added to the response. Note that one message can have more then one line. 
    *        This is not mandatory query parameter, by default its set to 0. In this case you will receive all log messages from specified times
    * @param offset number of messages what will be obtained for specified timestamp with respect to limit parameter (if any).
    *        This is not mandatory query parameter, by default its set to 0. In this case messages will be obtained from specified timestamp.
    * @param callback
    */
   public void getLogs(long timeStamp, int limit, int offset, AsyncRequestCallback<String> callback)
   {
      String url =
         restContext + "/cloud/logreader-service/log?timestamp=" + timeStamp + "&limit=" + limit + "&offset=" + offset;
      LogReaderUnmarshaller unmarshaller = new LogReaderUnmarshaller(callback);
      callback.setPayload(unmarshaller);
      callback.setEventBus(IDE.EVENT_BUS);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
