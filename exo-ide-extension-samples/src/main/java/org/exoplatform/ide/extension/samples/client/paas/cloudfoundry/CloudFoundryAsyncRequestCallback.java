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
package org.exoplatform.ide.extension.samples.client.paas.cloudfoundry;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.samples.client.SamplesClientService;
import org.exoplatform.ide.extension.samples.client.paas.login.LoggedInHandler;
import org.exoplatform.ide.extension.samples.client.paas.login.LoginEvent;

/**
 * Asynchronous CloudFoundry request. The {{@link #onFailure(Throwable)} method contains the check for user not authorized
 * exception, in this case - the {@link LoginEvent} is fired.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryAsyncRequestCallback.java Jul 8, 2011 3:36:01 PM vereshchaka $
 */
public abstract class CloudFoundryAsyncRequestCallback<T> extends AsyncRequestCallback<T>
{
   private LoggedInHandler loggedIn;

   public CloudFoundryAsyncRequestCallback(Unmarshallable<T> unmarshallable, LoggedInHandler loggedIn)
   {
      super(unmarshallable);
      this.loggedIn = loggedIn;
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
            && serverException.getMessage().contains("Authentication required."))
         {
            IDE.fireEvent(new LoginEvent(SamplesClientService.Paas.CLOUDFOUNDRY, loggedIn));
            return;
         }
         else
         {

            String msg = "";
            if (serverException.isErrorMessageProvided())
            {
               msg = serverException.getLocalizedMessage();
               if (msg.startsWith("{") && msg.endsWith("}"))
               {
                  msg = fromJson(msg);
               }
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

   private static String fromJson(String jsonMsg)
   {
      JSONObject jsonObject = JSONParser.parseStrict(jsonMsg).isObject();
      if (jsonObject == null)
         return jsonMsg;

      String result = "";

      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (jsonValue.isString() != null)
         {
            result += key + " : " + jsonValue.isString().stringValue() + "<br>";
         }
         else if (jsonValue.isNumber() != null)
         {
            result += key + " : " + (int)jsonValue.isNumber().doubleValue() + "<br>";
         }
      }

      return result;
   }
}
