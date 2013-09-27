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

import org.exoplatform.ide.git.client.marshaller.AddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.shared.AddRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 11:49:37 AM anya $
 */
public class AddRequestMarshallerGwtTest extends BaseGwtTest {

    /** Test add request with default file pattern. */
    public void testAddAllRequestMarshaller() {
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

    /** Test add request with set file patterns. */
    public void testAddFilesRequestMarshaller() {
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
