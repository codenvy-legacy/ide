/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.resources;


/**
 * Exception is throws when Resource API error occurs
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 4, 2012  
 */
public class ResourceException extends Exception
{
   private static final long serialVersionUID = 1L;

   /**
    * @param message
    * @param cause
    */
   public ResourceException(String message, Throwable cause)
   {
      super(message, cause);
   }
   /**
    * @param message
    */
   public ResourceException(String message)
   {
      super(message);
   }

   /**
    * @param e
    */
   public ResourceException(Throwable e)
   {
      super(e);
   }

}
