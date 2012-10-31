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
package org.exoplatform.ide.extension.appfog.client.services;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;
import org.exoplatform.ide.extension.appfog.shared.AppfogServices;
import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;

/**
 * Unmarshaller for Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogServicesUnmarshaller implements Unmarshallable<AppfogServices>
{
   /**
    * Appfog services (system and provisioned).
    */
   private AppfogServices appfogServices;

   private final class Keys
   {
      public static final String SYSTEM = "appfogSystemService";

      public static final String PROVISIONED = "appfogProvisionedService";
   }

   public AppfogServicesUnmarshaller()
   {
      appfogServices = AppfogExtension.AUTO_BEAN_FACTORY.services().as();
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      if (response.getText() == null || response.getText().isEmpty())
      {
         return;
      }

      JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();

      if (jsonObject.containsKey(Keys.SYSTEM))
      {
         JSONArray systemServices = jsonObject.get(Keys.SYSTEM).isArray();
         if (systemServices.size() > 0)
         {
            AppfogSystemService[] services = new AppfogSystemService[systemServices.size()];
            for (int i = 0; i < systemServices.size(); i++)
            {
               String value = systemServices.get(i).isObject().toString();
               services[i] =
                  AutoBeanCodex.decode(AppfogExtension.AUTO_BEAN_FACTORY, AppfogSystemService.class, value).as();
            }
            appfogServices.setAppfogSystemService(services);
         }
         else
         {
            appfogServices.setAppfogSystemService(new AppfogSystemService[0]);
         }
      }

      if (jsonObject.containsKey(Keys.PROVISIONED))
      {
         JSONArray provisionedServices = jsonObject.get(Keys.PROVISIONED).isArray();
         if (provisionedServices.size() > 0)
         {
            AppfogProvisionedService[] services = new AppfogProvisionedService[provisionedServices.size()];
            for (int i = 0; i < provisionedServices.size(); i++)
            {
               String value = provisionedServices.get(i).isObject().toString();
               services[i] =
                  AutoBeanCodex.decode(AppfogExtension.AUTO_BEAN_FACTORY, AppfogProvisionedService.class, value).as();
            }
            appfogServices.setAppfogProvisionedService(services);
         }
         else
         {
            appfogServices.setAppfogProvisionedService(new AppfogProvisionedService[0]);
         }
      }

   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload()
    */
   @Override
   public AppfogServices getPayload()
   {
      return appfogServices;
   }
}
