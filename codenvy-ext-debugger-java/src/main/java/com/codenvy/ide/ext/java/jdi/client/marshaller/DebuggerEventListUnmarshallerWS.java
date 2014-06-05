/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdi.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.shared.BreakPointEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEvent;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.StepEvent;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import java.util.ArrayList;

/**
 * Unmarshaller for deserializing debugger event list, which is received over WebSocket connection.
 *
 * @author Artem Zatsarynnyy
 */
public class DebuggerEventListUnmarshallerWS implements Unmarshallable<DebuggerEventList> {
    private DtoFactory        dtoFactory;
    private DebuggerEventList events;

    public DebuggerEventListUnmarshallerWS(DtoFactory dtoFactory) {
        this.dtoFactory = dtoFactory;
        this.events = dtoFactory.createDto(DebuggerEventList.class);
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        this.events.setEvents(new ArrayList<DebuggerEvent>());

        JSONObject jsonObject = JSONParser.parseStrict(response.getBody()).isObject();
        if (jsonObject == null) {
            return;
        }

        if (jsonObject.containsKey("events")) {
            JSONArray events = jsonObject.get("events").isArray();
            for (int i = 0; i < events.size(); i++) {
                JSONObject event = events.get(i).isObject();
                if (event.containsKey("type")) {
                    final int type = (int)event.get("type").isNumber().doubleValue();
                    if (DebuggerEvent.BREAKPOINT == type) {
                        BreakPointEvent breakPointEvent = dtoFactory.createDtoFromJson(event.toString(), BreakPointEvent.class);
                        this.events.getEvents().add(breakPointEvent);
                    } else if (DebuggerEvent.STEP == type) {
                        StepEvent stepEvent = dtoFactory.createDtoFromJson(event.toString(), StepEvent.class);
                        this.events.getEvents().add(stepEvent);
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public DebuggerEventList getPayload() {
        return events;
    }

}