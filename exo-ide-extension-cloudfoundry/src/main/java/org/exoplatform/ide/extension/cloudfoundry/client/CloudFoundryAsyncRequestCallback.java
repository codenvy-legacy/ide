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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginEvent;

/**
 * Asynchronous CloudFoundry request.
 * The {{@link #onFailure(Throwable)}} method contains the check for 
 * user not authorized exception, in this case - the {@link LoginEvent} is fired.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryAsyncRequestCallback.java Jul 8, 2011 3:36:01 PM vereshchaka $
 */
public abstract class CloudFoundryAsyncRequestCallback<T> extends AsyncRequestCallback<T>
{
   /**
    * Events handler.
    */
   private HandlerManager eventbus;
   
   private LoggedInHandler loggedIn;
   
   private LoginCanceledHandler loginCanceled;

   public CloudFoundryAsyncRequestCallback(HandlerManager eventBus, LoggedInHandler loggedIn,
      LoginCanceledHandler loginCanceled)
   {
      this.eventbus = eventBus;
      this.loggedIn = loggedIn;
      this.loginCanceled = loginCanceled;
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
                  && serverException.getMessage().contains("Authentication required."))
         {
            eventbus.fireEvent(new LoginEvent(loggedIn, loginCanceled));
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
      super.onFailure(exception);
   }
   
   private static String fromJson(String jsonMsg)
   {
      JavaScriptObject json = build(jsonMsg);
      if (json == null)
         return jsonMsg;
      JSONObject jsonObject = new JSONObject(json).isObject();
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
   
   /**
    * Build {@link JavaScriptObject} from string.
    * 
    * @param json string that contains object
    * @return {@link JavaScriptObject}
    */
   protected static native JavaScriptObject build(String json) /*-{
      try {
         var object = eval('(' + json + ')');
         return object;
      } catch (e) {
         return null;
      }
   }-*/;

}
