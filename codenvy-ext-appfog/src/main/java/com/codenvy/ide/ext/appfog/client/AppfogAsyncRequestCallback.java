/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.ext.appfog.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 * @see AppfogRESTfulRequestCallback
 */
public abstract class AppfogAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
    private final static String APPFOG_EXIT_CODE = "Appfog-Exit-Code";
    private LoggedInHandler            loggedIn;
    private LoginCanceledHandler       loginCanceled;
    private String                     loginUrl;
    private EventBus                   eventBus;
    private AppfogLocalizationConstant constant;
    private LoginPresenter             loginPresenter;
    private ConsolePart                console;

    /**
     * Create callback.
     *
     * @param unmarshaller
     * @param loggedIn
     * @param loginCanceled
     * @param eventBus
     * @param constant
     * @param console
     * @param loginPresenter
     */
    public AppfogAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled,
                                      EventBus eventBus, AppfogLocalizationConstant constant, ConsolePart console,
                                      LoginPresenter loginPresenter) {
        this(unmarshaller, loggedIn, loginCanceled, null, eventBus, constant, console, loginPresenter);
    }

    /**
     * Create callback.
     *
     * @param unmarshaller
     * @param loggedIn
     * @param loginCanceled
     * @param loginUrl
     * @param eventBus
     * @param constant
     * @param console
     * @param loginPresenter
     */
    public AppfogAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled,
                                      String loginUrl, EventBus eventBus, AppfogLocalizationConstant constant, ConsolePart console,
                                      LoginPresenter loginPresenter) {
        super(unmarshaller);
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        this.loginUrl = loginUrl;
        this.eventBus = eventBus;
        this.constant = constant;
        this.console = console;
        this.loginPresenter = loginPresenter;
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
                && serverException.getMessage().contains("Authentication required.")) {
                loginPresenter.showDialog(loggedIn, loginCanceled, loginUrl);
                return;
            } else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
                       && serverException.getHeader(APPFOG_EXIT_CODE) != null
                       && "200".equals(serverException.getHeader(APPFOG_EXIT_CODE))) {
                loginPresenter.showDialog(loggedIn, loginCanceled, loginUrl);
                return;
            } else if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus()
                       && serverException.getHeader(APPFOG_EXIT_CODE) != null
                       && "301".equals(serverException.getHeader(APPFOG_EXIT_CODE))) {
                Window.alert(constant.applicationNotFound());
                return;
            } else {
                String msg = "";
                if (serverException.isErrorMessageProvided()) {
                    msg = serverException.getLocalizedMessage();
                    if (RegExp.compile("Application '.+' already exists. Use update or delete.").exec(msg) != null) {
                        msg = "Application already exist on appfog";
                    } else if (RegExp.compile("Unexpected response from service gateway").exec(msg) != null) {
                        msg = "Appfog error: " + msg;
                    }
                } else {
                    msg = "Status:&nbsp;" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();
                }

                Window.alert(msg);
                return;
            }
        }
        console.print(exception.getMessage());
        eventBus.fireEvent(new ExceptionThrownEvent(exception));
    }
}