/*
pal * Copyright (C) 2011 eXo Platform SAS.
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
import com.codenvy.ide.rest.Unmarshallable;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;


/**
 * 
 */
public class ChildNamesUnmarshaller implements Unmarshallable<JsonArray<String>>
{
   private final JsonArray<String> items;

   /**
    * @param items
    */
   public ChildNamesUnmarshaller()
   {
      super();
      this.items = JsonCollections.createArray();
      this.items.clear();
   }

   /**
    * @see com.codenvy.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONValue jsonValue = JSONParser.parseLenient(response.getText());
         parseItems(jsonValue.isObject().get("items").isArray());
      }
      catch (Exception exc)
      {
         String message = "Can't parse folder content at <b>" + "id" + "</b>! ";
         throw new UnmarshallerException(message, exc);
      }
   }

   @Override
   public JsonArray<String> getPayload()
   {
      return this.items;
   }

   /**
    * Parse JSON Array as the list of names
    * 
    * @param itemsArray JSON array
    * @return list of children items
    */
   private void parseItems(JSONArray itemsArray)
   {
      for (int i = 0; i < itemsArray.size(); i++)
      {
         // get Json Object
         JSONObject object = itemsArray.get(i).isObject();
         // get name
         String name = object.get("name").isString().stringValue();
         items.add(name);
      }
   }

}
