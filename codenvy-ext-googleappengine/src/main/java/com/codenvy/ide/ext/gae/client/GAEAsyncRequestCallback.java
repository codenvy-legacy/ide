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
package com.codenvy.ide.ext.gae.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Callback that uses to proceed request.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 18, 2012 11:25:43 AM anya $
 */
public abstract class GAEAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
    private ConsolePart     console;
    private EventBus        eventBus;
    private LoginAction     loginAction;
    private GAELocalization constant;

    /**
     * Construct of the callback.
     */
    public GAEAsyncRequestCallback(Unmarshallable<T> unmarshaller, ConsolePart console, EventBus eventBus,
                                   GAELocalization constant, LoginAction loginAction) {
        super(unmarshaller);
        this.console = console;
        this.constant = constant;
        this.eventBus = eventBus;
        this.loginAction = loginAction;
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof UnauthorizedException) {
            if (loginAction != null) {
                loginAction.doLogin();
            }
            return;
        }
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (serverException.getMessage() != null) {
                console.print(serverException.getMessage());
            } else {
                console.print(constant.unknownErrorMessage());
            }
        } else {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
            console.print(exception.getMessage());
        }
    }
}