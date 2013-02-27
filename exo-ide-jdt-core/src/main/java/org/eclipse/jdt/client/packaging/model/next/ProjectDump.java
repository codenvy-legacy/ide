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
package org.eclipse.jdt.client.packaging.model.next;

import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectDump
{
   
//   public void dump(JavaProject project)
//   {
//      
//   }
   
   public void dumpProjectTree(Item item)
   {
      System.out.println("-------------------------------------------------------------------");
      dumpProjectTree(item, "    ");
      System.out.println("-------------------------------------------------------------------");
   }
   
   private void dumpProjectTree(Item item, String prefix)
   {
      if (item instanceof ProjectModel)
      {
         System.out.println(prefix + "# " + item.getName());
         for (Item i : ((ProjectModel)item).getChildren().getItems())
         {
            dumpProjectTree(i, prefix + "    ");
         }
      }
      else if (item instanceof FolderModel)
      {
         System.out.println(prefix + "> " + item.getName());
         for (Item i : ((FolderModel)item).getChildren().getItems())
         {
            dumpProjectTree(i, prefix + "    ");
         }
      }
      else if (item instanceof FileModel)
      {
         System.out.println(prefix + "  " + item.getName());
      }
   }
   
   
   
   public void dumpJavaProject(JavaProject project)
   {
      dumpJavaProject(project, false, "");
      
   }
   
   private void dumpJavaProject(JavaProject project, boolean isModule, String prefix)
   {
      System.out.println(prefix + (isModule ? "Module " : "Project ") + project.getName() + "    Path " + project.getPath());
      System.out.println(prefix + "Modules: " + project.getModules().size());
      for (ProjectModel module : project.getModules())
      {
         if (!(module instanceof JavaProject))
         {
            System.out.println(prefix + "Project " + module.getName() + " is not Java project!");
            continue;
         }
         
         dumpJavaProject((JavaProject)module, true, prefix + "    ");
      }
      
      System.out.println(prefix + "Source directories: " + project.getSourceDirectories().size());
      for (SourceDirectory sourceDirectory : project.getSourceDirectories())
      {
         dumpSourceDirectory(sourceDirectory, prefix + "    ");
      }
      
      System.out.println(prefix + "Classpath folders: " + project.getClasspathFolders().size());
      for (ClasspathFolder classpathFolder : project.getClasspathFolders())
      {
         dumpClasspathFolder(classpathFolder, prefix + "    ");
      }
      
//      updateFilesAndFolders();      
      
   }
   
   private void dumpSourceDirectory(SourceDirectory sourceDirectory, String prefix)
   {
      System.out.println(prefix + "Source directory " + sourceDirectory.getSourceDirectoryName() + "    Path " + sourceDirectory.getPath());
      System.out.println(prefix + "Packages: " + sourceDirectory.getPackages().size());
      for (Package p : sourceDirectory.getPackages())
      {
         dumpPackage(p, prefix + "    ");
      }
   }
   
   private void dumpPackage(Package p, String prefix)
   {
      String name = p.getPackageName().isEmpty() ? "Default" : p.getPackageName();
      System.out.println(prefix + "Package " + name);
      System.out.println(prefix + "Files: " + p.getFiles().size());
      for (FileModel file : p.getFiles())
      {
         System.out.println(prefix + "    File " + file.getPath());
      }
   }
   
   private void dumpClasspathFolder(ClasspathFolder classpathFolder, String prefix)
   {
      System.out.println(prefix + "Classpath folder " + classpathFolder.getName());
      for (Classpath classpath: classpathFolder.getClasspathList())
      {
         System.out.println(prefix + "    classpath > " + classpath.getName());
      }
   }

}
