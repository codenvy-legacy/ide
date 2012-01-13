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
package org.exoplatform.ide.git.client.marshaller;

import org.exoplatform.ide.git.shared.Status;

/**
 * The bean representing a status of the working tree (changed, untracked files).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 11:02:10 AM anya $
 * 
 */
public class StatusResponse extends Status
{
   /**
    * The string notion of the GIT work tree status. It contains the information of the modified, added, deleted files. Example
    * for short format:<br>
    * 
    * <pre>
    *   M README.txt
    *   A  test/abc/
    * </pre>
    */
   private String workTreeStatus;

   /**
    * @return the workTreeStatus
    */
   public String getWorkTreeStatus()
   {
      return workTreeStatus;
   }

   /**
    * @param workTreeStatus the workTreeStatus to set
    */
   public void setWorkTreeStatus(String workTreeStatus)
   {
      this.workTreeStatus = workTreeStatus;
   }

}
