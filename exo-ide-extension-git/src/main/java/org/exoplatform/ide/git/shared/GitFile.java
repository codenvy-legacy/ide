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

public class GitFile
{
   public enum FileStatus {
      NEW("new file", "A"), //
      DELETED("deleted", "D"), //
      MODIFIED("modified", "M"), //
      UNTRACKED("", "??");

      private final String longStatus;
      private final String shortStatus;

      private FileStatus(String longStatus, String shortStatus)
      {
         this.longStatus = longStatus;
         this.shortStatus = shortStatus;
      }

      public String getLongStatus()
      {
         return longStatus;
      }

      public String getShortStatus()
      {
         return shortStatus;
      }
   }

   private final String path;
   private final FileStatus status;

   /**
    * @param status
    * @param path
    */
   public GitFile(String path, FileStatus status)
   {
      this.path = path;
      this.status = status;
   }

   public String getPath()
   {
      return path;
   }

   public FileStatus getStatus()
   {
      return status;
   }
}
