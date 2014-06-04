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

import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.dto.server.DtoFactory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;

public class ContentTest extends LocalFileSystemTest {
    private final String lockToken     = "1234567890abcdef";
    private final byte[] content       = "__ContentTest__".getBytes();
    private final byte[] updateContent = "__UpdateContentTest__".getBytes();

    private String filePath;
    private String fileId;

    private String protectedFilePath;
    private String protectedFileId;

    private String lockedFilePath;
    private String lockedFileId;

    private String folderId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, "ContentTest_File.txt", content);
        lockedFilePath = createFile(testRootPath, "ContentTest_LockedFile.txt", content);
        protectedFilePath = createFile(testRootPath, "ContentTest_ProtectedFile.txt", content);
        String folderPath = createDirectory(testRootPath, "ContentTest_Folder");

        createLock(lockedFilePath, lockToken, Long.MAX_VALUE);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(1);
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        permissions.put(principal, EnumSet.of(BasicPermissions.ALL));
        writePermissions(protectedFilePath, permissions);

        fileId = pathToId(filePath);
        lockedFileId = pathToId(lockedFilePath);
        protectedFileId = pathToId(protectedFilePath);
        folderId = pathToId(folderPath);
    }

    public void testGetContent() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "content/" + fileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue(Arrays.equals(content, writer.getBody()));
        assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
    }

    public void testDownloadFile() throws Exception {
        // Expect the same as 'get content' plus header "Content-Disposition".
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "downloadfile/" + fileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue(Arrays.equals(content, writer.getBody()));
        assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(String.format("attachment; filename=\"%s\"", "ContentTest_File.txt"),
                     writer.getHeaders().getFirst("Content-Disposition"));
    }

    public void testGetContentFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "content/" + folderId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }

    public void testGetContentNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "content/" + protectedFileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
    }

    public void testGetContentByPath() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "contentbypath" + filePath;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue(Arrays.equals(content, writer.getBody()));
        assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
    }

    public void testGetContentByPathNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "contentbypath" + protectedFilePath;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
    }


    public void testUpdateContent() throws Exception {
        String requestPath = SERVICE_URI + "content/" + fileId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, null);
        assertEquals(204, response.getStatus());
        assertTrue(Arrays.equals(updateContent, readFile(filePath)));
        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
        validateProperties(filePath, expectedProperties);
    }

    public void testUpdateContentFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "content/" + folderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, updateContent, writer, null);
        assertEquals(400, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testUpdateContentNoPermissions() throws Exception {
        // Restore 'read' permission for 'admin'.
        // All requests in test use this principal by default.
        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(1);
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(principal, EnumSet.of(BasicPermissions.READ));
        writePermissions(protectedFilePath, permissions);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "content/" + protectedFileId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, writer, null);
        // Request must fail since 'admin' has not 'write' permission (only 'read').
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Content must not be updated", Arrays.equals(content, readFile(protectedFilePath)));
        assertNull("Properties must not be updated", readProperties(filePath));
    }

    public void testUpdateContentLocked() throws Exception {
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
        String requestPath = SERVICE_URI + "content/" + lockedFileId + '?' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, null);
        // File is locked.
        assertEquals(204, response.getStatus());
        assertTrue(Arrays.equals(updateContent, readFile(lockedFilePath))); // content updated
        // media type is set
        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
        validateProperties(lockedFilePath, expectedProperties);
    }

    public void testUpdateContentLockedNoLockToken() throws Exception {
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
        String requestPath = SERVICE_URI + "content/" + lockedFileId;
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, writer, null);
        // File is locked.
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Content must not be updated", Arrays.equals(content, readFile(lockedFilePath)));
        assertNull("Properties must not be updated", readProperties(lockedFilePath));
    }
}
