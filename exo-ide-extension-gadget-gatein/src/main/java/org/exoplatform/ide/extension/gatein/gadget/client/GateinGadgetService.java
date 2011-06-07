/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.gatein.gadget.client;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.URL;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GateinGadgetService
{

   private static final String CONTEXT = "/ide/gadget";

   private static final String DEPLOY = "/deploy";

   private static final String UNDEPLOY = "/undeploy";

   private HandlerManager eventBus;

   private Loader loader;

   private String restServiceContext;

   private String publicContext;

   public static final String GADGET_URL = "gadgetURL";

   public static final String PUBLIC_CONTEXT = "publicContext";

   public static final String PRIVATE_CONTEXT = "privateContext";

   public GateinGadgetService(HandlerManager eventBus, Loader loader, String restServiceContext, String gadgetServer,
      String publicContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
      this.publicContext = publicContext;
   }

   public void deployGadget(String href, AsyncRequestCallback<String> callback)
   {
      String url =
         restServiceContext +
         /*Configuration.getInstance().getContext() + */CONTEXT + DEPLOY + "?" + GADGET_URL + "="
            + URL.encodeQueryString(href) + "&" + PRIVATE_CONTEXT + "=" + URL.encodeQueryString(restServiceContext)
            + "&" + PUBLIC_CONTEXT + "=" + URL.encodeQueryString(publicContext);
      callback.setResult(url);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   public void undeployGadget(String href, AsyncRequestCallback<String> callback)
   {
      String url =
         restServiceContext + /*Configuration.getInstance().getContext() + */CONTEXT + UNDEPLOY + "?" + GADGET_URL
            + "=" + URL.encodeQueryString(href) + "&" + PRIVATE_CONTEXT + "="
            + URL.encodeQueryString(restServiceContext) + "&" + PUBLIC_CONTEXT + "="
            + URL.encodeQueryString(publicContext);
      callback.setResult(url);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

}
