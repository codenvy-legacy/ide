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

/**
 * Should be thrown for any errors that are not expressible by another VFS (Virtual File System) exception. Used as base class for
 * any VFS unchecked exceptions.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
@SuppressWarnings("serial")
public class VirtualFileSystemRuntimeException extends RuntimeException
{
   /**
    * @param message the detail message
    * @param cause the cause
    */
   public VirtualFileSystemRuntimeException(String message, Throwable cause)
   {
      super(message, cause);
   }

   /**
    * @param message the detail message
    */
   public VirtualFileSystemRuntimeException(String message)
   {
      super(message);
   }

   /**
    * @param cause the cause
    */
   public VirtualFileSystemRuntimeException(Throwable cause)
   {
      super(cause);
   }
}
