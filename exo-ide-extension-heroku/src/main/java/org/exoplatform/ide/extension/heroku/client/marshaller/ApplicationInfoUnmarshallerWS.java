/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.heroku.client.marshaller;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage;
import org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable;

import java.util.List;

/**
 * Unmarshaller for application information from JSON format from {@link ResponseMessage}.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfoUnmarshallerWS.java Nov 29, 2012 12:43:13 PM azatsarynnyy $
 *
 */
public class ApplicationInfoUnmarshallerWS implements Unmarshallable<List<Property>>
{
   private List<Property> properties;

   /**
    * @param applicationInfo application's information
    */
   public ApplicationInfoUnmarshallerWS(List<Property> properties)
   {
      this.properties = properties;
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable#unmarshal(org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage)
    */
   @Override
   public void unmarshal(ResponseMessage response) throws UnmarshallerException
   {
      if (response.getBody() == null || response.getBody().isEmpty())
      {
         return;
      }

      JSONValue json = JSONParser.parseStrict(response.getBody());
      if (json == null)
         return;
      JSONObject jsonObject = json.isObject();
      if (jsonObject == null)
         return;

      for (String key : jsonObject.keySet())
      {
         if (jsonObject.get(key).isString() != null)
         {
            String value = jsonObject.get(key).isString().stringValue();
            properties.add(new Property(key, value));
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable#getPayload()
    */
   @Override
   public List<Property> getPayload()
   {
      return properties;
   }

}
