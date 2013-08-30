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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerEventList;
import org.exoplatform.ide.extension.java.jdi.shared.StepEvent;

import java.util.ArrayList;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DebuggerEventListUnmarshaller implements Unmarshallable<DebuggerEventList> {

    private DebuggerEventList events;

    public DebuggerEventListUnmarshaller(DebuggerEventList events) {
        this.events = events;
        if (this.events.getEvents() == null)
            this.events.setEvents(new ArrayList<DebuggerEvent>());
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONObject jObj = JSONParser.parseStrict(response.getText()).isObject();
        if (jObj == null) {
            return;
        }

        if (jObj.containsKey("events")) {
            JSONArray jEvent = jObj.get("events").isArray();
            for (int i = 0; i < jEvent.size(); i++) {
                JSONObject je = jEvent.get(i).isObject();
                if (je.containsKey("type")) {
                    int type = (int)je.get("type").isNumber().doubleValue();
                    if (type == DebuggerEvent.BREAKPOINT) {
                        AutoBean<BreakPointEvent> bean = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoinEvent();
                        Splittable data = StringQuoter.split(je.toString());
                        AutoBeanCodex.decodeInto(data, bean);
                        events.getEvents().add(bean.as());
                    } else if (type == DebuggerEvent.STEP) {
                        AutoBean<StepEvent> bean = DebuggerExtension.AUTO_BEAN_FACTORY.stepEvent();
                        Splittable data = StringQuoter.split(je.toString());
                        AutoBeanCodex.decodeInto(data, bean);
                        events.getEvents().add(bean.as());
                    }
                }
            }
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    @Override
    public DebuggerEventList getPayload() {
        return events;
    }

}
