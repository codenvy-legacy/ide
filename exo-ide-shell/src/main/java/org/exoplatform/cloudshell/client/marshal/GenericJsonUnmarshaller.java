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
package org.exoplatform.cloudshell.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.cloudshell.client.commands.Utils;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

/**
 * This unmarshaller format JSON string to pretty HTML string.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 16, 2011 evgen $
 *
 */
public class GenericJsonUnmarshaller extends StringUnmarshaller
{

   private static final JSONObject defaultColorMap = new JSONObject();
   
   public static final String STRING = "string";

   public static final String NUMBER = "number";

   public static final String NULL = "null";

   public static final String BOOLEAN = "boolean";

   public static final String ARRAY_OPEN = "array-open";

   public static final String ARRAY_CLOSE = "array-close";

   public static final String ARRAY_COMMA = "array-comma";

   public static final String OBJECT_OPEN = "object-open";

   public static final String OBJECT_CLOSE = "object-close";

   public static final String OBJECT_KEY = "object-key";

   public static final String OBJECT_COLON = "object-colon";

   static
   {
      defaultColorMap.put(STRING, new JSONString("#246fd5"));
      defaultColorMap.put(NUMBER, new JSONString("#8B008B"));
      defaultColorMap.put(BOOLEAN, new JSONString("#CD5C5C"));
      defaultColorMap.put(NULL, new JSONString("#696969"));
      defaultColorMap.put(ARRAY_OPEN, new JSONString("#8B0000"));
      defaultColorMap.put(ARRAY_CLOSE, new JSONString("#8B0000"));
      defaultColorMap.put(ARRAY_COMMA, new JSONString("#8B0000"));
      defaultColorMap.put(OBJECT_OPEN, new JSONString("#b70000"));
      defaultColorMap.put(OBJECT_CLOSE, new JSONString("#b70000"));
      defaultColorMap.put(OBJECT_KEY, new JSONString("#1FBE1F"));
      defaultColorMap.put(OBJECT_COLON, new JSONString("#8B0000"));
   }

