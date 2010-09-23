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
import org.exoplatform.ide.client.model.conversation.marshal.UserInfoUnmarshaller;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;
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
      UserInfo userInfo = new UserInfo();
      
      UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller(userInfo);
      unmarshaller.unmarshal(new MockResponse());
      
     //UserInfo user = UserInfoUnmarshaller UserInfoParser.parse(JSON);
     assertEquals("root", userInfo.getName());
     assertTrue(userInfo.getGroups().contains("/platform/administrators"));
     assertTrue(userInfo.getGroups().contains("/platform/users"));
     assertEquals(2, userInfo.getGroups().size());
     assertTrue(userInfo.getRoles().contains("administrators"));
     assertTrue(userInfo.getRoles().contains("users"));
     assertEquals(2, userInfo.getRoles().size());
   }
   
   private class MockResponse extends Response
   {

      /**
       * @see com.google.gwt.http.client.Response#getHeader(java.lang.String)
       */
      @Override
      public String getHeader(String header)
      {
         // TODO Auto-generated method stub
         return null;
      }

      /**
       * @see com.google.gwt.http.client.Response#getHeaders()
       */
      @Override
      public Header[] getHeaders()
      {
         // TODO Auto-generated method stub
         return null;
      }

      /**
       * @see com.google.gwt.http.client.Response#getHeadersAsString()
       */
      @Override
      public String getHeadersAsString()
      {
         // TODO Auto-generated method stub
         return null;
      }

      /**
       * @see com.google.gwt.http.client.Response#getStatusCode()
       */
      @Override
      public int getStatusCode()
      {
         // TODO Auto-generated method stub
         return 0;
      }

      /**
       * @see com.google.gwt.http.client.Response#getStatusText()
       */
      @Override
      public String getStatusText()
      {
         // TODO Auto-generated method stub
         return null;
      }

      /**
       * @see com.google.gwt.http.client.Response#getText()
       */
      @Override
      public String getText()
      {
         return JSON;
      }
      
   }   
   
}
