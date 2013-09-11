/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.git.client;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.git.client.marshaller.*;
import org.exoplatform.ide.git.shared.BranchCheckoutRequest;
import org.exoplatform.ide.git.shared.BranchCreateRequest;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;
import org.exoplatform.ide.git.shared.BranchListRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 3:32:28 PM anya $
 */
public class BranchesMarshallerGwtTest extends BaseGwtTest {
    /** Test branch checkout request marshaller. */
    public void testBranchCheckoutRequestMarshaller() {
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

    /** Test branch checkout request and create new (if doesn't exist) marshaller. */
    public void testBranchCheckoutCreateNewRequestMarshaller() {
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

    /** Test branch delete request marshaller. */
    public void testBranchDeleteRequestMarshaller() {
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

    /** Test get list of local branches request marshaller. */
    public void testBranchListRequestMarshaller() {
        BranchListRequest branchListRequest = new BranchListRequest();
        BranchListRequestMarshaller marshaller = new BranchListRequestMarshaller(branchListRequest);
        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));
        assertTrue(jsonObject.containsKey(Constants.LIST_MODE));
        assertNotNull(jsonObject.get(Constants.LIST_MODE).isNull());
    }

    /** Test get list of remote branches request marshaller. */
    public void testBranchRemoteListRequestMarshaller() {
        BranchListRequest branchListRequest = new BranchListRequest(BranchListRequest.LIST_REMOTE);
        BranchListRequestMarshaller marshaller = new BranchListRequestMarshaller(branchListRequest);
        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));
        assertTrue(jsonObject.containsKey(Constants.LIST_MODE));
        assertEquals(BranchListRequest.LIST_REMOTE, jsonObject.get(Constants.LIST_MODE).isString().stringValue());
    }

    /** Test create new branch request marshaller. */
    public void testBranchCreateRequestMarshaller() {
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
