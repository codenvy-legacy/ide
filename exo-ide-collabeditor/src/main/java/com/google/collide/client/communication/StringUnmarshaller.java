/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.google.collide.client.communication;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulResponseMessage;
import org.exoplatform.ide.client.framework.websocket.messages.Unmarshallable;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class StringUnmarshaller implements Unmarshallable<StringBuilder>
{

   private StringBuilder message;

   /**
    * @param message
    */
   public StringUnmarshaller(StringBuilder message)
   {
      super();
      this.message = message;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void unmarshal(RESTfulResponseMessage response) throws UnmarshallerException
   {
      message.append(response.getBody());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public StringBuilder getPayload()
   {
      return message;
   }

}
