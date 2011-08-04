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
package org.exoplatform.cloudshell.client.crash;

import org.exoplatform.cloudshell.client.CloudShell;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import java.util.HashMap;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 2, 2011 2:41:44 PM anya $
 *
 */
public abstract class CRaSHCompleteListAsyncRequestCallback extends AsyncRequestCallback<HashMap<String, String>>
{
   /**
    * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
    */
   @Override
   protected void onFailure(Throwable exception)
   {
      if (exception instanceof ServerException)
      {
         ServerException serverException = (ServerException)exception;
         if (serverException.isErrorMessageProvided())
         {
            CloudShell.console().print(serverException.getMessage());
         }
         else
         {
            //TODO
         }
      }
      else
      {
         //TODO
      }
   }

}
