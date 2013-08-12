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
package com.codenvy.ide.ext.aws.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.EventsList;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for Events list.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class EventsListUnmarshaller implements Unmarshallable<EventsList> {
    private DtoClientImpls.EventsListImpl eventsList;

    /**
     * Create unmarshaller.
     *
     * @param eventsList
     */
    public EventsListUnmarshaller(DtoClientImpls.EventsListImpl eventsList) {
        this.eventsList = eventsList;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject eventsObject = JSONParser.parseStrict(text).isObject();
        if (eventsObject == null) {
            return;
        }

        DtoClientImpls.EventsListImpl dtoEvents = DtoClientImpls.EventsListImpl.deserialize(text);
        eventsList.setEvents(dtoEvents.getEvents());
        eventsList.setNextToken(dtoEvents.getNextToken());
    }

    /** {@inheritDoc} */
    @Override
    public EventsList getPayload() {
        return eventsList;
    }
}
