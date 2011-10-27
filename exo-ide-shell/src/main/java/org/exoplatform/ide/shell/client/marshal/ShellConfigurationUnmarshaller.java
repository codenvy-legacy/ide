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
package org.exoplatform.ide.shell.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.shell.client.model.ShellConfiguration;
import org.exoplatform.ide.shell.client.model.UserInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 12, 2011 evgen $
 *
 */
public class ShellConfigurationUnmarshaller implements Unmarshallable<ShellConfiguration>
{

   private static final String USER = "user";

   private static final String DEFAUTL_ENTRY_POINT = "defaultEntrypoint";

   public static String USER_ID = "userId";

   public static String ROLES = "roles";

   public static String GROUPS = "groups";

   private ShellConfiguration configuration;

   /**
    * @param configuration
    */
   public ShellConfigurationUnmarshaller(ShellConfiguration configuration)
   {
      super();
      this.configuration = configuration;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JSONValue value = JSONParser.parseStrict(response.getText());
      if (value.isObject() != null)
      {
         JSONObject object = value.isObject();
         if (object.containsKey(USER))
         {
            UserInfo userInfo = new UserInfo();
            configuration.setUserInfo(userInfo);
            parseUserInfo(object.get(USER).isObject());
         }
         if (object.containsKey(DEFAUTL_ENTRY_POINT))
         {
            configuration.setEntryPoint(object.get(DEFAUTL_ENTRY_POINT).isString().stringValue());
         }
      }
   }

   public void parseUserInfo(JSONObject json)
   {
      UserInfo userInfo = new UserInfo();
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

      configuration.setUserInfo(userInfo);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public ShellConfiguration getPayload()
   {
      return configuration;
   }

}
