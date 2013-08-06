/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import com.codenvy.ide.ext.gae.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.gae.shared.GaeUser;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LoginAction extends Action implements OAuthCallback {
    private String                 restContext;
    private UserClientService      userClientService;
    private GAEResources           resources;
    private GAEClientService       service;
    private EventBus               eventBus;
    private ConsolePart            console;
    private GAELocalization        constant;
    private Loader                 loader;
    private OAuthStatus            authStatus;
    private AsyncCallback<Boolean> callback;

    @Inject
    public LoginAction(@Named("restContext") String restContext, UserClientService userClientService,
                       GAEResources resources, GAEClientService service, EventBus eventBus, ConsolePart console,
                       GAELocalization constant, Loader loader) {
        super("Login...", "Login to Google App Engine.", resources.login());
        this.restContext = restContext;
        this.userClientService = userClientService;
        this.resources = resources;
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.loader = loader;

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                isUserLoggedIn();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (authStatus == OAuthStatus.LOGGED_IN) {
            doLogout();
        } else {
            doLogin(null);
        }
    }

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

    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        this.authStatus = authStatus;
        if (callback != null) {
            callback.onSuccess(authStatus == OAuthStatus.LOGGED_IN);
        }
    }

    public void isUserLoggedIn() {
        isUserLoggedIn(null);
    }

    public void isUserLoggedIn(AsyncCallback<Boolean> callback) {
        this.callback = callback;

        DtoClientImpls.GaeUserImpl gaeUser = DtoClientImpls.GaeUserImpl.make();
        GaeUserUnmarshaller unmarshaller = new GaeUserUnmarshaller(gaeUser);

        try {
            service.getLoggedUser(
                    new GAEAsyncRequestCallback<GaeUser>(unmarshaller, console, eventBus, constant, null) {
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

    public void doLogin(AsyncCallback<Boolean> callback) {
        this.callback = callback;
        doLogin();
    }

    //Todo need to be improved to fetch user id from something else, not by rest service
    public void doLogin() {
        com.codenvy.ide.client.DtoClientImpls.UserImpl dtoUser = com.codenvy.ide.client.DtoClientImpls.UserImpl.make();
        UserUnmarshaller unmarshaller = new UserUnmarshaller(dtoUser);

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

    private void openAuthWindow(User user) {
        String authUrl = "rest/ide/oauth/authenticate?oauth_provider=google"
                         + "&scope=https://www.googleapis.com/auth/appengine.admin"
                         + "&userId=" + user.getUserId() + "&redirect_after_login=/ide/" + Utils.getWorkspaceName();
        JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 450, 500, this);
        authWindow.loginWithOAuth();
    }

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

//    private void checkUserLogged(@NotNull final AsyncCallback<Boolean> authCallback) {
//        loader.setMessage("Checking if user has Google App Engine scope...");
//        loader.show();
//
//        DtoClientImpls.GaeUserImpl user = DtoClientImpls.GaeUserImpl.make();
//        GaeUserUnmarshaller unmarshaller = new GaeUserUnmarshaller(user);
//
//        try {
//            service.getLoggedUser(
//                    new GAEAsyncRequestCallback<GaeUser>(unmarshaller, console, eventBus, constant, null) {
//                        @Override
//                        protected void onSuccess(GaeUser result) {
//                            loader.hide();
//
//                            if (!GAEExtension.isUserHasGaeScopes(result.getToken())) {
////                                boolean doLogin = Window.confirm(
////                                        "You aren't authorize to complete this operation.\nDo you want to login on " +
////                                        "Google App Engine?");
////                                if (doLogin) {
//                                    doLogin(authCallback);
////                                } else {
////                                    authCallback.onSuccess(false);
////                                }
//                                onAuthenticated(OAuthStatus.LOGGED_IN);
//                            } else {
////                                authCallback.onSuccess(true);
//                                onAuthenticated(OAuthStatus.LOGGED_OUT);
//                            }
//                        }
//                    });
//        } catch (RequestException e) {
//            eventBus.fireEvent(new ExceptionThrownEvent(e));
//            console.print(e.getMessage());
//        }
//    }
}
