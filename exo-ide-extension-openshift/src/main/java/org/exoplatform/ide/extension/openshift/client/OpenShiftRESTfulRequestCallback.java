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
package org.exoplatform.ide.extension.openshift.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;

/**
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: OpenShiftRESTfulRequestCallback.java Nov 30, 2012 4:40:25 PM azatsarynnyy $
 * @see OpenShiftAsyncRequestCallback
 */
public abstract class OpenShiftRESTfulRequestCallback<T> extends RequestCallback<T> {

    private LoggedInHandler loggedInHandler;

    private LoginCanceledHandler loginCanceledHandler;

    private String errorMessage;

    public OpenShiftRESTfulRequestCallback(Unmarshallable<T> unmarshaller) {
        super(unmarshaller);
    }

    public OpenShiftRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedInHandler,
                                           LoginCanceledHandler loginCanceledHandler) {
        super(unmarshaller);

        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;
    }

    public OpenShiftRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedInHandler,
                                           LoginCanceledHandler loginCanceledHandler, String errorMessage) {
        super(unmarshaller);

        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;
        this.errorMessage = errorMessage;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.RequestCallback#onFailure(java.lang.Throwable) */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (HTTPStatus.OK == serverException.getHTTPStatus()
                && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED))) {
                IDE.fireEvent(new LoginEvent(loggedInHandler, loginCanceledHandler));
                return;
            }
        }

        if (errorMessage != null) {
            IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, errorMessage));
        } else {
            IDE.fireEvent(new ExceptionThrownEvent(exception));
        }
    }

}
