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

import com.google.gwt.json.client.JSONString;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.PushRequest;

/**
 * Marshaller for creating push request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 4, 2011 4:04:27 PM anya $
 *
 */
public class PushRequestMarshaller implements Marshallable, Constants
{

   /**
    * Push request.
    */
   private PushRequest pushRequest;

   /**
    * @param pushRequest push request
    */
   public PushRequestMarshaller(PushRequest pushRequest)
   {
      this.pushRequest = pushRequest;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(REMOTE, new JSONString(pushRequest.getRemote()));
      jsonObject.put(FORCE, JSONBoolean.getInstance(pushRequest.isForce()));
      if (pushRequest.getRefSpec() != null && pushRequest.getRefSpec().length > 0)
      {
         JSONArray array = new JSONArray();
         for (int i = 0; i < pushRequest.getRefSpec().length; i++)
         {
            array.set(i, new JSONString(pushRequest.getRefSpec()[i]));
         }
         jsonObject.put(REF_SPEC, array);
      }
      return jsonObject.toString();
   }
}
