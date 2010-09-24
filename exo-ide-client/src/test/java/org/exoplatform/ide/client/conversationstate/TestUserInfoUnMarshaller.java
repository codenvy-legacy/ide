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
package org.exoplatform.ide.client.conversationstate;

import org.exoplatform.ide.client.model.conversation.UserInfo;
import org.exoplatform.ide.client.model.conversation.marshal.UserInfoUnmarshaller;
import org.exoplatform.ide.testframework.http.MockResponse;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestUserInfoUnMarshaller extends GWTTestCase
{

   private static String JSON =
      "{\"roles\":[\"users\",\"administrators\"],\"userId\":\"root\",\"groups\":[\"/ide/administrators\",\"/ide/users\"]}";

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.IDEGwtTest";
   }

   public void testUserInfoUnmarshall()
   {
      UserInfo userInfo = new UserInfo();

      UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller(userInfo);
      unmarshaller.unmarshal(new MockResponse(JSON));
      assertEquals("root", userInfo.getName());
      assertTrue(userInfo.getGroups().contains("/ide/administrators"));
      assertTrue(userInfo.getGroups().contains("/ide/users"));
      assertEquals(2, userInfo.getGroups().size());
      assertTrue(userInfo.getRoles().contains("administrators"));
      assertTrue(userInfo.getRoles().contains("users"));
      assertEquals(2, userInfo.getRoles().size());
   }

}
