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
package com.codenvy.ide.api.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;

/**
 * Event that describes the fact that Codenvy browser's tab has been closed or closing now.
 *
 * @author Artem Zatsarynnyy
 */
public class WindowActionEvent extends GwtEvent<WindowActionHandler> {

    public static Type<WindowActionHandler> TYPE = new Type<>();

    /** Set of possible Window Actions. */
    public static enum WindowAction {
        CLOSING, CLOSED
    }

    private final WindowAction        windowAction;
    private final Window.ClosingEvent event;

    /** Creates a Window Closing Event. */
    public static WindowActionEvent createWindowClosingEvent(Window.ClosingEvent event) {
        return new WindowActionEvent(event, WindowAction.CLOSING);
    }

    /** Creates a Window Closed Event. */
    public static WindowActionEvent createWindowClosedEvent() {
        return new WindowActionEvent(null, WindowAction.CLOSED);
    }

    protected WindowActionEvent(Window.ClosingEvent event, WindowAction windowAction) {
        this.event = event;
        this.windowAction = windowAction;
    }

    @Override
    public Type<WindowActionHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Makes sense only for {@link WindowAction#CLOSING}.
     *
     * @see com.google.gwt.user.client.Window.ClosingEvent#setMessage(java.lang.String)
     */
    public void setMessage(String message) {
        event.setMessage(message);
    }

    @Override
    protected void dispatch(WindowActionHandler handler) {
        switch (windowAction) {
            case CLOSING:
                handler.onWindowClosing(this);
                break;
            case CLOSED:
                handler.onWindowClosed(this);
                break;
            default:
                break;
        }
    }
}
