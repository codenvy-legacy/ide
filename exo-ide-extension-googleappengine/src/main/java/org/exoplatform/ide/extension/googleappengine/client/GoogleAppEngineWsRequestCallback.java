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
package org.exoplatform.ide.extension.googleappengine.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable;
import org.exoplatform.ide.extension.googleappengine.client.login.LoginEvent;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 18, 2012 11:25:43 AM anya $
 */
public abstract class GoogleAppEngineWsRequestCallback<T> extends RequestCallback<T> {
    public GoogleAppEngineWsRequestCallback() {
    }

    public GoogleAppEngineWsRequestCallback(Unmarshallable<T> unmarshaller) {
        super(unmarshaller);
    }

    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable) */
    @Override
    protected void onFailure(Throwable exception) {
        if (exception instanceof UnauthorizedException) {
            IDE.fireEvent(new LoginEvent());
            return;
        }
        if (exception instanceof ServerException) {
            ServerException serverException = (ServerException)exception;
            if (serverException.getMessage() != null) {
                IDE.fireEvent(new OutputEvent(serverException.getMessage(), Type.ERROR));
            } else {
                IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION.unknownErrorMessage(), Type.ERROR));
            }
        } else {
            IDE.fireEvent(new ExceptionThrownEvent(exception));
        }
    }
}