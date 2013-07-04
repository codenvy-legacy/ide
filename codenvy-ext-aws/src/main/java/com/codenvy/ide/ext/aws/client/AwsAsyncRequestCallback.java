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
package com.codenvy.ide.ext.aws.client;

import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.websocket.rest.exceptions.ServerException;

/**
 * Asynchronous AWS request. The {@link #onFailure(Throwable)} method contains the check for user not authorized
 * exception, in this case - showDialog method calls on {@link LoginPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public abstract class AwsAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
    private       LoggedInHandler      loggedInHandler;
    private final LoginCanceledHandler loginCanceledHandler;
    private       LoginPresenter       loginPresenter;

    /**
     * Create callback.
     *
     * @param unmarshaller
     * @param loggedInHandler
     * @param loginCanceledHandler
     * @param loginPresenter
     */
    public AwsAsyncRequestCallback(Unmarshallable<T> unmarshaller,
                                   LoggedInHandler loggedInHandler,
                                   LoginCanceledHandler loginCanceledHandler, LoginPresenter loginPresenter) {
        super(unmarshaller);
        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;
        this.loginPresenter = loginPresenter;
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception.getMessage() != null && exception.getMessage().contains("Authentication required")) {
            loginPresenter.showDialog(loggedInHandler, loginCanceledHandler);
            return;
        } else if (exception instanceof UnauthorizedException) {
            loginPresenter.showDialog(loggedInHandler, loginCanceledHandler);
            return;
        } else if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (serverException.getMessage() != null && serverException.getMessage().contains("Authentication required")) {
                loginPresenter.showDialog(loggedInHandler, loginCanceledHandler);
                return;
            }
        }
        processFail(exception);
    }

    /**
     * Need to be implemented by user to handle exception on failure.
     *
     * @param exception throwed exception.
     */
    protected abstract void processFail(Throwable exception);
}