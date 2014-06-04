/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.shared.ExitCodes;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;
import com.codenvy.dto.server.DtoFactory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RenameTest extends LocalFileSystemTest {
    private final String lockToken = "01234567890abcdef";

    private String fileId;
    private String filePath;

    private String lockedFileId;
    private String lockedFilePath;

    private String protectedFileId;
    private String protectedFilePath;

    private String folderId;
    private String folderPath;

    private String protectedFolderId;
    private String protectedFolderPath;

    private Map<String, String[]> properties;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        properties = new HashMap<>(2);
        properties.put("MyProperty01", new String[]{"foo"});
        properties.put("MyProperty02", new String[]{"bar"});

        filePath = createFile(testRootPath, "RenameTest_File", DEFAULT_CONTENT_BYTES);
        // Add custom properties for file. Will check after rename to be sure all original properties are saved.
        writeProperties(filePath, properties);

        lockedFilePath = createFile(testRootPath, "RenameTest_LockedFile", DEFAULT_CONTENT_BYTES);

        protectedFilePath = createFile(testRootPath, "RenameTest_ProtectedFile", DEFAULT_CONTENT_BYTES);

        folderPath = createDirectory(testRootPath, "RenameTest_Folder");
        writeProperties(folderPath, properties);
        // Add custom properties for each item in tree. Will check after rename to be sure all original properties are saved.
        createTree(folderPath, 6, 4, properties);

        protectedFolderPath = createDirectory(testRootPath, "RenameTest_ProtectedFolder");
        createTree(protectedFolderPath, 6, 4, properties);
        writeProperties(protectedFolderPath, properties);

        createLock(lockedFilePath, lockToken, Long.MAX_VALUE);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(user, EnumSet.of(BasicPermissions.ALL));
        permissions.put(admin, EnumSet.of(BasicPermissions.READ));
        writePermissions(protectedFilePath, permissions);
        writePermissions(protectedFolderPath, permissions);

        fileId = pathToId(filePath);
        lockedFileId = pathToId(lockedFilePath);
        protectedFileId = pathToId(protectedFilePath);
        folderId = pathToId(folderPath);
        protectedFolderId = pathToId(protectedFolderPath);
    }

    public void testRenameFile() throws Exception {
        final String newName = "_FILE_NEW_NAME_";
        final String newMediaType = "text/*;charset=ISO-8859-1";
        String requestPath = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + newName + '&' +
                             "mediaType=" + newMediaType;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertFalse("File must be removed. ", exists(filePath));
        String expectedPath = testRootPath + '/' + newName;
        assertTrue("Not found new file in expected location. ", exists(expectedPath));
        assertTrue(Arrays.equals(DEFAULT_CONTENT_BYTES, readFile(expectedPath)));
        Map<String, String[]> expectedProperties = new HashMap<>(properties);
        expectedProperties.put("vfs:mimeType", new String[]{newMediaType});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testRenameFileAlreadyExists() throws Exception {
        final String newName = "_FILE_NEW_NAME_";
        final byte[] existedFileContent = "existed file".getBytes();
        final String existedFile = createFile(testRootPath, newName, existedFileContent);
        String requestPath = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + newName + '&' + "mediaType=" +
                             "text/*;charset=ISO-8859-1";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
        // Be sure file exists.
        assertTrue(exists(existedFile));
        // Check content.
        assertTrue(Arrays.equals(existedFileContent, readFile(existedFile)));
    }

    public void testRenameFileLocked() throws Exception {
        final String newName = "_FILE_NEW_NAME_";
        final String newMediaType = "text/*;charset=ISO-8859-1";
        String requestPath = SERVICE_URI + "rename/" + lockedFileId +
                             '?' + "newname=" + newName + '&' + "mediaType=" + newMediaType + '&' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = testRootPath + '/' + newName;
        assertFalse("File must be removed. ", exists(lockedFilePath));
        assertTrue("Not found new file in expected location. ", exists(expectedPath));
        assertTrue(Arrays.equals(DEFAULT_CONTENT_BYTES, readFile(expectedPath)));

        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{newMediaType});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testRenameFileLockedNoLockToken() throws Exception {
        final String newName = "_FILE_NEW_NAME_";
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "rename/" + lockedFileId +
                             '?' + "newname=" + newName + '&' + "mediaType=" + "text/*;charset=ISO-8859-1";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("File must not be removed. ", exists(lockedFilePath));
        String expectedPath = testRootPath + '/' + newName;
        assertFalse("File must not be created. ", exists(expectedPath));
        assertTrue(Arrays.equals(DEFAULT_CONTENT_BYTES, readFile(lockedFilePath)));
    }

    public void testRenameFileNoPermissions() throws Exception {
        final String newName = "_FILE_NEW_NAME_";
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "rename/" + protectedFileId + '?' + "newname=" + newName;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("File must not be removed. ", exists(protectedFilePath));
        String expectedPath = testRootPath + '/' + newName;
        assertFalse("File must not be created. ", exists(expectedPath));
        assertTrue(Arrays.equals(DEFAULT_CONTENT_BYTES, readFile(protectedFilePath)));
    }

    public void testRenameFolder() throws Exception {
        List<String> before = flattenDirectory(folderPath);

        final String newName = "_FOLDER_NEW_NAME_";
        String path = SERVICE_URI + "rename/" + folderId + '?' + "newname=" + newName;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        assertFalse("Folder must be removed. ", exists(folderPath));
        String expectedPath = testRootPath + '/' + newName;
        assertTrue("Not found new folder in expected location. ", exists(expectedPath));

        List<String> after = flattenDirectory(expectedPath);
        // Be sure there are no missed files.
        before.removeAll(after);
        assertTrue(String.format("Missed items: %s", before), before.isEmpty());

        validateProperties(expectedPath, properties, true);
    }

    public void testRenameFolderNoPermissions() throws Exception {
        final String newName = "_FOLDER_NEW_NAME_";
        List<String> before = flattenDirectory(protectedFolderPath);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "rename/" + protectedFolderId + '?' + "newname=" + newName;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Folder must not be removed. ", exists(protectedFolderPath));
        String expectedPath = testRootPath + '/' + newName;
        assertFalse("Folder must not be created. ", exists(expectedPath));
        List<String> after = flattenDirectory(protectedFolderPath);
        // Be sure there are no missed files.
        before.removeAll(after);
        assertTrue(String.format("Missed items: %s", before), before.isEmpty());
    }

    public void testRenameFolderUpdateMimeType() throws Exception {
        final String newName = "_FOLDER_NEW_NAME_";
        final String newMediaType = "text/directory%2BFOO"; // text/directory+FOO
        String path = SERVICE_URI + "rename/" + folderId + '?' + "newname=" + newName + '&' +
                      "mediaType=" + newMediaType;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = testRootPath + '/' + newName;
        assertTrue(exists(expectedPath));
        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{"text/directory+FOO"});
        validateProperties(expectedPath, expectedProperties, false); // media type updated only for current folder
        validateProperties(expectedPath, properties, true);
    }
}
