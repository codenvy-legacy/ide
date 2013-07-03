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
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.*;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class CreateTest extends LocalFileSystemTest {
    private String folderId;
    private String folderPath;

    private String protectedFolderPath;
    private String protectedFolderId;

    private String fileId;

    private String projectId;
    private String projectPath;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        folderPath = createDirectory(testRootPath, "CreateTest_Folder");
        protectedFolderPath = createDirectory(testRootPath, "CreateTest_ProtectedFolder");
        String filePath = createFile(testRootPath, "CreateTest_File", DEFAULT_CONTENT_BYTES);
        projectPath = createDirectory(testRootPath, "CreateTest_Project");

        Map<String, String[]> projectProperties = new HashMap<String, String[]>(1);
        projectProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        writeProperties(projectPath, projectProperties);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(2);
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));
        writePermissions(protectedFolderPath, permissions);

        folderId = pathToId(folderPath);
        protectedFolderId = pathToId(protectedFolderPath);
        fileId = pathToId(filePath);
        projectId = pathToId(projectPath);
    }

    public void testCreateFile() throws Exception {
        String name = "testCreateFile";
        String content = "test create file";
        String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("text/plain;charset=utf8");
        headers.put("Content-Type", contentType);

        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, content.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = folderPath + '/' + name;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(content, new String(readFile(expectedPath)));
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(1);
        expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testCreateFileAlreadyExists() throws Exception {
        String name = "testCreateFileAlreadyExists";
        createFile(folderPath, name, null);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }

    public void testCreateFileInRoot() throws Exception {
        String name = "FileInRoot";
        String content = "test create file";
        String requestPath = SERVICE_URI + "file/" + ROOT_ID + '?' + "name=" + name;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("text/plain;charset=utf8");
        headers.put("Content-Type", contentType);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, headers, content.getBytes(), writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = '/' + name;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(content, new String(readFile(expectedPath)));
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(1);
        expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testCreateFileNoContent() throws Exception {
        String name = "testCreateFileNoContent";
        String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = folderPath + '/' + name;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertTrue(readFile(expectedPath).length == 0);
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(1);
        expectedProperties.put("vfs:mimeType", new String[]{"application/octet-stream"});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testCreateFileNoMediaType() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFileNoMediaType";
        String content = "test create file without media type";
        String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, null, content.getBytes(), writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = folderPath + '/' + name;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(content, new String(readFile(expectedPath)));
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(1);
        expectedProperties.put("vfs:mimeType", new String[]{"application/octet-stream"});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testCreateFileNoName() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "file/" + folderId;
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }

    public void testCreateFileHavePermissions() throws Exception {
        String name = "testCreateFileHavePermissions";
        String content = "test create file";
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "file/" + protectedFolderId + '?' + "name=" + name;
        // Replace default principal by principal who has write permission.
        ConversationState user = new ConversationState(new Identity("andrew"));
        ConversationState.setCurrent(user);
        // --
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, null, content.getBytes(), writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = protectedFolderPath + '/' + name;
        assertTrue("File was not created in expected location. ", exists(expectedPath));
        assertEquals(content, new String(readFile(expectedPath)));
    }

    public void testCreateFileNoPermissions() throws Exception {
        String name = "testCreateFileNoPermissions";
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "file/" + protectedFolderId + '?' + "name=" + name;
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
        assertFalse(exists(protectedFolderPath + '/' + name));
    }

    public void testCreateFileWrongParent() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFileWrongParent";
        // Try to create new file in other file.
        String requestPath = SERVICE_URI + "file/" + fileId + '?' + "name=" + name;
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }

    public void testCreateFileWrongParentId() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFileWrongParentId";
        String requestPath = SERVICE_URI + "file/" + folderId + "_WRONG_ID" + '?' + "name=" + name;
        ContainerResponse response =
                launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(404, response.getStatus());
    }

    public void testCreateFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFolder";
        String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = folderPath + '/' + name;
        assertTrue("Folder was not created in expected location. ", exists(expectedPath));
    }

    public void testCreateFolderInRoot() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "FolderInRoot";
        String requestPath = SERVICE_URI + "folder/" + ROOT_ID + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = '/' + name;
        assertTrue("Folder was not created in expected location. ", exists(expectedPath));
    }

    public void testCreateFolderNoName() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "folder/" + folderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }

    public void testCreateFolderNoPermissions() throws Exception {
        String name = "testCreateFolderNoPermissions";
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "folder/" + protectedFolderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
        String expectedPath = protectedFolderPath + '/' + name;
        assertFalse(exists(expectedPath));
    }

    public void testCreateFolderWrongParentId() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFolderWrongParentId";
        String requestPath = SERVICE_URI + "folder/" + folderId + "_WRONG_ID" + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(404, response.getStatus());
    }

    public void testCreateFolderHierarchy() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFolderHierarchy/1/2/3/4/5";
        String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = folderPath + '/' + name;
        assertTrue("Folder was not created in expected location. ", exists(expectedPath));
    }

    public void testCreateFolderHierarchyExists() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFolderHierarchyExists/1/2/3/4/5";
        createDirectory(folderPath, name);
        String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }

    public void testCreateFolderHierarchy2() throws Exception {
        // create some items in path
        String name = "testCreateFolderHierarchy2/1/2/3";
        createDirectory(folderPath, name);
        // create the rest of path
        name += "/4/5";
        String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals(folderPath + "/testCreateFolderHierarchy2/1/2/3/4/5", ((Folder)response.getEntity()).getPath());
        String expectedPath = folderPath + '/' + name;
        assertTrue("Folder was not created in expected location. ", exists(expectedPath));
    }

    public void testCreateProject() throws Exception {
        // Type of project submitted in body.
        String name = "testCreateProject";
        String properties = "[{\"name\":\"vfs:projectType\", \"value\":[\"java\"]}]";
        //
        String requestPath = SERVICE_URI + "project/" + folderId + '?' + "name=" + name;
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, properties.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = folderPath + '/' + name;
        assertTrue("Project was not created in expected location. ", exists(expectedPath));
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(2);
        expectedProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        expectedProperties.put("vfs:projectType", new String[]{"java"});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testCreateProject2() throws Exception {
        // Type of project submitted in URL.
        String name = "testCreateProject2";
        String requestPath = SERVICE_URI + "project/" + folderId + '?' + "name=" + name + '&' + "type=" + "java";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = folderPath + '/' + name;
        assertTrue("Project was not created in expected location. ", exists(expectedPath));
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(2);
        expectedProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        expectedProperties.put("vfs:projectType", new String[]{"java"});
        validateProperties(expectedPath, expectedProperties);
    }

    public void testCreateProjectInProject() throws Exception {
        String name = "testCreateProjectInProject";
        String requestPath = SERVICE_URI + "project/" + projectId + '?' + "name=" + name + '&' + "type=" + "java";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = projectPath + '/' + name;
        assertTrue("Project was not created in expected location. ", exists(expectedPath));
        Map<String, String[]> expectedProperties = new HashMap<String, String[]>(2);
        expectedProperties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        expectedProperties.put("vfs:projectType", new String[]{"java"});
        validateProperties(expectedPath, expectedProperties);
    }
}
