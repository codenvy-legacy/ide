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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.shared.ExitCodes;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.dto.server.DtoFactory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;

public class MoveTest extends LocalFileSystemTest {
    private final String lockToken = "01234567890abcdef";

    private final String fileName                 = "MoveTest_File";
    private final String folderName               = "MoveTest_Folder";
    private final String lockedFileName           = "MoveTest_LockedFile";
    private final String protectedFileName        = "MoveTest_ProtectedFile";
    private final String protectedFolderName      = "MoveTest_ProtectedFolder";
    private final String protectedChildFolderName = "MoveTest_ProtectedChildFolder";
    private final String lockedChildFolderName    = "MoveTest_LockedChildFolder";

    private String destinationPath;
    private String destinationId;

    private String protectedDestinationPath;
    private String protectedDestinationId;

    private String fileId;
    private String filePath;

    private String protectedFileId;
    private String protectedFilePath;

    private String lockedFileId;
    private String lockedFilePath;

    private String protectedFolderId;
    private String protectedFolderPath;

    private String protectedChildFolderId;
    private String protectedChildFolderPath;

    private String lockedChildFolderId;
    private String lockedChildFolderPath;

    private String folderId;
    private String folderPath;

    private Map<String, String[]> properties;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(user, EnumSet.of(BasicPermissions.ALL));
        permissions.put(admin, EnumSet.of(BasicPermissions.READ));

        properties = new HashMap<>(2);
        properties.put("MyProperty01", new String[]{"foo"});
        properties.put("MyProperty02", new String[]{"bar"});

        filePath = createFile(testRootPath, fileName, DEFAULT_CONTENT_BYTES);
        lockedFilePath = createFile(testRootPath, lockedFileName, DEFAULT_CONTENT_BYTES);
        protectedFilePath = createFile(testRootPath, protectedFileName, DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, folderName);
        createTree(folderPath, 6, 4, properties);
        protectedFolderPath = createDirectory(testRootPath, protectedFolderName);
        createTree(protectedFolderPath, 6, 4, properties);
        protectedChildFolderPath = createDirectory(testRootPath, protectedChildFolderName);
        createTree(protectedChildFolderPath, 6, 4, properties);
        lockedChildFolderPath = createDirectory(testRootPath, lockedChildFolderName);
        createTree(lockedChildFolderPath, 6, 4, properties);

        List<String> l = flattenDirectory(protectedChildFolderPath);
        // Find one child in the list and remove write permission for 'admin'.
        writePermissions(protectedChildFolderPath + '/' + l.get(new Random().nextInt(l.size())), permissions);

        l = flattenDirectory(lockedChildFolderPath);
        // Find one child in the list and lock it.
        for (String s : l) {
            if (createLock(lockedChildFolderPath + '/' + s, lockToken, Long.MAX_VALUE)) {
                break;
            }
        }

        destinationPath = createDirectory(testRootPath, "MoveTest_Destination");
        protectedDestinationPath = createDirectory(testRootPath, "MoveTest_ProtectedDestination");

        createLock(lockedFilePath, lockToken, Long.MAX_VALUE);

        writePermissions(protectedDestinationPath, permissions);
        writePermissions(protectedFilePath, permissions);
        writePermissions(protectedFolderPath, permissions);

        fileId = pathToId(filePath);
        lockedFileId = pathToId(lockedFilePath);
        protectedFileId = pathToId(protectedFilePath);
        folderId = pathToId(folderPath);
        protectedFolderId = pathToId(protectedFolderPath);
        protectedChildFolderId = pathToId(protectedChildFolderPath);
        lockedChildFolderId = pathToId(lockedChildFolderPath);

