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
package com.codenvy.ide.ext.gae.client.actions;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.client.marshaller.UserUnmarshaller;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.*;
import com.codenvy.ide.ext.gae.client.marshaller.GaeUserUnmarshaller;
import com.codenvy.ide.ext.gae.shared.GaeUser;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.util.Utils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;

/**
 * Action for "PaaS/Google App Engine/Login..." to allow user to login on on Google.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoginAction extends Action implements OAuthCallback {
    private UserClientService      userClientService;
    private GAEResources           resources;
    private GAEClientService       service;
    private EventBus               eventBus;
    private ConsolePart            console;
    private GAELocalization        constant;
    private OAuthStatus            authStatus;
    private AsyncCallback<Boolean> callback;

    /** Constructor for action. */
    @Inject
    public LoginAction(UserClientService userClientService, GAEResources resources, GAEClientService service,
                       EventBus eventBus, ConsolePart console, GAELocalization constant) {
        super("Login...", "Login to Google App Engine.", resources.login());
        this.userClientService = userClientService;
        this.resources = resources;
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                isUserLoggedIn();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (authStatus == OAuthStatus.LOGGED_IN) {
            doLogout();
        } else {
            doLogin(null);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        if (authStatus == OAuthStatus.LOGGED_IN) {
            e.getPresentation().setIcon(resources.logout());
            e.getPresentation().setText("Logout...");
            e.getPresentation().setDescription("Logout from Google App Engine.");
        } else {
            e.getPresentation().setIcon(resources.login());
            e.getPresentation().setText("Login...");
            e.getPresentation().setDescription("Login to Google App Engine.");
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        this.authStatus = authStatus;
        if (callback != null) {
            callback.onSuccess(authStatus == OAuthStatus.LOGGED_IN);
        }
    }

    /** Checks if user is logged in. */
    public void isUserLoggedIn() {
        isUserLoggedIn(null);
    }

    /**
     * Checks if user is logged in and run callback function on authorize status.
     *
     * @param callback
     *         callback function for authorize status, function calls com.google.gwt.user.client.rpc
     *         .AsyncCallback#onSuccess(Boolean) with true value if user is logged in and otherwise if logged out.
     */
    public void isUserLoggedIn(AsyncCallback<Boolean> callback) {
        this.callback = callback;
        GaeUserUnmarshaller unmarshaller = new GaeUserUnmarshaller();

        try {
            service.getLoggedUser(new GAEAsyncRequestCallback<GaeUser>(unmarshaller, console, eventBus, constant, null) {
                @Override
                protected void onSuccess(GaeUser result) {
                    if (GAEExtension.isUserHasGaeScopes(result.getToken())) {
                        onAuthenticated(OAuthStatus.LOGGED_IN);
                    } else {
                        onAuthenticated(OAuthStatus.LOGGED_OUT);
                    }
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Start login on Google Services with callback function.
     *
     * @param callback
     *         callback function for authorize status, function calls com.google.gwt.user.client.rpc
     *         .AsyncCallback#onSuccess(Boolean) with true value if user is logged in and otherwise if logged out.
     */
    public void doLogin(AsyncCallback<Boolean> callback) {
        this.callback = callback;
        doLogin();
    }

    /**
     * Start login on Google Services without callback function.
     * <p/>
     * Todo need to be improved to fetch user id from something else, not by rest service
     */
    public void doLogin() {
        UserUnmarshaller unmarshaller = new UserUnmarshaller();

        try {
            this.userClientService.getUser(new AsyncRequestCallback<User>(unmarshaller) {
                @Override
                protected void onSuccess(User result) {
                    openAuthWindow(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException exception) {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
            console.print(exception.getMessage());
        }
    }

    /**
     * Open native popup window for starting user authorization on Google Services.
     *
     * @param user
     *         user id that logged in in Codenvy.
     */
    private void openAuthWindow(User user) {
        String authUrl = "rest/ide/oauth/authenticate?oauth_provider=google"
                         + "&scope=https://www.googleapis.com/auth/appengine.admin"
                         + "&userId=" + user.getUserId() + "&redirect_after_login=/ide/" + Utils.getWorkspaceName();
        JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 450, 500, this);
        authWindow.loginWithOAuth();
    }

    /** Starts log out from Google Services. */
    public void doLogout() {
        try {
            service.logout(new AsyncRequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                    onAuthenticated(OAuthStatus.LOGGED_OUT);
                    console.print(constant.logoutSuccess());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(constant.logoutNotLogged());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}
