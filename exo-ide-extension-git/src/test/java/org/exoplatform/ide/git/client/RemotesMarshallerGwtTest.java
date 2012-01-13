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
package org.exoplatform.ide.git.client;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.client.marshaller.RemoteAddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListRequestMarshaller;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.git.shared.RemoteListRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 * 
 */
public class RemotesMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test add remote repository request marshaller.
    */
   public void testRemoteAddRequestMarshaller()
   {
      String name = "remote1";
      String url = "remote/repository/location";

      RemoteAddRequest remoteAddRequest = new RemoteAddRequest(name, url);
      RemoteAddRequestMarshaller marshaller = new RemoteAddRequestMarshaller(remoteAddRequest);

      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.NAME));
      assertEquals(name, jsonObject.get(Constants.NAME).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.URL));
      assertEquals(url, jsonObject.get(Constants.URL).isString().stringValue());
   }

   /**
    * Test get the list of remote repositories.
    */
   public void testRemoteListRequestMarshaller()
   {
      RemoteListRequest remoteListRequest = new RemoteListRequest();
      remoteListRequest.setVerbose(true);
      RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));

      assertTrue(jsonObject.containsKey(Constants.REMOTE));
      assertNotNull(jsonObject.get(Constants.REMOTE).isNull());

      assertTrue(jsonObject.containsKey(Constants.VERBOSE));
      assertTrue(jsonObject.get(Constants.VERBOSE).isBoolean().booleanValue());
   }

   /**
    * Test get the info of pointed remote repository.
    */
   public void testRemoteRequestMarshaller()
   {
      String remote = "remote1";

      RemoteListRequest remoteListRequest = new RemoteListRequest(remote, true);
      RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));

      assertTrue(jsonObject.containsKey(Constants.REMOTE));
      assertEquals(remote, jsonObject.get(Constants.REMOTE).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.VERBOSE));
      assertTrue(jsonObject.get(Constants.VERBOSE).isBoolean().booleanValue());
   }
}
