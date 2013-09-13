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
import org.exoplatform.ide.git.client.marshaller.InitRequestMarshaller;
import org.exoplatform.ide.git.shared.InitRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class InitMarshallerGwtTest extends BaseGwtTest {
    /** Test init new repository request marshaller. */
    public void testInitRequestMarshaller() {
        String workingDir = "my/new/repo";

        InitRequest initRequest = new InitRequest(workingDir, false);
        InitRequestMarshaller marshaller = new InitRequestMarshaller(initRequest);
        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));
        assertTrue(jsonObject.containsKey(Constants.WORKNG_DIR));
        assertEquals(workingDir, jsonObject.get(Constants.WORKNG_DIR).isString().stringValue());

        assertTrue(jsonObject.containsKey(Constants.BARE));
        assertFalse(jsonObject.get(Constants.BARE).isBoolean().booleanValue());
    }
}
