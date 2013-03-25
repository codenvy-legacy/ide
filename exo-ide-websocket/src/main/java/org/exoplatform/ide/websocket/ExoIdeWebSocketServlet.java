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
import org.apache.catalina.websocket.StreamInbound;
import org.everrest.core.DependencySupplier;
import org.everrest.core.ResourceBinder;
import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.EverrestProcessor;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.impl.RequestDispatcher;
import org.everrest.core.impl.RequestHandlerImpl;
import org.everrest.core.impl.async.AsynchronousJobPool;
import org.everrest.websockets.EverrestWebSocketServlet;
import org.everrest.websockets.WSConnectionImpl;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.ConversationState;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.ContextResolver;

/**
 * Servlet used for processing requests to Everrest over WebSocket connections.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ExoIdeWebSocketServlet.java Nov 7, 2012 4:29:51 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class ExoIdeWebSocketServlet extends EverrestWebSocketServlet
{
   static final String CONVERSATION_STATE_SESSION_ATTRIBUTE_NAME = "ide.websocket." + ConversationState.class.getName();
   static final String ENVIRONMENT_SESSION_ATTRIBUTE_NAME = "ide.websocket." + EnvironmentContext.class.getName();

   @Override
   protected EverrestProcessor getEverrestProcessor()
   {
      final ExoContainer container = ExoContainerContext.getCurrentContainer();
      ResourceBinder resources = ((ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class));
      RequestDispatcher dispatcher = ((RequestDispatcher)container.getComponentInstanceOfType(RequestDispatcher.class));
      DependencySupplier dependencies = ((DependencySupplier)container.getComponentInstanceOfType(DependencySupplier.class));
      EverrestConfiguration config = new EverrestConfiguration();
      config.setProperty(EverrestConfiguration.METHOD_INVOKER_DECORATOR_FACTORY,
         WebSocketMethodInvokerDecoratorFactory.class.getName());
      ProviderBinder providers = ProviderBinder.getInstance();
      return new EverrestProcessor(new RequestHandlerImpl(dispatcher, providers, dependencies, config),
         resources,
         providers,
         null);
   }

   @Override
   protected AsynchronousJobPool getAsynchronousJobPool()
   {
      ProviderBinder providers = ProviderBinder.getInstance();
      if (providers != null)
      {
         ContextResolver<AsynchronousJobPool> asyncJobsResolver =
            providers.getContextResolver(AsynchronousJobPool.class, null);
         if (asyncJobsResolver != null)
         {
            return asyncJobsResolver.getContext(null);
         }
      }
      throw new IllegalStateException(
         "Unable get web socket connection. Asynchronous jobs feature is not configured properly. ");
   }

   @Override
   protected StreamInbound createWebSocketInbound(String s, HttpServletRequest req)
   {
      WSConnectionImpl wsConnection = (WSConnectionImpl)super.createWebSocketInbound(s, req);
      ConversationState conversationState = ConversationState.getCurrent();
      wsConnection.getHttpSession().setAttribute(CONVERSATION_STATE_SESSION_ATTRIBUTE_NAME, conversationState);
      EnvironmentContext environmentContext = EnvironmentContext.getCurrent();
      wsConnection.getHttpSession().setAttribute(ENVIRONMENT_SESSION_ATTRIBUTE_NAME, environmentContext);
      return wsConnection;
   }
}
