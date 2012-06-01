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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 29, 2012 4:55:24 PM anya $
 * 
 */
public class BackendsUnmarshaller implements Unmarshallable<List<Backend>>
{

   private List<Backend> backends;

   public BackendsUnmarshaller(List<Backend> backends)
   {
      this.backends = backends;
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

         JSONArray array = JSONParser.parseLenient(response.getText()).isArray();

         if (array == null)
         {
            return;
         }

         for (int i = 0; i < array.size(); i++)
         {
            JSONObject jsonObject = array.get(i).isObject();
            String value = (jsonObject.isObject() != null) ? jsonObject.isObject().toString() : "";

            AutoBean<Backend> backendBean =
               AutoBeanCodex.decode(GoogleAppEngineExtension.AUTO_BEAN_FACTORY, Backend.class, value);
            backends.add(backendBean.as());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new UnmarshallerException("Can't parse backends information.");
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload()
    */
   @Override
   public List<Backend> getPayload()
   {
      return backends;
   }

}
