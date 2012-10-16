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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.beanstalk.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: ConfigurationListUnmarshaller.java Oct 8, 2012 10:22:32 AM azatsarynnyy $
 *
 */
public class ConfigurationListUnmarshaller implements Unmarshallable<List<Configuration>>
{
   private List<Configuration> configurationList = new ArrayList<Configuration>();

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

      JSONArray value = JSONParser.parseLenient(response.getText()).isArray();

      if (value == null)
      {
         return;
      }

      for (int i = 0; i < value.size(); i++)
      {
         String payload = value.get(i).isObject().toString();

         AutoBean<Configuration> configurationBean =
            AutoBeanCodex.decode(AWSExtension.AUTO_BEAN_FACTORY, Configuration.class, payload);
         configurationList.add(configurationBean.as());
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload()
    */
   @Override
   public List<Configuration> getPayload()
   {
      return configurationList;
   }
}
