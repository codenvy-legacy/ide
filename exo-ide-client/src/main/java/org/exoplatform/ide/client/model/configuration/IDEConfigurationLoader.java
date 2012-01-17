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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;

/**
 * Created by The eXo Platform SAS .
 * 
 * @version $Id: $
 */

public class IDEConfigurationLoader
{

   /* Consts */
   public static final String APPLICATION_NAME = "IDE"; //$NON-NLS-1$

   /* Error messages */
   private static final String CANT_READ_CONFIGURATION = IDE.ERRORS_CONSTANT.confLoaderCantReadConfiguration();

   private static final String INVALID_CONFIGURATION_TITLE = IDE.ERRORS_CONSTANT.confInvalidConfTitle();

   /* Fields */
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
         String url = getConfigurationURL();
         AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
      }
      catch (Exception e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e, CANT_READ_CONFIGURATION));
      }
   }

   public boolean isLoaded()
   {
      return loaded;
   }

   private static native String getConfigurationURL()/*-{
                                                     return $wnd.configurationURL;
                                                     }-*/;

   public static native JavaScriptObject getAppConfig() /*-{
                                                         return $wnd.appConfig;
                                                         }-*/;
}
