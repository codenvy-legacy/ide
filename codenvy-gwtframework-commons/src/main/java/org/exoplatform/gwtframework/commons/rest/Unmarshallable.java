/**
 * Copyright (C) 2009 eXo Platform SAS.
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

package org.exoplatform.gwtframework.commons.rest;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;

/**
 * Created by The eXo Platform SAS        .
 *
 * @version $Id: $
 *          <p/>
 *          deserializer for response's body.
 *          <p/>
 *          By the contract:
 *          getResult() should never return null (should be initialized in impl's constructor
 *          and return the same object (with different content) before and after unmarshal
 */

public interface Unmarshallable<T> {

    /**
     * prepares an object from the incoming string
     *
     * @param body
     */
    void unmarshal(Response response) throws UnmarshallerException;

    /**
     * The content of the returned object normally differs before and
     * after unmarshall() but by the contract it should never be null
     *
     * @return the object deserialized from response
     */
    T getPayload();

}
