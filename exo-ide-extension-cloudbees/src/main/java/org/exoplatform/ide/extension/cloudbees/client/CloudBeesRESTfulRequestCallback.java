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
package org.exoplatform.ide.extension.cloudbees.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginEvent;

/**
 * WebSocket CloudBees request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception,
 * in this case - the {@link LoginEvent} is fired.
 *
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CloudBeesRESTfulRequestCallback.java Nov 30, 2012 2:29:52 PM azatsarynnyy $
 * @see CloudBeesAsyncRequestCallback
 */
public abstract class CloudBeesRESTfulRequestCallback<T> extends RequestCallback<T> {
    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    public CloudBeesRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                           LoginCanceledHandler loginCanceled) {
        super(unmarshaller);
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
    }

    public CloudBeesRESTfulRequestCallback(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled) {
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.RequestCallback#onFailure(java.lang.Throwable) */
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
                IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled));
                return;
            }
        }
        IDE.fireEvent(new ExceptionThrownEvent(exception));
    }

}