   /**
    * @param callback
    */
   public GenericJsonUnmarshaller(AsyncRequestCallback<String> callback)
   {
      super(callback);
   }



   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONValue jsonValue = JSONParser.parseLenient(response.getText());
         callback.setResult(toHtmlJson(jsonValue));
      }
      catch (Exception e)
      {
         e.printStackTrace();
         callback.setResult(response.getText());
      }
   }

   /**
    * Convert the supplied JSONValue to a pretty-printed HTML representation
    * 
    * @param value
    *            The value to convert.
    * @return An HTML string, or null if the value was a null.
    */
   private static String toHtmlJson(JSONValue value)
   {
      if (value == null)
      {

         return null;
      }
      return toHtmlJson(value, defaultColorMap);
   }

   /**
    * Convert the supplied JSONValue to a pretty-printed HTML string, using the
    * supplied color map.
    * 
    * @param value
    *            The value to convert.
    * @param colorMap
    * @return An HTML string, or null if the value was a Java null.
    */
   private static String toHtmlJson(JSONValue value, JSONObject colorMap)
   {
      if (value == null)
      {
         return null;
      }
      StringBuffer sb = new StringBuffer();
      sb.append("<pre>\n");
      toHtmlJson(value, sb, colorMap, 0);
      if (value.isArray() != null || value.isObject() != null)
      {
         sb.append("\n");
      }
      sb.append("</pre>");
      return sb.toString();
   }

   private static void toHtmlJson(JSONValue value, StringBuffer sb, JSONObject colorMap, int indent)
   {
      if (value.isString() != null)
      {
         sb.append("<span style='color:" + colorMap.get(STRING).isString().stringValue() + ";'>"
            + Utils.htmlEncode(value.toString()) + "</span>");
      }
      else if (value.isNumber() != null)
      {
         sb.append("<span style='color:" + colorMap.get(NUMBER).isString().stringValue() + ";'>"
            + Utils.htmlEncode(value.toString()) + "</span>");
      }
      else if (value.isNull() != null)
      {
         sb.append("<span style='color:" + colorMap.get(NULL).isString().stringValue() + ";'>null</span>");
      }
      else if (value.isBoolean() != null)
      {
         sb.append("<span style='color:" + colorMap.get(BOOLEAN).isString().stringValue() + ";'>"
            + Utils.htmlEncode(value.toString()) + "</span>");
      }
      else if (value.isArray() != null)
      {
         JSONArray array = value.isArray();
         if (array.size() == 0)
         {
            sb.append("<span style='color:" + colorMap.get(ARRAY_OPEN).isString().stringValue()
               + ";'>[</span><span style='color:" + colorMap.get(ARRAY_CLOSE).isString().stringValue() + ";'>]</span>");
         }
         else
         {
            sb.append("<span style='color:" + colorMap.get(ARRAY_OPEN).isString().stringValue() + ";'>[</span>\n");
            for (int i = 0; i < array.size(); ++i)
            {
               indent(indent + 1, sb);
               toHtmlJson(array.get(i), sb, colorMap, indent + 1);
               if (i + 1 != array.size())
               {
                  sb.append("<span style='color:" + colorMap.get(ARRAY_COMMA).isString().stringValue() + ";'>,</span>");
               }
               sb.append("\n");
            }
            indent(indent, sb);
            sb.append("<span style='color:" + colorMap.get(ARRAY_CLOSE).isString().stringValue() + ";'>]</span>");
         }
      }
      else if (value.isObject() != null)
      {
         JSONObject object = value.isObject();
         if (object.size() == 0)
         {
            sb.append("<span style='color:" + colorMap.get(OBJECT_OPEN).isString().stringValue()
               + ";'>{</span><span style='color:" + colorMap.get(OBJECT_CLOSE).isString().stringValue() + ";'>}</span>");
         }
         else
         {
            sb.append("<span style='color:" + colorMap.get(OBJECT_OPEN).isString().stringValue() + ";'>{</span>\n");
            int i = 0;
            for (String key : object.keySet())
            {
               indent(indent + 1, sb);
               sb.append("<span style='color:" + colorMap.get(OBJECT_KEY).isString().stringValue() + ";'>"
                  + quotedString(key) + "</span> <span style='color:"
                  + colorMap.get(OBJECT_COLON).isString().stringValue() + ";'>:</span> ");
               toHtmlJson(object.get(key), sb, colorMap, indent + 1);
               if (i + 1 != object.size())
               {
                  sb.append(",");
               }
               sb.append("\n");
               ++i;
            }
            indent(indent, sb);
            sb.append("<span style='color:" + colorMap.get(OBJECT_CLOSE).isString().stringValue() + ";'>}</span>");
         }
      }

   }

   public static String quotedString(String value)
   {
      if (value == null || value.length() == 0)
      {
         return "\"\"";
      }
      char b;
      char c = 0;
      int i;
      int len = value.length();
      StringBuffer sb = new StringBuffer(len + 4);
      String t;
      sb.append('"');
      for (i = 0; i < len; i += 1)
      {
         b = c;
         c = value.charAt(i);
         switch (c)
         {
            case '\\' :
            case '"' :
               sb.append('\\');
               sb.append(c);
               break;
            case '/' :
               if (b == '<')
               {
                  sb.append('\\');
               }
               sb.append(c);
               break;
            case '\b' :
               sb.append("\\b");
               break;
            case '\t' :
               sb.append("\\t");
               break;
            case '\n' :
               sb.append("\\n");
               break;
            case '\f' :
               sb.append("\\f");
               break;
            case '\r' :
               sb.append("\\r");
               break;
            default :
               if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100'))
               {
                  t = "000" + Integer.toHexString(c);
                  sb.append("\\u" + t.substring(t.length() - 4));
               }
               else
               {
                  sb.append(c);
               }
         }
      }
      sb.append('"');
      return sb.toString();
   }

   private static void indent(int indent, StringBuffer sb)
   {
      for (int i = 0; i < indent; ++i)
      {
         sb.append("    ");
      }
   }
}
