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
package org.exoplatform.ide.extension.appfog.client;

import com.google.gwt.regexp.shared.RegExp;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.appfog.client.login.LoginEvent;

/**
 * WebSocket AppFog request.
 *
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: AppfogRESTfulRequestCallback.java Nov 30, 2012 3:07:27 PM azatsarynnyy $
 * @see AppfogAsyncRequestCallback
 */
public abstract class AppfogRESTfulRequestCallback<T> extends RequestCallback<T> {
    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    private String loginUrl;

    private final static String APPFOG_EXIT_CODE = "Appfog-Exit-Code";

    public AppfogRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                        LoginCanceledHandler loginCanceled) {
        this(unmarshaller, loggedIn, loginCanceled, null);
    }

    public AppfogRESTfulRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                        LoginCanceledHandler loginCanceled, String loginUrl) {
        super(unmarshaller);
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        this.loginUrl = loginUrl;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.RequestCallback#onFailure(java.lang.Throwable) */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
                && serverException.getMessage().contains("Authentication required.")) {
                IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled, loginUrl));
                return;
            } else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
                       && serverException.getHeader(APPFOG_EXIT_CODE) != null
                       && "200".equals(serverException.getHeader(APPFOG_EXIT_CODE))) {
                IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled, loginUrl));
                return;
            } else if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus()
                       && serverException.getHeader(APPFOG_EXIT_CODE) != null
                       && "301".equals(serverException.getHeader(APPFOG_EXIT_CODE))) {
                Dialogs.getInstance().showError(AppfogExtension.LOCALIZATION_CONSTANT.applicationNotFound());
                return;
            } else {
                String msg = "";
                if (serverException.isErrorMessageProvided()) {
                    msg = serverException.getLocalizedMessage();
                    if (RegExp.compile("Application '.+' already exists. Use update or delete.").exec(msg) != null) {
                        msg = "Application already exist on appfog";
                    } else if (RegExp.compile("Unexpected response from service gateway").exec(msg) != null) {
                        msg = "Appfog error: " + msg;
                    }
                } else {
                    msg = "Status:&nbsp;" + serverException.getHTTPStatus();// + "&nbsp;" + serverException.getStatusText();
                }

                Dialogs.getInstance().showError(msg);
                return;
            }
        }
        IDE.fireEvent(new ExceptionThrownEvent(exception));
    }
}
