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

import org.exoplatform.ide.git.client.marshaller.LogRequestMarshaller;
import org.exoplatform.ide.git.shared.LogRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class LogMarshallerGwtTest extends BaseGwtTest {
    /** Test get log of the commits request marshaller. */
    public void testLogRequestMarshaller() {
        LogRequest logRequest = new LogRequest();
        LogRequestMarshaller marshaller = new LogRequestMarshaller(logRequest);
        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));

        assertNotNull(jsonObject);
    }
}
