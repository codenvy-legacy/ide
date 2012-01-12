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
import org.exoplatform.ide.git.client.marshaller.FetchRequestMarshaller;
import org.exoplatform.ide.git.shared.FetchRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 28, 2011 9:55:30 AM anya $
 *
 */
public class FetchMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test fetch from remote repository request marshaller.
    */
   public void testFetchRequestMarshaller()
   {
      String remote = "origin";
      String refsSpec1 = "refs/heads/featured:refs/remotes/origin/featured";
      String refsSpec2 = "refs/heads/test:refs/remotes/origin/test";

      FetchRequest fetchRequest = new FetchRequest(new String[]{refsSpec1, refsSpec2}, remote, true, 0);
      FetchRequestMarshaller marshaller = new FetchRequestMarshaller(fetchRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.REF_SPEC));
      assertEquals(2, jsonObject.get(Constants.REF_SPEC).isArray().size());
      assertEquals(refsSpec1, jsonObject.get(Constants.REF_SPEC).isArray().get(0).isString().stringValue());
      assertEquals(refsSpec2, jsonObject.get(Constants.REF_SPEC).isArray().get(1).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.REMOTE));
      assertEquals(remote, jsonObject.get(Constants.REMOTE).isString().stringValue());

      assertTrue(jsonObject.containsKey(Constants.REMOVE_DELETED_REFS));
      assertTrue(jsonObject.get(Constants.REMOVE_DELETED_REFS).isBoolean().booleanValue());
   }
}
