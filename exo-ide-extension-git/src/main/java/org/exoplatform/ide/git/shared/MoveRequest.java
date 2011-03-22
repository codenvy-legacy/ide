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
package org.exoplatform.ide.git.shared;

/**
 * Request to move or rename a file or directory.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MoveRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class MoveRequest extends GitRequest
{
   /**
    * Source.
    */
   private String source;

   /**
    * Target.
    */
   private String target;

   /**
    * @param source move source
    * @param target move target
    */
   public MoveRequest(String source, String target)
   {
      this.source = source;
      this.target = target;
   }

   /**
    * "Empty" move request. Corresponding setters used to setup required
    * parameters.
    */
   public MoveRequest()
   {
   }

   /**
    * @return source
    */
   public String getSource()
   {
      return source;
   }

   /**
    * @param source move source
    */
   public void setSource(String source)
   {
      this.source = source;
   }

   /**
    * @return target
    */
   public String getTarget()
   {
      return target;
   }

   /**
    * @param target move target
    */
   public void setTarget(String target)
   {
      this.target = target;
   }
}
