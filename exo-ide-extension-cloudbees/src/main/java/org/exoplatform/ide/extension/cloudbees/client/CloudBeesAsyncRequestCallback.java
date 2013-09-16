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
package org.exoplatform.ide.extension.cloudbees.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginEvent;

/**
 * Asynchronous CloudBees request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception,
 * in this case - the {@link LoginEvent} is fired.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: HerokuAsyncRequestCallback.java Jun 24, 2011 2:27:50 PM vereshchaka $
 * @see CloudBeesRESTfulRequestCallback
 */
public abstract class CloudBeesAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    public CloudBeesAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                         LoginCanceledHandler loginCanceled) {
        super(unmarshaller);
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
    }

    public CloudBeesAsyncRequestCallback(LoggedInHandler loggedIn,
                                         LoginCanceledHandler loginCanceled) {
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable) */
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
