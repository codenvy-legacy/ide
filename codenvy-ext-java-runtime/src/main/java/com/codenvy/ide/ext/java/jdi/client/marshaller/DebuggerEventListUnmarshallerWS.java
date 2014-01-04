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
 * Unmarshaller for deserialize debugger event list, which is received over WebSocket connection.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: DebuggerEventListUnmarshallerWS.java Nov 19, 2012 12:42:58 PM azatsarynnyy $
 */
public class DebuggerEventListUnmarshallerWS implements Unmarshallable<DebuggerEventList> {
    private DtoFactory        dtoFactory;
    private DebuggerEventList events;

    public DebuggerEventListUnmarshallerWS(DtoFactory dtoFactory) {
        this.dtoFactory = dtoFactory;
        this.events = dtoFactory.createDto(DebuggerEventList.class);
        this.events.setEvents(new ArrayList<DebuggerEvent>());
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        JSONObject jObj = JSONParser.parseStrict(response.getBody()).isObject();
        if (jObj == null) {
            return;
        }

        if (jObj.containsKey("events")) {
            JSONArray jEvent = jObj.get("events").isArray();
            for (int i = 0; i < jEvent.size(); i++) {
                JSONObject je = jEvent.get(i).isObject();
                if (je.containsKey("type")) {
                    int type = (int)je.get("type").isNumber().doubleValue();
                    switch (type) {
                        case DebuggerEvent.BREAKPOINT:
                            BreakPointEvent breakPointEvent = dtoFactory.createDtoFromJson(je.toString(), BreakPointEvent.class);
                            events.getEvents().add(breakPointEvent);
                        case DebuggerEvent.STEP:
                            StepEvent stepEvent = dtoFactory.createDtoFromJson(je.toString(), StepEvent.class);
                            events.getEvents().add(stepEvent);
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