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
package com.codenvy.ide.extension.cloudfoundry.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginCanceledHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

/**
 * WebSocket CloudFoundry request. The {@link #onFailure(Throwable)} method contains the check for user not authorized
 * exception, in this case - showDialog method calls on {@link LoginPresenter}.
 *
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CloudFoundryRESTfulRequestCallback.java Nov 30, 2012 9:58:20 AM azatsarynnyy $
 * @see CloudFoundryAsyncRequestCallback
 */
public abstract class CloudFoundryRESTfulRequestCallback<T> extends RequestCallback<T> {
    private LoggedInHandler      loggedIn;
    private LoginCanceledHandler loginCanceled;
    private String               loginUrl;
    private final static String CLOUDFOUNDRY_EXIT_CODE = "Cloudfoundry-Exit-Code";
    private EventBus                            eventBus;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

    /**
     * Create callback.
     *
     * @param unmarshaller
     * @param loggedIn
     * @param loginCanceled
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param paasProvider
     */
    public CloudFoundryRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                              LoginCanceledHandler loginCanceled, EventBus eventBus, ConsolePart console,
                                              CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter,
                                              CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this(unmarshaller, loggedIn, loginCanceled, null, eventBus, console, constant, loginPresenter, paasProvider);
    }

    /**
     * Create callback.
     *
     * @param unmarshaller
     * @param loggedIn
     * @param loginCanceled
     * @param loginUrl
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param paasProvider
     */
    public CloudFoundryRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                              LoginCanceledHandler loginCanceled, String loginUrl, EventBus eventBus, ConsolePart console,
                                              CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter,
                                              CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        super(unmarshaller);
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        this.loginUrl = loginUrl;
        this.eventBus = eventBus;
        this.constant = constant;
        this.console = console;
        this.loginPresenter = loginPresenter;
        this.paasProvider = paasProvider;
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
                && serverException.getMessage().contains("Authentication required.")) {
                loginPresenter.showDialog(loggedIn, loginCanceled, loginUrl, paasProvider);
                return;
            } else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
                       && serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE) != null
                       && "200".equals(serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE))) {
                loginPresenter.showDialog(loggedIn, loginCanceled, loginUrl, paasProvider);
                return;
            } else if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus()
                       && serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE) != null
                       && "301".equals(serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE))) {
                Window.alert(constant.applicationNotFound());
                return;
            } else {
                String msg = "";
                if (serverException.isErrorMessageProvided()) {
                    msg = serverException.getLocalizedMessage();
                } else {
                    msg = "Status:&nbsp;" + serverException.getHTTPStatus();// + "&nbsp;" + serverException.getStatusText();
                }

                Window.alert(msg);
                return;
            }
        }
        eventBus.fireEvent(new ExceptionThrownEvent(exception));
        console.print(exception.getMessage());
    }
}