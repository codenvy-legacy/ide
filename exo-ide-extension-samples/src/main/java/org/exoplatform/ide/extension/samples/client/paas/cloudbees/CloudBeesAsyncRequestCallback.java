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
package org.exoplatform.ide.extension.samples.client.paas.cloudbees;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.extension.samples.client.SamplesClientService;
import org.exoplatform.ide.extension.samples.client.paas.login.LoggedInHandler;
import org.exoplatform.ide.extension.samples.client.paas.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.samples.client.paas.login.LoginEvent;

/**
 * Asynchronous CloudBees request. The {{@link #onFailure(Throwable)} method contains the check for user not authorized exception,
 * in this case - the {@link LoginEvent} is fired.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesAsyncRequestCallback.java Sep 12, 2011 6:04:02 PM vereshchaka $
 * 
 * @param <T>
 */
public abstract class CloudBeesAsyncRequestCallback<T> extends AsyncRequestCallback<T>
{
   /**
    * Events handler.
    */
   private HandlerManager eventbus;

   private LoggedInHandler loggedIn;

   private LoginCanceledHandler loginCanceled;

   public CloudBeesAsyncRequestCallback(HandlerManager eventBus, LoggedInHandler loggedIn)
   {
      this(eventBus, loggedIn, null);
   }

   public CloudBeesAsyncRequestCallback(HandlerManager eventBus, LoggedInHandler loggedIn,
      LoginCanceledHandler loginCanceled)
   {
      this.eventbus = eventBus;
      this.loggedIn = loggedIn;
      this.loginCanceled = loginCanceled;
      setEventBus(eventBus);
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
         // because of CloudBees returned not 401 status, but 500 status
         // and explanation, that user not autherised in text message,
         // that's why we must parse text message
         final String exceptionMsg = serverException.getMessage();
         if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus() && serverException.getMessage() != null
            && exceptionMsg.contains("AuthFailure"))
         {
            eventbus.fireEvent(new LoginEvent(SamplesClientService.Paas.CLOUDBEES, loggedIn, loginCanceled));
            return;
         }
      }
      super.onFailure(exception);
   }

}
