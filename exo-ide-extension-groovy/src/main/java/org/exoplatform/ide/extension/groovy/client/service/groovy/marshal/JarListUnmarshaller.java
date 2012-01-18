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

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.extension.groovy.shared.Attribute;
import org.exoplatform.ide.extension.groovy.shared.Jar;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JarListUnmarshaller implements Unmarshallable<List<Jar>>
{

   private List<Jar> jarList;

   public JarListUnmarshaller(List<Jar> jarList)
   {
      this.jarList = jarList;
   }

   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JSONArray jsonArray = JSONParser.parseStrict(response.getText()).isArray();
      if (jsonArray == null)
      {
         return;
      }

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONObject jarJSONObj = jsonArray.get(i).isObject();

         String jarPath = jarJSONObj.get("path").isString().stringValue();
         Jar jar = new Jar(jarPath);
         jarList.add(jar);

         JSONArray propertiesArray = jarJSONObj.get("attributes").isArray();
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

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public List<Jar> getPayload()
   {
      return jarList;
   }

}
