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

import com.codenvy.commons.env.EnvironmentContext;

import org.apache.catalina.websocket.StreamInbound;
import org.everrest.core.DependencySupplier;
import org.everrest.core.ResourceBinder;
import org.everrest.core.impl.*;
import org.everrest.core.impl.async.AsynchronousJobPool;
import org.everrest.websockets.EverrestWebSocketServlet;
import org.everrest.websockets.WSConnectionImpl;
import org.everrest.websockets.message.*;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.ConversationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.ContextResolver;
import java.lang.reflect.Method;

/**
 * Servlet used for processing requests to Everrest over WebSocket connections.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ExoIdeWebSocketServlet.java Nov 7, 2012 4:29:51 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class ExoIdeWebSocketServlet extends EverrestWebSocketServlet {
    static final         String CONVERSATION_STATE_SESSION_ATTRIBUTE_NAME = "ide.websocket." + ConversationState.class.getName();
    static final         String ENVIRONMENT_SESSION_ATTRIBUTE_NAME        = "ide.websocket." + EnvironmentContext.class.getName();
    static final         String MDC_CONTEXT_ATTRIBUTE_NAME                = "ide.websocket.slf4j.mdc.context";
    private static final Logger LOG                                       = LoggerFactory.getLogger(ExoIdeWebSocketServlet.class);
    private static Method mdc_getCopyOfContextMap;

    static {
        try {
            Class<?> c = Thread.currentThread().getContextClassLoader().loadClass("org.slf4j.MDC");
            mdc_getCopyOfContextMap = c.getDeclaredMethod("getCopyOfContextMap");
        } catch (Exception ignore) {
        }
    }

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
            ContextResolver<AsynchronousJobPool> asyncJobsResolver = providers.getContextResolver(AsynchronousJobPool.class, null);
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
        if (mdc_getCopyOfContextMap != null) {
            try {
                wsConnection.getHttpSession().setAttribute(MDC_CONTEXT_ATTRIBUTE_NAME, mdc_getCopyOfContextMap.invoke(null));
            } catch (Throwable t) {
                LOG.error(t.getMessage(), t);
            }
        }
        return wsConnection;
    }
}
