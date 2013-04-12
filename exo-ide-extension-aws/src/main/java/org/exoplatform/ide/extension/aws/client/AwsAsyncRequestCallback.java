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
package org.exoplatform.ide.extension.aws.client;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.aws.client.login.LoginEvent;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 18, 2012 11:07:50 AM anya $
 */
public abstract class AwsAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
    private LoggedInHandler loggedInHandler;
    private final LoginCanceledHandler loginCanceledHandler;

    public AwsAsyncRequestCallback(Unmarshallable<T> unmarshaller,
                                   LoggedInHandler loggedInHandler,
                                   LoginCanceledHandler loginCanceledHandler) {
        super(unmarshaller);
        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;
    }

    public AwsAsyncRequestCallback(LoggedInHandler loggedInHandler, LoginCanceledHandler loginCanceledHandler) {
        this.loggedInHandler = loggedInHandler;
        this.loginCanceledHandler = loginCanceledHandler;
    }


    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable) */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof UnauthorizedException) {
            IDE.fireEvent(new LoginEvent(loggedInHandler, loginCanceledHandler));
            return;
        } else if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (serverException.getMessage() != null && serverException.getMessage().contains("Authentication required")) {
                IDE.fireEvent(new LoginEvent(loggedInHandler, loginCanceledHandler));
                return;
            }
        }
        processFail(exception);
    }

    protected abstract void processFail(Throwable exception);
}
