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

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RenameTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class RenameTest extends MemoryFileSystemTest {
    private MemoryFolder renameTestFolder;
    private String       fileId;
    private String       folderId;
    private MemoryFile   file;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        renameTestFolder = new MemoryFolder(name);
        testRoot.addChild(renameTestFolder);

        MemoryFolder folder = new MemoryFolder("RenameFileTest_FOLDER");
        renameTestFolder.addChild(folder);
        folderId = folder.getId();

        file = new MemoryFile("file", "text/plain",
                              new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        renameTestFolder.addChild(file);
        fileId = file.getId();

        memoryContext.putItem(renameTestFolder);
    }

    public void testRenameFile() throws Exception {
        String path = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_" + '&' + "mediaType=" +
                      "text/*;charset=ISO-8859-1";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = renameTestFolder.getPath() + '/' + "_FILE_NEW_NAME_";
        assertNotNull(memoryContext.getItemByPath(expectedPath));

        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        checkFileContext(DEFAULT_CONTENT, "text/*;charset=ISO-8859-1", file);
    }

    public void testRenameFileAlreadyExists() throws Exception {
        MemoryFile existed = new MemoryFile("_FILE_NEW_NAME_", "text/plain",
                                            new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        renameTestFolder.addChild(existed);
        memoryContext.putItem(existed);

        String path = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_" + '&' + "mediaType=" +
                      "text/*;charset=ISO-8859-1";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
    }

    public void testRenameFileLocked() throws Exception {
        String lockToken = file.lock();
        String path = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_" + '&' + "mediaType=" +
                      "text/*;charset=ISO-8859-1" + '&' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = renameTestFolder.getPath() + '/' + "_FILE_NEW_NAME_";
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        checkFileContext(DEFAULT_CONTENT, "text/*;charset=ISO-8859-1", file);
    }

    public void testRenameFileLocked_NoLockToken() throws Exception {
        file.lock();
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_" + '&' + "mediaType=" +
                      "text/*;charset=ISO-8859-1";
        String originPath = file.getPath();
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        assertNotNull("File must not be renamed since it is locked. ", memoryContext.getItemByPath(originPath));
        String expectedPath = renameTestFolder.getPath() + '/' + "_FILE_NEW_NAME_";
        assertNull("File must not be renamed since it is locked.", memoryContext.getItemByPath(expectedPath));
    }

    public void testRenameFileNoPermissions() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        file.updateACL(Arrays.asList(ace), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_";
        String originPath = file.getPath();
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertNotNull("File must not be renamed since permissions restriction. ", memoryContext.getItemByPath(originPath));
        String expectedPath = renameTestFolder.getPath() + '/' + "_FILE_NEW_NAME_";
        assertNull("File must not be renamed since permissions restriction.", memoryContext.getItemByPath(expectedPath));
    }

    public void testRenameFolder() throws Exception {
        String path = SERVICE_URI + "rename/" + folderId + '?' + "newname=" + "_FOLDER_NEW_NAME_";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = renameTestFolder.getPath() + '/' + "_FOLDER_NEW_NAME_";
        assertNotNull(memoryContext.getItemByPath(expectedPath));
    }

    public void testConvertFolder() throws Exception {
        String path = SERVICE_URI + "rename/" + folderId + '?' + "newname=" + "_FOLDER_NEW_NAME_" +
                      '&' + "mediaType=" + "text/vnd.ideproject%2Bdirectory";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = renameTestFolder.getPath() + '/' + "_FOLDER_NEW_NAME_";
        assertNotNull(memoryContext.getItemByPath(expectedPath));
        MemoryFolder folder = (MemoryFolder)memoryContext.getItemByPath(expectedPath);
        assertTrue("Regular folder must be converted to project. ", folder.isProject());
    }
}
