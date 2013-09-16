/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.eclipse.jdt.refactoring;

import static org.junit.Assert.assertTrue;

import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.ui.refactoring.RenameSupport;

import org.apache.commons.io.IOUtils;
import org.exoplatform.ide.eclipse.jdt.JdtBaseTest;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Ignore
public class RenameTest extends JdtBaseTest {

    private IJavaProject javaProject;

    private IPackageFragment packageFragment;

    private String projectName;

    @Before
    public void init() throws CoreException {
        projectName = "test" + new Random().nextInt();
        javaProject = createJavaProject(projectName);
        IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoots()[0];
        packageFragment = packageFragmentRoot.createPackageFragment("com", true, null);
        packageFragment.open(null);

    }

    @Test
    public void renameType()
            throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException {
        ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
                                                                         "package com;\npublic class My{\n private My instance = null;\n}",
                                                                         true, null);

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
    public void renameField()
            throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException {
        ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
                                                                         "package com;\npublic class My{\n public static My instance = " +
                                                                         "null;\n}",
                                                                         true, null);

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
    public void renameMethod()
            throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException {
        ICompilationUnit myCUnit = packageFragment.createCompilationUnit("My.java",
                                                                         "package com;\npublic class My{\n public void some(){\n}}", true,
                                                                         null);

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
    public void renameVariable()
            throws CoreException, InvocationTargetException, InterruptedException, IOException, VirtualFileSystemException {
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
