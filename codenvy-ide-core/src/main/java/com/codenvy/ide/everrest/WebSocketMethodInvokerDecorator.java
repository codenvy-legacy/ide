/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
