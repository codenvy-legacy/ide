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