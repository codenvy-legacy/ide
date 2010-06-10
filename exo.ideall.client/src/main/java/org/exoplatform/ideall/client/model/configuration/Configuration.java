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

package org.exoplatform.ideall.client.model.configuration;

import org.exoplatform.gwtframework.commons.initializer.ApplicationInitializer;
import org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedEvent;
import org.exoplatform.gwtframework.commons.initializer.event.ApplicationConfigurationReceivedHandler;
import org.exoplatform.gwtframework.commons.loader.Loader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window.Location;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class Configuration implements ApplicationConfigurationReceivedHandler
{

   public final static String APPLICATION = "IDEall";

   private static final String CONFIG_NODENAME = "configuration";

   private final static String CONTEXT = "context";

   private final static String ENTRY_POINT = "entryPoint";

   private final static String GADGET_SERVER = "gadgetServer";

   private final static String PUBLIC_CONTEXT = "publicContext";

   public static final String LOOPBACK_SERVICE_CONTEXT = "/services/loopbackcontent";

   public static final String UPLOAD_SERVICE_CONTEXT = "/services/upload";

   private String defaultEntryPoint;

   private String context;

   private String loopbackServiceContext;

   private String uploadServiceContext;

   private String publicContext;

   private String gadgetURL = GWT.getModuleBaseURL();

   private String gadgetServer;

   private boolean loaded = false;

   private static Configuration instance;

   private HandlerManager eventBus;

   public static Configuration getInstance()
   {
      return instance;
   }

   public Configuration(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      instance = this;
   }

   public void loadConfiguration(HandlerManager eventBus, Loader loader)
   {
      ApplicationInitializer applicationInitializer = new ApplicationInitializer(eventBus, APPLICATION, loader);
      applicationInitializer.getApplicationConfiguration(CONFIG_NODENAME);
   }

   public void onConfigurationReceived(ApplicationConfigurationReceivedEvent event)
   {
      JSONObject config = event.getApplicationConfiguration().getConfiguration().isObject();

      if (config.containsKey(CONTEXT))
      {
         context = config.get(Configuration.CONTEXT).isString().stringValue();
         loopbackServiceContext = context + LOOPBACK_SERVICE_CONTEXT;
         uploadServiceContext = context + UPLOAD_SERVICE_CONTEXT;
      }
      else
      {
         sendErrorMessage(CONTEXT);
         return;
      }

      if (config.containsKey(PUBLIC_CONTEXT))
         publicContext = config.get(Configuration.PUBLIC_CONTEXT).isString().stringValue();
      else
      {
         sendErrorMessage(PUBLIC_CONTEXT);
         return;
      }

      
      if (config.containsKey(ENTRY_POINT))
         defaultEntryPoint = config.get(Configuration.ENTRY_POINT).isString().stringValue();
//      else
//      {
//         sendErrorMessage(ENTRY_POINT);
//         return;
//      }

      if (config.containsKey(GADGET_SERVER))
         //TODO: now we can load gadget only from current host
         gadgetServer =
            Location.getProtocol() + "//" + Location.getHost() + config.get(GADGET_SERVER).isString().stringValue();
      else
      {
         sendErrorMessage(GADGET_SERVER);
         return;
      }

      loaded = true;
      eventBus.fireEvent(new ConfigurationReceivedSuccessfullyEvent());
   }

   /**
    * @return the context
    */
   public String getContext()
   {
      return context;
   }

   public String getDefaultEntryPoint()
   {
      return defaultEntryPoint;
   }

   /**
    * @param context the context to set
    */
   public void setContext(String context)
   {
      this.context = context;
   }

   /**
    * @return the publicContext
    */
   public String getPublicContext()
   {
      return publicContext;
   }

   /**
    * @param publicContext the publicContext to set
    */
   public void setPublicContext(String publicContext)
   {
      this.publicContext = publicContext;
   }

   /**
    * @return the gadgetServer
    */
   public String getGadgetServer()
   {
      return gadgetServer;
   }

   /**
    * @param gadgetServer the gadgetServer to set
    */
   public void setGadgetServer(String gadgetServer)
   {
      this.gadgetServer = gadgetServer;
   }

   /**
    * @return the gadgetURL
    */
   public String getGadgetURL()
   {
      return gadgetURL;
   }

   public boolean isLoaded()
   {
      return loaded;
   }

   /**
    * @return the loopbackServiceContext
    */
   public String getLoopbackServiceContext()
   {
      return loopbackServiceContext;
   }

   /**
    * @param loopbackServiceContext the loopbackServiceContext to set
    */
   public void setLoopbackServiceContext(String loopbackServiceContext)
   {
      this.loopbackServiceContext = loopbackServiceContext;
   }

   public String getUploadServiceContext()
   {
      return uploadServiceContext;
   }

   public void setUploadServiceContext(String uploadServiceContext)
   {
      this.uploadServiceContext = uploadServiceContext;
   }

   private void sendErrorMessage(String message)
   {
      String m = "Invalid configuration missing : " + message + " item";
      eventBus.fireEvent(new InvalidConfigurationRecievedEvent(m));
   }

   public static native String getRegistryURL() /*-{
       return $wnd.registryURL;
    }-*/;

}
