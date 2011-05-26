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

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.xml.client.Node;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationSettingsUnmarshaller implements Const, Unmarshallable
{

   private ApplicationSettings applicationSettings;

   public final static String ERROR_MESSAGE = "Can't parse application settings!";

   public ApplicationSettingsUnmarshaller(ApplicationSettings applicationSettings)
   {
      this.applicationSettings = applicationSettings;
   }

   private Node getChildNode(Node node, String name)
   {
      for (int i = 0; i < node.getChildNodes().getLength(); i++)
      {
         Node childNode = node.getChildNodes().item(i);
         if (name.equals(childNode.getNodeName()))
         {
            return childNode;
         }
      }

      return null;
   }

   private static native String javaScriptDecodeURIComponent(String text) /*-{
                                                                          return decodeURIComponent(text);
                                                                          }-*/;

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         //         Document dom = XMLParser.parse(response.getText());
         //
         //         Node configurationNode = dom.getElementsByTagName(SETTINGS).item(0);
         //
         //         NodeList nodes = configurationNode.getChildNodes();
         //         for (int i = 0; i < nodes.getLength(); i++)
         //         {
         //            Node node = nodes.item(i);
         //
         //            String nodeName = node.getNodeName();
         //            if (nodeName.endsWith("_str"))
         //            {
         //               parseStringValue(node);
         //            }
         //            else if (nodeName.endsWith("_int"))
         //            {
         //               parseIntegerValue(node);
         //            }
         //            else if (nodeName.endsWith("_bool"))
         //            {
         //               parseBooleanValue(node);
         //            }
         //            else if (nodeName.endsWith("_list"))
         //            {
         //               parseListValue(node);
         //            }
         //            else if (nodeName.endsWith("_map"))
         //            {
         //               parseMapValue(node);
         //            }
         //            else
         //            {
         //               new Exception("Can't parse node value").printStackTrace();
         //            }
         //
         //         }
         JSONValue value = JSONParser.parseStrict(response.getText());
         parseSettings(value);

      }
      catch (Exception exc)
      {
         //new Exception().printStackTrace();
         exc.printStackTrace();
         throw new UnmarshallerException(ERROR_MESSAGE);
      }
   }

   /**
    * @param settings
    */
   public void parseSettings(JSONValue settings)
   {
      for (String key : settings.isObject().keySet())
      {
         JSONValue v = settings.isObject().get(key);
         if (v.isArray() != null)
         {
            parseListValue(key, v.isArray());
         }
         else if (v.isBoolean() != null)
         {
            parseBooleanValue(key, v.isBoolean());
         }
         else if (v.isNull() != null)
         {
            //TODO
         }
         else if (v.isNumber() != null)
         {
            parseNumberValue(key, v.isNumber());
         }
         else if (v.isObject() != null)
         {
            parseMapValue(key, v.isObject());
         }
         else if (v.isString() != null)
         {
            parseStringValue(key, v.isString());
         }
      }
   }

   /**
    * @param key
    * @param string
    */
   private void parseStringValue(String key, JSONString string)
   {
      applicationSettings.setValue(key, string.stringValue(), Store.REGISTRY);
   }

   /**
    * @param key
    * @param object
    */
   private void parseMapValue(String key, JSONObject object)
   {
      Map<String, String> map = new LinkedHashMap<String, String>();
      for (String k : object.keySet())
      {
         map.put(k, object.get(k).isString().stringValue());
      }
      applicationSettings.setValue(key, map, Store.REGISTRY);
   }

   /**
    * @param key
    * @param number
    */
   private void parseNumberValue(String key, JSONNumber number)
   {
      applicationSettings.setValue(key, (int)number.doubleValue(), Store.REGISTRY);
   }

   /**
    * @param key 
    * @param bool
    */
   private void parseBooleanValue(String key, JSONBoolean bool)
   {
      applicationSettings.setValue(key, bool.booleanValue(), Store.REGISTRY);
   }

   /**
    * @param key 
    * @param array
    */
   private void parseListValue(String key, JSONArray array)
   {
      List<String> list = new ArrayList<String>();
      for (int i = 0; i < array.size(); i++)
      {
         list.add(array.get(i).isString().stringValue());
      }
      applicationSettings.setValue(key, list, Store.REGISTRY);
   }

   //   private void parseStringValue(Node node)
   //   {
   //      String name = node.getNodeName();
   //      name = name.substring(0, name.length() - "_str".length());
   //
   //      if (!node.hasChildNodes())
   //      {
   //         applicationSettings.setValue(name, "", Store.REGISTRY);
   //      }
   //      else
   //      {
   //         Node valueNode = node.getChildNodes().item(0);
   //         String value = valueNode.getNodeValue();
   //         value = javaScriptDecodeURIComponent(value);
   //         applicationSettings.setValue(name, value, Store.REGISTRY);
   //      }
   //   }

   //   private void parseIntegerValue(Node node)
   //   {
   //      String name = node.getNodeName();
   //      name = name.substring(0, name.length() - "_int".length());
   //
   //      if (!node.hasChildNodes())
   //      {
   //         applicationSettings.setValue(name, new Integer(0), Store.REGISTRY);
   //      }
   //      else
   //      {
   //         Node valueNode = node.getChildNodes().item(0);
   //         String value = valueNode.getNodeValue();
   //         value = javaScriptDecodeURIComponent(value);
   //
   //         Integer intValue = new Integer(value);
   //         applicationSettings.setValue(name, intValue, Store.REGISTRY);
   //      }
   //   }

   //   private void parseBooleanValue(Node node)
   //   {
   //      String name = node.getNodeName();
   //      name = name.substring(0, name.length() - "_bool".length());
   //
   //      if (!node.hasChildNodes())
   //      {
   //         applicationSettings.setValue(name, Boolean.FALSE, Store.REGISTRY);
   //      }
   //      else
   //      {
   //         Node valueNode = node.getChildNodes().item(0);
   //         String value = valueNode.getNodeValue();
   //         value = javaScriptDecodeURIComponent(value);
   //
   //         Boolean booleanValue = new Boolean(value);
   //         applicationSettings.setValue(name, booleanValue, Store.REGISTRY);
   //      }
   //   }

   //   private void parseListValue(Node node)
   //   {
   //      String name = node.getNodeName();
   //      name = name.substring(0, name.length() - "_list".length());
   //
   //      if (!node.hasChildNodes())
   //      {
   //         applicationSettings.setValue(name, new ArrayList<String>(), Store.REGISTRY);
   //      }
   //      else
   //      {
   //         List<String> items = new ArrayList<String>();
   //
   //         for (int i = 0; i < node.getChildNodes().getLength(); i++)
   //         {
   //            Node itemNode = node.getChildNodes().item(i);
   //            String value = !itemNode.hasChildNodes() ? "" : itemNode.getChildNodes().item(0).getNodeValue();
   //            value = javaScriptDecodeURIComponent(value);
   //            items.add(value);
   //         }
   //
   //         applicationSettings.setValue(name, items, Store.REGISTRY);
   //      }
   //   }

   //   private void parseMapValue(Node node)
   //   {
   //      String name = node.getNodeName();
   //      name = name.substring(0, name.length() - "_map".length());
   //
   //      if (!node.hasChildNodes())
   //      {
   //         applicationSettings.setValue(name, new LinkedHashMap<String, String>(), Store.REGISTRY);
   //      }
   //      else
   //      {
   //         Map<String, String> map = new LinkedHashMap<String, String>();
   //
   //         for (int i = 0; i < node.getChildNodes().getLength(); i++)
   //         {
   //            Node itemNode = node.getChildNodes().item(i);
   //
   //            Node keyNode = getChildNode(itemNode, "key");
   //            Node valueNode = getChildNode(itemNode, "value");
   //
   //            String key = keyNode.getChildNodes().item(0).getNodeValue();
   //            key = javaScriptDecodeURIComponent(key);
   //            String value = valueNode.getChildNodes().item(0).getNodeValue();
   //            value = javaScriptDecodeURIComponent(value);
   //            map.put(key, value);
   //         }
   //
   //         applicationSettings.setValue(name, map, Store.REGISTRY);
   //      }
   //
   //   }

}
