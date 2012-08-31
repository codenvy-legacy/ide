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
package org.exoplatform.ide.vfs.shared;

/**
 * Provide set of exit codes of Virtual Files System operation. Such codes can be used as a supplement to the HTTP status of the
 * client to help define more precisely the type of error.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ExitCodes
{
   public static final int SUCCESS = 0;

   /** If operation fails cause to any constraints. */
   public static final int CONSTRAINT = 100;

   /** If any parameter of request is not acceptable. */
   public static final int INVALID_ARGUMENT = 101;

   /** Name conflict. */
   public static final int ITEM_EXISTS = 102;

   /** Item with specified path or ID does not exist. */
   public static final int ITEM_NOT_FOUND = 103;

   /** Lock conflict. */
   public static final int LOCK_CONFLICT = 104;

   /** Requested action is not supported. */
   public static final int UNSUPPORTED = 105;

   /** Performed action is not allowed for caller. */
   public static final int NOT_PERMITTED = 106;

   public static final int INTERNAL_ERROR = 200;

   private ExitCodes()
   {
   }
}
