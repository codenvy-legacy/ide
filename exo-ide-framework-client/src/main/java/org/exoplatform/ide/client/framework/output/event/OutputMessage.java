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
package org.exoplatform.ide.client.framework.output.event;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputMessage
{

   public enum Type 
   {

      INFO, ERROR, WARNING, LOG, OUTPUT

   }

   private String message;

   private OutputMessage.Type type;

   public OutputMessage(String message, OutputMessage.Type type)
   {
      this.message = message;
      this.type = type;
   }

   /**
    * @return the message
    */
   public String getMessage()
   {
      return message;
   }

   /**
    * @return the type
    */
   public OutputMessage.Type getType()
   {
      return type;
   }

}
