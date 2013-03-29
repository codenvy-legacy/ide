/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;


/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class PropertyUnmarshaller implements Unmarshallable<JsonArray<Property>>
{
   protected JsonArray<Property> properties = JsonCollections.createArray();

   public PropertyUnmarshaller()
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONObject itemObject = JSONParser.parseLenient(response.getText()).isObject();
         JsonArray<Property> properties = JSONDeserializer.PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
         this.properties.addAll(properties);
      }
      catch (Exception exc)
      {
         String message = "Can't parse item's properties " + response.getText();
         throw new UnmarshallerException(message, exc);
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JsonArray<Property> getPayload()
   {
      return this.properties;
   }
}