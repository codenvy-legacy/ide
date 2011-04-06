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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.git.shared.Branch;

import java.util.List;

/**
 * Unmarshaller for list of branches.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 5, 2011 2:14:51 PM anya $
 *
 */
public class BranchListUnmarshaller extends JSONUmarshaller
{
   /**
    * List of branches.
    */
   private List<Branch> branches;

   /**
    * @param branches branches
    */
   public BranchListUnmarshaller(List<Branch> branches)
   {
      this.branches = branches;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JavaScriptObject json = build(response.getText());
      if (json == null)
         return;

      JSONArray array = new JSONArray(json);
      if (array == null || array.size() <= 0)
         return;

      for (int i = 0; i < array.size(); i++)
      {
         JSONObject object = array.get(i).isObject();
         if (object == null)
            continue;
         String name = "";
         String displayName = "";
         boolean active = false;
         if (object.containsKey(ACTIVE))
         {
            active = (object.get(ACTIVE).isBoolean() != null) ? object.get(ACTIVE).isBoolean().booleanValue() : false;
         }
         if (object.containsKey(NAME))
         {
            name = (object.get(NAME).isString() != null) ? object.get(NAME).isString().stringValue() : name;
         }
         if (object.containsKey(DISPLAY_NAME))
         {
            displayName =
               (object.get(DISPLAY_NAME).isString() != null) ? object.get(DISPLAY_NAME).isString().stringValue()
                  : displayName;
         }

         branches.add(new Branch(name, active, displayName));
      }
   }

}
