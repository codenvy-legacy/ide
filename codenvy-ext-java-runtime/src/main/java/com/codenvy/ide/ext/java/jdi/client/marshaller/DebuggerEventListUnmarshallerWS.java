/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.java.jdi.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;

/**
 * Unmarshaller for deserialize debugger event list, which is received over WebSocket connection.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: DebuggerEventListUnmarshallerWS.java Nov 19, 2012 12:42:58 PM azatsarynnyy $
 */
public class DebuggerEventListUnmarshallerWS implements Unmarshallable<DebuggerEventList> {
    private DtoClientImpls.DebuggerEventListImpl events;

    /**
     * Create unmarshaller.
     *
     * @param events
     */
    public DebuggerEventListUnmarshallerWS(@NotNull DtoClientImpls.DebuggerEventListImpl events) {
        this.events = events;
        this.events.setEvents(JsonCollections.<DebuggerEvent>createArray());
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        DtoClientImpls.DebuggerEventListImpl events = DtoClientImpls.DebuggerEventListImpl.deserialize(response.getBody());
        if (events == null) {
            return;
        }
        this.events.setEvents(events.getEvents());
    }

    /** {@inheritDoc} */
    @Override
    public DebuggerEventList getPayload() {
        return events;
    }
}