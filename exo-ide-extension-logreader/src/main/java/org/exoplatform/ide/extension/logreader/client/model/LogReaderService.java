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

   public void getLastLog(AsyncRequestCallback<LogEntry> callback)
   {
      String url = restContext + "/log-reader-service/last-log";
      sendRequest(url, callback);
   }

   public void getPrevLog(String token, AsyncRequestCallback<LogEntry> callback)
   {
      String url = restContext + "/log-reader-service/prev-log?token=" + token;
      sendRequest(url, callback);
   }

   public void getNextLog(String token, AsyncRequestCallback<LogEntry> callback)
   {
      String url = restContext + "/log-reader-service/next-log?token=" + token;
      sendRequest(url, callback);
   }

   public void getLog(String token, AsyncRequestCallback<LogEntry> callback)
   {
      String url = restContext + "/log-reader-service/log?token=" + token;
      sendRequest(url, callback);
   }

   private void sendRequest(String url, AsyncRequestCallback<LogEntry> callback)
   {
      LogEntry logEntry = new LogEntry();
      LogReaderUnmarshaller unmarshaller = new LogReaderUnmarshaller(logEntry);
      callback.setPayload(unmarshaller);
      callback.setResult(logEntry);
      callback.setEventBus(IDE.EVENT_BUS);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
