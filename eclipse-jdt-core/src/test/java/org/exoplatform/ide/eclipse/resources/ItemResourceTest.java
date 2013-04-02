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
        FileImpl file = (FileImpl)vfs.getItemByPath(originPath.toString(), null, PropertyFilter.NONE_FILTER);
        long expectedModificationStamp = file.getLastModificationDate();

        assertEquals(expectedModificationStamp, actualModificationStamp);
    }

}
