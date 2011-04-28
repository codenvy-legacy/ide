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

import org.exoplatform.ide.git.client.marshaller.AddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.shared.AddRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 27, 2011 11:49:37 AM anya $
 *
 */
public class AddRequestMarshallerGwtTest extends BaseGwtTest
{

   /**
    * Test add request with default file pattern.
    */
   public void testAddAllRequestMarshaller()
   {
      AddRequest addRequest = new AddRequest();
      addRequest.setUpdate(true);
      String json = new AddRequestMarshaller(addRequest).marshal();
      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertNotNull(jsonObject);
      assertTrue(jsonObject.containsKey(Constants.UPDATE));
      assertTrue(jsonObject.get(Constants.UPDATE).isBoolean().booleanValue());

      assertTrue(jsonObject.containsKey(Constants.FILE_PATTERN));
      assertNotNull(jsonObject.get(Constants.FILE_PATTERN).isArray());
      assertEquals(1, jsonObject.get(Constants.FILE_PATTERN).isArray().size());
      assertEquals(".", jsonObject.get(Constants.FILE_PATTERN).isArray().get(0).isString().stringValue());
   }

   /**
    * Test add request with set file patterns.
    */
   public void testAddFilesRequestMarshaller()
   {
      String firstPattern = "text.txt";
      String secondPattern = "test/abs.txt";
      String[] filePatterns = new String[]{firstPattern, secondPattern};

      AddRequest addRequest = new AddRequest(filePatterns, false);
      String json = new AddRequestMarshaller(addRequest).marshal();
      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertNotNull(jsonObject);
      assertTrue(jsonObject.containsKey(Constants.UPDATE));
      assertFalse(jsonObject.get(Constants.UPDATE).isBoolean().booleanValue());

      assertTrue(jsonObject.containsKey(Constants.FILE_PATTERN));
      assertNotNull(jsonObject.get(Constants.FILE_PATTERN).isArray());
      assertEquals(2, jsonObject.get(Constants.FILE_PATTERN).isArray().size());
      assertEquals(firstPattern, jsonObject.get(Constants.FILE_PATTERN).isArray().get(0).isString().stringValue());
      assertEquals(secondPattern, jsonObject.get(Constants.FILE_PATTERN).isArray().get(1).isString().stringValue());
   }
}
