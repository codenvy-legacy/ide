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
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.everrest.test.mock.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;

public class UploadFileTest extends LocalFileSystemTest {
    private String folderId;
    private String folderPath;

    private String protectedFolderId;
    private String protectedFolderPath;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        folderPath = createDirectory(testRootPath, "UploadTest");
        protectedFolderPath = createDirectory(testRootPath, "UploadTest_Protected");

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);

        permissions.put(user, EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
        permissions.put(admin, EnumSet.of(BasicPermissions.READ));
        writePermissions(protectedFolderPath, permissions);

        folderId = pathToId(folderPath);
        protectedFolderId = pathToId(protectedFolderPath);
    }

    public void testUploadNewFile() throws Exception {
        final String fileName = "testUploadNewFile";
        final String fileContent = "test upload file";
        final String fileMediaType = "text/plain;charset=utf8";
        ContainerResponse response = doUploadFile(folderId, fileName, fileMediaType, fileContent, "", "", false);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue(((String)response.getEntity()).isEmpty()); // empty if successful
        String expectedPath = folderPath + '/' + fileName;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(fileContent, new String(readFile(expectedPath)));

        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{fileMediaType});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testUploadNewFileNoPermissions() throws Exception {
        final String fileName = "testUploadNewFileNoPermissions";
        final String fileContent = "test upload file";
        final String fileMediaType = "text/plain;charset=utf8";
        ContainerResponse response = doUploadFile(protectedFolderId, fileName, fileMediaType, fileContent, "", "", false);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus()); // always 200 even for errors
        assertTrue(((String)response.getEntity()).startsWith("<pre>Code: 106"));
        String expectedPath = protectedFolderPath + '/' + fileName;
        assertFalse("File must not be created. ", exists(expectedPath));
    }

    public void testUploadNewFileInRootFolder() throws Exception {
        final String fileName = "testUploadNewFile";
        final String fileContent = "test upload file";
        final String fileMediaType = "text/plain;charset=utf8";
        folderId = ROOT_ID;
        ContainerResponse response = doUploadFile(folderId, fileName, fileMediaType, fileContent, "", "", false);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue(((String)response.getEntity()).isEmpty()); // empty if successful
        String expectedPath = '/' + fileName;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(fileContent, new String(readFile(expectedPath)));
        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{fileMediaType});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testUploadNewFileCustomizeName() throws Exception {
        final String fileName = "testUploadNewFileCustomizeName";
        final String fileContent = "test upload file with custom name";
        final String fileMediaType = "text/plain;charset=utf8";
        // Name of file passed in HTML form. If present it should be used instead of original file name.
        final String formFileName = fileName + ".txt";
        ContainerResponse response = doUploadFile(folderId, fileName, fileMediaType, fileContent, "", formFileName, false);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue(((String)response.getEntity()).isEmpty()); // empty if successful
        String expectedPath = folderPath + '/' + formFileName;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(fileContent, new String(readFile(expectedPath)));
        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{fileMediaType});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testUploadNewFileCustomizeMediaType() throws Exception {
        final String fileName = "testUploadNewFileCustomizeMediaType";
        final String fileContent = "test upload file with custom media type";
        final String fileMediaType = "application/octet-stream";
        final String formFileName = fileName + ".txt";
        final String formMediaType = "text/plain;charset=utf8"; // should be used instead of fileMediaType
        ContainerResponse response =
                doUploadFile(folderId, fileName, fileMediaType, fileContent, formMediaType, formFileName, false);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue(((String)response.getEntity()).isEmpty()); // empty if successful
        String expectedPath = folderPath + '/' + formFileName;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(fileContent, new String(readFile(expectedPath)));

        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{formMediaType});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testUploadFileAlreadyExists() throws Exception {
        final String fileName = "existedFile";
        final String fileMediaType = "application/octet-stream";
        final String fileContent = "existed file";
        createFile(folderPath, fileName, fileContent.getBytes());
        ContainerResponse response = doUploadFile(folderId, fileName, fileMediaType, DEFAULT_CONTENT, "", "", false);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus()); // always 200 even for errors
        assertTrue(((String)response.getEntity()).startsWith("<pre>Code: 102"));
        assertEquals(fileContent, new String(readFile(folderPath + '/' + fileName)));
    }

    public void testUploadFileAlreadyExistsAndProtected() throws Exception {
        final String fileName = "existedProtectedFile";
        final String fileMediaType = "application/octet-stream";
        final String fileContent = "existed protected file";
        String path = createFile(folderPath, fileName, fileContent.getBytes());
        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(admin, EnumSet.of(BasicPermissions.READ));
        permissions.put(user, EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
        writePermissions(path, permissions);
        // File is protected by ACL and may not be overwritten even if 'overwrite' parameter is 'true'
        ContainerResponse response = doUploadFile(folderId, fileName, fileMediaType, DEFAULT_CONTENT, "", "", true);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus()); // always 200 even for errors
        assertTrue(((String)response.getEntity()).startsWith("<pre>Code: 106"));
        assertEquals(fileContent, new String(readFile(folderPath + '/' + fileName)));
    }

    public void testUploadFileAlreadyExistsAndLocked() throws Exception {
        final String fileName = "existedLockedFile";
        final String fileMediaType = "application/octet-stream";
        final String fileContent = "existed locked file";
        String path = createFile(folderPath, fileName, fileContent.getBytes());
        createLock(path, "1234567890", Long.MAX_VALUE);
        // File is locked and may not be overwritten even if 'overwrite' parameter is 'true'
        ContainerResponse response = doUploadFile(folderId, fileName, fileMediaType, DEFAULT_CONTENT, "", "", true);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus()); // always 200 even for errors
        assertTrue(((String)response.getEntity()).startsWith("<pre>Code: 104"));
        assertEquals(fileContent, new String(readFile(folderPath + '/' + fileName)));
    }

    public void testUploadFileAlreadyExistsOverwrite() throws Exception {
        String fileName = "existedFileOverwrite";
        final String fileContent = "existed file overwrite";
        createFile(folderPath, fileName, fileContent.getBytes());
        final String newFileContent = "test upload and overwrite existed file";
        final String fileMediaType = "text/plain;charset=utf8";
        ContainerResponse response = doUploadFile(folderId, fileName, fileMediaType, newFileContent, "", "", true);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue(((String)response.getEntity()).isEmpty()); // empty if successful
        String expectedPath = folderPath + '/' + fileName;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(newFileContent, new String(readFile(expectedPath)));

        Map<String, String[]> expectedProperties = new HashMap<>(1);
        expectedProperties.put("vfs:mimeType", new String[]{fileMediaType});
        validateProperties(expectedPath, expectedProperties);
    }

    private ContainerResponse doUploadFile(String parentId,
                                           String fileName,
                                           String fileMediaType,
                                           String fileContent,
                                           String formMediaType,
                                           String formFileName,
                                           boolean formOverwrite) throws Exception {
        String requestPath = SERVICE_URI + "uploadfile/" + parentId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("multipart/form-data; boundary=abcdef"));
        byte[] formData = String.format(uploadBodyPattern,
                                        fileName, fileMediaType, fileContent, formMediaType, formFileName, formOverwrite)
                                .getBytes();
        EnvironmentContext env = new EnvironmentContext();
        env.put(HttpServletRequest.class, new MockHttpServletRequest("", new ByteArrayInputStream(formData),
                                                                     formData.length, "POST", headers));
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, formData, writer, env);
        if (writer.getBody() != null) {
            log.info(new String(writer.getBody()));
        }
        return response;
    }

    private static final String uploadBodyPattern =
            "--abcdef\r\nContent-Disposition: form-data; name=\"file\"; filename=\"%1$s\"\r\nContent-Type: %2$s\r\n\r\n%3$s\r\n" +
            "--abcdef\r\nContent-Disposition: form-data; name=\"mimeType\"\r\n\r\n%4$s\r\n" +
            "--abcdef\r\nContent-Disposition: form-data; name=\"name\"\r\n\r\n%5$s\r\n" +
            "--abcdef\r\nContent-Disposition: form-data; name=\"overwrite\"\r\n\r\n%6$b\r\n" +
            "--abcdef--\r\n";
}
