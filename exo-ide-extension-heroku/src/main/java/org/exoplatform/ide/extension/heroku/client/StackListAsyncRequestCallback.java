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
import org.exoplatform.ide.extension.heroku.client.marshaller.StackListUnmarshaller;
import org.exoplatform.ide.extension.heroku.shared.Stack;

import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous callback for stack list response.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 29, 2011 10:16:33 AM anya $
 */
public abstract class StackListAsyncRequestCallback extends AsyncRequestCallback<List<Stack>> {
    /** Handler of the {@link LoggedInEvent}. */
    private LoggedInHandler loggedInHandler;

    /**
     * @param eventBus
     *         event handlers manager
     * @param handler
     *         handler of the {@link LoggedInEvent}
     */
    public StackListAsyncRequestCallback(LoggedInHandler loggedInHandler) {
        super(new StackListUnmarshaller(new ArrayList<Stack>()));
        this.loggedInHandler = loggedInHandler;
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
