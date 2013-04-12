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
package org.exoplatform.ide.eclipse.resources;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests deleting resources.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: DeleteTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class DeleteTest extends ResourcesBaseTest {
    private IWorkspaceRoot workspaceRootResource;

    private IProject projectResource;

    private IFolder emptyFolderResource;

    private IFolder nonEmptyFolderResource;

    private IFile fileResource;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        workspaceRootResource = (IWorkspaceRoot)ws.newResource(new Path("/"), IResource.ROOT);

        projectResource = (IProject)ws.newResource(new Path("/project"), IResource.PROJECT);
        projectResource.create(new NullProgressMonitor());

        emptyFolderResource = (IFolder)ws.newResource(projectResource.getFullPath().append("empty_folder"),
                                                      IResource.FOLDER);
        emptyFolderResource.create(true, true, new NullProgressMonitor());

        nonEmptyFolderResource = (IFolder)ws.newResource(projectResource.getFullPath().append("non_empty_folder"),
                                                         IResource.FOLDER);
        nonEmptyFolderResource.create(true, true, new NullProgressMonitor());
        ((IFile)ws.newResource(nonEmptyFolderResource.getFullPath().append("file"), IResource.FILE)).create(null, true,
                                                                                                            new NullProgressMonitor());

        fileResource = (IFile)ws.newResource(projectResource.getFullPath().append("file"), IResource.FILE);
        fileResource.create(null, true, new NullProgressMonitor());
    }

    @Test
    public void testDeleteWorkspaceRoot() throws Exception {
        assertTrue(workspaceRootResource.members().length > 0);
        workspaceRootResource.delete(true, true, new NullProgressMonitor());
        assertEquals(0, workspaceRootResource.members().length);
    }

    @Test
    public void testDeleteProject() throws Exception {
        assertTrue(projectResource.exists());
        IResource[] members = projectResource.members();
        assertTrue(members.length > 0);
        for (IResource member : members) {
            assertTrue(member.exists());
        }

        projectResource.delete(true, new NullProgressMonitor());
        assertFalse(projectResource.exists());
        for (IResource member : members) {
            assertFalse(member.exists());
        }
    }

    @Test
    public void testDeleteEmptyFolder() throws Exception {
        assertTrue(emptyFolderResource.exists());
        assertEquals(0, emptyFolderResource.members().length);
        emptyFolderResource.delete(true, new NullProgressMonitor());
        assertFalse(emptyFolderResource.exists());
    }

    @Test
    public void testDeleteNonEmptyFolder() throws Exception {
        assertTrue(nonEmptyFolderResource.exists());
        IResource[] members = nonEmptyFolderResource.members();
        assertTrue(members.length > 0);
        for (IResource member : members) {
            assertTrue(member.exists());
        }

        nonEmptyFolderResource.delete(true, new NullProgressMonitor());
        assertFalse(nonEmptyFolderResource.exists());
        for (IResource member : members) {
            assertFalse(member.exists());
        }
    }

    @Test
    public void testDeleteFile() throws Exception {
        assertTrue(fileResource.exists());
        fileResource.delete(true, new NullProgressMonitor());
        assertFalse(fileResource.exists());
    }

}
