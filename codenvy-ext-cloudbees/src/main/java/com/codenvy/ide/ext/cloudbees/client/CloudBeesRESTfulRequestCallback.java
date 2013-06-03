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
package com.codenvy.ide.ext.cloudbees.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.codenvy.ide.websocket.rest.exceptions.ServerException;
import com.google.web.bindery.event.shared.EventBus;

/**
 * WebSocket CloudBees request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception, in this
 * case - showDialog method calls on {@link LoginPresenter}.
 *
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CloudBeesRESTfulRequestCallback.java Nov 30, 2012 2:29:52 PM azatsarynnyy $
 * @see CloudBeesAsyncRequestCallback
 */
public abstract class CloudBeesRESTfulRequestCallback<T> extends RequestCallback<T> {
    private LoggedInHandler      loggedIn;
    private LoginCanceledHandler loginCanceled;
    private EventBus             eventBus;
    private ConsolePart          console;
    private LoginPresenter       loginPresenter;

    /**
     * Create callback.
     *
     * @param unmarshaller
     * @param loggedIn
     * @param loginCanceled
     * @param eventBus
     * @param console
     * @param loginPresenter
     */
    public CloudBeesRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled,
                                           EventBus eventBus, ConsolePart console, LoginPresenter loginPresenter) {
        super(unmarshaller);
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        this.eventBus = eventBus;
        this.console = console;
        this.loginPresenter = loginPresenter;
    }

    /**
     * Create callback.
     *
     * @param loggedIn
     * @param loginCanceled
     * @param eventBus
     * @param console
     * @param loginPresenter
     */
    public CloudBeesRESTfulRequestCallback(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled, EventBus eventBus,
                                           ConsolePart console, LoginPresenter loginPresenter) {
        this(null, loggedIn, loginCanceled, eventBus, console, loginPresenter);
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            // because of CloudBees returned not 401 status, but 500 status
            // and explanation, that user not autherised in text message,
            // that's why we must parse text message
            final String exceptionMsg = serverException.getMessage();
            if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
                && exceptionMsg.contains("AuthFailure")) {
                loginPresenter.showDialog(loggedIn, loginCanceled);
                return;
            }
        }
        console.print(exception.getMessage());
        eventBus.fireEvent(new ExceptionThrownEvent(exception));
    }
}