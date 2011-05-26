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
package org.exoplatform.ide.client.model.configuration;

import com.google.gwt.user.client.Window;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.json.client.JSONObject;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.model.configuration.marshal.IDEConfigurationUnmarshaller;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class IDEConfigurationLoader
{

   public static final String APPLICATION_NAME = "IDE"; //$NON-NLS-1$

   private boolean loaded = false;

   private HandlerManager eventBus;

   private Loader loader;

   public IDEConfigurationLoader(HandlerManager eventBus, Loader loader)
   {
      this.eventBus = eventBus;
      this.loader = loader;
   }

   public void loadConfiguration(AsyncRequestCallback<IDEInitializationConfiguration> callback)
   {
      try
      {
         IDEInitializationConfiguration conf = new IDEInitializationConfiguration();
         String url = getConfigurationURL();
         IDEConfigurationUnmarshaller unmarshaller =
            new IDEConfigurationUnmarshaller(conf, new JSONObject(getAppConfig()));
         callback.setPayload(unmarshaller);
         callback.setResult(conf);
         callback.setEventBus(eventBus);
         AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
      }
      catch (Exception e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e, "Can't read initialization configuration!"));
      }
   }

   public boolean isLoaded()
   {
      return loaded;
   }

   private void showErrorMessage(String message)
   {
      String mes = "Invalid configuration:  missing " + message + " item"; //$NON-NLS-1$ //$NON-NLS-2$
      Dialogs.getInstance().showError("Invalid configuration", mes);
   }

   private static native String getConfigurationURL()/*-{
		return $wnd.configurationURL;
   }-*/;

   private static native String getRegistryURL() /*-{
		return $wnd.registryURL;
   }-*/;

   private static native JavaScriptObject getAppConfig() /*-{
		return $wnd.appConfig;
   }-*/;

}
