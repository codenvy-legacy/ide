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

import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.JavaModelException;

import org.apache.commons.io.IOUtils;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CreateJavaModelTest extends JdtBaseTest {

    private IJavaProject javaProject;

    private IPackageFragment packageFragment;

    private String projectName;

    @Before
    public void init() throws CoreException {
        projectName = "test" + new Random().nextInt();
        javaProject = createJavaProject(projectName);
        javaProject.open(null);
        IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoot(
                javaProject.getProject().getFolder("src"));
        packageFragment = packageFragmentRoot.createPackageFragment("com", true, null);
        packageFragment.open(null);
    }

    @Test
    public void createCompilationUnit() throws JavaModelException, VirtualFileSystemException, IOException {
        ICompilationUnit compilationUnit = packageFragment.createCompilationUnit("My.java",
                                                                                 "package com;\npublic class My{}", true, null);
        ContentStream content = vfs.getContent("/" + projectName + "/src/com/My.java", null);
        String c = IOUtils.toString(content.getStream());
        assertTrue(c.contains("public class My{}"));
    }

    @Test
    public void renameCuTest() throws JavaModelException, VirtualFileSystemException, IOException {
        ICompilationUnit compilationUnit = packageFragment.createCompilationUnit("My.java",
                                                                                 "package com;\npublic class My{}", true, null);
        compilationUnit.rename("MyClass.java", true, null);

        ContentStream content = vfs.getContent("/" + projectName + "/src/com/MyClass.java", null);
        String c = IOUtils.toString(content.getStream());
        assertTrue(c.contains("public class MyClass{}"));
    }

    @Test
    public void renameCuWithDep() throws JavaModelException, VirtualFileSystemException, IOException {
        ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
                                                                         "package com;\npublic class My{\n private My instance = null;}",
                                                                         true, null);

        ICompilationUnit ourCUnit = packageFragment.createCompilationUnit("Our.java",
                                                                          "package com;\npublic class Our{\npublic My fff;}", true, null);

        myCUnit.rename("MyClass.java", true, null);

        ContentStream content = vfs.getContent("/" + projectName + "/src/com/MyClass.java", null);
        String c = IOUtils.toString(content.getStream());
        assertTrue(c.contains("public class MyClass{"));

        content = vfs.getContent("/" + projectName + "/src/com/Our.java", null);
        System.out.println(c);
        System.out.println(IOUtils.toString(content.getStream()));

    }

}
