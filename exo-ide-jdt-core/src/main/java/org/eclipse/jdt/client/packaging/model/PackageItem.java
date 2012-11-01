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
package org.eclipse.jdt.client.packaging.model;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Folder;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PackageItem
{

   private String packageName;

   private String resourceDirectory;

   private FolderModel packageFolder;

   private List<FileModel> files = new ArrayList<FileModel>();

   public PackageItem(String packageName, String resourceDirectory, FolderModel packageFolder)
   {
      this.packageName = packageName;
      this.resourceDirectory = resourceDirectory;
      this.packageFolder = packageFolder;
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public FolderModel getPackageFolder()
   {
      return packageFolder;
   }

   public void setPackageFolder(FolderModel packageFolder)
   {
      this.packageFolder = packageFolder;
   }

   public List<FileModel> getFiles()
   {
      return files;
   }

   public String getResourceDirectory()
   {
      return resourceDirectory;
   }

   public void setResourceDirectory(String resourceDirectory)
   {
      this.resourceDirectory = resourceDirectory;
   }

}
