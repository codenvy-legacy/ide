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
package org.exoplatform.ide.extension.googleappengine.client;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.googleappengine.client.login.GAEOperationFailedEvent;
import org.exoplatform.ide.extension.googleappengine.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.googleappengine.client.login.LoginEvent;
import org.exoplatform.ide.extension.googleappengine.client.login.LoginFailedEvent;
import org.exoplatform.ide.extension.googleappengine.client.login.PerformOperationHandler;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 18, 2012 11:25:43 AM anya $
 * 
 */
public abstract class GoogleAppEngineAsyncRequestCallback<T> extends AsyncRequestCallback<T>
{
   private PerformOperationHandler performOperationHandler;

   private LoginCanceledHandler loginCanceledHandler;

   protected GoogleAppEngineAsyncRequestCallback(Unmarshallable<T> unmarshaller,
      PerformOperationHandler performOperationHandler, LoginCanceledHandler loginCanceledHandler)
   {
      super(unmarshaller);
      this.performOperationHandler = performOperationHandler;
      this.loginCanceledHandler = loginCanceledHandler;
   }

   protected GoogleAppEngineAsyncRequestCallback(PerformOperationHandler performOperationHandler,
      LoginCanceledHandler loginCanceledHandler)
   {
      this.performOperationHandler = performOperationHandler;
      this.loginCanceledHandler = loginCanceledHandler;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
    */
   @Override
   protected void onFailure(Throwable exception)
   {
      if (exception instanceof ServerException)
      {
         ServerException serverException = (ServerException)exception;
         if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus())
         {
            String message = serverException.getMessage();
            if (message.contains("password do not match."))
            {
               IDE.fireEvent(new LoginEvent(performOperationHandler, loginCanceledHandler));
               return;
            }
            else if (message.contains("verify you are a human."))
            {
               IDE.fireEvent(new LoginFailedEvent());
               return;
            }
            else
            {
               IDE.fireEvent(new GAEOperationFailedEvent());
            }
         }
         IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.ERROR));
      }
   }
}
