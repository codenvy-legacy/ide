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
import org.exoplatform.ide.git.client.marshaller.DiffRequestMarshaller;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 5, 2011 5:24:39 PM anya $
 *
 */
public class DiffMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test the project's diff request marshaller.
    */
   public void testProjectsDiffRequestMarshaller()
   {
      DiffRequest diffRequest = new DiffRequest(null, DiffType.NAME_STATUS, false, 0);
      DiffRequestMarshaller marshaller = new DiffRequestMarshaller(diffRequest);

      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertFalse(jsonObject.containsKey(Constants.FILE_FILTER));
      assertTrue(jsonObject.containsKey(Constants.TYPE));
      assertEquals(DiffType.NAME_STATUS.name(), jsonObject.get(Constants.TYPE).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.NO_RENAMES));
      assertFalse(jsonObject.get(Constants.NO_RENAMES).isBoolean().booleanValue());
   }

   /**
    * Test the resource's diff request marshaller.
    */
   public void testResourceDiffRequestMarshaller()
   {
      String test = "testFile.xml";

      DiffRequest diffRequest = new DiffRequest(new String[]{test}, DiffType.RAW, true, 0);
      DiffRequestMarshaller marshaller = new DiffRequestMarshaller(diffRequest);

      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.FILE_FILTER));
      assertNotNull(jsonObject.get(Constants.FILE_FILTER).isArray());
      assertEquals(1, jsonObject.get(Constants.FILE_FILTER).isArray().size());
      assertEquals(test, jsonObject.get(Constants.FILE_FILTER).isArray().get(0).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.TYPE));
      assertEquals(DiffType.RAW.name(), jsonObject.get(Constants.TYPE).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.NO_RENAMES));
      assertTrue(jsonObject.get(Constants.NO_RENAMES).isBoolean().booleanValue());
   }
}
