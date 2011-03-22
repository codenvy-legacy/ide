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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitUser.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class GitUser
{
   private String name;
   private String email;

   /**
    * @param name name
    * @param email email
    */
   public GitUser(String name, String email)
   {
      if (name == null)
         throw new NullPointerException("name");
      this.name = name;
      this.email = email;
   }

   /**
    * @param name
    */
   public GitUser(String name)
   {
      this(name, "");
   }

   public String getName()
   {
      return name;
   }

   public String getEmail()
   {
      return email;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + ((email == null) ? 0 : email.hashCode());
      hash = hash * 31 + name.hashCode();
      return hash;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      GitUser other = (GitUser)obj;
      if (email == null)
      {
         if (other.email != null)
            return false;
      }
      else if (!email.equals(other.email))
         return false;
      return name.equals(other.name);
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "GitUser [name=" + name + ", email=" + email + "]";
   }
}
