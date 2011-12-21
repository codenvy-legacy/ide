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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;

/**
 * Unmarshaller for application information response.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 7, 2011 11:08:05 AM anya $
 *
 */
public class AppInfoUmarshaller implements org.exoplatform.gwtframework.commons.rest.Unmarshallable, Constants
{
   /**
    * Application information.
    */
   private AppInfo appInfo;

   /**
    * @param appInfo application information
    */
   public AppInfoUmarshaller(AppInfo appInfo)
   {
      this.appInfo = appInfo;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();
      if (jsonObject == null)
         return;

      for (String key : jsonObject.keySet())
      {
         String value = (jsonObject.get(key).isString() != null) ? jsonObject.get(key).isString().stringValue() : "";
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
               (long)((jsonObject.get(key).isNumber() != null) ? jsonObject.get(key).isNumber().doubleValue() : 0);
            appInfo.setCreationTime(date);
         }
      }
   }
}
