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
import org.exoplatform.ide.git.client.marshaller.FetchRequestMarshaller;
import org.exoplatform.ide.git.shared.FetchRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class FetchMarshallerGwtTest extends BaseGwtTest {
    /** Test fetch from remote repository request marshaller. */
    public void testFetchRequestMarshaller() {
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
