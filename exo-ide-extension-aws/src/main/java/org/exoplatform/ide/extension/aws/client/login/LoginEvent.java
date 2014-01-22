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
package org.exoplatform.ide.extension.aws.client.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to log in AWS.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 3:04:41 PM anya $
 */
public class LoginEvent extends GwtEvent<LoginHandler> {

    /** Type used to register the event. */
    public static final GwtEvent.Type<LoginHandler> TYPE = new GwtEvent.Type<LoginHandler>();

    private LoggedInHandler loggedInHandler;

    private LoginCanceledHandler loginCanceledHandler;

    public LoginEvent() {
        loggedInHandler = null;
        loginCanceledHandler = null;
    }

    public LoginEvent(LoggedInHandler loggedInHandler, LoginCanceledHandler loginCanceledHandler) {
        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LoginHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(LoginHandler handler) {
        handler.onLogin(this);
    }

    /** @return the loggedInHandler */
    public LoggedInHandler getLoggedInHandler() {
        return loggedInHandler;
    }

    /** @return the loginCanceledHandler */
    public LoginCanceledHandler getLoginCanceledHandler() {
        return loginCanceledHandler;
    }
}