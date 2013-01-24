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

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.exoplatform.ide.eclipse.resources.ResourcesBaseTest;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OpenProjectTest extends ResourcesBaseTest
{
   @Before
   public void createProject() throws VirtualFileSystemException
   {
      List<Property> prop = new ArrayList<Property>();

      prop.add(new PropertyImpl("NATURES_ID", Arrays.asList(JavaCore.NATURE_ID)));
      Project project = vfs.createProject(vfs.getInfo().getRoot().getId(), "proj", null, prop);
      vfs.createFolder(project.getId(), "bin");
      vfs.createFolder(project.getId(), "src/main/java/com/exo");
      Item folder = vfs.getItemByPath("/proj/src/main/java/com/exo", null, PropertyFilter.NONE_FILTER);
      vfs.createFile(folder.getId(), "My.java", MediaType.TEXT_PLAIN_TYPE,
         new ByteArrayInputStream("package com.exo;\npublic class My{ private My ins = null; public void dodo(){}}".getBytes()));

      vfs.createFile(folder.getId(), "Foo.java", MediaType.TEXT_PLAIN_TYPE,
         new ByteArrayInputStream("package com.exo;\npublic class Foo{ My fff; \n public void dome(){ My ddd = fff;}}".getBytes()));
      vfs.createFile(project.getId(), ".classpath", MediaType.TEXT_PLAIN_TYPE, new ByteArrayInputStream(
         "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<classpath><classpathentry kind=\"output\" path=\"bin\"/><classpathentry kind=\"src\" path=\"src/main/java\"/></classpath>".getBytes()));

   }

   @Test
   public void openProject() throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException
   {
      IProject project = ws.getRoot().getProject("proj");
      IJavaProject javaProject = JavaCore.create(project);
      javaProject.open(null);
      CountDownLatch latch = new CountDownLatch(2);
      JavaModelManager.getIndexManager().indexAll(javaProject.getProject(), latch);
      latch.await(2, TimeUnit.MINUTES);
      IType type = javaProject.findType("com.exo.My");
      RenameSupport renameSupport = RenameSupport.create(type.getCompilationUnit(), "MyClass",
         RenameSupport.UPDATE_REFERENCES);
      IStatus status = renameSupport.preCheck();
      System.out.println(status.getMessage());
      renameSupport.perform();
      ContentStream content = vfs.getContent("/proj/src/main/java/com/exo/MyClass.java", null);
      String c = IOUtils.toString(content.getStream());
      System.out.println(c);
      assertTrue(c.contains("class MyClass"));
      //      assertTrue(c.contains("MyClass ins"));
      content = vfs.getContent("/proj/src/main/java/com/exo/Foo.java", null);

      String ourContent = IOUtils.toString(content.getStream());
      System.out.println(ourContent);
      assertTrue(ourContent.contains("MyClass fff"));
   }

   @Test
   public void renameFild() throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException
   {
      IProject project = ws.getRoot().getProject("proj");
      IJavaProject javaProject = JavaCore.create(project);
      javaProject.open(null);
      CountDownLatch latch = new CountDownLatch(2);
      JavaModelManager.getIndexManager().indexAll(javaProject.getProject(), latch);
      latch.await(2, TimeUnit.MINUTES);
      IType type = javaProject.findType("com.exo.Foo");
      RenameSupport renameSupport = RenameSupport.create(type.getField("fff"), "www",
         RenameSupport.UPDATE_REFERENCES);
      IStatus status = renameSupport.preCheck();
      System.out.println(status.getMessage());
      renameSupport.perform();
      ContentStream content = vfs.getContent("/proj/src/main/java/com/exo/Foo.java", null);
      String c = IOUtils.toString(content.getStream());
      System.out.println(c);
      assertTrue(c.contains("My ddd = www;"));
      //      assertTrue(c.contains("MyClass ins"));
//      content = vfs.getContent("/proj/src/main/java/com/exo/Foo.java", null);

//      String ourContent = IOUtils.toString(content.getStream());
//      System.out.println(ourContent);
//      assertTrue(ourContent.contains("MyClass fff"));
   }


}
