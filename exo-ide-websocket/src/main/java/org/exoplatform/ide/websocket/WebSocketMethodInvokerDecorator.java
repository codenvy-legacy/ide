/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
