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
