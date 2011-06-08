/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.model.configuration.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window.Location;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.model.configuration.IDEInitializationConfiguration;
import org.exoplatform.ide.client.model.conversation.marshal.UserInfoUnmarshaller;
import org.exoplatform.ide.client.model.settings.marshal.ApplicationSettingsUnmarshaller;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 25, 2011 evgen $
 *
 */
public class IDEConfigurationUnmarshaller implements Unmarshallable
{

   private final static String CONTEXT = "context";

   private final static String GADGET_SERVER = "gadgetServer";

   private final static String PUBLIC_CONTEXT = "publicContext";

   public static final String LOOPBACK_SERVICE_CONTEXT = "/ide/loopbackcontent";

   public static final String UPLOAD_SERVICE_CONTEXT = "/ide/upload";

   private static final String APP_CONFIG = "configuration";

   private static final String USER_SETTINGS = "userSettings";

   private static final String DEFAUTL_ENTRY_POINT = "defaultEntrypoint";

   private static final String IS_DISCOVERABLE = "discoverable";

   private static final String USER = "user";
   
   private static final String INVALID_CONFIGURATION_TITLE = IDE.ERRORS_CONSTANT.confInvalidConfTitle();

   //   private static final String HTTPS_PORT = "httpsPort";

   private IDEInitializationConfiguration initializationConfiguration;

   private IDEConfiguration configuration;

   private JSONObject defaultAppConfiguration;

   /**
    * @param initializationConfiguration
    * @param defaultAppConfiguration 
    */
   public IDEConfigurationUnmarshaller(IDEInitializationConfiguration initializationConfiguration,
      JSONObject defaultAppConfiguration)
   {
      this.initializationConfiguration = initializationConfiguration;
      this.defaultAppConfiguration = defaultAppConfiguration;
      configuration = new IDEConfiguration();
      configuration.setRegistryURL(getRegistryURL());
      this.initializationConfiguration.setIdeConfiguration(configuration);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONValue value = JSONParser.parseStrict(response.getText());
         if (value.isObject() != null)
         {
            JSONObject object = value.isObject();
            if (object.containsKey(APP_CONFIG))
            {
               parseAppConfig(object.get(APP_CONFIG).isObject());
            }
            else
            {
               parseAppConfig(defaultAppConfiguration);
            }

            ApplicationSettings applicationSettings = new ApplicationSettings();
            initializationConfiguration.setSettings(applicationSettings);
            if (object.containsKey(USER_SETTINGS))
            {
               new ApplicationSettingsUnmarshaller(applicationSettings).parseSettings(object.get(USER_SETTINGS));
            }

            if (object.containsKey(DEFAUTL_ENTRY_POINT))
            {
               initializationConfiguration.getIdeConfiguration().setDefaultEntryPoint(
                  object.get(DEFAUTL_ENTRY_POINT).isString().stringValue());
            }

            if (object.containsKey(IS_DISCOVERABLE))
            {
               initializationConfiguration.setDiscoverable(object.get(IS_DISCOVERABLE).isBoolean().booleanValue());
            }

            if (object.containsKey(USER))
            {
               UserInfo userInfo = new UserInfo();
               initializationConfiguration.setUserInfo(userInfo);
               new UserInfoUnmarshaller(userInfo).parseUserInfo(object.get(USER).isObject());
            }

         }
         else
            throw new Exception("Received JSON value not a Object.");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new UnmarshallerException("Can't parse application settings!");
      }
   }

   private void parseAppConfig(JSONObject jsonConfiguration)
   {

      if (jsonConfiguration.containsKey(CONTEXT))
      {
         configuration.setContext(jsonConfiguration.get(CONTEXT).isString().stringValue());
         configuration.setLoopbackServiceContext(configuration.getContext() + LOOPBACK_SERVICE_CONTEXT);
         configuration.setUploadServiceContext(configuration.getContext() + UPLOAD_SERVICE_CONTEXT);
      }
      else
      {
         showErrorMessage(CONTEXT);
         return;
      }

      if (jsonConfiguration.containsKey(PUBLIC_CONTEXT))
         configuration.setPublicContext(jsonConfiguration.get(PUBLIC_CONTEXT).isString().stringValue());
      else
      {
         showErrorMessage(PUBLIC_CONTEXT);
         return;
      }

      //      if (jsonConfiguration.containsKey(HTTPS_PORT))
      //         configuration.setHttpsPort((int)jsonConfiguration.get(HTTPS_PORT).isNumber().doubleValue());
      //      else
      //      {
      //         showErrorMessage(HTTPS_PORT);
      //         return;
      //      }

      if (jsonConfiguration.containsKey(GADGET_SERVER))
         //TODO: now we can load gadget only from current host
         configuration.setGadgetServer(Location.getProtocol() + "//" + Location.getHost()
            + jsonConfiguration.get(GADGET_SERVER).isString().stringValue());
      else
      {
         showErrorMessage(GADGET_SERVER);
         return;
      }

   }

   private void showErrorMessage(String message)
   {
      String m = "Invalid configuration:  missing " + message + " item";
      Dialogs.getInstance().showError(INVALID_CONFIGURATION_TITLE, m);
   }

   private static native String getRegistryURL() /*-{
		return $wnd.registryURL;
   }-*/;

}
