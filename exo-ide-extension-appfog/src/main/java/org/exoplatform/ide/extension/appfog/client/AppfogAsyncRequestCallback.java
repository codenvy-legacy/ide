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
package org.exoplatform.ide.extension.appfog.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.appfog.client.login.LoginEvent;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public abstract class AppfogAsyncRequestCallback<T> extends AsyncRequestCallback<T>
{
   private LoggedInHandler loggedIn;

   private LoginCanceledHandler loginCanceled;

   private String loginUrl;

   private final static String APPFOG_EXIT_CODE = "Cloudfoundry-Exit-Code";

   public AppfogAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                     LoginCanceledHandler loginCanceled)
   {
      this(unmarshaller, loggedIn, loginCanceled, null);
   }

   public AppfogAsyncRequestCallback(Unmarshallable<T> unmarshaller, LoggedInHandler loggedIn,
                                     LoginCanceledHandler loginCanceled, String loginUrl)
   {
      super(unmarshaller);
      this.loggedIn = loggedIn;
      this.loginCanceled = loginCanceled;
      this.loginUrl = loginUrl;
   }

   @Override
   protected void onFailure(Throwable exception)
   {
      if (exception instanceof ServerException)
      {
         ServerException serverException = (ServerException)exception;
         if (HTTPStatus.OK == serverException.getHTTPStatus() && serverException.getMessage() != null
            && serverException.getMessage().contains("Authentication required."))
         {
            IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled, loginUrl));
            return;
         }
         else if (HTTPStatus.FORBIDDEN == serverException.getHTTPStatus()
            && serverException.getHeader(APPFOG_EXIT_CODE) != null
            && "200".equals(serverException.getHeader(APPFOG_EXIT_CODE)))
         {
            IDE.fireEvent(new LoginEvent(loggedIn, loginCanceled, loginUrl));
            return;
         }
         else if (HTTPStatus.NOT_FOUND == serverException.getHTTPStatus()
            && serverException.getHeader(APPFOG_EXIT_CODE) != null
            && "301".equals(serverException.getHeader(APPFOG_EXIT_CODE)))
         {
            Dialogs.getInstance().showError(AppfogExtension.LOCALIZATION_CONSTANT.applicationNotFound());
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
            Dialogs.getInstance().showError(msg);
            return;
         }
      }
      IDE.fireEvent(new ExceptionThrownEvent(exception));
   }
}
