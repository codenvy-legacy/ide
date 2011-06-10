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
package org.exoplatform.ide.extension.groovy.client.service.groovy.marshal;

import java.util.List;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import org.exoplatform.ide.extension.groovy.shared.Attribute;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JarListUnmarshaller implements Unmarshallable
{

   private List<Jar> jarList;

   public JarListUnmarshaller(List<Jar> jarList)
   {
      this.jarList = jarList;
   }

   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JavaScriptObject json = build(response.getText());
      JSONArray jsonArray = new JSONArray(json);

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONObject jarJSONObj = jsonArray.get(i).isObject();

         Jar jar = new Jar(jarJSONObj.get("path").isString().stringValue());
         jarList.add(jar);

         JSONArray propertiesArray = jarJSONObj.get("properties").isArray();
         for (int pi = 0; pi < propertiesArray.size(); pi++)
         {
            JSONObject propertyObject = propertiesArray.get(pi).isObject();
            String name = propertyObject.get("name").isString().stringValue();
            String value = propertyObject.get("value").isString().stringValue();

            Attribute jarProperty = new Attribute(name, value);
            jar.getAttributes().add(jarProperty);
         }
      }
   }

   private static native JavaScriptObject build(String json) /*-{
      return eval('(' + json + ')');      
   }-*/;

}
