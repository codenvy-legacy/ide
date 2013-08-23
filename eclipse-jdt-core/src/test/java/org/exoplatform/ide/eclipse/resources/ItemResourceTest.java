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

import com.codenvy.eclipse.resources.ItemResource;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

import org.exoplatform.ide.vfs.shared.FileImpl;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.junit.Test;

/**
 * General tests for {@link ItemResource}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ItemResourceTest.java Jan 8, 2013 12:54:02 PM azatsarynnyy $
 */
public class ItemResourceTest extends ResourcesBaseTest {
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testExistence() throws Exception {
        IPath originPath = new Path("/project/folder/file");
        IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
        assertFalse(fileResource.exists());

        fileResource.create(null, false, new NullProgressMonitor());
        assertTrue(fileResource.exists());
    }

    @Test
    public void testGetName() throws Exception {
        IPath originPath = new Path("/project/folder/file");
        IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
        assertEquals(originPath.lastSegment(), fileResource.getName());
    }

    @Test
    public void testGetFileExtension() throws Exception {
        IPath originPath = new Path("/project/folder/file.bin");
        IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
        assertEquals(originPath.getFileExtension(), fileResource.getFileExtension());

        originPath = new Path("/project/folder/file.");
        fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
        assertEquals(originPath.getFileExtension(), "");

        originPath = new Path("/project/folder/file");
        fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
        assertNull(originPath.getFileExtension());
    }

    @Test
    public void testGetFullPath() throws Exception {
        IPath originPath = new Path("/project/folder/file");
        IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
        assertEquals(originPath, fileResource.getFullPath());
    }

    @Test
    public void testGetModificationStamp() throws Exception {
        IPath originPath = new Path("/project/folder/file");
        IFile fileResource = (IFile)ws.newResource(originPath, IResource.FILE);
        fileResource.create(null, true, new NullProgressMonitor());

        long actualModificationStamp = fileResource.getModificationStamp();
        FileImpl file = (FileImpl)vfs.getItemByPath(originPath.toString(), null, false, PropertyFilter.NONE_FILTER);
        long expectedModificationStamp = file.getLastModificationDate();

        assertEquals(expectedModificationStamp, actualModificationStamp);
    }

}
