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
import org.exoplatform.ide.git.shared.AddRequest;

/**
 * Marshaller for add changes to index request.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 29, 2011 5:27:47 PM anya $
 *
 */
public class AddRequestMarshaller implements Marshallable, Constants
{
   /**
    * Add changes to index request.
    */
   private AddRequest addRequest;

   /**
    * @param addRequest add changes to index request
    */
   public AddRequestMarshaller(AddRequest addRequest)
   {
      this.addRequest = addRequest;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(UPDATE, JSONBoolean.getInstance(addRequest.isUpdate()));
      if (addRequest.getFilepattern() != null && addRequest.getFilepattern().length > 0)
      {
         JSONArray filePatternArray = new JSONArray();
         for (int i = 0; i < addRequest.getFilepattern().length; i++)
         {
            filePatternArray.set(i, new JSONString(addRequest.getFilepattern()[i]));
         }
         jsonObject.put(FILE_PATTERN, filePatternArray);
      }
      return jsonObject.toString();
   }

}
