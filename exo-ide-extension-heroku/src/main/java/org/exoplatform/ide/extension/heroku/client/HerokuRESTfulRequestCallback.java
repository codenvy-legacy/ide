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
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.login.LoginEvent;
import org.exoplatform.ide.extension.heroku.client.marshaller.ApplicationInfoUnmarshallerWS;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket Heroku request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception, in
 * this case - the {@link LoginEvent} is fired.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: HerokuRESTfulRequestCallback.java Nov 30, 2012 2:00:07 PM azatsarynnyy $
 * @see HerokuAsyncRequestCallback
 */
public abstract class HerokuRESTfulRequestCallback extends RequestCallback<List<Property>> {
    private LoggedInHandler loggedInHandler;

    public HerokuRESTfulRequestCallback(LoggedInHandler handler) {
        super(new ApplicationInfoUnmarshallerWS(new ArrayList<Property>()));
        this.loggedInHandler = handler;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.RequestCallback#onFailure(java.lang.Throwable) */
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
