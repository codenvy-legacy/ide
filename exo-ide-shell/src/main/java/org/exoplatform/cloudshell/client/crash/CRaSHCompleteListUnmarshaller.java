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

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.HashMap;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 2, 2011 2:26:48 PM anya $
 *
 */
public class CRaSHCompleteListUnmarshaller implements Unmarshallable
{
   private HashMap<String, String> completeList;

   public CRaSHCompleteListUnmarshaller(HashMap<String, String> completeList)
   {
      this.completeList = completeList;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();
         if (jsonObject .keySet() == null || jsonObject .keySet().size() <= 0)
         {
            return;
         }
         
         for (String key : jsonObject.keySet())
         {
            completeList.put(key, jsonObject.get(key).isString().stringValue());
         }   
         
      } catch (Exception e) {
         e.printStackTrace();
         //TODO
         throw new UnmarshallerException("");
      }
   }

}
