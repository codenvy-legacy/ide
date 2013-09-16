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
package org.exoplatform.ide.extension.cloudfoundry.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginEvent;

/**
 * Asynchronous CloudFoundry request. The {@link #onFailure(Throwable)} method contains the check for user not authorized
 * exception, in this case - the {@link LoginEvent} is fired.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryAsyncRequestCallback.java Jul 8, 2011 3:36:01 PM vereshchaka $
 * @see CloudFoundryRESTfulRequestCallback
 */
public abstract class CloudFoundryAsyncRequestCallback<T> extends AsyncRequestCallback<T> {
    private LoggedInHandler loggedIn;

    private LoginCanceledHandler loginCanceled;

    private String loginUrl;

    private final static String CLOUDFOUNDRY_EXIT_CODE = "Cloudfoundry-Exit-Code";

    private PAAS_PROVIDER paasProvider;

    public CloudFoundryAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                            LoginCanceledHandler loginCanceled, PAAS_PROVIDER paasProvider) {
        this(unmarshaller, loggedIn, loginCanceled, null, paasProvider);
    }

    public CloudFoundryAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                            LoginCanceledHandler loginCanceled, String loginUrl, PAAS_PROVIDER paasProvider) {
        super(unmarshaller);
        this.loggedIn = loggedIn;
        this.loginCanceled = loginCanceled;
        this.loginUrl = loginUrl;
        this.paasProvider = paasProvider;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback#onFailure(java.lang.Throwable) */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
                && serverException.getMessage().contains("Authentication required.")) {
                IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled, loginUrl, paasProvider));
                return;
            } else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
                       && serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE) != null
                       && "200".equals(serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE))) {
                IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled, loginUrl, paasProvider));
                return;
            } else if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus()
                       && serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE) != null
                       && "301".equals(serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE))) {
                Dialogs.getInstance().showError(CloudFoundryExtension.LOCALIZATION_CONSTANT.applicationNotFound());
                return;
            } else {
                String msg = "";
                if (serverException.isErrorMessageProvided()) {
                    msg = serverException.getLocalizedMessage();
                } else {
                    msg = "Status:&nbsp;" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();
                }
                Dialogs.getInstance().showError(msg);
                return;
            }
        }
        IDE.fireEvent(new ExceptionThrownEvent(exception));
    }

}
