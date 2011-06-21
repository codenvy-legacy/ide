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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.login.LoginEvent;
import org.exoplatform.ide.extension.heroku.client.rake.RakeCommandResult;

/**
 * Asynchronous Heroku request for executing rake commands.
 * The {{@link #onFailure(Throwable)}} method contains the check for 
 * user not authorized exception, in this case - the {@link LoginEvent} is fired.
 * The returned result is {@link RakeCommandResult}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 20, 2011 9:42:51 AM anya $
 *
 */
public abstract class RakeCommandAsyncRequestCallback extends AsyncRequestCallback<RakeCommandResult>
{
   /**
    * Events handler.
    */
   private HandlerManager eventbus;

   /**
    * Handler of the {@link LoggedInEvent}.
    */
   private LoggedInHandler loggedInHandler;

   /**
    * @param eventBus event handlers manager
    * @param handler handler of the {@link LoggedInEvent}
    */
   public RakeCommandAsyncRequestCallback(HandlerManager eventBus, LoggedInHandler handler)
   {
      this.eventbus = eventBus;
      this.loggedInHandler = handler;
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
         if (HTTPStatus.UNAUTHORIZED == serverException.getHTTPStatus())
         {
            eventbus.addHandler(LoggedInEvent.TYPE, loggedInHandler);
            eventbus.fireEvent(new LoginEvent());
            return;
         }
      }
      super.onFailure(exception);
   }
}
