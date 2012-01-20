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

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.git.shared.GitFile;
import org.exoplatform.ide.git.shared.GitFile.FileStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Unmarshaller for the Git work tree status response.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 11:11:27 AM anya $
 * 
 */
public class StatusResponseUnmarshaller implements Unmarshallable<StatusResponse>, Constants
{
   /**
    * The status response.
    */
   private StatusResponse statusResponse;

   /**
    * Indicates whether response is in text format.
    */
   private boolean textFormat;

   /**
    * @param statusResponse status response
    * @param textFormat text format of the response or not
    */
   public StatusResponseUnmarshaller(StatusResponse statusResponse, boolean textFormat)
   {
      this.statusResponse = statusResponse;
      this.textFormat = textFormat;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      if (response.getText() == null || response.getText().isEmpty())
      {
         return;
      }

      if (textFormat)
      {
         statusResponse.setWorkTreeStatus(response.getText());
         return;
      }

      JSONObject statusObject = JSONParser.parseStrict(response.getText()).isObject();
      if (statusObject == null)
         return;

      statusResponse.setChangedNotCommited(getValuesByKey(CHANGED_NOT_COMMITED, statusObject));
      statusResponse.setChangedNotUpdated(getValuesByKey(CHANGED_NOT_UPDATED, statusObject));
      statusResponse.setUntracked(getValuesByKey(UNTRACKED, statusObject));

   }

   /**
    * Parse JSON object's property of array type to list, by given property name.
    * 
    * @param key
    * @param jsonObject
    * @return {@link List}
    */
   private List<GitFile> getValuesByKey(String key, JSONObject jsonObject)
   {
      List<GitFile> values = new ArrayList<GitFile>();
      if (jsonObject.containsKey(key))
      {
         JSONArray array = jsonObject.get(key).isArray();
         if (array == null || array.size() <= 0)
            return values;

         for (int i = 0; i < array.size(); i++)
         {
            JSONObject fileObject = array.get(i).isObject();
            if (fileObject != null)
            {
               String path =
                  (fileObject.containsKey(PATH) && fileObject.get(PATH).isString() != null) ? fileObject.get(PATH)
                     .isString().stringValue() : "";
               FileStatus status =
                  (fileObject.containsKey(STATUS) && fileObject.get(STATUS).isString() != null) ? FileStatus
                     .valueOf(fileObject.get(STATUS).isString().stringValue()) : null;
               values.add(new GitFile(path, status));
            }
         }
      }
      return values;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public StatusResponse getPayload()
   {
      return statusResponse;
   }
}
