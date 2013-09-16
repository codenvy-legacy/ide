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
package org.exoplatform.ide.eclipse.jdt;

import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IProjectDescription;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.jdt.core.IClasspathEntry;
import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.JavaCore;

import org.exoplatform.ide.eclipse.resources.ResourcesBaseTest;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class JdtBaseTest extends ResourcesBaseTest {

    public IJavaProject createJavaProject(final String name) throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(name);
        project.create(null);
        project.open(null);
        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[]{JavaCore.NATURE_ID});
        project.setDescription(description, null);
        IJavaProject javaProject = JavaCore.create(project);
        IFolder binFolder = project.getFolder("bin");
        binFolder.create(false, true, null);
        javaProject.setOutputLocation(binFolder.getFullPath(), null);
        //      List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
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
        javaProject.open(null);
        packageRoot.open(null);

        //      FileResource classPath = (FileResource)ws.newResource(new Path("/" + name + "/.classpath"), IResource.FILE);
        //      classPath.create(new ByteArrayInputStream(
        //         ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<classpath><classpathentry kind=\"output\" path=\"bin\"/>" +
        // "<classpathentry kind=\"src\" path=\"src\"/></classpath>").getBytes()),
        //         true, new NullProgressMonitor());

        return javaProject;
    }

}