        destinationId = pathToId(destinationPath);
        protectedDestinationId = pathToId(protectedDestinationPath);
    }

    public void testMoveFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + fileId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationPath + '/' + fileName;
        assertFalse("File must be moved. ", exists(filePath));
        assertTrue("Not found file in destination location. ", exists(expectedPath));
    }

    public void testMoveFileAlreadyExist() throws Exception {
        byte[] existedFileContent = "existed file".getBytes();
        String existedFile = createFile(destinationPath, fileName, existedFileContent);
        String requestPath = SERVICE_URI + "move/" + fileId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
        // untouched ??
        assertTrue(exists(existedFile));
        assertTrue(Arrays.equals(existedFileContent, readFile(existedFile)));
    }

    public void testMoveLockedFile() throws Exception {
        String requestPath = SERVICE_URI + "move/" + lockedFileId +
                             '?' + "parentId=" + destinationId + '&' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertFalse("File must be moved. ", exists(lockedFilePath));
        String expectedPath = destinationPath + '/' + lockedFileName;
        assertTrue("Not found file in destination location. ", exists(expectedPath));
    }

    public void testMoveLockedFileNoLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + lockedFileId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("File must not be moved. ", exists(lockedFilePath));
        String expectedPath = destinationPath + '/' + lockedFileName;
        assertFalse("File must not be moved. ", exists(expectedPath));
    }

    public void testMoveFileNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + protectedFileId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Source file not found. ", exists(protectedFilePath));
        String expectedPath = destinationPath + '/' + protectedFileName;
        assertFalse("File must not be moved. ", exists(expectedPath));
    }

    public void testMoveFileNoPermissions_Destination() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + fileId + '?' + "parentId=" + protectedDestinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Source file not found. ", exists(protectedFilePath));
        String expectedPath = protectedDestinationPath + '/' + protectedFileName;
        assertFalse("File must not be moved. ", exists(expectedPath));
    }

    public void testMoveFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + folderId + '?' + "parentId=" + destinationId;
        List<String> before = flattenDirectory(folderPath);
        final long start = System.currentTimeMillis();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        final long end = System.currentTimeMillis();
        log.info(">>>>> Move tree time: {}ms", (end - start));
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationPath + '/' + folderName;
        assertFalse("Folder must be moved. ", exists(folderPath));
        assertTrue("Not found file in destination location. ", exists(expectedPath));
        List<String> after = flattenDirectory(expectedPath);
        before.removeAll(after);
        assertTrue(String.format("Missed items: %s", before), before.isEmpty());
    }

    public void testMoveFolderNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + protectedFolderId + '?' + "parentId=" + destinationId;
        List<String> before = flattenDirectory(protectedFolderPath);
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        String expectedPath = destinationPath + '/' + protectedFolderName;
        assertTrue("Source folder not found. ", exists(protectedFolderPath));
        assertFalse("Folder must not be moved. ", exists(expectedPath));
        List<String> after = flattenDirectory(protectedFolderPath);
        // Be sure there are no missed files.
        before.removeAll(after);
        assertTrue(String.format("Missed items: %s", before), before.isEmpty());
    }

    public void testMoveFolderAlreadyExist() throws Exception {
        createDirectory(destinationPath, folderName);
        String requestPath = SERVICE_URI + "move/" + folderId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
        // untouched ??
        assertTrue("Source folder not found. ", exists(folderPath));
    }

    public void testMoveFolderChildLocked() throws Exception {
        List<String> sourceBefore = flattenDirectory(lockedChildFolderPath);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + lockedChildFolderId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        // Items copied but we are fail when try delete source tree
        List<String> destination = flattenDirectory(destinationPath + '/' + lockedChildFolderName);
        List<String> sourceBeforeCopy = new ArrayList<>(sourceBefore);
        sourceBeforeCopy.removeAll(destination);
        assertTrue(String.format("Missed items: %s", sourceBeforeCopy), sourceBeforeCopy.isEmpty());
        // Be sure source folder is untouched.
        assertTrue("Folder must not be removed. ", exists(lockedChildFolderPath));
        List<String> sourceAfter = flattenDirectory(lockedChildFolderPath);
        sourceBefore.removeAll(sourceAfter);
        assertTrue(String.format("Missed items: %s", sourceBefore), sourceBefore.isEmpty());
    }

    public void testMoveFolderChildNoPermissions() throws Exception {
        List<String> sourceBefore = flattenDirectory(protectedChildFolderPath);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "move/" + protectedChildFolderId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        // Items copied but we are fail when try delete source tree.
        List<String> destination = flattenDirectory(destinationPath + '/' + protectedChildFolderName);
        List<String> sourceBeforeCopy = new ArrayList<>(sourceBefore);
        sourceBeforeCopy.removeAll(destination);
        assertTrue(String.format("Missed items: %s", sourceBeforeCopy), sourceBeforeCopy.isEmpty());
        // Be sure source folder is untouched.
        assertTrue("Folder must not be removed. ", exists(protectedChildFolderPath));
        List<String> sourceAfter = flattenDirectory(protectedChildFolderPath);
        sourceBefore.removeAll(sourceAfter);
        assertTrue(String.format("Missed items: %s", sourceBefore), sourceBefore.isEmpty());
    }

}
