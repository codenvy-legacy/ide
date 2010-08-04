/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.model.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.initializer.RegistryConstants;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.configuration.Configuration;
import org.exoplatform.ide.client.model.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.model.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.model.settings.event.SaveApplicationSettingsHandler;
import org.exoplatform.ide.client.model.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.model.settings.marshal.ApplicationSettingsMarshaller;
import org.exoplatform.ide.client.model.settings.marshal.ApplicationSettingsUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SettingsService implements SaveApplicationSettingsHandler
{

   private static final String LIST_ITEMS_DELIMITER = "#";

   private HandlerManager eventBus;

   private Loader loader;

   private String registryServiceURL;

   private String userName;

   public SettingsService(HandlerManager eventBus, String registryServiceURL, String userName, Loader loader)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.registryServiceURL = registryServiceURL;
      this.userName = userName;

      eventBus.addHandler(SaveApplicationSettingsEvent.TYPE, this);

      getApplicationSettings(new ApplicationSettings());
   }

   private String getURL()
   {
      String url =
         registryServiceURL + "/" + RegistryConstants.EXO_USERS + "/" + userName + "/" + Configuration.APPLICATION_NAME;
      return url;
   }

   private void getApplicationSettings(ApplicationSettings applicationSettings)
   {
      restoreFromCookies(applicationSettings);

      String url = getURL() + "/?nocache=" + Random.nextInt();

      ApplicationSettingsReceivedEvent event = new ApplicationSettingsReceivedEvent(applicationSettings);
      ApplicationSettingsUnmarshaller unmarshaller = new ApplicationSettingsUnmarshaller(applicationSettings);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   //   public void saveSetting(ApplicationSettings applicationSettings)
   //   {
   //      String url = getURL() + "/?createIfNotExist=true";
   //      
   //      ApplicationSettingsMarshaller marshaller = new ApplicationSettingsMarshaller(applicationSettings);
   //      ApplicationSettingsSavedEvent event = new ApplicationSettingsSavedEvent(applicationSettings);
   //
   //      String errorMessage = "Registry service is not deployed.";
   //      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);
   //
   //      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
   //      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT").header(
   //         HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   //   }

   public void onSaveApplicationSettings(final SaveApplicationSettingsEvent event)
   {
      if (event.getSaveType() == SaveType.COOKIES)
      {
         storeCookies(event.getApplicationSettings());
         new Timer()
         {
            @Override
            public void run()
            {
               eventBus.fireEvent(new ApplicationSettingsSavedEvent(event.getApplicationSettings(), SaveType.COOKIES));
            }
         }.schedule(10);

         return;
      }

      if (event.getSaveType() == SaveType.BOTH)
      {
         storeCookies(event.getApplicationSettings());
      }

      saveSettingsToRegistry(event.getApplicationSettings(), event.getSaveType());
   }

   private void saveSettingsToRegistry(ApplicationSettings applicationSettings, SaveType saveType)
   {
      String url = getURL() + "/?createIfNotExist=true";

      ApplicationSettingsMarshaller marshaller = new ApplicationSettingsMarshaller(applicationSettings);
      ApplicationSettingsSavedEvent event = new ApplicationSettingsSavedEvent(applicationSettings, saveType);
      
      String errorMessage = "Registry service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);
      
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT").header(
         HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);

   }

   private void storeCookies(ApplicationSettings applicationSettings)
   {
      Iterator<String> keyIter = applicationSettings.getValues().keySet().iterator();
      while (keyIter.hasNext())
      {
         String key = keyIter.next();
         Store storing = applicationSettings.getStoredIn(key);

         if (storing == null || storing == Store.REGISTRY || storing == Store.NONE)
         {
            // Setting stores to registry. Skip it
            continue;
         }

         Object value = applicationSettings.getValue(key);
         if (value instanceof String)
         {
            storeString(key, (String)value);
         }
         else if (value instanceof Integer)
         {
            storeInteger(key, (Integer)value);
         }
         else if (value instanceof Boolean)
         {
            storeBoolean(key, (Boolean)value);
         }
         else if (value instanceof List)
         {
            storeList(key, value);
         }
      }
   }

   private void storeString(String key, String value)
   {
      Cookies.setCookie(key + "_str", value);
   }

   private void storeInteger(String key, Integer value)
   {
      Cookies.setCookie(key, "_int" + value);
   }

   private void storeBoolean(String key, Boolean value)
   {
      Cookies.setCookie(key + "_bool", "" + value);
   }

   @SuppressWarnings("unchecked")
   private void storeList(String key, Object value)
   {
      List<String> list = (List<String>)value;

      String listItems = "";
      for (String item : list)
      {
         if (!"".equals(listItems))
         {
            listItems += LIST_ITEMS_DELIMITER;
         }

         listItems += javaScriptEncodeURIComponent(item);
      }

      Cookies.setCookie(key + "_list", listItems);
   }

   private void restoreFromCookies(ApplicationSettings applicationSettings)
   {
      for (String name : Cookies.getCookieNames())
      {
         if (name.endsWith("_str"))
         {
            readStringValue(applicationSettings, name);
         }
         else if (name.endsWith("_int"))
         {
         }
         else if (name.endsWith("_bool"))
         {
            restoreBooleanValue(applicationSettings, name);
         }
         else if (name.endsWith("_list"))
         {
            restoreListValue(applicationSettings, name);
         }
      }
   }

   private String getName(String name, String suffix)
   {
      return name.substring(0, name.length() - suffix.length());
   }

   private void readStringValue(ApplicationSettings applicationSettings, String name)
   {
      String n = getName(name, "_str");
      String value = Cookies.getCookie(name);
      applicationSettings.setValue(n, value, Store.COOKIES);
   }

   private void restoreBooleanValue(ApplicationSettings applicationSettings, String name)
   {
      String n = getName(name, "_bool");
      String value = Cookies.getCookie(name);
      applicationSettings.setValue(n, new Boolean(value), Store.COOKIES);
   }

   private void restoreListValue(ApplicationSettings applicationSettings, String name)
   {
      String n = getName(name, "_bool");
      String value = Cookies.getCookie(name);
      List<String> list = new ArrayList<String>();

      String[] items = value.split(LIST_ITEMS_DELIMITER);
      for (String i : items)
      {
         String v = javaScriptDecodeURIComponent(i);
         if ("".equals(v)) {
            continue;
         }
         list.add(v);
      }

      applicationSettings.setValue(n, list, Store.COOKIES);
   }

   private static native String javaScriptDecodeURIComponent(String text) /*-{
        return decodeURIComponent(text);
     }-*/;

   private static native String javaScriptEncodeURIComponent(String text) /*-{
        return encodeURIComponent(text);
     }-*/;

}
