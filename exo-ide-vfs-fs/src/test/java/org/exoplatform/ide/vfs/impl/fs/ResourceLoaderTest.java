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
