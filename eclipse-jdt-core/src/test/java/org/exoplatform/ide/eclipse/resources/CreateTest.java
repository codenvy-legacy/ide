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
