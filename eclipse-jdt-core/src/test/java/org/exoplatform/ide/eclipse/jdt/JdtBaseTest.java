/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.eclipse.jdt;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.exoplatform.ide.eclipse.resources.ResourcesBaseTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class JdtBaseTest extends ResourcesBaseTest
{

   public IJavaProject createJavaProject(final String name) throws CoreException
   {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      IProject project = root.getProject(name);
      project.create(null);
      project.open(null);
      IProjectDescription description = project.getDescription();
      description.setNatureIds(new String[]{JavaCore.NATURE_ID});
      project.setDescription(description, null);
      IJavaProject javaProject = JavaCore.create(project);
      javaProject.open(null);
      IFolder binFolder = project.getFolder("bin");
      binFolder.create(false, true, null);
      javaProject.setOutputLocation(binFolder.getFullPath(), null);
      List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
      //      IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
      //      LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
      //      for (LibraryLocation element : locations) {
      //         entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
      //      }
      //add libs to project class path
      //      javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

      IFolder sourceFolder = project.getFolder("src");
      sourceFolder.create(false, true, null);
      IPackageFragmentRoot packageRoot = javaProject.getPackageFragmentRoot(sourceFolder);
      IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
      IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
      System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
      newEntries[oldEntries.length] = JavaCore.newSourceEntry(packageRoot.getPath());
      javaProject.setRawClasspath(newEntries, null);
      packageRoot.open(null);

      //      FileResource classPath = (FileResource)ws.newResource(new Path("/" + name + "/.classpath"), IResource.FILE);
      //      classPath.create(new ByteArrayInputStream(
      //         ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<classpath><classpathentry kind=\"output\" path=\"bin\"/>" + "<classpathentry kind=\"src\" path=\"src\"/></classpath>").getBytes()),
      //         true, new NullProgressMonitor());

      return javaProject;
   }

}
