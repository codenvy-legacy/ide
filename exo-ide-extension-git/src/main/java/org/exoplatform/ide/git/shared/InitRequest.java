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
 * Request to create new git repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class InitRequest extends GitRequest
{
   /**
    * Working directory for new git repository.
    */
   private String workingDir;

   /**
    * If <code>true</code> then bare repository created.
    */
   private boolean bare;

   /**
    * @param workingDir working directory for new git repository
    * @param bare <code>true</code> then bare repository created
    */
   public InitRequest(String workingDir, boolean bare)
   {
      this.workingDir = workingDir;
      this.bare = bare;
   }

   /**
    * "Empty" init request. Corresponding setters used to setup required
    * parameters.
    */
   public InitRequest()
   {
   }

   /**
    * @return working directory for new git repository
    */
   public String getWorkingDir()
   {
      return workingDir;
   }

   /**
    * @param workingDir working directory for new git repository
    */
   public void setWorkingDir(String workingDir)
   {
      this.workingDir = workingDir;
   }

   /**
    * @return <code>true</code> then bare repository created
    */
   public boolean isBare()
   {
      return bare;
   }

   /**
    * @param bare <code>true</code> then bare repository created
    */
   public void setBare(boolean bare)
   {
      this.bare = bare;
   }
}
