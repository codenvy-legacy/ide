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

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.git.client.marshaller.RemoteListUnmarshaller;
import org.exoplatform.ide.git.shared.Remote;

import java.util.ArrayList;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class RemoteListUnmarshallerGwtTest extends BaseGwtTest {
    private final String REMOTE_LIST_RESPONSE = "[{\"name\":\"origin\",\"url\":\"git/anya/remoteToAdd\"}]";

    /** Test remote list response unmarshaller. */
    public void testLogResponseUnmarshaller() {
        java.util.List<Remote> remotes = new ArrayList<Remote>();

        RemoteListUnmarshaller unmarshaller = new RemoteListUnmarshaller(remotes);
        try {
            unmarshaller.unmarshal(new MockResponse(REMOTE_LIST_RESPONSE));
        } catch (UnmarshallerException e) {
            fail(e.getMessage());
        }

        assertEquals(1, remotes.size());
        Remote remote = remotes.get(0);
        assertEquals("git/anya/remoteToAdd", remote.getUrl());
        assertEquals("origin", remote.getName());
    }
}
