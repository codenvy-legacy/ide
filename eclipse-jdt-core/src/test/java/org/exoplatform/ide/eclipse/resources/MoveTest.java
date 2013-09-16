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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.codenvy.eclipse.resources.FileResource;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

import org.junit.Test;

/**
 * Tests moving resources.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MoveTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class MoveTest extends ResourcesBaseTest {
    private IWorkspaceRoot workspaceRootForMove;

    private IProject projectForMove;

    private IFolder nonEmptyFolderForMove;

    private IFolder emptyFolderForMove;

    private IFile fileForMove;

    private IFile nonExistingFileForMove;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        workspaceRootForMove = (IWorkspaceRoot)ws.newResource(new Path("/"), IResource.ROOT);

        projectForMove = (IProject)ws.newResource(new Path("/project"), IResource.PROJECT);
        projectForMove.create(new NullProgressMonitor());

        emptyFolderForMove = (IFolder)ws.newResource(projectForMove.getFullPath().append("empty_folder"),
                                                     IResource.FOLDER);
        emptyFolderForMove.create(true, true, new NullProgressMonitor());

        nonEmptyFolderForMove = (IFolder)ws.newResource(projectForMove.getFullPath().append("non_empty_folder"),
                                                        IResource.FOLDER);
        nonEmptyFolderForMove.create(true, true, new NullProgressMonitor());
        ((IFile)ws.newResource(nonEmptyFolderForMove.getFullPath().append("file"), IResource.FILE)).create(null, true,
                                                                                                           new NullProgressMonitor());

        fileForMove = (FileResource)ws.newResource(projectForMove.getFullPath().append("file"), IResource.FILE);
        fileForMove.create(null, true, new NullProgressMonitor());

        nonExistingFileForMove = (FileResource)ws.newResource(projectForMove.getFullPath().append("file_not_exist"),
                                                              IResource.FILE);
    }

    @Test(expected = CoreException.class)
    public void testMoveWorkspaceRoot() throws Exception {
        workspaceRootForMove.move(new Path("/"), true, new NullProgressMonitor());
    }

    @Test
    public void testMoveProject() throws Exception {
        IPath destinationPath = new Path("/project1_moved");

        projectForMove.move(destinationPath, true, new NullProgressMonitor());
        assertFalse(projectForMove.exists());
        assertTrue(ws.newResource(destinationPath, IResource.PROJECT).exists());
    }

    @Test
    public void testMoveEmptyFolder() throws Exception {
        IPath destinationPath = new Path("/project2_moved/folder2_moved/folder3_moved");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        emptyFolderForMove.move(destinationPath, true, new NullProgressMonitor());
        assertFalse(emptyFolderForMove.exists());
        assertTrue(ws.newResource(destinationPath, IResource.FOLDER).exists());
    }

    @Test
    public void testMoveNonEmptyFolder() throws Exception {
        IPath destinationPath = new Path("/project_moved/folder_moved/folder1_moved");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        IResource[] members = nonEmptyFolderForMove.members();
        for (IResource member : members) {
            assertTrue(member.exists());
        }

        nonEmptyFolderForMove.move(destinationPath, true, new NullProgressMonitor());
        assertFalse(nonEmptyFolderForMove.exists());
        for (IResource member : members) {
            assertFalse(member.exists());
        }
        assertTrue(ws.newResource(destinationPath, IResource.FOLDER).exists());
    }

    @Test
    public void testMoveFile() throws Exception {
        IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());

        fileForMove.move(destinationPath, true, new NullProgressMonitor());
        assertFalse(fileForMove.exists());
        assertTrue(ws.newResource(destinationPath, IResource.FILE).exists());
    }

    @Test(expected = CoreException.class)
    public void testMoveFile_AlreadyExist() throws Exception {
        IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
        IFile destinationFile = (IFile)ws.newResource(destinationPath, IResource.FILE);
        destinationFile.create(null, true, new NullProgressMonitor());
        fileForMove.move(destinationPath, true, new NullProgressMonitor());
    }

    @Test(expected = CoreException.class)
    public void testMoveFile_ResourceNotExist() throws Exception {
        IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
        IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
                                                                  IResource.FOLDER);
        parentDestinationFolder.create(true, true, new NullProgressMonitor());
        nonExistingFileForMove.move(destinationPath, true, new NullProgressMonitor());
    }

    @Test(expected = CoreException.class)
    public void testMoveFile_ParentDestinationNotExist() throws Exception {
        IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
        fileForMove.move(destinationPath, true, new NullProgressMonitor());
    }

    @Test(expected = CoreException.class)
    public void testMoveFileToWorkspaceRoot() throws Exception {
        fileForMove.move(new Path("/"), true, new NullProgressMonitor());
    }

}
