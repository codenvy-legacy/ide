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
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEventList;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointList;
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
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DebuggerClientService
{

   private static final String BASE_URL = "/rest/ide/java/debug";

   private static final String CONNECT = "/connect";

   private static final String DISCONNECT = "/disconnect";

   private static final String BREAKPOINT_ADD = "/breakpoints/add";

   private static final String BREAKPOINT = "/breakpoints";

   private static final String BREAKPOINT_SWITCH = "/breakpoints/switch";

   private static final String EVENTS = "/events";

   private static final String DUMP = "/dump";

   private static final String RESUME = "/resume";

   private static DebuggerClientService instance;

   public static DebuggerClientService getInstance()
   {
      if (instance == null)
      {
         instance = new DebuggerClientService();
      }
      return instance;
   }

   public void create(String host, int port, AsyncRequestCallback<DebuggerInfo> callback) throws RequestException
   {
      Loader loader = new GWTLoader();
      loader.setMessage("Connection... to the host " + host);
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + CONNECT + "?" + "host=" + host + "&port=" + port)
         .loader(loader).send(callback);
   }

   public void disconnect(String id, AsyncRequestCallback callback) throws RequestException
   {
      Loader loader = new GWTLoader();
      loader.setMessage("DisConnection... ");
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + DISCONNECT + "/" + id).loader(loader).send(callback);
   }

   public void addBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPoint> callback)
      throws RequestException
   {
      AutoBean<BreakPoint> ab = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoint(breakPoint);
      String json = AutoBeanCodex.encode(ab).getPayload();
      AsyncRequest.build(RequestBuilder.POST, BASE_URL + BREAKPOINT_ADD + "/" + id).data(json)
         .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
   }

   public void getBreakPoints(String id, AsyncRequestCallback<BreakPointList> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + BREAKPOINT + "/" + id).loader(new EmptyLoader()).send(callback);
   }

   public void switchBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPointList> callback)
      throws RequestException
   {
      AutoBean<BreakPoint> ab = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoint(breakPoint);
      String json = AutoBeanCodex.encode(ab).getPayload();
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + BREAKPOINT_SWITCH + "/" + id).data(json)
         .loader(new EmptyLoader()).send(callback);
   }

   public void checkEvents(String id, AsyncRequestCallback<BreakPointEventList> asyncRequestCallback)
      throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + EVENTS + "/" + id).loader(new EmptyLoader())
         .send(asyncRequestCallback);
   }

   public void dump(String id, AsyncRequestCallback<StackFrameDump> callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + DUMP + "/" + id).loader(new EmptyLoader()).send(callback);
   }

   public void resume(String id, AsyncRequestCallback callback) throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, BASE_URL + RESUME + "/" + id).loader(new EmptyLoader()).send(callback);
   }

   public void getValue(String id, Variable var, AsyncRequestCallback<Value> callback) throws RequestException
   {
      AutoBean<VariablePath> autoBean2 = DebuggerExtension.AUTO_BEAN_FACTORY.variablePath(var.getVariablePath());
      String json = AutoBeanCodex.encode(autoBean2).getPayload();
      AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/value/" + id).data(json)
         .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
   }

}
