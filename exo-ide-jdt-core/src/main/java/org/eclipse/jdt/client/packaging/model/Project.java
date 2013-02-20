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
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class Project
{

   private ProjectModel project;

   private List<ResourceDirectory> resourceDirectories = new ArrayList<ResourceDirectory>();

   private List<Dependencies> dependencies = new ArrayList<Dependencies>();

   private List<Project> projects = new ArrayList<Project>();

   private List<FolderModel> folders = new ArrayList<FolderModel>();

   private List<FileModel> files = new ArrayList<FileModel>();

   public Project(ProjectModel project)
   {
      this.project = project;
   }

   public ProjectModel getProject()
   {
      return project;
   }

   public List<ResourceDirectory> getResourceDirectories()
   {
      return resourceDirectories;
   }

   public void setResourceDirectories(List<ResourceDirectory> resourceDirectories)
   {
      this.resourceDirectories = resourceDirectories;
   }

   public List<Dependencies> getDependencies()
   {
      return dependencies;
   }

   public void setDependencies(List<Dependencies> dependencies)
   {
      this.dependencies = dependencies;
   }

   public List<Project> getProjects()
   {
      return projects;
   }

   public void setProjects(List<Project> projects)
   {
      this.projects = projects;
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
