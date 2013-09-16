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
package org.exoplatform.ide.extension.appfog.client.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to log in Appfog.
 * Implement {@link LoginHandler} to handle event.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class LoginEvent extends GwtEvent<LoginHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<LoginHandler> TYPE = new GwtEvent.Type<LoginHandler>();

    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    private String loginUrl;

    /**
     * @param loggedIn
     * @param loginCanceled
     * @param loginUrl
     */
    public LoginEvent(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled, String loginUrl) {
        super();
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        this.loginUrl = loginUrl;
    }

    public LoginEvent(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled) {
        this(loggedIn, loginCanceled, null);
    }

    public LoggedInHandler getLoggedIn() {
        return loggedIn;
    }

    /** @return the loginCanceled */
    public LoginCanceledHandler getLoginCanceled() {
        return loginCanceled;
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

    /** @return the loginUrl */
    public String getLoginUrl() {
        return loginUrl;
    }

}
