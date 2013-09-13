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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceLoaderTest extends MemoryFileSystemTest {
    private String folderId;
    private String folderPath;
    private String fileId;
    private String filePath;

    private String vfsId = MY_WORKSPACE_ID;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder resourceLoaderTestFolder = new MemoryFolder(name);
        testRoot.addChild(resourceLoaderTestFolder);

        MemoryFolder folder = new MemoryFolder("GetResourceTest_FOLDER");
        resourceLoaderTestFolder.addChild(folder);
        MemoryFile childFile = new MemoryFile("file1", "text/plain",
                                              new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        folder.addChild(childFile);
        folderId = folder.getId();
        folderPath = folder.getPath();

        MemoryFile file = new MemoryFile("GetResourceTest_FILE", "text/plain",
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        resourceLoaderTestFolder.addChild(file);
        fileId = file.getId();
        filePath = file.getPath();

        memoryContext.putItem(resourceLoaderTestFolder);
    }

    public void testLoadFileByID() throws Exception {
        URL file = new URI("ide+vfs", '/' + vfsId, fileId).toURL();
        final String expectedURL = "ide+vfs:/" + vfsId + '#' + fileId;
        assertEquals(expectedURL, file.toString());
        byte[] b = new byte[128];
        InputStream in = file.openStream();
        int num = in.read(b);
        in.close();
        assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
    }

    public void testLoadFileByPath() throws Exception {
        URL file = new URI("ide+vfs", '/' + vfsId, filePath).toURL();
        final String expectedURL = "ide+vfs:/" + vfsId + '#' + filePath;
        assertEquals(expectedURL, file.toString());
        byte[] b = new byte[128];
        InputStream in = file.openStream();
        int num = in.read(b);
        in.close();
        assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
    }

    public void testLoadFolderByID() throws Exception {
        URL folder = new URI("ide+vfs", '/' + vfsId, folderId).toURL();
        final String expectedURL = "ide+vfs:/" + vfsId + '#' + folderId;
        assertEquals(expectedURL, folder.toString());
        byte[] b = new byte[128];
        InputStream in = folder.openStream();
        int num = in.read(b);
        in.close();
        assertEquals("file1\n", new String(b, 0, num));
    }

    public void testLoadFolderByPath() throws Exception {
        URL folder = new URI("ide+vfs", '/' + vfsId, folderPath).toURL();
        final String expectedURL = "ide+vfs:/" + vfsId + '#' + folderPath;
        assertEquals(expectedURL, folder.toString());
        byte[] b = new byte[128];
        InputStream in = folder.openStream();
        int num = in.read(b);
        in.close();
        assertEquals("file1\n", new String(b, 0, num));
    }
}
