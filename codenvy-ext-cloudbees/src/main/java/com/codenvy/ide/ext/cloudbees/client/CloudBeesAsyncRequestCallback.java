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
package com.codenvy.ide.ext.cloudbees.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Asynchronous CloudBees request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception, in this
 * case - showDialog method calls on {@link LoginPresenter}.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: HerokuAsyncRequestCallback.java Jun 24, 2011 2:27:50 PM vereshchaka $
 * @see CloudBeesRESTfulRequestCallback
 */
public abstract class CloudBeesAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
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
    public CloudBeesAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                         LoginCanceledHandler loginCanceled, EventBus eventBus, ConsolePart console,
                                         LoginPresenter loginPresenter) {
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
    public CloudBeesAsyncRequestCallback(LoggedInHandler loggedIn, LoginCanceledHandler loginCanceled, EventBus eventBus,
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