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

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for debugger event list.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
public class DebuggerEventListUnmarshaller implements Unmarshallable<DebuggerEventList> {
    private DtoClientImpls.DebuggerEventListImpl events;

    /**
     * Create unmarshaller.
     *
     * @param events
     */
    public DebuggerEventListUnmarshaller(DtoClientImpls.DebuggerEventListImpl events) {
        this.events = events;
        this.events.setEvents(JsonCollections.<DebuggerEvent>createArray());
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        DtoClientImpls.DebuggerEventListImpl list = DtoClientImpls.DebuggerEventListImpl.deserialize(response.getText());
        if (list == null) {
            return;
        }

        JsonArray<DebuggerEvent> listEvents = list.getEvents();
        for (int i = 0; i < listEvents.size(); i++) {
            DebuggerEvent event = listEvents.get(i);
            int type = event.getType();
            if (type == DebuggerEvent.BREAKPOINT) {
                DtoClientImpls.DebuggerEventImpl debuggerEvent = DtoClientImpls.DebuggerEventImpl.make();
                debuggerEvent.setType(type);
                events.getEvents().add(debuggerEvent);
            } else if (type == DebuggerEvent.STEP) {
                DtoClientImpls.StepEventImpl stepEvent = DtoClientImpls.StepEventImpl.make();
                stepEvent.setType(type);
                events.getEvents().add(stepEvent);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public DebuggerEventList getPayload() {
        return events;
    }
}