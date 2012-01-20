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
package org.exoplatform.ide.extension.openshift.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Unmarshaller for getting user information from response in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 7, 2011 11:54:18 AM anya $
 * 
 */
public class RHUserInfoUnmarshaller implements Unmarshallable<RHUserInfo>, Constants
{
   /**
    * User information.
    */
   private RHUserInfo userInfo;

   /**
    * @param userInfo user's information
    */
   public RHUserInfoUnmarshaller(RHUserInfo userInfo)
   {
      this.userInfo = userInfo;
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
         return;

      for (String key : jsonObject.keySet())
      {
         String value = (jsonObject.get(key).isString() != null) ? jsonObject.get(key).isString().stringValue() : "";
         if (LOGIN.equals(key))
         {
            userInfo.setRhlogin(value);
         }
         else if (DOMAIN.equals(key))
         {
            userInfo.setRhcDomain(value);
         }
         else if (NAMESPACE.equals(key))
         {
            userInfo.setNamespace(value);
         }
         else if (UUID.equals(key))
         {
            userInfo.setUuid(value);
         }
         else if (APPS.equals(key))
         {
            userInfo.setApps(getApplications(jsonObject.get(key).isArray()));
         }
      }
   }

   /**
    * Get the list of applications from JSON array.
    * 
    * @param array JSON array
    * @return {@link List}
    */
   protected List<AppInfo> getApplications(JSONArray array)
   {
      List<AppInfo> apps = new ArrayList<AppInfo>();
      if (array == null || array.size() == 0)
         return apps;
      for (int i = 0; i < array.size(); i++)
      {
         JSONObject jsonApp = array.get(i).isObject();
         AppInfo appInfo = new AppInfo();
         for (String key : jsonApp.keySet())
         {
            String value = (jsonApp.get(key).isString() != null) ? jsonApp.get(key).isString().stringValue() : "";
            if (NAME.equals(key))
            {
               appInfo.setName(value);
            }
            else if (GIT_URL.equals(key))
            {
               appInfo.setGitUrl(value);
            }
            else if (PUBLIC_URL.equals(key))
            {
               appInfo.setPublicUrl(value);
            }
            else if (TYPE.equals(key))
            {
               appInfo.setType(value);
            }
            else if (CREATION_DATE.equals(key))
            {
               long date =
                  (long)((jsonApp.get(key).isNumber() != null) ? jsonApp.get(key).isNumber().doubleValue() : 0);
               appInfo.setCreationTime(date);
            }
         }
         apps.add(appInfo);
      }
      return apps;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public RHUserInfo getPayload()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
