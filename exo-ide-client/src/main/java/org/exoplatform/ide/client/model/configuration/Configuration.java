/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ide.client.model.configuration;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.initializer.ApplicationInitializer;
import org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedEvent;
import org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedHandler;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window.Location;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class Configuration implements ApplicationConfigurationReceivedHandler
{

   public final static String APPLICATION_NAME = "IDE";

   private static final String CONFIG_NODENAME = "configuration";

   private final static String CONTEXT = "context";

   private final static String ENTRY_POINT = "entryPoint";

   private final static String GADGET_SERVER = "gadgetServer";

   private final static String PUBLIC_CONTEXT = "publicContext";

   public static final String LOOPBACK_SERVICE_CONTEXT = "/services/loopbackcontent";

   public static final String UPLOAD_SERVICE_CONTEXT = "/services/upload";

   private boolean loaded = false;

   private HandlerManager eventBus;
   
   private ApplicationConfiguration configuration;

   public Configuration(HandlerManager eventBus, Loader loader)
   {
      this.eventBus = eventBus;      
      eventBus.addHandler(ApplicationConfigurationReceivedEvent.TYPE, this);
      configuration = new ApplicationConfiguration(getRegistryURL());
      ApplicationInitializer applicationInitializer = new ApplicationInitializer(eventBus, APPLICATION_NAME, loader);
      applicationInitializer.getApplicationConfiguration(CONFIG_NODENAME);
   }

   public void onConfigurationReceived(ApplicationConfigurationReceivedEvent event)
   {
      JSONObject jsonConfiguration = event.getApplicationConfiguration().getConfiguration().isObject();
      
      if (jsonConfiguration.containsKey(CONTEXT))
      {
         configuration.setContext(jsonConfiguration.get(Configuration.CONTEXT).isString().stringValue());
         configuration.setLoopbackServiceContext(configuration.getContext() + LOOPBACK_SERVICE_CONTEXT);
         configuration.setUploadServiceContext(configuration.getContext() + UPLOAD_SERVICE_CONTEXT);         
      }
      else
      {
         showErrorMessage(CONTEXT);
         return;
      }

      if (jsonConfiguration.containsKey(PUBLIC_CONTEXT))
         configuration.setPublicContext(jsonConfiguration.get(Configuration.PUBLIC_CONTEXT).isString().stringValue());
      else
      {
         showErrorMessage(PUBLIC_CONTEXT);
         return;
      }

      // TODO [IDE-307] handle absence of appConfig["entryPoint"] property in the configuration (case 1) 
      if (jsonConfiguration.containsKey(ENTRY_POINT))
      {
         configuration.setDefaultEntryPoint(jsonConfiguration.get(Configuration.ENTRY_POINT).isString().stringValue());
      }      
      else
      {
         showErrorMessage(ENTRY_POINT);
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

}
