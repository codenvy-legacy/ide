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
package org.exoplatform.ide.client.project.packaging.model;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectItem
{

   List<ResourceDirectoryItem> resourceDirectories = new ArrayList<ResourceDirectoryItem>();

   List<DependencyListItem> dependencies = new ArrayList<DependencyListItem>();

   List<FolderModel> folders = new ArrayList<FolderModel>();

   List<FileModel> files = new ArrayList<FileModel>();

   private ProjectModel project;

   public ProjectItem(ProjectModel project)
   {
      this.project = project;
   }

   public ProjectModel getProject()
   {
      return project;
   }

   public List<ResourceDirectoryItem> getResourceDirectories()
   {
      return resourceDirectories;
   }

   public void setResourceDirectories(List<ResourceDirectoryItem> resourceDirectories)
   {
      this.resourceDirectories = resourceDirectories;
   }

   public List<DependencyListItem> getDependencies()
   {
      return dependencies;
   }

   public void setDependencies(List<DependencyListItem> dependencies)
   {
      this.dependencies = dependencies;
   }

   public List<FolderModel> getFolders()
   {
      return folders;
   }

   public void setFolders(List<FolderModel> folders)
   {
      this.folders = folders;
   }

   public List<FileModel> getFiles()
   {
      return files;
   }

   public void setFiles(List<FileModel> files)
   {
      this.files = files;
   }

}
