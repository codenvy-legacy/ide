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
package com.codenvy.ide.rest;

import com.codenvy.ide.commons.exception.UnmarshallerException;

import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for "Location" HTTP Header.
 * Uses in {@link AsyncRequest} for run REST Service Asynchronous
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 16, 2011 evgen $
 *
 */
public class LocationUnmarshaller implements Unmarshallable<StringBuilder>
{

   private StringBuilder result;

   /**
    * @param result
    */
   public LocationUnmarshaller(StringBuilder result)
   {
      super();
      this.result = result;
   }

   /**
    * @see com.codenvy.ide.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   public void unmarshal(Response response) throws UnmarshallerException
   {
      result.append(response.getHeader("Location"));
   }

   /**
    * @see com.codenvy.ide.rest.Unmarshallable#getPayload()
    */
   public StringBuilder getPayload()
   {
      return result;
   }

}
