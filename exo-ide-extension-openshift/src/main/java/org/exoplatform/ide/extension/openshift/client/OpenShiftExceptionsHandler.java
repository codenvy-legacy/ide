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
package org.exoplatform.ide.extension.openshift.client;

import org.exoplatform.gwtframework.commons.rest.copy.ServerException;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Handler for OpenShift exceptions. Error, handled by {@link OpenShiftExceptionsHandler} is passed with
 * {@link OpenShiftExceptionThrownEvent} event. Checks whether it is {@link ServerException} and contains provided message and
 * Express exit code. Alternative error message also can be passed by {@link OpenShiftExceptionThrownEvent}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 10, 2011 5:08:56 PM anya $
 * 
 */
public class OpenShiftExceptionsHandler implements OpenShiftExceptionThrownHandler
{
   /**
    *
    */
   public OpenShiftExceptionsHandler()
   {
      IDE.addHandler(OpenShiftExceptionThrownEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownHandler#onOpenShiftExceptionThrown(org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent)
    */
   @Override
   public void onOpenShiftExceptionThrown(OpenShiftExceptionThrownEvent event)
   {
      Throwable error = event.getException();

      if (error instanceof ServerException)
      {
         ServerException serverException = (ServerException)error;
         String exitCode = serverException.getHeader("Express-Exit-Code");
         String expressExitCodeMessage =
            (exitCode != null) ? "<br>" + OpenShiftExtension.LOCALIZATION_CONSTANT.expressExitCode(exitCode) : "";

         if (serverException.isErrorMessageProvided())
         {
            String html =
               "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText() + "<br><br><hr><br>"
                  + serverException.getMessage() + expressExitCodeMessage;
            Dialogs.getInstance().showError(html);
         }
         else
         {
            String html = "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();

            if (event.getErrorMessage() != null)
            {
               html += "<br><hr><br>Possible reasons:<br>" + event.getErrorMessage() + expressExitCodeMessage;
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

}
