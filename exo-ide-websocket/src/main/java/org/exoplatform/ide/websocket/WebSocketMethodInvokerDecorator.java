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
package org.exoplatform.ide.websocket;

import org.everrest.core.ApplicationContext;
import org.everrest.core.impl.method.MethodInvokerDecorator;
import org.everrest.core.method.MethodInvoker;
import org.everrest.core.resource.GenericMethodResource;
import org.everrest.websockets.WSConnection;
import org.exoplatform.services.security.ConversationState;

/**
 * Intended to prepare environment to invoke resource method when request received through web socket connection.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class WebSocketMethodInvokerDecorator extends MethodInvokerDecorator {
    /**
     * @param decoratedInvoker
     *         decorated MethodInvoker
     */
    public WebSocketMethodInvokerDecorator(MethodInvoker decoratedInvoker) {
        super(decoratedInvoker);
    }

    @Override
    public Object invokeMethod(Object resource, GenericMethodResource genericMethodResource, ApplicationContext context) {
        WSConnection wsConnection = (WSConnection)org.everrest.core.impl.EnvironmentContext.getCurrent().get(WSConnection.class);
        if (wsConnection != null && ConversationState.getCurrent() == null) {
            ConversationState.setCurrent((ConversationState)wsConnection.getHttpSession().getAttribute(
                    ExoIdeWebSocketServlet.CONVERSATION_STATE_SESSION_ATTRIBUTE_NAME));
            com.codenvy.commons.env.EnvironmentContext.setCurrent(
                    (com.codenvy.commons.env.EnvironmentContext)wsConnection.getHttpSession().getAttribute(
                            ExoIdeWebSocketServlet.ENVIRONMENT_SESSION_ATTRIBUTE_NAME));
            try {
                return super.invokeMethod(resource, genericMethodResource, context);
            } finally {
                ConversationState.setCurrent(null);
                com.codenvy.commons.env.EnvironmentContext.reset();
            }
        }
        return super.invokeMethod(resource, genericMethodResource, context);
    }
}
