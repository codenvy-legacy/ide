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

import com.google.gwt.json.client.JSONObject;

import com.google.gwt.core.client.JavaScriptObject;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.git.shared.Branch;

/**
 * Unmarshaller for {@link Branch} in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 11, 2011 12:29:46 PM anya $
 * 
 */
public class BranchUnmarshaller extends JSONUmarshaller
{
   /**
    * Branch.
    */
   private Branch branch;

   /**
    * @param branch branch
    */
   public BranchUnmarshaller(Branch branch)
   {
      this.branch = branch;
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

      JSONObject object = new JSONObject(json).isObject();
      if (object == null)
         return;
      if (object.containsKey(ACTIVE))
      {
         boolean active =
            (object.get(ACTIVE).isBoolean() != null) ? object.get(ACTIVE).isBoolean().booleanValue() : false;
         branch.setActive(active);
      }
      if (object.containsKey(NAME))
      {
         String name = (object.get(NAME).isString() != null) ? object.get(NAME).isString().stringValue() : "";
         branch.setName(name);
      }
      if (object.containsKey(DISPLAY_NAME))
      {
         String displayName =
            (object.get(DISPLAY_NAME).isString() != null) ? object.get(DISPLAY_NAME).isString().stringValue() : "";
         branch.setDisplayName(displayName);
      }
   }
}
