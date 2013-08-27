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
