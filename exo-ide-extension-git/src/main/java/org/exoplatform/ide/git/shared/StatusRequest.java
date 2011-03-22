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
 * Request to get working tree status.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: StatusRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class StatusRequest extends GitRequest
{
   /**
    * If <code>true</code> then show status in short-format.
    * <p>
    * For example:
    * 
    * <pre>
    *  M README.txt
    * A  a/b/c/d/aaa
    * D  toRemove
    * ?? a/b/c/d/bbb
    * </pre>
    */
   private boolean shortFormat;

   /**
    * @param shortFormat if <code>true</code> then show status in short-format
    */
   public StatusRequest(boolean shortFormat)
   {
      this.shortFormat = shortFormat;
   }

   /**
    * "Empty" status request. Corresponding setters used to setup required
    * parameters.
    */
   public StatusRequest()
   {
   }

   /**
    * @return if <code>true</code> then show status in short-format
    * @see #shortFormat
    */
   public boolean isShortFormat()
   {
      return shortFormat;
   }

   /**
    * @param shortFormat if <code>true</code> then show status in short-format
    * @see #shortFormat
    */
   public void setShortFormat(boolean shortFormat)
   {
      this.shortFormat = shortFormat;
   }
}
