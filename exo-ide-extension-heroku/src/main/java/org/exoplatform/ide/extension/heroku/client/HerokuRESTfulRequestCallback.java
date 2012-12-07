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
package org.exoplatform.ide.extension.heroku.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.login.LoginEvent;
import org.exoplatform.ide.extension.heroku.client.marshaller.ApplicationInfoUnmarshallerWS;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket Heroku request. The {@link #onFailure(Throwable)} method contains the check for user not authorized exception, in
 * this case - the {@link LoginEvent} is fired.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: HerokuRESTfulRequestCallback.java Nov 30, 2012 2:00:07 PM azatsarynnyy $
 *
 * @see HerokuAsyncRequestCallback
 */
public abstract class HerokuRESTfulRequestCallback extends RequestCallback<List<Property>>
{
   private LoggedInHandler loggedInHandler;

   public HerokuRESTfulRequestCallback(LoggedInHandler handler)
   {
      super(new ApplicationInfoUnmarshallerWS(new ArrayList<Property>()));
      this.loggedInHandler = handler;
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.rest.RequestCallback#onFailure(java.lang.Throwable)
    */
   @Override
   protected void onFailure(Throwable exception)
   {
      if (exception instanceof ServerException)
      {
         ServerException serverException = (ServerException)exception;
         if ((HTTPStatus.OK == serverException.getHTTPStatus() || HTTPStatus.INTERNAL_ERROR == serverException
            .getHTTPStatus())
            && serverException.getMessage() != null
            && serverException.getMessage().contains("Authentication required"))
         {
            IDE.addHandler(LoggedInEvent.TYPE, loggedInHandler);
            IDE.fireEvent(new LoginEvent());
            return;
         }
      }
      IDE.fireEvent(new ExceptionThrownEvent(exception));
   }
}
