/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.model.conversation.marshal;

import org.exoplatform.ide.client.model.conversation.UserInfo;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class UserInfoParser
{
   
   public static String USER_ID = "userId";
   
   public static String ROLES = "roles";
   
   public static String GROUPS = "groups";
   
   
   
   public static UserInfo parse(String jsonString)
   {
      UserInfo user =  new UserInfo();
      JSONObject json = JSONParser.parse(jsonString).isObject();
      if (json.containsKey(USER_ID)) {
         user.setName(json.get(USER_ID).isString().stringValue());
      }
      if (json.containsKey(ROLES)) {
         JSONArray jsonRoles = json.get(ROLES).isArray();
         for (int i = 0; i < jsonRoles.size(); i++)
         {
            user.getRoles().add(jsonRoles.get(i).isString().stringValue());
         }
      }
      if (json.containsKey(GROUPS)) {
         JSONArray jsonGroups = json.get(GROUPS).isArray();
         for (int i = 0; i < jsonGroups.size(); i++)
         {
            user.getGroups().add(jsonGroups.get(i).isString().stringValue());
         }
      }
      return user;
   }
   
}
