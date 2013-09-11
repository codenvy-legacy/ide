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
package org.exoplatform.ide.extension.openshift.client.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs after user's logged in OpenShift action. If it ends with fail, then {{@link #isFailed()} returns <code>true</code>.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 31, 2011 11:15:36 AM anya $
 */
public class LoggedInEvent extends GwtEvent<LoggedInHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<LoggedInHandler> TYPE = new GwtEvent.Type<LoggedInHandler>();

    /** If <code>true</code> log in failed. */
    private boolean isFailed;

    public LoggedInEvent() {
    }

    /**
     * @param isFailed
     *         if <code>true</code> log in failed
     */
    public LoggedInEvent(boolean isFailed) {
        this.isFailed = isFailed;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LoggedInHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(LoggedInHandler handler) {
        handler.onLoggedIn(this);
    }

    /** @return if <code>true</code> log in failed */
    public boolean isFailed() {
        return isFailed;
    }
}
