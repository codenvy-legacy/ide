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
 * @version $Id: BranchListRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchListRequest extends GitRequest
{
   /**
    * Show both remote and local branches. <br/>
    * Corresponds to -a option in C git.
    */
   public static final String LIST_ALL = "a";
   /**
    * Show both remote branches. <br/>
    * Corresponds to -r option in C git.
    */
   public static final String LIST_REMOTE = "r";

   /**
    * Branches list mode.
    */
   private String listMode;

   /**
    * Create list branches request that will retrieved local branches.
    */
   public BranchListRequest()
   {
   }

   /**
    * Create list branch request with specified list mode. Parameter
    * <code>listMode</code> may be either 'a' or 'r'. If 'a' is specified then
    * all branches (local and remote) will be displayed. If 'r' is specified
    * then remote branches only will be displayed. May be <code>null</code> also
    * in this case only local branches displayed. This is default behavior.
    * 
    * @param listMode list branch mode
    */
   public BranchListRequest(String listMode)
   {
      setListMode(listMode);
   }

   /**
    * @return branches list mode
    */
   public String getListMode()
   {
      return listMode;
   }

   /**
    * @param listMode may be either 'a' or 'r'. If 'a' is specified then all
    *           branches (local and remote) will be displayed. If 'r' is
    *           specified then remote branches only will be displayed. May be
    *           <code>null</code> also in this case only local branches
    *           displayed
    */
   public void setListMode(String listMode)
   {
      this.listMode = listMode;
   }
}
