/*
 * Copyright (C) 2013 eXo Platform SAS.
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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Factory of WebApplicationException that contains error message in HTML format.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class HtmlErrorFormatter
{
   public static void sendErrorAsHTML(Exception e)
   {
      // GWT framework (used on client side) requires result in HTML format if use HTML forms.
      if (e instanceof ItemAlreadyExistException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.ITEM_EXISTS),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof ItemNotFoundException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.ITEM_NOT_FOUND),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof InvalidArgumentException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.INVALID_ARGUMENT),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof ConstraintException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.CONSTRAINT),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof PermissionDeniedException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.NOT_PERMITTED),
            MediaType.TEXT_HTML).build());
      }
      else if (e instanceof LockException)
      {
         throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.LOCK_CONFLICT),
            MediaType.TEXT_HTML).build());
      }
      throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.INTERNAL_ERROR),
         MediaType.TEXT_HTML).build());
   }

   private static String formatAsHtml(String message, int exitCode)
   {
      return String.format("<pre>Code: %d Text: %s</pre>", exitCode, message);
   }

   private HtmlErrorFormatter()
   {
   }
}
