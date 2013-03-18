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
package com.codenvy.ide.extension.cloudfoundry.client;

import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginCanceledHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Asynchronous CloudFoundry request. The {@link #onFailure(Throwable)} method contains the check for user not authorized
 * exception, in this case - showDialog method calls on {@link LoginPresenter}.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryAsyncRequestCallback.java Jul 8, 2011 3:36:01 PM vereshchaka $
 * 
 * @see CloudFoundryRESTfulRequestCallback
 */
public abstract class CloudFoundryAsyncRequestCallback<T> extends AsyncRequestCallback<T>
{
   private LoggedInHandler loggedIn;

   private LoginCanceledHandler loginCanceled;

   private String loginUrl;

   private EventBus eventBus;

   private Console console;

   private CloudFoundryLocalizationConstant constant;

   private LoginPresenter loginPresenter;

   private final static String CLOUDFOUNDRY_EXIT_CODE = "Cloudfoundry-Exit-Code";

   /**
    * Create callback.
    * 
    * @param unmarshaller
    * @param loggedIn
    * @param loginCanceled
    * @param eventBus
    * @param console
    * @param constant
    * @param loginPresenter
    */
   public CloudFoundryAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
      LoginCanceledHandler loginCanceled, EventBus eventBus, Console console,
      CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter)
   {
      this(unmarshaller, loggedIn, loginCanceled, null, eventBus, console, constant, loginPresenter);
   }

   /**
    * Create callback.
    * 
    * @param unmarshaller
    * @param loggedIn
    * @param loginCanceled
    * @param loginUrl
    * @param eventBus
    * @param console
    * @param constant
    * @param loginPresenter
    */
   public CloudFoundryAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
      LoginCanceledHandler loginCanceled, String loginUrl, EventBus eventBus, Console console,
      CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter)
   {
      super(unmarshaller);
      this.loggedIn = loggedIn;
      this.loginCanceled = loginCanceled;
      this.loginUrl = loginUrl;
      this.eventBus = eventBus;
      this.console = console;
      this.constant = constant;
      this.loginPresenter = loginPresenter;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void onFailure(Throwable exception)
   {
      if (exception instanceof ServerException)
      {
         ServerException serverException = (ServerException)exception;
         if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
            && serverException.getMessage().contains("Authentication required."))
         {
            loginPresenter.showDialog(loggedIn, loginCanceled, loginUrl);
            return;
         }
         else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
            && serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE) != null
            && "200".equals(serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE)))
         {
            loginPresenter.showDialog(loggedIn, loginCanceled, loginUrl);
            return;
         }
         else if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus()
            && serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE) != null
            && "301".equals(serverException.getHeader(CLOUDFOUNDRY_EXIT_CODE)))
         {
            Window.alert(constant.applicationNotFound());
            return;
         }
         else
         {
            String msg = "";
            if (serverException.isErrorMessageProvided())
            {
               msg = serverException.getLocalizedMessage();
            }
            else
            {
               msg = "Status:&nbsp;" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();
            }

            Window.alert(msg);
            return;
         }
      }

      eventBus.fireEvent(new ExceptionThrownEvent(exception));
      console.print(exception.getMessage());
   }
}