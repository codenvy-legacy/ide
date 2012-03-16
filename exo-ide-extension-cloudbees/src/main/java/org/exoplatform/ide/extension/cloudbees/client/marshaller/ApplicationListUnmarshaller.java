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
package org.exoplatform.ide.extension.cloudbees.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2011 evgen $
 * 
 */
public class ApplicationListUnmarshaller implements Unmarshallable<List<ApplicationInfo>>
{

   private List<ApplicationInfo> apps;

   /**
    * @param apps
    */
   public ApplicationListUnmarshaller(List<ApplicationInfo> apps)
   {
      this.apps = apps;
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
      if (jsonObject == null)
      {
         return;
      }

      for (String key : jsonObject.keySet())
      {
         String value = (jsonObject.get(key).isString() != null) ? jsonObject.get(key).isString().stringValue() : "";

         AutoBean<ApplicationInfo> appInfoBean =
            AutoBeanCodex.decode(CloudBeesExtension.AUTO_BEAN_FACTORY, ApplicationInfo.class, value);
         apps.add(appInfoBean.as());
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public List<ApplicationInfo> getPayload()
   {
      return apps;
   }
}
