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
 * Request to create new branch.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchCreateRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchCreateRequest extends GitRequest
{
   /**
    * Name of branch to create.
    */
   private String name;

   /**
    * The name of a commit at which to start the new branch. If
    * <code>null</code> the HEAD will be used.
    */
   private String startPoint;

   /**
    * @param name name of branch to be created
    * @param startPoint name of a commit at which to start the new branch. If
    *           <code>null</code> the HEAD will be used
    */
   public BranchCreateRequest(String name, String startPoint)
   {
      this.name = name;
      this.startPoint = startPoint;
   }

   /**
    * "Empty" request to create branch. Corresponding setters used to setup
    * required behavior.
    */
   public BranchCreateRequest()
   {
   }

   /**
    * @return name of branch to be created
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name name of branch to be created
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return name of a commit at which to start the new branch. If
    *         <code>null</code> the HEAD will be used
    */
   public String getStartPoint()
   {
      return startPoint;
   }

   /**
    * @param startPoint name of a commit at which to start the new branch. If
    *           <code>null</code> the HEAD will be used
    */
   public void setStartPoint(String startPoint)
   {
      this.startPoint = startPoint;
   }
}
