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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import com.google.gwt.json.client.JSONParser;

import com.google.gwt.json.client.JSONObject;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;

/**
 * Unmarshaller for system information response on JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 18, 2011 10:45:14 AM anya $
 *
 */
public class SystemInfoUnmarshaller implements Unmarshallable, Constants
{
   /**
    * System information.
    */
   private SystemInfo systemInfo;

   /**
    * @param systemInfo system information
      
    */
   public SystemInfoUnmarshaller(SystemInfo systemInfo)
   {
      this.systemInfo = systemInfo;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();
         if (jsonObject == null)
            return;
         systemInfo.setDescription(jsonObject.get(DESCRIPTION).isString().stringValue());
         systemInfo.setName(jsonObject.get(NAME).isString().stringValue());
         systemInfo.setSupport(jsonObject.get(SUPPORT).isString().stringValue());
         systemInfo.setUser(jsonObject.get(USER).isString().stringValue());
         systemInfo.setVersion(jsonObject.get(VERSION).isString().stringValue());
         systemInfo.setLimits(parseSystemResources(jsonObject.get(LIMITS).isObject()));
         systemInfo.setUsage(parseSystemResources(jsonObject.get(USAGE).isObject()));
      }
      catch (Exception e)
      {
         throw new UnmarshallerException(CloudFoundryExtension.LOCALIZATION_CONSTANT.systemInfoUnmarshallerError());
      }
   }

   /**
    * Parse JSON object to {@link SystemResources} bean.
    * 
    * @param jsonObject 
    * @return {@link SystemResources} system resources
    */
   protected SystemResources parseSystemResources(JSONObject jsonObject)
   {
      SystemResources systemResources = new SystemResources();
      systemResources.setApps((int)jsonObject.get(APPS).isNumber().doubleValue());
      systemResources.setMemory((int)jsonObject.get(MEMORY).isNumber().doubleValue());
      systemResources.setServices((int)jsonObject.get(SERVICES).isNumber().doubleValue());
      return systemResources;
   }

}
