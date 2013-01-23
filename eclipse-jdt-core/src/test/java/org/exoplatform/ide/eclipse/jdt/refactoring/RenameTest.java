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
package org.exoplatform.ide.eclipse.jdt.refactoring;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.exoplatform.ide.eclipse.jdt.JdtBaseTest;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class RenameTest extends JdtBaseTest
{

   private IJavaProject javaProject;

   private IPackageFragment packageFragment;

   private String projectName;

   @Before
   public void init() throws CoreException
   {
      projectName = "test" + new Random().nextInt();
      javaProject = createJavaProject(projectName);
      IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoots()[0];
      packageFragment = packageFragmentRoot.createPackageFragment("com", true, null);
      packageFragment.open(null);

   }

   @Test
   public void renameType() throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException
   {
      ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
         "package com;\npublic class My{\n private My instance = null;\n}", true, null);

      ICompilationUnit ourCUnit = packageFragment.createCompilationUnit("Our.java", "package com;\n" +
         "import dsd.asds.Ass;\n" +
         "public class Our{\npublic com.My fff; \n" +
         "private Ass ddd(){}\n }", true, null);

      RenameSupport renameSupport = RenameSupport.create(myCUnit, "MyClass", RenameSupport.UPDATE_REFERENCES);
      IStatus status = renameSupport.preCheck();
      System.out.println(status.getMessage());
      renameSupport.perform();

      ContentStream content = vfs.getContent("/" + projectName + "/src/com/MyClass.java", null);
      String c = IOUtils.toString(content.getStream());
      assertTrue(c.contains("class MyClass"));
      assertTrue(c.contains("private MyClass instance"));
      content = vfs.getContent("/" + projectName + "/src/com/Our.java", null);

      String ourContent = IOUtils.toString(content.getStream());
      assertTrue(ourContent.contains("public com.MyClass fff"));
   }

   @Test
   public void renameField() throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException
   {
      ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
         "package com;\npublic class My{\n public static My instance = null;\n}", true, null);

      ICompilationUnit ourCUnit = packageFragment.createCompilationUnit("Our.java", "package com;\n" +
         "import dsd.asds.Ass;\n" +
         "public class Our{\npublic com.My fff; \n" +
         "private Ass ddd(){" +
         "My.instance = null;\n" +
         "}\n }", true, null);

      RenameSupport renameSupport = RenameSupport.create(myCUnit.getType("My").getField("instance"), "get",
         RenameSupport.UPDATE_REFERENCES);
      IStatus status = renameSupport.preCheck();
      System.out.println(status.getMessage());
      renameSupport.perform();

      ContentStream content = vfs.getContent("/" + projectName + "/src/com/My.java", null);
      String c = IOUtils.toString(content.getStream());
      assertTrue(c.contains("public static My get"));
      content = vfs.getContent("/" + projectName + "/src/com/Our.java", null);

      String ourContent = IOUtils.toString(content.getStream());
      assertTrue(ourContent.contains("My.get = null"));
   }

   @Test
   public void renameMethod() throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException
   {
      ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
         "package com;\npublic class My{\n public void some(){\n}}", true, null);

      ICompilationUnit ourCUnit = packageFragment.createCompilationUnit("Our.java", "package com;\n" +
         "import dsd.asds.Ass;\n" +
         "public class Our{\npublic com.My fff; \n" +
         "private Ass ddd(){" +
         "fff.some();\n" +
         "}\n }", true, null);

      RenameSupport renameSupport = RenameSupport.create(myCUnit.getType("My").getMethods()[0], "bar",
         RenameSupport.UPDATE_REFERENCES);
      IStatus status = renameSupport.preCheck();
      System.out.println(status.getMessage());
      renameSupport.perform();

      ContentStream content = vfs.getContent("/" + projectName + "/src/com/My.java", null);
      String c = IOUtils.toString(content.getStream());
      assertTrue(c.contains("public void bar()"));
      content = vfs.getContent("/" + projectName + "/src/com/Our.java", null);

      String ourContent = IOUtils.toString(content.getStream());
      assertTrue(ourContent.contains("fff.bar();"));
   }

   @Test
   public void renameVariable() throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException
   {
      ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
         "package com;\npublic class My{\n public void some(String name){System.out.print(\"Hello \" + name);\n}}",
         true, null);

      ICompilationUnit ourCUnit = packageFragment.createCompilationUnit("Our.java", "package com;\n" +
         "import dsd.asds.Ass;\n" +
         "public class Our{\npublic com.My fff; \n" +
         "private Ass ddd(){" +
         "fff.some(\"sdfsdf\");\n" +
         "}\n }", true, null);

      RenameSupport renameSupport = RenameSupport.create(myCUnit.getType("My").getMethods()[0].getParameters()[0],
         "foo", RenameSupport.UPDATE_REFERENCES);
      IStatus status = renameSupport.preCheck();
      System.out.println(status.getMessage());
      renameSupport.perform();

      ContentStream content = vfs.getContent("/" + projectName + "/src/com/My.java", null);
      String c = IOUtils.toString(content.getStream());
      assertTrue(c.contains("public void some(String foo)"));
      assertTrue(c.contains("System.out.print(\"Hello \" + foo);"));

   }
}
