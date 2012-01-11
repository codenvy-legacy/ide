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

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.MergeResult.MergeStatus;

/**
 * Unmarshaller for merge result in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 20, 2011 12:01:03 PM anya $
 *
 */
public class MergeUnmarshaller implements Unmarshallable, Constants
{
   /**
    * Result of merge operation.
    */
   private Merge merge;

   /**
    * @param merge result of merge operation
    */
   public MergeUnmarshaller(Merge merge)
   {
      this.merge = merge;
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
         if (jsonObject.containsKey(CONFLICTS) && jsonObject.get(CONFLICTS).isArray() != null)
         {
            JSONArray array = jsonObject.get(CONFLICTS).isArray();
            merge.setConflicts(getArray(array));
         }
         if (jsonObject.containsKey(MERGED_COMMITS) && jsonObject.get(MERGED_COMMITS).isArray() != null)
         {
            JSONArray array = jsonObject.get(MERGED_COMMITS).isArray();
            merge.setMergedCommits(getArray(array));
         }
         if (jsonObject.containsKey(MERGE_STATUS) && jsonObject.get(MERGE_STATUS).isString() != null)
         {
            merge.setMergeStatus(MergeStatus.valueOf(jsonObject.get(MERGE_STATUS).isString().stringValue()));
         }
         if (jsonObject.containsKey(NEW_HEAD) && jsonObject.get(NEW_HEAD).isString() != null)
         {
            merge.setNewHead(jsonObject.get(NEW_HEAD).isString().stringValue());
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException(GitExtension.MESSAGES.mergeUnmarshallerFailed());
      }
   }

   /**
    * Get array from JSON array.
    * 
    * @param jsonArray JSON array
    * @return array of {@link String}
    */
   private String[] getArray(JSONArray jsonArray)
   {
      if (jsonArray == null || jsonArray.size() == 0)
      {
         return null;
      }
      String[] array = new String[jsonArray.size()];
      for (int i = 0; i < jsonArray.size(); i++)
      {
         array[i] = jsonArray.get(i).isString().stringValue();
      }
      return array;
   }
}
