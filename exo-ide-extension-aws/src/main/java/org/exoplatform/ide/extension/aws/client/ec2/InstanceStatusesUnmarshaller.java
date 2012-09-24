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
package org.exoplatform.ide.extension.aws.client.ec2;


/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: InstanceStatusesUnmarshaller.java Sep 21, 2012 6:05:42 PM azatsarynnyy $
 *
 */
public class InstanceStatusesUnmarshaller //implements Unmarshallable<List<InstanceStatusInfo>>
{

//   private List<InstanceStatusInfo> instanceStatusesList;
//
//   public InstanceStatusesUnmarshaller(List<InstanceStatusInfo> instanceStatusesList)
//   {
//      this.instanceStatusesList = instanceStatusesList;
//   }
//
//   /**
//    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
//    */
//   @Override
//   public void unmarshal(Response response) throws UnmarshallerException
//   {
//      try
//      {
//         if (response.getText() == null || response.getText().isEmpty())
//         {
//            return;
//         }
//         JSONArray array = JSONParser.parseLenient(response.getText()).isArray();
//         if (array == null)
//         {
//            return;
//         }
//         for (int i = 0; i < array.size(); i++)
//         {
//            JSONObject jsonObject = array.get(i).isObject();
//            String value = (jsonObject.isObject() != null) ? jsonObject.isObject().toString() : "";
//            AutoBean<InstanceStatusInfo> autoBean =
//               AutoBeanCodex.decode(AWSExtension.AUTO_BEAN_FACTORY, InstanceStatusInfo.class, value);
//            instanceStatusesList.add(autoBean.as());
//         }
//      }
//      catch (Exception e)
//      {
//         throw new UnmarshallerException("Can't parse security group list." + e.getMessage());
//      }
//   }
//
//   /**
//    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
//    */
//   @Override
//   public List<InstanceStatusInfo> getPayload()
//   {
//      return instanceStatusesList;
//   }

}
