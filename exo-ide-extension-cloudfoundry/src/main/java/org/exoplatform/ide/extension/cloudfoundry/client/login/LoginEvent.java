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
package org.exoplatform.ide.extension.cloudfoundry.client.login;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;

/**
 * Event occurs, when user tries to log in CloudFoundry.
 * Implement {@link LoginHandler} to handle event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 7, 2011 12:32:53 PM anya $
 */
public class LoginEvent extends GwtEvent<LoginHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<LoginHandler> TYPE = new GwtEvent.Type<LoginHandler>();

    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    private String loginUrl;

    private PAAS_PROVIDER paasProvider;

    /**
     * 
     * @param loggedIn
     * @param loginCanceled
     * @param loginUrl
     * @param paasProvider
     */
    public LoginEvent(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled, String loginUrl, PAAS_PROVIDER paasProvider) {
        super();
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        this.loginUrl = loginUrl;
        this.paasProvider = paasProvider;
    }

    public LoginEvent(PAAS_PROVIDER paasProvider) {
        this(null, null, null, paasProvider);
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

    public PAAS_PROVIDER getPaasProvider() {
        return paasProvider;
    }

}
