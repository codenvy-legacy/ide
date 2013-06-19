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

import static org.junit.Assert.*;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;
import com.codenvy.commons.lang.IoUtil;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Tests creating resources.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class CreateTest extends ResourcesBaseTest {

    private IProject projectResource;

    private IFolder folderResource;

    private IFile fileResource;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        projectResource = (IProject)ws.newResource(new Path("/project"), IResource.PROJECT);
        folderResource = (IFolder)ws.newResource(projectResource.getFullPath().append("folder"), IResource.FOLDER);
        fileResource = (IFile)ws.newResource(folderResource.getFullPath().append("file"), IResource.FILE);
    }

    @Test
    public void testCreateProject() throws Exception {
        IPath originPath = projectResource.getFullPath();

        assertFalse(projectResource.exists());
        projectResource.create(new NullProgressMonitor());
        assertTrue(projectResource.exists());

        assertTrue(projectResource.getType() == IResource.PROJECT);
        assertEquals(originPath, projectResource.getFullPath());
    }

    @Test
    public void testCreateFolder() throws Exception {
        IPath originPath = folderResource.getFullPath();

        assertFalse(folderResource.exists());
        folderResource.create(true, true, new NullProgressMonitor());
        assertTrue(folderResource.exists());

        assertTrue(folderResource.getType() == IResource.FOLDER);
        assertEquals(originPath, folderResource.getFullPath());
    }

    @Test
    public void testCreateFile() throws Exception {
        IPath originPath = fileResource.getFullPath();
        String content = "test create file";

        assertFalse(fileResource.exists());
        InputStream contentsStream = new ByteArrayInputStream(content.getBytes());
        fileResource.create(contentsStream, true, new NullProgressMonitor());
        assertTrue(fileResource.exists());

        assertTrue(fileResource.getType() == IResource.FILE);
        assertEquals(originPath, fileResource.getFullPath());

        String actualContents = IoUtil.readStream(fileResource.getContents());
        assertEquals(actualContents, content);
    }

    //   @Test(expected = CoreException.class)
    //   public void testCreateFileAlreadyExist() throws Exception
    //   {
    //      assertFalse(fileResource.exists());
    //      fileResource.create(null, true, new NullProgressMonitor());
    //      assertTrue(fileResource.exists());
    //      fileResource.create(null, true, new NullProgressMonitor());
    //   }

}
