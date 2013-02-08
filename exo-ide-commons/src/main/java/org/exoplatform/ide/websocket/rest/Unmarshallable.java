/**
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
 *
 */

package org.exoplatform.ide.websocket.rest;

import org.exoplatform.ide.commons.exception.UnmarshallerException;

/**
 * Deserializer for the body of the {@link ResponseMessage}.
 * 
 * By the contract:
 * <code>getPayload()</code> should never return <code>null</code> (should be initialized in impl's constructor
 * and return the same object (with different content) before and after <code>unmarshal()</code>.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: Unmarshallable.java Nov 9, 2012 10:25:33 AM azatsarynnyy $
 *
 * @param <T>
 */
public interface Unmarshallable<T>
{
   /**
    * Prepares an object from the incoming {@link ResponseMessage}.
    * 
    * @param resopnse {@link ResponseMessage}
    */
   void unmarshal(ResponseMessage response) throws UnmarshallerException;

   /**
    * The content of the returned object normally differs before and 
    * after <code>unmarshall()</code> but by the contract it should never be <code>null</code>. 
    * 
    * @return an object deserialized from the {@link ResponseMessage} 
    */
   T getPayload();
}