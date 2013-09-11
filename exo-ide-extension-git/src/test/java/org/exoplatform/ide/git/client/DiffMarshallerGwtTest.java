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
import org.exoplatform.ide.git.client.marshaller.DiffRequestMarshaller;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 5, 2011 5:24:39 PM anya $
 */
public class DiffMarshallerGwtTest extends BaseGwtTest {
    /** Test the project's diff request marshaller. */
    public void testProjectsDiffRequestMarshaller() {
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

    /** Test the resource's diff request marshaller. */
    public void testResourceDiffRequestMarshaller() {
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
