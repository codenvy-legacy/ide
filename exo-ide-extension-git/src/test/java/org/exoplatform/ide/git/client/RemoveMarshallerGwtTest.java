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
import org.exoplatform.ide.git.client.marshaller.RemoveRequestMarshaller;
import org.exoplatform.ide.git.shared.RmRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class RemoveMarshallerGwtTest extends BaseGwtTest {
    /** Test remove paths from index and working tree request marshaller. */
    public void testRemovePathsRequestMarshaller() {
        String path1 = "test/files.txt";
        String path2 = "test2/*";

        RmRequest rmRequest = new RmRequest(new String[]{path1, path2});
        RemoveRequestMarshaller marshaller = new RemoveRequestMarshaller(rmRequest);

        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));
        assertTrue(jsonObject.containsKey(Constants.FILES));
        assertEquals(2, jsonObject.get(Constants.FILES).isArray().size());
        assertEquals(path1, jsonObject.get(Constants.FILES).isArray().get(0).isString().stringValue());
        assertEquals(path2, jsonObject.get(Constants.FILES).isArray().get(1).isString().stringValue());
    }
}
