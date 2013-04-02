/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class ResourceLoaderTest extends LocalFileSystemTest {
    private String folderId;
    private String folderPath;
    private String fileId;
    private String filePath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        filePath = createFile(testRootPath, "ResourceTest_File", DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, "ResourceTest_Folder");
        createFile(folderPath, "file1", null);

        fileId = pathToId(filePath);
        folderId = pathToId(folderPath);
    }

    public void testLoadFileById() throws Exception {
        URL file = new URI("ide+vfs", "/" + MY_WORKSPACE_ID, fileId).toURL();
        final String expectedURL = "ide+vfs:/" + MY_WORKSPACE_ID + '#' + fileId;
        assertEquals(expectedURL, file.toString());
        byte[] b = new byte[128];
        InputStream in = file.openStream();
        int num = in.read(b);
        in.close();
        assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
    }

    public void testLoadFileByPath() throws Exception {
        URL file = new URI("ide+vfs", "/" + MY_WORKSPACE_ID, filePath).toURL();
        final String expectedURL = "ide+vfs:/" + MY_WORKSPACE_ID + '#' + filePath;
        assertEquals(expectedURL, file.toString());
        byte[] b = new byte[128];
        InputStream in = file.openStream();
        int num = in.read(b);
        in.close();
        assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
    }

    public void testLoadFolderById() throws Exception {
        URL folder = new URI("ide+vfs", "/" + MY_WORKSPACE_ID, folderId).toURL();
        final String expectedURL = "ide+vfs:/" + MY_WORKSPACE_ID + '#' + folderId;
        assertEquals(expectedURL, folder.toString());
        byte[] b = new byte[128];
        InputStream in = folder.openStream();
        int num = in.read(b);
        in.close();
        assertEquals("file1\n", new String(b, 0, num));
    }

    public void testLoadFolderByPath() throws Exception {
        URL folder = new URI("ide+vfs", "/" + MY_WORKSPACE_ID, folderPath).toURL();
        final String expectedURL = "ide+vfs:/" + MY_WORKSPACE_ID + '#' + folderPath;
        assertEquals(expectedURL, folder.toString());
        byte[] b = new byte[128];
        InputStream in = folder.openStream();
        int num = in.read(b);
        in.close();
        assertEquals("file1\n", new String(b, 0, num));
    }
}
