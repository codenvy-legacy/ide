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

import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.client.marshaller.PushRequestMarshaller;
import org.exoplatform.ide.git.shared.PushRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class PushMarshallerGwtTest extends BaseGwtTest {
    /** Test push to remote repository request marshaller. */
    public void testPushRequestMarshaller() {
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
