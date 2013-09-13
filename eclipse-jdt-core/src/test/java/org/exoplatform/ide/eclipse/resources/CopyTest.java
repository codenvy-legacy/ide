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
package org.exoplatform.ide.eclipse.resources;

import static org.junit.Assert.assertTrue;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests copying resources.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CopyTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class CopyTest extends ResourcesBaseTest {
    private IWorkspaceRoot workspaceRootForMove;

    private IProject projectForCopy;

    private IFolder nonEmptyFolderForCopy;

    private IFolder emptyFolderForCopy;

    private IFile fileForCopy;

    private IFile nonExistingFileForCopy;

    private static final String DEFAULT_CONTENT = "test_content";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        workspaceRootForMove = (IWorkspaceRoot)ws.newResource(new Path("/"), IResource.ROOT);

        projectForCopy = (IProject)ws.newResource(new Path("/project"), IResource.PROJECT);
        projectForCopy.create(new NullProgressMonitor());

        emptyFolderForCopy = (IFolder)ws.newResource(projectForCopy.getFullPath().append("empty_folder"),
                                                     IResource.FOLDER);
        emptyFolderForCopy.create(true, true, new NullProgressMonitor());

        nonEmptyFolderForCopy = (IFolder)ws.newResource(projectForCopy.getFullPath().append("non_empty_folder"),
                                                        IResource.FOLDER);
        nonEmptyFolderForCopy.create(true, true, new NullProgressMonitor());
        InputStream contentsStream1 = new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
        ((IFile)ws.newResource(nonEmptyFolderForCopy.getFullPath().append("file"), IResource.FILE)).create(
                contentsStream1, true, new NullProgressMonitor());

        fileForCopy = (IFile)ws.newResource(projectForCopy.getFullPath().append("file"), IResource.FILE);
        InputStream contentsStream2 = new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
        fileForCopy.create(contentsStream2, true, new NullProgressMonitor());

        nonExistingFileForCopy = (IFile)ws.newResource(projectForCopy.getFullPath().append("file_not_exist"),
                                                       IResource.FILE);
    }

    @Test(expected = CoreException.class)
    public void testCopyWorkspaceRoot() throws Exception {
        workspaceRootForMove.copy(new Path("/"), true, new NullProgressMonitor());
    }

    @Test
    public void testCopyProject() throws Exception {
        IPath destinationPath = new Path("/parent_project/project_copy");

        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        projectForCopy.copy(destinationPath, true, new NullProgressMonitor());
        assertTrue(projectForCopy.exists());
        assertTrue(ws.newResource(destinationPath, IResource.PROJECT).exists());
    }

    @Test
    public void testCopyEmptyFolder() throws Exception {
        IPath destinationPath = new Path("/project2_copy/folder2_copy/folder3_copy");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        emptyFolderForCopy.copy(destinationPath, true, new NullProgressMonitor());
        assertTrue(emptyFolderForCopy.exists());
        assertTrue(ws.newResource(destinationPath, IResource.FOLDER).exists());
    }

    @Test
    public void testCopyNonEmptyFolder() throws Exception {
        IPath destinationPath = new Path("/project_copy/folder_copy/folder1_copy");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        IResource[] members = nonEmptyFolderForCopy.members();
        for (IResource member : members) {
            assertTrue(member.exists());
        }

        nonEmptyFolderForCopy.copy(destinationPath, true, new NullProgressMonitor());
        assertTrue(nonEmptyFolderForCopy.exists());
        for (IResource member : members) {
            assertTrue(member.exists());
        }
        assertTrue(ws.newResource(destinationPath, IResource.FOLDER).exists());
    }

    @Test
    public void testCopyFile() throws Exception {
        IPath destinationPath = new Path("/project_copy/folder_copy/file_copy");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        fileForCopy.copy(destinationPath, true, new NullProgressMonitor());
        assertTrue(fileForCopy.exists());
        assertTrue(ws.newResource(destinationPath, IResource.FILE).exists());
    }

    @Test(expected = CoreException.class)
    public void testCopyFile_AlreadyExist() throws Exception {
        IPath destinationPath = new Path("/project_copy/folder_copy/file_copy");
        IFile destinationFile = (IFile)ws.newResource(destinationPath, IResource.FILE);
        destinationFile.create(null, true, new NullProgressMonitor());
        fileForCopy.copy(destinationPath, true, new NullProgressMonitor());
    }

    @Test(expected = CoreException.class)
    public void testCopyFile_ResourceNotExist() throws Exception {
        IPath destinationPath = new Path("/project_copy/folder_copy/file_copy");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        nonExistingFileForCopy.copy(destinationPath, true, new NullProgressMonitor());
    }

    @Test(expected = CoreException.class)
    public void testCopyFile_ParentDestinationNotExist() throws Exception {
        IPath destinationPath = new Path("/project_copy/folder_copy/file_copy");
        fileForCopy.copy(destinationPath, true, new NullProgressMonitor());
    }

    @Test(expected = CoreException.class)
    public void testCopyFileToWorkspaceRoot() throws Exception {
        fileForCopy.copy(new Path("/"), true, new NullProgressMonitor());
    }

}
