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
import org.exoplatform.ide.client.model.conversation.marshal.UserInfoParser;

import com.google.gwt.junit.client.GWTTestCase;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestUserInfoParser extends GWTTestCase
{
   
   private static String JSON = "{\"roles\":[\"users\",\"administrators\"],\"userId\":\"root\",\"groups\":[\"/platform/administrators\",\"/platform/users\"]}";


   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.IDEGwtTest";
   }
   
   
   public void testUserInfoParser()
   {
     UserInfo user = UserInfoParser.parse(JSON);
     assertEquals("root", user.getName());
     assertTrue(user.getGroups().contains("/platform/administrators"));
     assertTrue(user.getGroups().contains("/platform/users"));
     assertEquals(2, user.getGroups().size());
     assertTrue(user.getRoles().contains("administrators"));
     assertTrue(user.getRoles().contains("users"));
     assertEquals(2, user.getRoles().size());
   }
   
}
