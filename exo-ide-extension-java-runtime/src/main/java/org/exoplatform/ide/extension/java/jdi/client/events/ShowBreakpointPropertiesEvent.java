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
package org.exoplatform.ide.extension.java.jdi.client.events;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

/**
 * Event occurs when user tries to show breakpoint properties.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowBreakpointPropertiesEvent.java May 8, 2012 13:00:37 PM azatsarynnyy $
 */
public class ShowBreakpointPropertiesEvent extends GwtEvent<ShowBreakpointPropertiesHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<ShowBreakpointPropertiesHandler> TYPE =
            new GwtEvent.Type<ShowBreakpointPropertiesHandler>();

    /** Current breakpoint. */
    private BreakPoint breakPoint;

    /**
     * @param breakPoint
     *         current breakpoint
     */
    public ShowBreakpointPropertiesEvent(BreakPoint breakPoint) {
        this.breakPoint = breakPoint;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowBreakpointPropertiesHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShowBreakpointPropertiesHandler handler) {
        handler.onShowBreakpointProperties(this);
    }

    /** @return the breakpoint */
    public BreakPoint getBreakPoint() {
        return breakPoint;
    }
}
