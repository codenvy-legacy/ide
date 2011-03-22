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
 * Request to show changes between commits.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: DiffRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class DiffRequest extends GitRequest
{
   /**
    * Type of diff output.
    */
   public enum DiffType {
      /**
       * Only names of modified, added, deleted files.
       */
      NAME_ONLY {
         @Override
         public String toString()
         {
            return "--name-only";
         }
      },
      /**
       * Names staus of modified, added, deleted files.
       * <p>
       * Example:
       * 
       * <pre>
       * D   README.txt
       * A   HOW-TO.txt
       * </pre>
       */
      NAME_STATUS {
         @Override
         public String toString()
         {
            return "--name-status";
         }
      },
      RAW {
         @Override
         public String toString()
         {
            return "--raw";
         }
      }
   }

   /**
    * Filter of file to show diff. It may be either list of file names to show
    * diff or name of directory to show all files under them.
    */
   private String[] fileFilter;

   /**
    * Type of output.
    * 
    * @see DiffType
    */
   private DiffType type = DiffType.RAW;

   /**
    * Do not show renames in diff output.
    */
   private boolean noRenames = true;

   /**
    * Limit of showing renames in diff output. This attribute has sense if
    * {@link #noRenames} is <code>false</code>.
    */
   private int renameLimit;

   /**
    * @param fileFilter filter of file to show diff. It may be either list of
    *           file names to show diff or name of directory to show all files
    *           under them
    * @param type type of diff output
    * @param noRenames do not show renames. Default is <code>true</code>
    * @param renameLimit limit of showing renames in diff output. This attribute
    *           has sense if {@link #noRenames} is <code>false</code>
    */
   public DiffRequest(String[] fileFilter, DiffType type, boolean noRenames, int renameLimit)
   {
      this.fileFilter = fileFilter;
      this.type = type;
      this.noRenames = noRenames;
      this.renameLimit = renameLimit;
   }

   /**
    * "Empty" diff request. Corresponding setters used to setup required
    * parameters.
    */
   public DiffRequest()
   {
   }

   /**
    * @return filter of file to show diff. It may be either list of file names
    *         or name of directory to show all files under them
    */
   public String[] getFileFilter()
   {
      return fileFilter;
   }

   /**
    * @param fileFilter filter of file to show diff. It may be either list of
    *           file names or name of directory to show all files under them
    */
   public void setFileFilter(String[] fileFilter)
   {
      this.fileFilter = fileFilter;
   }

   /**
    * @return type of diff output
    */
   public DiffType getType()
   {
      return type;
   }

   /**
    * @param type type of diff output
    */
   public void setType(DiffType type)
   {
      this.type = type;
   }

   /**
    * @return <code>true</code> if renames must not be showing in diff result
    */
   public boolean isNoRenames()
   {
      return noRenames;
   }

   /**
    * @param noRenames <code>true</code> if renames must not be showing in diff
    *           result
    */
   public void setNoRenames(boolean noRenames)
   {
      this.noRenames = noRenames;
   }

   /**
    * @return limit of showing renames in diff output. This attribute has sense
    *         if {@link #noRenames} is <code>false</code>
    */
   public int getRenameLimit()
   {
      return renameLimit;
   }

   /**
    * @param renameLimit limit of showing renames in diff output. This attribute
    *           has sense if {@link #noRenames} is <code>false</code>
    */
   public void setRenameLimit(int renameLimit)
   {
      this.renameLimit = renameLimit;
   }
}
