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
import com.codenvy.api.vfs.shared.ItemType;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.api.vfs.shared.dto.Project;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.user.UserImpl;
import com.codenvy.dto.server.DtoFactory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;
import static com.codenvy.commons.lang.IoUtil.deleteRecursive;

public class CopyTest extends LocalFileSystemTest {
    private final String fileName    = "CopyTest_File";
    private final String folderName  = "CopyTest_Folder";
    private final String projectName = "CopyTest_Project";

    private Map<String, String[]> projectProperties;

    private String destinationPath;
    private String destinationId;

    private String protectedDestinationPath;
    private String protectedDestinationId;

    private String destinationProjectPath;
    private String destinationProjectId;

    private String fileId;
    private String filePath;

    private String folderId;
    private String folderPath;

    private String projectId;
    private String projectPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, fileName, DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, folderName);
        createTree(folderPath, 6, 4, null);
        projectPath = createDirectory(testRootPath, projectName);
        createTree(projectPath, 6, 4, null);
        destinationPath = createDirectory(testRootPath, "CopyTest_DestinationFolder");
        protectedDestinationPath = createDirectory(testRootPath, "CopyTest_ProtectedDestinationFolder");
        destinationProjectPath = createDirectory(testRootPath, "CopyTest_DestinationProject");

        projectProperties = new HashMap<>(2);
        projectProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        projectProperties.put("vfs:projectType", new String[]{"java"});
        writeProperties(projectPath, projectProperties);
        writeProperties(destinationProjectPath, projectProperties);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);

        permissions.put(user, EnumSet.of(BasicPermissions.ALL));
        permissions.put(admin, EnumSet.of(BasicPermissions.READ));
        writePermissions(protectedDestinationPath, permissions);

        fileId = pathToId(filePath);
        folderId = pathToId(folderPath);
        projectId = pathToId(projectPath);
        destinationId = pathToId(destinationPath);
        protectedDestinationId = pathToId(protectedDestinationPath);
        destinationProjectId = pathToId(destinationProjectPath);

        // check we see items as projects
        assertEquals(ItemType.PROJECT, getItem(projectId).getItemType());
        assertEquals(ItemType.PROJECT, getItem(destinationProjectId).getItemType());
    }

    public void testCopyFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationPath + '/' + fileName;
        assertTrue("Source file not found. ", exists(filePath));
        assertTrue("Not found file in destination location. ", exists(expectedPath));
        assertTrue(Arrays.equals(DEFAULT_CONTENT_BYTES, readFile(expectedPath)));
    }

    public void testCopyFileAlreadyExist() throws Exception {
        byte[] existedFileContent = "existed file".getBytes();
        String existedFile = createFile(destinationPath, fileName, existedFileContent);
        String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
        // untouched ??
        assertTrue(exists(existedFile));
        assertTrue(Arrays.equals(existedFileContent, readFile(existedFile)));
    }

    public void testCopyFileHavePermissionsDestination() throws Exception {
        // Destination resource is protected but set user who has permits as current.
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + protectedDestinationId;
        EnvironmentContext.getCurrent().setUser(new UserImpl("andrew"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = protectedDestinationPath + '/' + fileName;
        assertTrue("Source file not found. ", exists(filePath));
        assertTrue("Not found file in destination location. ", exists(expectedPath));
        assertTrue(Arrays.equals(DEFAULT_CONTENT_BYTES, readFile(expectedPath)));
    }

    public void testCopyFileNoPermissionsDestination() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + protectedDestinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
        String expectedPath = protectedDestinationPath + '/' + fileName;
        assertTrue("Source file not found. ", exists(filePath));
        assertFalse(exists(expectedPath));
    }

    public void testCopyFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + destinationId;
        final long start = System.currentTimeMillis();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        final long end = System.currentTimeMillis();
        log.info(">>>>> Copy tree time: {}ms", (end - start));
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationPath + '/' + folderName;
        assertTrue("Source folder not found. ", exists(folderPath));
        assertTrue("Not found folder in destination location. ", exists(expectedPath));
        compareDirectories(folderPath, expectedPath, true);
    }

    public void testCopyFolderContainsFileNoReadPermission() throws Exception {
        List<String> l = flattenDirectory(folderPath);
        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(1);
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        permissions.put(principal, EnumSet.of(BasicPermissions.ALL));
        Random r = new Random();
        // Find one file randomly and apply permissions to it.
        String protectedFilePath = folderPath + '/' + l.get(r.nextInt(l.size()));
        java.io.File propertiesOfProtectedFile;
        while (true) {
            java.io.File f = getIoFile(protectedFilePath);
            if (f.isFile()) {
                Map<String, String[]> m = new HashMap<>();
                m.put("test_property", new String[]{"test value"});
                propertiesOfProtectedFile = writeProperties(protectedFilePath, m);
                writePermissions(protectedFilePath, permissions);
                break;
            } else {
                protectedFilePath = folderPath + '/' + l.get(r.nextInt(l.size()));
            }
        }

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + destinationId;
        final long start = System.currentTimeMillis();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        final long end = System.currentTimeMillis();
        log.info(">>>>> Copy tree time: {}ms", (end - start));
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationPath + '/' + folderName;
        assertTrue("Source folder not found. ", exists(folderPath));
        assertTrue("Not found folder in destination location. ", exists(expectedPath));

        // Remove protected file and its properties file from original directory before comparing.
        // Protected file must not be copied.
        assertTrue(getIoFile(protectedFilePath).delete());
        assertTrue(propertiesOfProtectedFile.delete());
        compareDirectories(folderPath, expectedPath, true);
    }

    public void testCopyFolderContainsFolderNoReadPermission() throws Exception {
        List<String> l = flattenDirectory(folderPath);
        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(1);
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        permissions.put(principal, EnumSet.of(BasicPermissions.ALL));
        Random r = new Random();
        // Find one file randomly and apply permissions to it.
        String protectedFolderPath = folderPath + '/' + l.get(r.nextInt(l.size()));
        java.io.File propertiesOfProtectedFile;
        while (true) {
            java.io.File f = getIoFile(protectedFolderPath);
            if (f.isDirectory() && f.listFiles().length > 0) {
                Map<String, String[]> m = new HashMap<>();
                m.put("test_property", new String[]{"test value"});
                propertiesOfProtectedFile = writeProperties(protectedFolderPath, m);
                for (String relPath : flattenDirectory(protectedFolderPath)) {
                    writeProperties(protectedFolderPath + '/' + relPath, m);
                }
                writePermissions(protectedFolderPath, permissions);
                break;
            } else {
                protectedFolderPath = folderPath + '/' + l.get(r.nextInt(l.size()));
            }
        }

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + destinationId;
        final long start = System.currentTimeMillis();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        final long end = System.currentTimeMillis();
        log.info(">>>>> Copy tree time: {}ms", (end - start));
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationPath + '/' + folderName;
        assertTrue("Source folder not found. ", exists(folderPath));
        assertTrue("Not found folder in destination location. ", exists(expectedPath));

        // Remove protected folder, its child and its properties file from original directory before comparing.
        // Protected folder must not be copied.
        // Will delete all files inside folder and its metadata (properties) files if any.
        assertTrue(deleteRecursive(getIoFile(protectedFolderPath)));
        assertTrue(propertiesOfProtectedFile.delete());
        compareDirectories(folderPath, expectedPath, true);
    }

    public void testCopyFolderAlreadyExist() throws Exception {
        createDirectory(destinationPath, folderName);
        String requestPath = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + destinationId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus());
        assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
        assertTrue("Source folder not found. ", exists(folderPath));
    }

    public void testCopyProject() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + projectId + '?' + "parentId=" + destinationId;
        final long start = System.currentTimeMillis();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        final long end = System.currentTimeMillis();
        log.info(">>>>> Copy tree time: {}ms", (end - start));
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationPath + '/' + projectName;
        assertTrue("Source project not found. ", exists(projectPath));
        assertTrue("Not found project in destination location. ", exists(expectedPath));
        compareDirectories(projectPath, expectedPath, true);
        validateProperties(projectPath, projectProperties);  // check source is not updated
        validateProperties(expectedPath, projectProperties); // check properties is copied
    }

    public void testCopyProjectToProject() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "copy/" + projectId + '?' + "parentId=" + destinationProjectId;
        final long start = System.currentTimeMillis();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        final long end = System.currentTimeMillis();
        log.info(">>>>> Copy tree time: {}ms", (end - start));
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = destinationProjectPath + '/' + projectName;
        assertTrue("Source project not found. ", exists(projectPath));
        assertTrue("Not found project in destination location. ", exists(expectedPath));
        compareDirectories(projectPath, expectedPath, true);
        validateProperties(projectPath, projectProperties);  // check source is not updated
        validateProperties(expectedPath, projectProperties); // check properties is copied
        validateProperties(destinationProjectPath, projectProperties);  // check destination project
    }
}
