/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.server.exceptions;

import org.exoplatform.ide.vfs.shared.ExitCodes;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ItemNotFoundExceptionMapper.java 74306 2011-09-29 13:55:18Z andrew00x $
 */
@Provider
public class ItemNotFoundExceptionMapper implements ExceptionMapper<ItemNotFoundException>
{
   /**
    * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
    */
   @Override
   public Response toResponse(ItemNotFoundException exception)
   {
      return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN)
         .header("X-Exit-Code", Integer.toString(ExitCodes.ITEM_NOT_FOUND)).build();
   }
}
