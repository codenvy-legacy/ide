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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import org.exoplatform.ide.extension.aws.shared.beanstalk.Event;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EventsListImpl implements EventsList {
    private List<Event> events;
    private String      nextToken;

    @Override
    public List<Event> getEvents() {
        if (events == null) {
            events = new ArrayList<Event>();
        }
        return events;
    }

    @Override
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String getNextToken() {
        return nextToken;
    }

    @Override
    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    @Override
    public String toString() {
        return "EventsListImpl{" +
               "events=" + events +
               ", nextToken='" + nextToken + '\'' +
               '}';
    }
}
