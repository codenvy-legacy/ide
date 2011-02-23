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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window.Location;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.initializer.ApplicationConfiguration;
import org.exoplatform.gwtframework.commons.initializer.ApplicationInitializer;
import org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedEvent;
import org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedHandler;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class IDEConfigurationLoader implements ApplicationConfigurationReceivedHandler
{

   public final static String APPLICATION_NAME = "IDE";

   private static final String CONFIG_NODENAME = "configuration";

   private final static String CONTEXT = "context";

   private final static String GADGET_SERVER = "gadgetServer";

   private final static String PUBLIC_CONTEXT = "publicContext";

   public static final String LOOPBACK_SERVICE_CONTEXT = "/ide/loopbackcontent";

   public static final String UPLOAD_SERVICE_CONTEXT = "/ide/upload";

   private boolean loaded = false;

   private HandlerManager eventBus;

   private IDEConfiguration configuration;

   private Loader loader;

   public IDEConfigurationLoader(HandlerManager eventBus, Loader loader)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      eventBus.addHandler(ApplicationConfigurationReceivedEvent.TYPE, this);
   }

//   public void loadConfiguration()
//   {
//      loadConfiguration(new IDEConfiguration());
//   }

   public void loadConfiguration(IDEConfiguration configuration)
   {
      this.configuration = configuration;
      configuration.setRegistryURL(getRegistryURL());
      final ApplicationInitializer applicationInitializer = new ApplicationInitializer(eventBus, APPLICATION_NAME, loader);
      applicationInitializer.getApplicationConfiguration(CONFIG_NODENAME,
         new AsyncRequestCallback<ApplicationConfiguration>()
         {

            @Override
            protected void onSuccess(ApplicationConfiguration result)
            {
               configurationReceived(result);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               applicationInitializer.getConfigurationFromRegistry();
            }
         });
   }

   private void configurationReceived(ApplicationConfiguration appConfiguration)
   {
      JSONObject jsonConfiguration = appConfiguration.getConfiguration().isObject();

      if (jsonConfiguration.containsKey(CONTEXT))
      {
         configuration.setContext(jsonConfiguration.get(IDEConfigurationLoader.CONTEXT).isString().stringValue());
         configuration.setLoopbackServiceContext(configuration.getContext() + LOOPBACK_SERVICE_CONTEXT);
         configuration.setUploadServiceContext(configuration.getContext() + UPLOAD_SERVICE_CONTEXT);
      }
      else
      {
         showErrorMessage(CONTEXT);
         return;
      }

      if (jsonConfiguration.containsKey(PUBLIC_CONTEXT))
         configuration.setPublicContext(jsonConfiguration.get(IDEConfigurationLoader.PUBLIC_CONTEXT).isString()
            .stringValue());
      else
      {
         showErrorMessage(PUBLIC_CONTEXT);
         return;
      }

      if (jsonConfiguration.containsKey(GADGET_SERVER))
         //TODO: now we can load gadget only from current host
         configuration.setGadgetServer(Location.getProtocol() + "//" + Location.getHost()
            + jsonConfiguration.get(GADGET_SERVER).isString().stringValue());
      else
      {
         showErrorMessage(GADGET_SERVER);
         return;
      }

      loaded = true;
      eventBus.fireEvent(new ConfigurationReceivedSuccessfullyEvent(configuration));
   }

   public boolean isLoaded()
   {
      return loaded;
   }

   private void showErrorMessage(String message)
   {
      String m = "Invalid configuration:  missing " + message + " item";
      Dialogs.getInstance().showError("Invalid configuration", m);
   }

   private static native String getRegistryURL() /*-{
                                                 return $wnd.registryURL;
                                                 }-*/;

   /**
    * @see org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedHandler#onConfigurationReceived(org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedEvent)
    */
   @Override
   public void onConfigurationReceived(ApplicationConfigurationReceivedEvent event)
   {
      configurationReceived(event.getApplicationConfiguration());
   }

}
