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

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEExceptionThrownEventHandler implements ExceptionThrownHandler
{

   public static void handlerEvent(ExceptionThrownEvent event)
   {
      Throwable error = event.getError();
      System.out.println(event.getErrorMessage());
      //      error.printStackTrace();

      if (error instanceof ServerException)
      {
         ServerException serverException = (ServerException)error;

         System.out.println("IDEExceptionThrownEventHandler.handlerEvent() server exc");
         if (serverException.isErrorMessageProvided())
         {
            System.out.println(">>> err msg prov");
            String html =
               "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText() + "<br><br><hr><br>"
                  + serverException.getMessage();
            Dialogs.getInstance().showError(html);
         }
         else
         {
            System.out.println(">>> err msg doesn't prov");
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
         System.out.println("IDEExceptionThrownEventHandler.handlerEvent() not server exc");
         if (error != null)
            Dialogs.getInstance().showError(error.getMessage());
         else
            Dialogs.getInstance().showError(event.getErrorMessage());
      }
   }

   /* 
    * Handler of any errors which throws by application
    * 
    * (non-Javadoc)
    * @see org.exoplatform.gwt.commons.exceptions.ExceptionThrownHandler#onError(org.exoplatform.gwt.commons.exceptions.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlerEvent(event);
   }

}
