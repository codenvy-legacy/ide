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
 * Request to get list of remotes. If {@link #remote} is specified then info
 * about this remote only given.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RemoteListRequest extends GitRequest
{
   /**
    * If <code>true</code> show remote url and name otherwise show remote name
    * only.
    */
   private boolean verbose;

   /**
    * Remote name. May be <code>null</code> if need to get info about all
    * remotes.
    */
   private String remote;

   /**
    * @param remote remote name. May be <code>null</code> if need to get info
    *           about all remotes
    * @param verbose if <code>true</code> show remote url and name otherwise
    *           show remote name only
    */
   public RemoteListRequest(String remote, boolean verbose)
   {
      this.remote = remote;
      this.verbose = verbose;
   }

   /**
    * @return if <code>true</code> show remote url and name otherwise show
    *         remote name only
    */
   public boolean isVerbose()
   {
      return verbose;
   }

   /**
    * @param verbose if <code>true</code> show remote url and name otherwise
    *           show remote name only
    */
   public void setVerbose(boolean verbose)
   {
      this.verbose = verbose;
   }

   /**
    * @return remote name
    * @see #remote
    */
   public String getRemote()
   {
      return remote;
   }

   /**
    * @param remote remote name
    * @see #remote
    */
   public void setRemote(String remote)
   {
      this.remote = remote;
   }
}
