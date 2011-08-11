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
package org.exoplatform.ide.vfs.client.model;

/**
 * @author eXo
 * @version $Id: Sep 10, 2010 $
 */
public class Lock
{
   private String owner;

   private String lockToken;

   private int timeout;

   /**
    * @param owner
    * @param lockToken
    * @param timeout
    */
   public Lock(String owner, String lockToken, int timeout)
   {
      this.owner = owner;
      this.lockToken = lockToken;
      this.timeout = timeout;
   }

   /**
    * @return the owner
    */
   public String getOwner()
   {
      return owner;
   }

   /**
    * @return the lockToken
    */
   public String getLockToken()
   {
      return lockToken;
   }

   /**
    * @return the timeout
    */
   public int getTimeout()
   {
      return timeout;
   }

   /**
    * @param timeout the timeout to set
    */
   public void setTimeout(int timeout)
   {
      this.timeout = timeout;
   }
}
