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
import org.exoplatform.ide.git.shared.InitRequest;

/**
 * Marshaller for creation request in JSON format for {@link InitRequest}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 11:48:40 AM anya $
 * 
 */
public class InitRequestMarshaller implements Marshallable, Constants
{
   /**
    * Initialize repository request.
    */
   private InitRequest initRequest;

   /**
    * @param initRequest initialize repository request
    */
   public InitRequestMarshaller(InitRequest initRequest)
   {
      this.initRequest = initRequest;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(WORKNG_DIR, new JSONString(initRequest.getWorkingDir()));
      jsonObject.put(BARE, JSONBoolean.getInstance(initRequest.isBare()));
      return jsonObject.toString();
   }

}
