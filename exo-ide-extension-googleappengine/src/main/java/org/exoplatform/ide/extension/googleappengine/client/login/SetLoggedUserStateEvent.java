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
package org.exoplatform.ide.extension.googleappengine.client.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs to set the state of the App Engine user.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 23, 2012 10:27:32 AM anya $
 */
public class SetLoggedUserStateEvent extends GwtEvent<SetLoggedUserStateHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<SetLoggedUserStateHandler> TYPE = new GwtEvent.Type<SetLoggedUserStateHandler>();

    private boolean isLogged;

    /**
     * @param isLogged
     *         logged user state
     */
    public SetLoggedUserStateEvent(boolean isLogged) {
        this.isLogged = isLogged;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SetLoggedUserStateHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(SetLoggedUserStateHandler handler) {
        handler.onSetLoggedUserState(this);
    }

    /** @return the isLogged */
    public boolean isLogged() {
        return isLogged;
    }
}
