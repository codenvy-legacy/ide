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
package org.exoplatform.ide.client.model.conversation.marshal;

import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UserInfoUnmarshaller implements Unmarshallable
{

   public static String USER_ID = "userId";

   public static String ROLES = "roles";

   public static String GROUPS = "groups";

   private UserInfo userInfo;

   public UserInfoUnmarshaller(UserInfo userInfo)
   {
      this.userInfo = userInfo;
   }

   public void unmarshal(Response response)
   {
      JSONObject json = JSONParser.parse(response.getText()).isObject();
      if (json.containsKey(USER_ID))
      {
         userInfo.setName(json.get(USER_ID).isString().stringValue());
      }
      if (json.containsKey(ROLES))
      {
         JSONArray jsonRoles = json.get(ROLES).isArray();
         for (int i = 0; i < jsonRoles.size(); i++)
         {
            userInfo.getRoles().add(jsonRoles.get(i).isString().stringValue());
         }
      }
      if (json.containsKey(GROUPS))
      {
         JSONArray jsonGroups = json.get(GROUPS).isArray();
         for (int i = 0; i < jsonGroups.size(); i++)
         {
            userInfo.getGroups().add(jsonGroups.get(i).isString().stringValue());
         }
      }
   }

}
