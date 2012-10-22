/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ApplicationRunnerClientService
{
   private static String BASE_URL;

   private static ApplicationRunnerClientService instance;

   private String restContext;

   public ApplicationRunnerClientService(String restContext)
   {
      this.restContext = restContext;
      BASE_URL = restContext + "/ide/java/runner";
      instance = this;
   }

   public static ApplicationRunnerClientService getInstance()
   {
      return instance;
   }

   public void runApplication(String project, String war, boolean useWebSocket,
      AsyncRequestCallback<ApplicationInstance> callback) throws RequestException
   {
      RequestStatusHandler statusHandler = null;
      if (!useWebSocket)
      {
         statusHandler = new RunningAppStatusHandler(project);
      }

      String requestUrl = BASE_URL + "/run?war=" + war + "&usewebsocket=" + useWebSocket;
      Loader loader = new GWTLoader();
      loader.setMessage("Starting.... ");
      AsyncRequest.build(RequestBuilder.GET, requestUrl, !useWebSocket).requestStatusHandler(statusHandler)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);

   }

   public void debugApplication(String project, String war, boolean useWebSocket,
      AsyncRequestCallback<DebugApplicationInstance> callback) throws RequestException
   {
      RequestStatusHandler statusHandler = null;
      if (!useWebSocket)
      {
         statusHandler = new RunningAppStatusHandler(project);
      }

      String requestUrl = BASE_URL + "/debug?war=" + war + "&suspend=false" + "&usewebsocket=" + useWebSocket;
      Loader loader = new GWTLoader();
      loader.setMessage("Starting.... ");
      AsyncRequest.build(RequestBuilder.GET, requestUrl, !useWebSocket).requestStatusHandler(statusHandler)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);

   }

   public void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      String url = BASE_URL + "/logs";
      StringBuilder params = new StringBuilder("?name=");
      params.append(name);

      Loader loader = new GWTLoader();
      loader.setMessage("Retrieving logs.... ");

      AsyncRequest.build(RequestBuilder.GET, url + params.toString()).loader(loader).send(callback);
   }

   public void prolongExpirationTime(String name, long time, AsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = BASE_URL + "/prolong";
      StringBuilder params = new StringBuilder("?name=").append(name).append("&time=").append(time);

      AsyncRequest.build(RequestBuilder.GET, url + params.toString()).send(callback);
   }

}
