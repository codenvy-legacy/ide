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
package org.exoplatform.ide.client.model.settings;

import com.google.gwt.http.client.RequestException;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Cookies;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.copy.MimeType;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.marshal.ApplicationSettingsMarshaller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends {@link SettingsService}
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: SettingsServiceImpl.java Mar 30, 2011 11:13:03 AM vereshchaka $
 * 
 */
public class SettingsServiceImpl extends SettingsService
{

   /**
    * 
    */
   private static final String USER_NAME_DELIMITER = "-";

   private static final String LIST_ITEMS_DELIMITER = "#";

   private static final String MAP_ITEMS_DELIMETER = LIST_ITEMS_DELIMITER;

   private static final String MAP_KEYS_DELIMETER = "@";

   private static final String COOKIE_PREFIX = "eXo-IDE-";

   private HandlerManager eventBus;

   private Loader loader;

   private String registryServiceURL;

   private String userName;

   private String restContext;

   private ApplicationSettings applicationSettings = new ApplicationSettings();

   public SettingsServiceImpl(HandlerManager eventBus, String registryServiceURL, String userName, Loader loader,
      String restContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.registryServiceURL = registryServiceURL;
      this.userName = userName;
      this.restContext = restContext;
   }

   private String getURL()
   {
      return restContext + "/ide/configuration";
   }

   /**
    * @see org.exoplatform.ide.client.model.settings.SettingsService#saveSettingsToCookies(org.exoplatform.ide.client.framework.settings.ApplicationSettings)
    */
   @Override
   public void saveSettingsToCookies(ApplicationSettings applicationSettings)
   {
      storeCookies(applicationSettings);
   }

   @Override
   public void saveSettingsToServer(ApplicationSettings applicationSettings,
      AsyncRequestCallback<ApplicationSettings> callback) throws RequestException
   {
      String url = getURL();

      ApplicationSettingsMarshaller marshaller = new ApplicationSettingsMarshaller(applicationSettings);

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(marshaller.marshal()).send(callback);

   }

   // ------- Implementation --------------

   private void storeCookies(ApplicationSettings applicationSettings)
   {
      Iterator<String> keyIter = applicationSettings.getValues().keySet().iterator();
      while (keyIter.hasNext())
      {
         String key = keyIter.next();
         Store storing = applicationSettings.getStore(key);

         if (storing == null || storing == Store.SERVER || storing == Store.NONE)
         {
            // Setting stores to registry. Skip it
            continue;
         }

         Object value = applicationSettings.getValueAsObject(key);
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
         else if (value instanceof Map)
         {
            storeMap(key, value);
         }
      }
   }

   /**
    * @param key
    * @param value
    */
   private void storeMap(String key, Object value)
   {
      @SuppressWarnings("unchecked")
      Map<String, String> map = (Map<String, String>)value;

      String lockTokens = "";

      for (String k : map.keySet())
      {
         String val = map.get(k);
         if (!"".equals(lockTokens))
         {
            lockTokens += MAP_KEYS_DELIMETER;
         }
         lockTokens += javaScriptEncodeURIComponent(k);
         lockTokens += MAP_ITEMS_DELIMETER + val;

      }

      setCookie(key + "_map", lockTokens, new Date(System.currentTimeMillis() + 3600000));
   }

   private void storeString(String key, String value)
   {
      setCookie(key + "_str", value);
   }

   private void storeInteger(String key, Integer value)
   {
      setCookie(key, "_int" + value);
   }

   private void storeBoolean(String key, Boolean value)
   {
      setCookie(key + "_bool", "" + value);
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

      setCookie(key + "_list", listItems);
   }

   private void setCookie(String name, String value)
   {
      Cookies.setCookie(COOKIE_PREFIX + userName + USER_NAME_DELIMITER + name, value);
   }

   private void setCookie(String name, String value, Date expires)
   {
      Cookies.setCookie(COOKIE_PREFIX + userName + USER_NAME_DELIMITER + name, value, expires);
   }

   private Collection<String> getCookieNames()
   {
      List<String> cookies = new ArrayList<String>();

      String prefix = COOKIE_PREFIX + userName + USER_NAME_DELIMITER;

      for (String name : Cookies.getCookieNames())
      {
         if (name.startsWith(prefix))
         {

            cookies.add(name.substring(prefix.length()));
         }
      }

      return cookies;
   }

   public void restoreFromCookies(ApplicationSettings applicationSettings)
   {
      for (String name : getCookieNames())
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
         else if (name.endsWith("_map"))
         {
            restoreMapValue(applicationSettings, name);
         }
      }
   }

   private String getCookie(String name)
   {
      return Cookies.getCookie(COOKIE_PREFIX + userName + USER_NAME_DELIMITER + name);

   }

   /**
    * @param applicationSettings
    * @param name
    */
   private void restoreMapValue(ApplicationSettings applicationSettings, String name)
   {
      String n = getName(name, "_map");

      String value = getCookie(name);

      Map<String, String> map = new LinkedHashMap<String, String>();

      String[] items = value.split(MAP_KEYS_DELIMETER);

      for (int i = 0; i < items.length; i++)
      {
         String s = items[i];
         if ("".equals(s))
         {
            continue;
         }
         String[] v = s.split(MAP_ITEMS_DELIMETER);
         map.put(javaScriptDecodeURIComponent(v[0]), v[1]);
      }
      applicationSettings.setValue(n, map, Store.COOKIES);
   }

   private String getName(String name, String suffix)
   {
      return name.substring(0, name.length() - suffix.length());
   }

   private void readStringValue(ApplicationSettings applicationSettings, String name)
   {
      String n = getName(name, "_str");
      String value = getCookie(name);
      applicationSettings.setValue(n, value, Store.COOKIES);
   }

   private void restoreBooleanValue(ApplicationSettings applicationSettings, String name)
   {
      String n = getName(name, "_bool");
      String value = getCookie(name);
      applicationSettings.setValue(n, Boolean.valueOf(value), Store.COOKIES);
   }

   private void restoreListValue(ApplicationSettings applicationSettings, String name)
   {
      String n = getName(name, "_list");
      String value = getCookie(name);
      List<String> list = new ArrayList<String>();

      String[] items = value.split(LIST_ITEMS_DELIMITER);
      for (String i : items)
      {
         String v = javaScriptDecodeURIComponent(i);
         if ("".equals(v))
         {
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
