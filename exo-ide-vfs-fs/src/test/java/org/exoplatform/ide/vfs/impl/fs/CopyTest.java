/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.*;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

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

        projectProperties = new HashMap<String, String[]>(2);
        projectProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        projectProperties.put("vfs:projectType", new String[]{"java"});
        writeProperties(projectPath, projectProperties);
        writeProperties(destinationProjectPath, projectProperties);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(2);
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));
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
        ConversationState user = new ConversationState(new Identity("andrew"));
        ConversationState.setCurrent(user);
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
        compareDirectories(folderPath, expectedPath);
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
        compareDirectories(projectPath, expectedPath);
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
        compareDirectories(projectPath, expectedPath);
        validateProperties(projectPath, projectProperties);  // check source is not updated
        validateProperties(expectedPath, projectProperties); // check properties is copied
        validateProperties(destinationProjectPath, projectProperties);  // check destination project
    }
}
