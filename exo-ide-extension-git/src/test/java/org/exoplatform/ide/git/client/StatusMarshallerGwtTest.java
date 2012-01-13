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
import org.exoplatform.ide.git.client.marshaller.StatusRequestMarshaller;
import org.exoplatform.ide.git.shared.StatusRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 * 
 */
public class StatusMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test get work tree status request marshaller.
    */
   public void testStatusRequestMarshaller()
   {
      StatusRequest statusRequest = new StatusRequest(false);
      StatusRequestMarshaller marshaller = new StatusRequestMarshaller(statusRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));

      assertTrue(jsonObject.containsKey(Constants.SHORT_FORMAT));
      assertFalse(jsonObject.get(Constants.SHORT_FORMAT).isBoolean().booleanValue());

      assertFalse(jsonObject.containsKey(Constants.FILE_FILTER));
   }

   /**
    * Test get status of the pointed file request marshaller.
    */
   public void testFileStatusRequestMarshaller()
   {
      String file = "test/test.txt";

      StatusRequest statusRequest = new StatusRequest(new String[]{file}, true);
      StatusRequestMarshaller marshaller = new StatusRequestMarshaller(statusRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));

      assertTrue(jsonObject.containsKey(Constants.SHORT_FORMAT));
      assertTrue(jsonObject.get(Constants.SHORT_FORMAT).isBoolean().booleanValue());

      assertTrue(jsonObject.containsKey(Constants.FILE_FILTER));
      assertEquals(1, jsonObject.get(Constants.FILE_FILTER).isArray().size());
      assertEquals(file, jsonObject.get(Constants.FILE_FILTER).isArray().get(0).isString().stringValue());
   }
}
