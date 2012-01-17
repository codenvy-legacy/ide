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
package org.exoplatform.ide.client.model.settings.marshal;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationSettingsMarshaller implements Marshallable
{

   private ApplicationSettings applicationSettings;

   public ApplicationSettingsMarshaller(ApplicationSettings applicationSettings)
   {
      this.applicationSettings = applicationSettings;
   }

   public String marshal()
   {
      JSONObject settings = new JSONObject();
      Map<String, Object> valueMap = applicationSettings.getValues();
      Iterator<String> keyIter = valueMap.keySet().iterator();
      while (keyIter.hasNext())
      {
         String key = keyIter.next();

         if (applicationSettings.getStore(key) != Store.SERVER)
         {
            continue;
         }

         Object value = valueMap.get(key);
         if (value instanceof String)
         {
            settings.put(key, new JSONString((String)value));
         }
         else if (value instanceof Integer)
         {
            settings.put(key, new JSONNumber((Double)value));
         }
         else if (value instanceof Boolean)
         {
            settings.put(key, JSONBoolean.getInstance((Boolean)value));
         }
         else if (value instanceof List)
         {
            settings.put(key, getListNode(value));
         }
         else if (value instanceof Map)
         {
            settings.put(key, getMapNode(value));
         }
      }
      return settings.toString();
   }

   @SuppressWarnings("unchecked")
   private JSONArray getListNode(Object value)
   {
      JSONArray array = new JSONArray();
      List<String> values = (List<String>)value;
      int index = 0;
      for (String v : values)
      {
         array.set(index, new JSONString(v));
         index++;
      }
      return array;
   }

   @SuppressWarnings("unchecked")
   private JSONObject getMapNode(Object value)
   {
      JSONObject map = new JSONObject();
      Map<String, String> values = (Map<String, String>)value;
      Iterator<String> keyIter = values.keySet().iterator();
      while (keyIter.hasNext())
      {
         String k = keyIter.next();
         String v = values.get(k);
         map.put(k, new JSONString(v));
      }
      return map;
   }
}
