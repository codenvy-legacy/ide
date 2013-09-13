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
package org.exoplatform.ide.client.framework.application.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;

/**
 * Event is fired, before the browser window closes
 * or navigates to a different site.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: Apr 20, 2012 11:27:05 AM azatsarynnyy $
 */
public class ApplicationClosingEvent extends GwtEvent<ApplicationClosingHandler> {
    /** Type, used to register event. */
    public static final GwtEvent.Type<ApplicationClosingHandler> TYPE = new GwtEvent.Type<ApplicationClosingHandler>();

    /** Link to event needed for set the message. */
    private Window.ClosingEvent windowClosingEvent;

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationClosingHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @param event
     *         event fired just before the browser window
     *         closes or navigates to a different site
     */
    public ApplicationClosingEvent(Window.ClosingEvent event) {
        windowClosingEvent = event;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationClosingHandler handler) {
        handler.onApplicationClosing(this);
    }

    /**
     * The message to display to the user to see
     * whether they really want to leave the page.
     *
     * @param message
     *         the message to display to user
     */
    public void setMessage(String message) {
        windowClosingEvent.setMessage(message);
    }
}
