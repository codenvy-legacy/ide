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

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.everrest.CodenvyAsynchronousJobPool;

import org.apache.catalina.websocket.StreamInbound;
import org.everrest.core.DependencySupplier;
import org.everrest.core.ResourceBinder;
import org.everrest.core.impl.*;
import org.everrest.core.impl.async.AsynchronousJobPool;
import org.everrest.websockets.EverrestWebSocketServlet;
import org.everrest.websockets.WSConnectionImpl;
import org.everrest.websockets.message.JsonMessageConverter;
import org.everrest.websockets.message.Message;
import org.everrest.websockets.message.MessageConversionException;
import org.everrest.websockets.message.MessageConverter;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.ContextResolver;

/**
 * Servlet used for processing requests to Everrest over WebSocket connections.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ExoIdeWebSocketServlet.java Nov 7, 2012 4:29:51 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class ExoIdeWebSocketServlet extends EverrestWebSocketServlet {
    private static final Log    LOG                                       = ExoLogger.getLogger(ExoIdeWebSocketServlet.class);
    static final         String CONVERSATION_STATE_SESSION_ATTRIBUTE_NAME = "ide.websocket." + ConversationState.class.getName();
    static final         String ENVIRONMENT_SESSION_ATTRIBUTE_NAME        = "ide.websocket." + EnvironmentContext.class.getName();

    @Override
    protected EverrestProcessor getEverrestProcessor() {
        final ExoContainer container = ExoContainerContext.getCurrentContainer();
        ResourceBinder resources = ((ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class));
        RequestDispatcher dispatcher = ((RequestDispatcher)container.getComponentInstanceOfType(RequestDispatcher.class));
        DependencySupplier dependencies = ((DependencySupplier)container.getComponentInstanceOfType(DependencySupplier.class));
        EverrestConfiguration config = new EverrestConfiguration();
        config.setProperty(EverrestConfiguration.METHOD_INVOKER_DECORATOR_FACTORY, WebSocketMethodInvokerDecoratorFactory.class.getName());
        ProviderBinder providers = ProviderBinder.getInstance();
        return new EverrestProcessor(new RequestHandlerImpl(dispatcher, providers, dependencies, config), resources, providers, null);
    }

    @Override
    public void init() throws ServletException {
        // XXX (IDE-2665) : temporary to see which message from client cause error on server side when try to parse input message.
        getServletContext().setAttribute(MESSAGE_CONVERTER_ATTRIBUTE, new MessageConverter() {
            private final MessageConverter delegate = new JsonMessageConverter();

            @Override
            public <T extends Message> T fromString(String message, Class<T> clazz) throws MessageConversionException {
                try {
                    return delegate.fromString(message, clazz);
                } catch (MessageConversionException e) {
                    LOG.error("Invalid input message: " + message);
                    throw e;
                }
            }

            @Override
            public String toString(Message output) throws MessageConversionException {
                return delegate.toString(output);
            }
        });
        super.init();
    }

    @Override
    protected AsynchronousJobPool getAsynchronousJobPool() {
        ProviderBinder providers = ProviderBinder.getInstance();
        if (providers != null) {
            ContextResolver<CodenvyAsynchronousJobPool> asyncJobsResolver = providers.getContextResolver(CodenvyAsynchronousJobPool.class, null);
            if (asyncJobsResolver != null) {
                return asyncJobsResolver.getContext(null);
            }
        }
        throw new IllegalStateException("Unable get web socket connection. Asynchronous jobs feature is not configured properly. ");
    }

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest req) {
        WSConnectionImpl wsConnection = (WSConnectionImpl)super.createWebSocketInbound(s, req);
        ConversationState conversationState = ConversationState.getCurrent();
        wsConnection.getHttpSession().setAttribute(CONVERSATION_STATE_SESSION_ATTRIBUTE_NAME, conversationState);
        EnvironmentContext environmentContext = EnvironmentContext.getCurrent();
        wsConnection.getHttpSession().setAttribute(ENVIRONMENT_SESSION_ATTRIBUTE_NAME, environmentContext);
        return wsConnection;
    }
}
