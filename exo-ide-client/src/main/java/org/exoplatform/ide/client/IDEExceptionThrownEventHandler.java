/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerDisconnectedException;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.exception.UnauthorizedException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEExceptionThrownEventHandler
{

   public IDEExceptionThrownEventHandler(ExceptionThrownEvent event)
   {
      Log.info("IDEExceptionThrownEventHandler.IDEExceptionThrownEventHandler()");
      
      Throwable error = event.getException();
      Log.info(event.getErrorMessage());
      
      if (error instanceof UnauthorizedException) {
         return;
      }

      if (error instanceof ServerDisconnectedException)
      {
         showServerDisconnectedDialog((ServerDisconnectedException)error);
         return;
      }

      if (error instanceof ServerException)
      {
         ServerException serverException = (ServerException)error;
         if (serverException.isErrorMessageProvided())
         {
            String html =
               "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText() + "<br><br><hr><br>"
                  + serverException.getMessage();
            Dialogs.getInstance().showError(html);
         }
         else
         {
            String html = "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();
            if (event.getErrorMessage() != null)
            {
               html += "<br><hr><br>Possible reasons:<br>" + event.getErrorMessage();
            }
            Dialogs.getInstance().showError(html);
         }
      }
      else
      {
         if (error != null)
            Dialogs.getInstance().showError(error.getMessage());
         else
            Dialogs.getInstance().showError(event.getErrorMessage());
      }
   }


   private void showServerDisconnectedDialog(final ServerDisconnectedException exception)
   {
      Log.info("Displays Server Disconnected Dialog....");
      
      String message = IDE.IDE_LOCALIZATION_CONSTANT.serverDisconnected();
      Dialogs.getInstance().ask("IDE", message, new BooleanValueReceivedHandler()
      {
         @Override
         public void booleanValueReceived(Boolean value)
         {
            if (value != null && value == true)
            {
               AsyncRequest asyncRequest = exception.getAsyncRequest();
               if (asyncRequest != null)
               {
                  Log.info("call  < asyncRequest.sendRequest(); > from ServerDisconnectedDialog ");
                  asyncRequest.sendRequest();
               }
            }
         }
      });
   }

}
