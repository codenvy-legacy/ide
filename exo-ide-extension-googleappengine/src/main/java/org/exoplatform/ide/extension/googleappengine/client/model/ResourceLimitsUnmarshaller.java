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
package org.exoplatform.ide.extension.googleappengine.client.model;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 31, 2012 4:32:27 PM anya $
 * 
 */
public class ResourceLimitsUnmarshaller implements Unmarshallable<List<ResourceLimit>>
{
   private List<ResourceLimit> resourceLimits;

   public ResourceLimitsUnmarshaller(List<ResourceLimit> resourceLimits)
   {
      this.resourceLimits = resourceLimits;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         if (response.getText() == null || response.getText().isEmpty())
         {
            return;
         }

         JSONObject json = JSONParser.parseLenient(response.getText()).isObject();

         if (json == null)
         {
            return;
         }

         Iterator<String> keysIterator = json.keySet().iterator();
         while (keysIterator.hasNext())
         {
            String key = keysIterator.next();
            Long value = (long)json.get(key).isNumber().doubleValue();
            resourceLimits.add(new ResourceLimit(key, value));
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't map with long values.");
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload()
    */
   @Override
   public List<ResourceLimit> getPayload()
   {
      return resourceLimits;
   }
}
