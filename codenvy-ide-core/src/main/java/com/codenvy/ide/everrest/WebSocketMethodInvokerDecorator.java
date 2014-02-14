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
package com.codenvy.ide.everrest;

import com.codenvy.commons.env.EnvironmentContext;

import org.everrest.core.ApplicationContext;
import org.everrest.core.impl.method.MethodInvokerDecorator;
import org.everrest.core.method.MethodInvoker;
import org.everrest.core.resource.GenericMethodResource;
import org.everrest.websockets.WSConnection;

/**
 * Intended to prepare environment to invoke resource method when request received through web socket connection.
 *
 * @author andrew00x
 */
class WebSocketMethodInvokerDecorator extends MethodInvokerDecorator {
    WebSocketMethodInvokerDecorator(MethodInvoker decoratedInvoker) {
        super(decoratedInvoker);
    }

    @Override
    public Object invokeMethod(Object resource, GenericMethodResource genericMethodResource, ApplicationContext context) {
        WSConnection wsConnection = (WSConnection)org.everrest.core.impl.EnvironmentContext.getCurrent().get(WSConnection.class);
        if (wsConnection != null) {
            EnvironmentContext.setCurrent(
                    (EnvironmentContext)wsConnection.getHttpSession().getAttribute(CodenvyEverrestWebSocketServlet.ENVIRONMENT_CONTEXT));
            try {
                return super.invokeMethod(resource, genericMethodResource, context);
            } finally {
                EnvironmentContext.reset();
            }
        }
        return super.invokeMethod(resource, genericMethodResource, context);
    }
}
