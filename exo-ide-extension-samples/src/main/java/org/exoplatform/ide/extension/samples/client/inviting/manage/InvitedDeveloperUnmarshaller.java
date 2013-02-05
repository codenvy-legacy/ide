/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.inviting.manage;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InvitedDeveloperUnmarshaller implements Unmarshallable<List<Invite>>
{
   private List<Invite> invites;

   public InvitedDeveloperUnmarshaller(List<Invite> invites)
   {
      this.invites = invites;
   }

   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         if (response.getText() == null || response.getText().isEmpty())
         {
            return;
         }

         JSONArray array = JSONParser.parseStrict(response.getText()).isArray();

         if (array == null)
         {
            return;
         }

         for (int i = 0; i < array.size(); i++)
         {
            JSONObject developer = array.get(i).isObject();
            String email = developer.get("email").isString().stringValue();
            String uuid = developer.get("uuid").isString().stringValue();
            Boolean inviteActivated = developer.get("activated").isBoolean().booleanValue();
            Boolean valid = developer.get("valid").isBoolean().booleanValue();

            invites.add(new InvitedDeveloper(email, uuid, inviteActivated, valid));
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse invites information.");
      }
   }

   @Override
   public List<Invite> getPayload()
   {
      return invites;
   }
}
