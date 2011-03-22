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
 * Request to delete branch.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchDeleteRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchDeleteRequest extends GitRequest
{
   /**
    * Name of branch to delete.
    */
   private String name;

   /**
    * If <code>true</code> delete branch {@link #name} even if it is not fully
    * merged. It is corresponds to -D options in C git.
    */
   private boolean force;

   /**
    * @param name name of branch to delete
    * @param force if <code>true</code> delete branch {@link #name} even if it
    *           is not fully merged
    */
   public BranchDeleteRequest(String name, boolean force)
   {
      this.name = name;
      this.force = force;
   }

   /**
    * "Empty" request to delete branch. Corresponding setters used to setup
    * required behavior.
    */
   public BranchDeleteRequest()
   {
   }

   /**
    * @return name of branch to delete
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name name of branch to delete
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return if <code>true</code> then delete branch {@link #name} even if it
    *         is not fully merged
    */
   public boolean isForce()
   {
      return force;
   }

   /**
    * @param force if <code>true</code> delete branch {@link #name} even if it
    *           is not fully merged
    */
   public void setForce(boolean force)
   {
      this.force = force;
   }
}
