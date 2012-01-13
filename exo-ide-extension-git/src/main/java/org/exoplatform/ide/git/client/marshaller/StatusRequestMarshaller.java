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
package org.exoplatform.ide.git.client.marshaller;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.StatusRequest;

/**
 * Marshaller for creation request in JSON format for {@link StatusRequest}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 28, 2011 2:32:53 PM anya $
 * 
 */
public class StatusRequestMarshaller implements Marshallable, Constants
{

   /**
    * Status request.
    */
   private StatusRequest statusRequest;

   /**
    * @param statusRequest
    */
   public StatusRequestMarshaller(StatusRequest statusRequest)
   {
      this.statusRequest = statusRequest;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(SHORT_FORMAT, JSONBoolean.getInstance(statusRequest.isShortFormat()));
      if (statusRequest.getFileFilter() != null && statusRequest.getFileFilter().length > 0)
      {
         JSONArray filterArray = new JSONArray();
         for (int i = 0; i < statusRequest.getFileFilter().length; i++)
         {
            filterArray.set(i, new JSONString(statusRequest.getFileFilter()[i]));
         }
         jsonObject.put(FILE_FILTER, filterArray);
      }
      return jsonObject.toString();
   }
}
