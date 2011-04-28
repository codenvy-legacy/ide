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

import org.exoplatform.ide.git.client.marshaller.CommitRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.shared.CommitRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 28, 2011 9:55:30 AM anya $
 *
 */
public class CommitMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test add (modified and deleted) and commit request marshaller.
    */
   public void testCommitAllRequestMarshaller()
   {
      String message = "my test commit";

      CommitRequest commitRequest = new CommitRequest(message, true);
      CommitRequestMarshaller marshaller = new CommitRequestMarshaller(commitRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.MESSAGE));
      assertEquals(message, jsonObject.get(Constants.MESSAGE).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.ALL));
      assertTrue(jsonObject.get(Constants.ALL).isBoolean().booleanValue());
   }
   
   /**
    * Test commit request marshaller.
    */
   public void testCommitRequestMarshaller()
   {
      String message = "my test commit";

      CommitRequest commitRequest = new CommitRequest();
      commitRequest.setMessage(message);
      
      CommitRequestMarshaller marshaller = new CommitRequestMarshaller(commitRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.MESSAGE));
      assertEquals(message, jsonObject.get(Constants.MESSAGE).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.ALL));
      assertFalse(jsonObject.get(Constants.ALL).isBoolean().booleanValue());
   }
}
