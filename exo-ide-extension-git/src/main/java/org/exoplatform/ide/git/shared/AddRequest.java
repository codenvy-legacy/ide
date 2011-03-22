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
 * Request to add content of working tree to Git index. This action prepares
 * content to next commit.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AddRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class AddRequest extends GitRequest
{
   /**
    * Default file pattern that will be used if {@link #filepattern} is not set.
    * All content of working tree will be added in index.
    */
   public static final String[] DEFAULT_PATTERN = new String[]{"."};

   /**
    * Files to add content from.
    */
   private String[] filepattern = DEFAULT_PATTERN;

   /**
    * If <code>true</code> than never stage new files, but stage modified new
    * contents of tracked files. It will remove files from the index if the
    * corresponding files in the working tree have been removed. If
    * <code>false</code> then new files and modified files added to the index.
    */
   private boolean update;

   /**
    * @param filepattern files to add content from
    * @param update if <code>true</code> than never stage new files, but stage
    *           modified new contents of tracked files. It will remove files
    *           from the index if the corresponding files in the working tree
    *           have been removed. If <code>false</code> (default) then new
    *           files and modified files added to the index.
    */
   public AddRequest(String[] filepattern, boolean update)
   {
      this.filepattern = filepattern;
      this.update = update;
   }

   /**
    * "Empty" request to add content of working tree to Git index. Corresponding
    * setters used to setup required behavior.
    */
   public AddRequest()
   {
   }

   /**
    * @return files to add content from
    */
   public String[] getFilepattern()
   {
      return filepattern;
   }

   /**
    * @param filepattern files to add content from. If <code>null</code> the
    *           special {@link AddRequest#DEFAULT_PATTERN} pattern will be used
    *           instead
    */
   public void setFilepattern(String[] filepattern)
   {
      this.filepattern = (filepattern == null) ? DEFAULT_PATTERN : filepattern;
   }

   /**
    * @return if <code>true</code> than never stage new files, but stage
    *         modified new contents of tracked files. It will remove files from
    *         the index if the corresponding files in the working tree have been
    *         removed. If <code>false</code> then new files and modified files
    *         added to the index.
    */
   public boolean isUpdate()
   {
      return update;
   }

   /**
    * @param update if <code>true</code> than never stage new files, but stage
    *           modified new contents of tracked files. It will remove files
    *           from the index if the corresponding files in the working tree
    *           have been removed. If <code>false</code> then new files and
    *           modified files added to the index.
    */
   public void setUpdate(boolean update)
   {
      this.update = update;
   }
}
