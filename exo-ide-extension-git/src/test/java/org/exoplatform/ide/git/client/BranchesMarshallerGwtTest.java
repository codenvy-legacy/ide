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

import org.exoplatform.ide.git.client.marshaller.BranchCheckoutRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchCreateRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchDeleteRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchListRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.BranchCreateRequest;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;
import org.exoplatform.ide.git.shared.BranchListRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 3:32:28 PM anya $
 * 
 */
public class BranchesMarshallerGwtTest extends BaseGwtTest
{
   /**
    * Test branch checkout request marshaller.
    */
   public void testBranchCheckoutRequestMarshaller()
   {
      String branch = "test1";

      BranchCheckoutRequest branchCheckoutRequest = new BranchCheckoutRequest();
      branchCheckoutRequest.setName(branch);
      BranchCheckoutRequestMarshaller marshaller = new BranchCheckoutRequestMarshaller(branchCheckoutRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.NAME));
      assertEquals(branch, jsonObject.get(Constants.NAME).isString().stringValue());
      assertTrue(jsonObject.containsKey(Constants.CREATE_NEW));
      assertFalse(jsonObject.get(Constants.CREATE_NEW).isBoolean().booleanValue());
      assertFalse(jsonObject.containsKey(Constants.START_POINT));
   }

   /**
    * Test branch checkout request and create new (if doesn't exist) marshaller.
    */
   public void testBranchCheckoutCreateNewRequestMarshaller()
   {
      String branch = "test2";

      BranchCheckoutRequest branchCheckoutRequest = new BranchCheckoutRequest(branch, null, true);
      BranchCheckoutRequestMarshaller marshaller = new BranchCheckoutRequestMarshaller(branchCheckoutRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.NAME));
      assertEquals(branch, jsonObject.get(Constants.NAME).isString().stringValue());
      assertTrue(jsonObject.containsKey(Constants.CREATE_NEW));
      assertTrue(jsonObject.get(Constants.CREATE_NEW).isBoolean().booleanValue());
      assertFalse(jsonObject.containsKey(Constants.START_POINT));
   }

   /**
    * Test branch delete request marshaller.
    */
   public void testBranchDeleteRequestMarshaller()
   {
      String branch = "branchToDelete";

      BranchDeleteRequest branchDeleteRequest = new BranchDeleteRequest(branch, true);
      BranchDeleteRequestMarshaller marshaller = new BranchDeleteRequestMarshaller(branchDeleteRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.NAME));
      assertEquals(branch, jsonObject.get(Constants.NAME).isString().stringValue());
      assertTrue(jsonObject.containsKey(Constants.FORCE));
      assertTrue(jsonObject.get(Constants.FORCE).isBoolean().booleanValue());
   }

   /**
    * Test get list of local branches request marshaller.
    */
   public void testBranchListRequestMarshaller()
   {
      BranchListRequest branchListRequest = new BranchListRequest();
      BranchListRequestMarshaller marshaller = new BranchListRequestMarshaller(branchListRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.LIST_MODE));
      assertNotNull(jsonObject.get(Constants.LIST_MODE).isNull());
   }

   /**
    * Test get list of remote branches request marshaller.
    */
   public void testBranchRemoteListRequestMarshaller()
   {
      BranchListRequest branchListRequest = new BranchListRequest(BranchListRequest.LIST_REMOTE);
      BranchListRequestMarshaller marshaller = new BranchListRequestMarshaller(branchListRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.LIST_MODE));
      assertEquals(BranchListRequest.LIST_REMOTE, jsonObject.get(Constants.LIST_MODE).isString().stringValue());
   }

   /**
    * Test create new branch request marshaller.
    */
   public void testBranchCreateRequestMarshaller()
   {
      String branch = "newBranch";

      BranchCreateRequest branchCreateRequest = new BranchCreateRequest();
      branchCreateRequest.setName(branch);

      BranchCreateRequestMarshaller marshaller = new BranchCreateRequestMarshaller(branchCreateRequest);
      String json = marshaller.marshal();

      assertNotNull(json);

      JSONObject jsonObject = new JSONObject(build(json));
      assertTrue(jsonObject.containsKey(Constants.NAME));
      assertEquals(branch, jsonObject.get(Constants.NAME).isString().stringValue());
   }
}
