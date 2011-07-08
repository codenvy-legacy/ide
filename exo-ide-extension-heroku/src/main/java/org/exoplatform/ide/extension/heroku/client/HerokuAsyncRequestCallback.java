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
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

import java.util.List;

/**
 * Asynchronous Heroku request.
 * The {{@link #onFailure(Throwable)}} method contains the check for 
 * user not authorized exception, in this case - the {@link LoginEvent} is fired.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 27, 2011 12:17:17 PM anya $
 *
 */
public abstract class HerokuAsyncRequestCallback extends AsyncRequestCallback<List<Property>>
{
   /**
    * Events handler.
    */
   private HandlerManager eventbus;

   private LoggedInHandler loggedInHandler;

   /**
    * @param eventBus events handler
    */
   public HerokuAsyncRequestCallback(HandlerManager eventBus, LoggedInHandler handler)
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
         if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
            && serverException.getMessage().contains("Authentication required"))
         {
            eventbus.addHandler(LoggedInEvent.TYPE, loggedInHandler);
            eventbus.fireEvent(new LoginEvent());
            return;
         }
      }
      super.onFailure(exception);
   }

}
