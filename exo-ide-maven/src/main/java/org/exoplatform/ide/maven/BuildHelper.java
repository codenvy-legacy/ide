/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.maven;

import java.io.File;
import java.io.FilenameFilter;
import java.security.SecureRandom;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class BuildHelper
{
   private static final String BUILDER_FILE_PREFIX = "build-";
   private static final SecureRandom gen = new SecureRandom();

   public static FilenameFilter makeBuilderFilesFilter()
   {
      return new FilenameFilter()
      {
         @Override
         public boolean accept(File dir, String name)
         {
            return name.startsWith(BUILDER_FILE_PREFIX);
         }
      };
   }

   /**
    * Create new directory with random name.
    *
    * @param parent parent for creation directory
    * @return newly created directory
    */
   public static File makeProjectDirectory(File parent)
   {
      File dir = new File(parent, BUILDER_FILE_PREFIX + Long.toString(Math.abs(gen.nextLong())));
      if (!dir.mkdirs())
      {
         throw new RuntimeException("Unable create project directory. ");
      }
      return dir;
   }

   /**
    * Remove specified file or directory.
    *
    * @param fileOrDirectory the file or directory to cancel
    * @return <code>true</code> if specified File was deleted and <code>false</code> otherwise
    */
   public static boolean delete(File fileOrDirectory)
   {
      if (fileOrDirectory.isDirectory())
      {
         for (File f : fileOrDirectory.listFiles())
         {
            if (!delete(f))
            {
               return false;
            }
         }
      }
      return !fileOrDirectory.exists() || fileOrDirectory.delete();
   }

   private BuildHelper()
   {
   }
}
