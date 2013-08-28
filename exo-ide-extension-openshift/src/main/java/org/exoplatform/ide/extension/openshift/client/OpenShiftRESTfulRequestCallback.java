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
