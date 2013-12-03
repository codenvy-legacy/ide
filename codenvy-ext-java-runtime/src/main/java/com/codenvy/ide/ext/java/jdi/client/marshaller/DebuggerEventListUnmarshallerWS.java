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
package com.codenvy.ide.ext.java.jdi.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * Unmarshaller for deserialize debugger event list, which is received over WebSocket connection.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: DebuggerEventListUnmarshallerWS.java Nov 19, 2012 12:42:58 PM azatsarynnyy $
 */
public class DebuggerEventListUnmarshallerWS implements Unmarshallable<DebuggerEventList> {
//    private DtoClientImpls.DebuggerEventListImpl events;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
//        events = DtoClientImpls.DebuggerEventListImpl.deserialize(response.getBody());
    }

    /** {@inheritDoc} */
    @Override
    public DebuggerEventList getPayload() {
        return null;//events;
    }
}