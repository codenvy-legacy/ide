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
import org.exoplatform.ide.git.client.marshaller.PushRequestMarshaller;
import org.exoplatform.ide.git.shared.PushRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 28, 2011 9:55:30 AM anya $
 *
 */
public class PushMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test push to remote repository request marshaller.
    */
   public void testPushRequestMarshaller()
   {
      String refspec1 = "refs/heads/master:refs/heads/test";
      String refspec2 = "refs/heads/master:refs/heads/test";
      String remote = "origin";

      PushRequest pushRequest = new PushRequest(new String[]{refspec1, refspec2}, remote, true, 0);
      PushRequestMarshaller marshaller = new PushRequestMarshaller(pushRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.REMOTE));
      assertEquals(remote, jsonObject.get(Constants.REMOTE).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.FORCE));
      assertTrue(jsonObject.get(Constants.FORCE).isBoolean().booleanValue());

      assertTrue(jsonObject.containsKey(Constants.REF_SPEC));
      assertEquals(2, jsonObject.get(Constants.REF_SPEC).isArray().size());
      assertEquals(refspec1, jsonObject.get(Constants.REF_SPEC).isArray().get(0).isString().stringValue());
      assertEquals(refspec2, jsonObject.get(Constants.REF_SPEC).isArray().get(1).isString().stringValue());
   }
}
