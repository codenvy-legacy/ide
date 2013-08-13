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