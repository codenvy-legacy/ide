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

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointList;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEventList;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.Value;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;
import org.exoplatform.ide.extension.java.jdi.shared.VariablePath;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DebuggerClientService
{

   private static String BASE_URL;
   
   private String restContext;  

   private static DebuggerClientService instance;
   
   public DebuggerClientService(String restContext)
   {
      BASE_URL = restContext + "/ide/java/debug";
      this.restContext = restContext;
      instance = this;
   }

   public static DebuggerClientService getInstance()
   {
      return instance;
   }

   public void create(String host, int port, AsyncRequestCallback<DebuggerInfo> callback) throws RequestException
   {
      Loader loader = new GWTLoader();
      loader.setMessage("Connection... to the host " + host);
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/connect?host=" + host + "&port=" + port)
         .loader(loader).send(callback);
   }

   public void disconnect(String id, AsyncRequestCallback<String> callback) throws RequestException
   {
      Loader loader = new GWTLoader();
      loader.setMessage("DisConnection... ");
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/disconnect/" + id).loader(loader).send(callback);
   }

   public void addBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPoint> callback)
      throws RequestException
   {
      AutoBean<BreakPoint> ab = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoint(breakPoint);
      String json = AutoBeanCodex.encode(ab).getPayload();
      AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/breakpoints/add/" + id).data(json)
         .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
   }

   public void deleteBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPoint> callback)
      throws RequestException
   {
      AutoBean<BreakPoint> ab = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoint(breakPoint);
      String json = AutoBeanCodex.encode(ab).getPayload();
      AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/breakpoints/delete/" + id).data(json)
         .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
   }

   public void getBreakPoints(String id, AsyncRequestCallback<BreakPointList> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/breakpoints/" + id).loader(new EmptyLoader()).send(callback);
   }


   public void checkEvents(String id, AsyncRequestCallback<DebuggerEventList> callback)
      throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/events/" + id).loader(new EmptyLoader())
         .send(callback);
   }

   public void dump(String id, AsyncRequestCallback<StackFrameDump> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/dump/" + id).loader(new EmptyLoader()).send(callback);
   }

   public void resume(String id, AsyncRequestCallback<String> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/resume/" + id).loader(new EmptyLoader()).send(callback);
   }

   public void getValue(String id, Variable var, AsyncRequestCallback<Value> callback) throws RequestException
   {
      AutoBean<VariablePath> autoBean2 = DebuggerExtension.AUTO_BEAN_FACTORY.variablePath(var.getVariablePath());
      String json = AutoBeanCodex.encode(autoBean2).getPayload();
      AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/value/" + id).data(json)
         .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
   }

   public void stepInto(String id, AsyncRequestCallback<String> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/step/into/" + id).loader(new EmptyLoader()).send(callback);
   }

   public void stepOver(String id, AsyncRequestCallback<String> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/step/over/" + id).loader(new EmptyLoader()).send(callback);
   }

   public void stepReturn(String id, AsyncRequestCallback<String> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/step/out/" + id).loader(new EmptyLoader()).send(callback);
   }
   
   
   
   public void createApplication(String server, String name, String type, String url, int instances, int memory, boolean nostart,
      String vfsId, String projectId, String war, AsyncRequestCallback<CloudFoundryApplication> callback)
      throws RequestException
   {
      final String requestUrl = restContext + "/ide/cloudfoundry/apps/create";

      String params = "name=" + name;
      params += (server == null) ? "" : "&server=" + server;
      params += (type != null) ? "&type=" + type : "";
      params += (url != null) ? "&url=" + url : "";
      params += "&instances=" + instances;
      params += "&mem=" + memory;
      params += "&nostart=" + nostart;
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (war != null) ? "&war=" + war : "";

      System.out.println("DebuggerClientService.createApplication()" + requestUrl + "?" + params);
      
      Loader loader = new GWTLoader();
      loader.setMessage("Starting.... ");
      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);

   }

}
