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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.ide.commons.exception.UnmarshallerException;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;
import org.exoplatform.ide.rest.Unmarshallable;

/**
 * Unmarshaller for CloudFoundry services.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 2:07:24 PM anya $
 * 
 */
public class CloudFoundryServicesUnmarshaller implements Unmarshallable<CloudFoundryServices>
{
   /**
    * CloudFoundry services (system and provisioned).
    */
   private CloudFoundryServices cloudfoundryServices;

   private final class Keys
   {
      public static final String SYSTEM = "system";

      public static final String PROVISIONED = "provisioned";
   }

   public CloudFoundryServicesUnmarshaller()
   {
      // TODO
      //      cloudfoundryServices = CloudFoundryExtension.AUTO_BEAN_FACTORY.services().as();
   }

   /**
    * {@inheritDoc}
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
            SystemService[] services = new SystemService[systemServices.size()];
            for (int i = 0; i < systemServices.size(); i++)
            {
               String value = systemServices.get(i).isObject().toString();
               services[i] =
                  AutoBeanCodex.decode(CloudFoundryExtension.AUTO_BEAN_FACTORY, SystemService.class, value).as();
            }
            cloudfoundryServices.setSystem(services);
         }
         else
         {
            cloudfoundryServices.setSystem(new SystemService[0]);
         }
      }

      if (jsonObject.containsKey(Keys.PROVISIONED))
      {
         JSONArray provisionedServices = jsonObject.get(Keys.PROVISIONED).isArray();
         if (provisionedServices.size() > 0)
         {
            ProvisionedService[] services = new ProvisionedService[provisionedServices.size()];
            for (int i = 0; i < provisionedServices.size(); i++)
            {
               String value = provisionedServices.get(i).isObject().toString();
               services[i] =
                  AutoBeanCodex.decode(CloudFoundryExtension.AUTO_BEAN_FACTORY, ProvisionedService.class, value).as();
            }
            cloudfoundryServices.setProvisioned(services);
         }
         else
         {
            cloudfoundryServices.setProvisioned(new ProvisionedService[0]);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CloudFoundryServices getPayload()
   {
      return cloudfoundryServices;
   }
}
