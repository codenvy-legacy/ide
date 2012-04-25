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
package org.exoplatform.ide.extension.openshift.server;

import java.io.IOException;
import java.io.InputStream;

/**
 * Read response from openshift server and represent it in implementation specific type.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ExpressResponseReader<T>
{
   /**
    * Read object from stream.
    *
    * @param in
    *    stream that contains response from openshift server
    * @return implementation specific representation of response
    * @throws ParsingResponseException
    *    if any parsing response error occurs
    * @throws IOException
    *    if any i/o error occurs
    */
   T readObject(InputStream in) throws ParsingResponseException, IOException;
}
