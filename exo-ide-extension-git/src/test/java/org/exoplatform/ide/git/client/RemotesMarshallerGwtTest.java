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
import org.exoplatform.ide.git.client.marshaller.RemoteAddRequestMarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListRequestMarshaller;
import org.exoplatform.ide.git.shared.RemoteAddRequest;
import org.exoplatform.ide.git.shared.RemoteListRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class RemotesMarshallerGwtTest extends BaseGwtTest {
    /** Test add remote repository request marshaller. */
    public void testRemoteAddRequestMarshaller() {
        String name = "remote1";
        String url = "remote/repository/location";

        RemoteAddRequest remoteAddRequest = new RemoteAddRequest(name, url);
        RemoteAddRequestMarshaller marshaller = new RemoteAddRequestMarshaller(remoteAddRequest);

        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));
        assertTrue(jsonObject.containsKey(Constants.NAME));
        assertEquals(name, jsonObject.get(Constants.NAME).isString().stringValue());

        assertTrue(jsonObject.containsKey(Constants.URL));
        assertEquals(url, jsonObject.get(Constants.URL).isString().stringValue());
    }

    /** Test get the list of remote repositories. */
    public void testRemoteListRequestMarshaller() {
        RemoteListRequest remoteListRequest = new RemoteListRequest();
        remoteListRequest.setVerbose(true);
        RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));

        assertTrue(jsonObject.containsKey(Constants.REMOTE));
        assertNotNull(jsonObject.get(Constants.REMOTE).isNull());

        assertTrue(jsonObject.containsKey(Constants.VERBOSE));
        assertTrue(jsonObject.get(Constants.VERBOSE).isBoolean().booleanValue());
    }

    /** Test get the info of pointed remote repository. */
    public void testRemoteRequestMarshaller() {
        String remote = "remote1";

        RemoteListRequest remoteListRequest = new RemoteListRequest(remote, true);
        RemoteListRequestMarshaller marshaller = new RemoteListRequestMarshaller(remoteListRequest);

        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));

        assertTrue(jsonObject.containsKey(Constants.REMOTE));
        assertEquals(remote, jsonObject.get(Constants.REMOTE).isString().stringValue());

        assertTrue(jsonObject.containsKey(Constants.VERBOSE));
        assertTrue(jsonObject.get(Constants.VERBOSE).isBoolean().booleanValue());
    }
}
