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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestBuilder;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestCallback;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ApplicationRunnerClientService
{
   public static final String PROLONG = "/ide/java/runner/prolong";

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

   public void runApplication(String project, String war, boolean useJRebel,
      AsyncRequestCallback<ApplicationInstance> callback) throws RequestException
   {
      String requestUrl = BASE_URL + "/run?war=" + war;

      String data = "";
      if (useJRebel)
      {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("jrebel", new JSONString("true"));
         data = jsonObject.toString();
      }

      Loader loader = new GWTLoader();
      loader.setMessage("Starting.... ");
      AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
         .requestStatusHandler(new RunningAppStatusHandler(project))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data).send(callback);

   }

   public void debugApplication(String project, String war, boolean useJRebel,
      AsyncRequestCallback<ApplicationInstance> callback) throws RequestException
   {
      String data = "";
      if (useJRebel)
      {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("jrebel", new JSONString("true"));
         data = jsonObject.toString();
      }

      String requestUrl = BASE_URL + "/debug?war=" + war + "&suspend=false";
      Loader loader = new GWTLoader();
      loader.setMessage("Starting.... ");
      AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
         .requestStatusHandler(new RunningAppStatusHandler(project))
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data).send(callback);

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

   public void prolongExpirationTime(String name, long time, RESTfulRequestCallback<Object> callback)
      throws WebSocketException
   {
      StringBuilder params = new StringBuilder("?name=").append(name).append("&time=").append(time);
      RESTfulRequestBuilder.build(RequestBuilder.GET, PROLONG + params).send(callback);
   }

   public void updateApplication(String name, String war, AsyncRequestCallback<Object> callback)
      throws RequestException
   {
      String url = BASE_URL + "/update";
      StringBuilder params = new StringBuilder("?name=").append(name).append("&war=").append(war);

      Loader loader = new GWTLoader();
      loader.setMessage("Updating application.... ");

      AsyncRequest.build(RequestBuilder.GET, url + params.toString()).loader(loader).send(callback);
   }

}
