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

import com.google.gwt.core.client.JsonUtils;

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

public class ApplicationSettingsMarshaller implements Const, Marshallable
{

   private ApplicationSettings applicationSettings;

   public ApplicationSettingsMarshaller(ApplicationSettings applicationSettings)
   {
      this.applicationSettings = applicationSettings;
   }

   private static native String javaScriptEncodeURIComponent(String text) /*-{
                                                                          return encodeURIComponent(text);
                                                                          }-*/;

   public String marshal()
   {
      String xml = "{";
      Map<String, Object> valueMap = applicationSettings.getValues();
      Iterator<String> keyIter = valueMap.keySet().iterator();
      while (keyIter.hasNext())
      {
         String key = keyIter.next();

         if (applicationSettings.getStore(key) != Store.REGISTRY)
         {
            continue;
         }

         Object value = valueMap.get(key);

         if (value instanceof String)
         {
            xml += getStringNode(key, value);
         }
         else if (value instanceof Integer)
         {
            xml += getIntegerNode(key, value);
         }
         else if (value instanceof Boolean)
         {
            xml += getBooleanNode(key, value);
         }
         else if (value instanceof List)
         {
            xml += getListNode(key, value);
         }
         else if (value instanceof Map)
         {
            xml += getMapNode(key, value);
         }
      }
      if (xml.endsWith(","))
         xml = xml.substring(0, xml.length() - 1);
      xml += "}";
      return xml;
   }

   private String getStringNode(String key, Object value)
   {
      //      return "<" + xmlNodeName + ">" + javaScriptEncodeURIComponent("" + value) + "</" + xmlNodeName + ">";
      return "\"" + key + "\":\"" + value + "\",";
   }

   private String getIntegerNode(String key, Object value)
   {
      //      return "<" + xmlNodeName + ">" + javaScriptEncodeURIComponent("" + value) + "</" + xmlNodeName + ">";
      return "\"" + key + "\":" + value + ",";
   }

   private String getBooleanNode(String key, Object value)
   {
      //      return "<" + xmlNodeName + ">" + javaScriptEncodeURIComponent("" + value) + "</" + xmlNodeName + ">";
      return "\"" + key + "\":" + value + ",";
   }

   @SuppressWarnings("unchecked")
   private String getListNode(String key, Object value)
   {
      String xml = "\"" + key + "\":[";
      List<String> values = (List<String>)value;
      for (String v : values)
      {
         xml +=  JsonUtils.escapeValue(v) + ",";
      }
      if (xml.endsWith(","))
         xml = xml.substring(0, xml.length() - 1);
      xml += "],";
      

      //      String xml = "<" + xmlNodeName + ">";
      //
      //      List<String> values = (List<String>)value;
      //      for (String v : values)
      //      {
      //         String subXML = "<item>" + javaScriptEncodeURIComponent(v) + "</item>";
      //         xml += subXML;
      //      }
      //
      //      xml += "</" + xmlNodeName + ">";
      return xml;
   }

   @SuppressWarnings("unchecked")
   private String getMapNode(String key, Object value)
   {
      String xml = "\"" + key + "\":{";

      Map<String, String> values = (Map<String, String>)value;
      Iterator<String> keyIter = values.keySet().iterator();
      while (keyIter.hasNext())
      {
         String k = keyIter.next();
         String v = values.get(k);
         xml += JsonUtils.escapeValue(k) + ":" + JsonUtils.escapeValue(v)+ ",";
      }

      if (xml.endsWith(","))
         xml = xml.substring(0, xml.length() - 1);
      xml += "},";
      //      String xml = "<" + xmlNodeName + ">";
      //
      //      Map<String, String> values = (Map<String, String>)value;
      //      Iterator<String> keyIter = values.keySet().iterator();
      //      while (keyIter.hasNext())
      //      {
      //         String k = keyIter.next();
      //         String v = values.get(k);
      //
      //         String subXML = "<item>";
      //
      //         subXML += "<key>" + javaScriptEncodeURIComponent(k) + "</key>";
      //         subXML += "<value>" + javaScriptEncodeURIComponent(v) + "</value>";
      //
      //         subXML += "</item>";
      //
      //         xml += subXML;
      //      }
      //
      //      xml += "</" + xmlNodeName + ">";
      return xml;
   }

}
