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
import org.exoplatform.ide.extension.heroku.client.marshaller.StackMigrationResponse;
import org.exoplatform.ide.extension.heroku.client.marshaller.StackMigrationUnmarshaller;

/**
 * Asynchronous callback on migrate stack response.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 1, 2011 4:10:31 PM anya $
 */
public abstract class StackMigrationAsyncRequestCallback extends AsyncRequestCallback<StackMigrationResponse> {
    /** Handler of the {@link LoggedInEvent}. */
    private LoggedInHandler loggedInHandler;

    /**
     * @param eventBus
     *         event handlers manager
     * @param handler
     *         handler of the {@link LoggedInEvent}
     */
    public StackMigrationAsyncRequestCallback(LoggedInHandler handler) {
        super(new StackMigrationUnmarshaller(new StackMigrationResponse()));
        this.loggedInHandler = handler;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable) */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
                && serverException.getMessage().contains("Authentication required")) {
                IDE.addHandler(LoggedInEvent.TYPE, loggedInHandler);
                IDE.fireEvent(new LoginEvent());
                return;
            }
        }
        IDE.fireEvent(new ExceptionThrownEvent(exception));
    }

}
