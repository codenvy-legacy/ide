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
package org.exoplatform.ide.extension.heroku.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.login.LoginEvent;
import org.exoplatform.ide.extension.heroku.client.marshaller.ApplicationInfoUnmarshaller;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous Heroku request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception, in
 * this case - the {@link LoginEvent} is fired.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 27, 2011 12:17:17 PM anya $
 * @see HerokuRESTfulRequestCallback
 */
public abstract class HerokuAsyncRequestCallback extends AsyncRequestCallback<List<Property>> {
    private LoggedInHandler loggedInHandler;

    /**
     * @param eventBus
     *         events handler
     */
    public HerokuAsyncRequestCallback(LoggedInHandler handler) {
        super(new ApplicationInfoUnmarshaller(new ArrayList<Property>()));
        this.loggedInHandler = handler;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback#onFailure(java.lang.Throwable) */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if ((HTTPStatus.OK == serverException.getHTTPStatus() || HTTPStatus.INTERNAL_ERROR == serverException
                    .getHTTPStatus())
                && serverException.getMessage() != null
                && serverException.getMessage().contains("Authentication required")) {
                IDE.addHandler(LoggedInEvent.TYPE, loggedInHandler);
                IDE.fireEvent(new LoginEvent());
                return;
            }
        }
        IDE.fireEvent(new ExceptionThrownEvent(exception));
    }
}
