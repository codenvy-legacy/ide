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
package org.exoplatform.ide.extension.java.client.marshaller;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.java.shared.MavenResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Unmarshaller for response from server, when {@link MavenResponse} is returned.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateJavaProjectUnmarshaller.java Jun 22, 2011 5:06:40 PM vereshchaka $
 * 
 */
public class MavenResponseUnmarshaller implements Unmarshallable
{
   public interface Constants
   {
      public static final String RESULT = "result";

      public static final String EXIT_CODE = "exitCode";

      public static final String OUTPUT = "output";
   }

   private MavenResponse mavenResponse;

   public MavenResponseUnmarshaller(MavenResponse mavenResponse)
   {
      this.mavenResponse = mavenResponse;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JavaScriptObject json = build(response.getText());
      if (json == null)
         return;
      JSONObject jsonObject = new JSONObject(json).isObject();
      if (jsonObject == null)
         return;

      parseObject(jsonObject);
   }

   private void parseObject(JSONObject jsonObject)
   {
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals(Constants.EXIT_CODE))
         {
            if (jsonValue.isNumber() != null)
            {
               mavenResponse.setExitCode((int)jsonValue.isNumber().doubleValue());
            }
         }
         else if (key.equals(Constants.OUTPUT))
         {
            if (jsonValue.isString() != null)
            {
               mavenResponse.setOutput(jsonValue.isString().stringValue());
            }
            else
            {
               mavenResponse.setOutput("");
            }

         }
         else if (key.equals(Constants.RESULT))
         {
            Map<String, String> result = new HashMap<String, String>();
            if (jsonValue.isObject() != null)
            {
               parseMapValue(key, jsonValue.isObject(), result);
            }
            mavenResponse.setResult(result);
         }
      }
   }

   /**
    * @param key
    * @param object
    */
   private void parseMapValue(String key1, JSONObject jsonObject, Map<String, String> objectsMap)
   {
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (jsonValue.isString() != null)
         {
            objectsMap.put(key, jsonValue.isString().stringValue());
         }
      }
   }

   /**
    * Build {@link JavaScriptObject} from string.
    * 
    * @param json string that contains object
    * @return {@link JavaScriptObject}
    */
   protected static native JavaScriptObject build(String json) /*-{
                                                               try {
                                                               var object = eval('(' + json + ')');
                                                               return object;
                                                               } catch (e) {
                                                               return null;
                                                               }
                                                               }-*/;

}
