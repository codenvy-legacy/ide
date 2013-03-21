/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.everrest;

import com.codenvy.commons.env.EnvironmentContext;

import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.async.AsynchronousJobPool;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;


/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CodenvyAsynchronousJobPool.java Feb 28, 2013 vetal $
 *
 */
@Provider
public class CodenvyAsynchronousJobPool extends AsynchronousJobPool implements ContextResolver<AsynchronousJobPool>
{
   public CodenvyAsynchronousJobPool(EverrestConfiguration config)
   {
      super(config);
   }

   /**
    * @see org.everrest.core.impl.async.AsynchronousJobPool#newCallable(java.lang.Object, java.lang.reflect.Method,
    *      java.lang.Object[])
    */
   @Override
   protected Callable<Object> newCallable(Object resource, Method method, Object[] params)
   {
      return new CallableWrapper(super.newCallable(resource, method, params));
   }

   private static class CallableWrapper implements Callable<Object>
   {
      private final EnvironmentContext envContext;
      private final ConversationState state;
      private final Callable<Object> callable;

      public CallableWrapper(Callable<Object> callable)
      {
         this.callable = callable;
         state = ConversationState.getCurrent();
         envContext = EnvironmentContext.getCurrent();
      }

      @Override
      public Object call() throws Exception
      {
         ConversationState.setCurrent(state == null
            ? new ConversationState(new Identity(IdentityConstants.ANONIM)) : state);
         EnvironmentContext.setCurrent(envContext);
         try
         {
            return callable.call();
         }
         finally
         {
            EnvironmentContext.setCurrent(null);
            ConversationState.setCurrent(null);
         }
      }
   }
}
