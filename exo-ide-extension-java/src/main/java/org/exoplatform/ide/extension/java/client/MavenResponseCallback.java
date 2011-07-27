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
package org.exoplatform.ide.extension.java.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.java.client.marshaller.MavenResponseUnmarshaller.Constants;
import org.exoplatform.ide.extension.java.shared.MavenResponse;

/**
 * Async request callback for maven responses, contains processing errors.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 26, 2011 5:17:01 PM anya $
 *
 */
public abstract class MavenResponseCallback extends AsyncRequestCallback<MavenResponse>
{
   private HandlerManager eventBus;

   public MavenResponseCallback(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
    */
   @Override
   protected void onFailure(Throwable exception)
   {
      if (!(exception instanceof ServerException))
      {
         super.onFailure(exception);
         return;
      }

      if (exception.getMessage() == null || exception.getMessage().isEmpty())
      {
         super.onFailure(exception);
      }
      else
      {
         //Try to get error output from maven response:
         JSONObject jsonObject = null;
         try
         {
            jsonObject = JSONParser.parseStrict(exception.getMessage()).isObject();
         }
         catch (JSONException e)
         {
            super.onFailure(exception);
            return;
         }

         if (jsonObject != null && jsonObject.containsKey(Constants.OUTPUT)
            && jsonObject.get(Constants.OUTPUT).isString() != null)
         {
            String output = jsonObject.get(Constants.OUTPUT).isString().stringValue();
            output = output.replace("\n", "<br>");
            eventBus.fireEvent(new OutputEvent(output, Type.OUTPUT));
         }
         else
         {
            super.onFailure(exception);
         }
      }
   }
}
