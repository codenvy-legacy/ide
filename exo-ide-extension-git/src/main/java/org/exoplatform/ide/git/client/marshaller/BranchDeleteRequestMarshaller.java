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

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.BranchDeleteRequest;

/**
 * Marshaller to create branch delete request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 11, 2011 12:10:34 PM anya $
 *
 */
public class BranchDeleteRequestMarshaller implements Marshallable, Constants
{
   /**
    * Branch delete request.
    */
   private BranchDeleteRequest branchDeleteRequest;

   /**
    * @param branchDeleteRequest branch delete request
    */
   public BranchDeleteRequestMarshaller(BranchDeleteRequest branchDeleteRequest)
   {
      this.branchDeleteRequest = branchDeleteRequest;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(NAME, new JSONString(branchDeleteRequest.getName()));
      jsonObject.put(FORCE, JSONBoolean.getInstance(branchDeleteRequest.isForce()));
     
      return jsonObject.toString();
   }
}
