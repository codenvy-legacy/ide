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
package org.exoplatform.ide.client.model.discovery.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.discovery.RestService;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 21, 2010 5:24:56 PM evgen $
 *
 */
public class RestServicesUnmarshaller implements Unmarshallable<List<RestService>>
{

   /**
    * 
    */
   private static final String ROOT_RESOURCES = "rootResources";

   private static final String PARSE_REST_SERVICE_ERROR = IDE.ERRORS_CONSTANT.restServiceParseError();

   private List<RestService> restServices;

   /**
    * @param restServices
    */
   public RestServicesUnmarshaller(List<RestService> restServices)
   {
      this.restServices = restServices;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseRestServices(response.getText());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException(PARSE_REST_SERVICE_ERROR);
      }
   }

   /**
    * Parse JSON and fill restServices list.
    * @param text
    */
   private void parseRestServices(String text)
   {
      JSONObject jso = JSONParser.parseStrict(text).isObject();
      if (jso.containsKey(ROOT_RESOURCES))
      {
         JSONArray jsa = jso.get(ROOT_RESOURCES).isArray();
         for (int i = 0; i < jsa.size(); i++)
         {
            JSONObject service = jsa.get(i).isObject();
            String fqn = service.get("fqn").isString().stringValue();
            String path = service.get("path").isString().stringValue();
            if (!path.startsWith("/"))
            {
               path = "/" + path;
            }
            String regexp = service.get("regex").isString().stringValue();
            restServices.add(new RestService(fqn, path, regexp));
         }
      }

   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public List<RestService> getPayload()
   {
      return restServices;
   }

}
