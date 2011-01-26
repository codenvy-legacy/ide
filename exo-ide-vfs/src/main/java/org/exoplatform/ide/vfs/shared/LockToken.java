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
package org.exoplatform.ide.vfs.shared;

/**
 * Representation of LockToken used to interaction with client via JSON. A lock
 * token is a opaque token, which identifies a particular lock. Is used for
 * replace existed lock or performing actions on locked object.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class LockToken
{
   private final String token;

   /**
    * @param token string representation of lock token
    * @throws IllegalArgumentException if <code>token == null</code>
    */
   public LockToken(String token)
   {
      if (token == null)
         throw new IllegalArgumentException("Lock token may not be null. ");
      this.token = token;
   }

   public String getLockToken()
   {
      return token;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if ((obj == null) || (obj.getClass() != this.getClass()))
         return false;
      LockToken otherLockToken = (LockToken)obj;
      return token.equals(otherLockToken.token);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      int hash = 8;
      hash = hash * 31 + token.hashCode();
      return hash;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "LockToken [token=" + token + "]";
   }
}
