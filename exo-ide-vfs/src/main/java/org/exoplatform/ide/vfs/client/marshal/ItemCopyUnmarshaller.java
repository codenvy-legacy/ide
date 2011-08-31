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
package org.exoplatform.ide.vfs.client.marshal;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 26, 2011 evgen $
 *
 */
public class ItemCopyUnmarshaller implements Unmarshallable<StringBuilder>
{

   private StringBuilder location;

   /**
    * @param location
    */
   public ItemCopyUnmarshaller(StringBuilder location)
   {
      super();
      this.location = location;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      location.append(response.getHeader("Location"));
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public StringBuilder getPayload()
   {
      return location;
   }

}
