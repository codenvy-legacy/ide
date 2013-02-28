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

import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.async.AsynchronousJobPool;
import org.exoplatform.ide.commons.EnvironmentContext;
import org.exoplatform.services.security.ConversationState;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
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
   @SuppressWarnings("unchecked")
   @Override
   protected Callable<Object> newCallable(Object resource, Method method, Object[] params)
   {
      final Callable<Object> callable = super.newCallable(resource, method, params);
      java.lang.reflect.Proxy.newProxyInstance(callable.getClass().getClassLoader(), callable
         .getClass().getInterfaces(), new EnvironmentContextRestoreHandler(callable)).toString();
      return (Callable<Object>)java.lang.reflect.Proxy.newProxyInstance(callable.getClass().getClassLoader(), callable
         .getClass().getInterfaces(), new EnvironmentContextRestoreHandler(callable));
   }

   private static final class EnvironmentContextRestoreHandler implements InvocationHandler
   {
      private final WeakReference<EnvironmentContext> environmentContextHolder;
      private final Callable callable;

      public EnvironmentContextRestoreHandler(Callable callable)
      {
         this.callable = callable;
         // Copy EnvironmentContext from a 'main' thread.
         // EnvironmentContext is very important component for Codenvy IDE environment.
         // Need have initialized it for each asynchronous thread. 
         EnvironmentContext current = EnvironmentContext.getCurrentEnvironment();
         environmentContextHolder = new WeakReference<EnvironmentContext>(current);
      }

      @Override
      public Object invoke(Object proxy, Method theMethod, Object[] theParams) throws Throwable
      {
         if ("call".equals(theMethod.getName()))
         {
            try
            {
               EnvironmentContext saved = environmentContextHolder.get();
               if (saved == null)
               {
                  saved = new EnvironmentContext();
               }
               EnvironmentContext.setCurrentEnvironment(saved);
               // Directly call method 'call' to simplify exception's handling.
               return callable.call();
            }
            finally
            {
               ConversationState.setCurrent(null);
            }
         }
         // Call other methods with reflection. It may be 'hashCode', 'equals' or 'toString' methods.
         return theMethod.invoke(callable, theParams);
      }
   }
}
