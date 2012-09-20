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
package org.exoplatform.ide.resources.marshal;

import com.google.gwt.http.client.Response;

import org.exoplatform.ide.commons.exception.UnmarshallerException;
import org.exoplatform.ide.rest.Unmarshallable;


/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: FileContentUnmarshaller Feb 3, 2011 9:42:13 AM evgen $
 * 
 */
public class StringUnmarshaller implements Unmarshallable<StringBuilder>
{

   private final StringBuilder string;

   public StringUnmarshaller()
   {
      this.string = new StringBuilder();
   }

   @Override
   public StringBuilder getPayload()
   {
      return string;
   }

   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      string.append(response.getText());
   }

}
