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

import com.codenvy.api.vfs.shared.Project;

import org.everrest.core.impl.ContainerResponse;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class ProjectUpdateTest extends LocalFileSystemTest {
    private String projectId;

    private String projectFileId;
    private String projectFilePath;

    private String projectFolderId;
    private String projectFolderPath;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        String projectPath = createDirectory(testRootPath, "ProjectUpdateTest_Project");
        Map<String, String[]> props = new HashMap<>(1);
        props.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        writeProperties(projectPath, props);
        createTree(projectPath, 6, 4, null);

        List<String> l = flattenDirectory(projectPath);
        // Find one child in the list and lock it.
        for (int i = 0, size = l.size(); i < size && (projectFilePath == null || projectFolderPath == null); i++) {
            String s = l.get(i);
            String path = projectPath + '/' + s;
            File f = getIoFile(path);
            if (f.isFile()) {
                projectFilePath = path;
            } else if (f.isDirectory()) {
                projectFolderPath = path;
            }
        }

        projectFileId = pathToId(projectFilePath);
        projectFolderId = pathToId(projectFolderPath);
        projectId = pathToId(projectPath);

        String requestPath = SERVICE_URI + "watch/start/" + projectId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
    }

    @Override
    public void tearDown() throws Exception {
        String requestPath = SERVICE_URI + "watch/stop/" + projectId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
        super.tearDown();
    }

    private long readUpdateTime() throws Exception {
        String lastUpdateTime = getItem(projectId).getPropertyValue("vfs:lastUpdateTime");
        assertNotNull(lastUpdateTime); // must be set
        return Long.parseLong(lastUpdateTime);
    }

    public void testUpdateContent() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        byte[] newContent = "test update content".getBytes();
        String requestPath = SERVICE_URI + "content/" + projectFileId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, newContent, null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    public void testCreateNewFile() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String name = "some_file.txt";
        byte[] newContent = "test create new file".getBytes();
        String requestPath = SERVICE_URI + "file/" + projectId + '?' + "name=" + name;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, newContent, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    public void testDeleteFile() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String requestPath = SERVICE_URI + "delete/" + projectFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    public void testMoveFile() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String newFolderId = pathToId(createDirectory(testRootPath, "dest_folder"));
        String requestPath = SERVICE_URI + "move/" + projectFileId + '?' + "parentId=" + newFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    public void testRenameFile() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String newName = "_new_name_";
        String newMediaType = "text/plain";
        String requestPath = SERVICE_URI + "rename/" + projectFileId + '?' + "newname=" + newName + '&' +
                             "mediaType=" + newMediaType;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }


    public void testCreateNewFolder() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String name = "some_folder";
        String requestPath = SERVICE_URI + "folder/" + projectId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    public void testDeleteFolder() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String requestPath = SERVICE_URI + "delete/" + projectFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    public void testMoveFolder() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String newFolderId = pathToId(createDirectory(testRootPath, "dest_folder2"));
        String requestPath = SERVICE_URI + "move/" + projectFolderId + '?' + "parentId=" + newFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    public void testRenameFolder() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String newName = "_new_name_";
        String newMediaType = "text/plain";
        String requestPath = SERVICE_URI + "rename/" + projectFolderId + '?' + "newname=" + newName + '&' +
                             "mediaType=" + newMediaType;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
    }

    // Update metadata and ACL. Project update listener must not be notified.

    public void testUpdateItem() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        String requestPath = SERVICE_URI + "item/" + projectFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, properties.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("vfs:lastUpdateTime must not be updated", longLastUpdateTime, readUpdateTime());
    }

    public void testUpdateAcl() throws Exception {
        long longLastUpdateTime = readUpdateTime();
        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"all\"]}," +
                     "{\"principal\":{\"name\":\"john\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}]";
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        String requestPath = SERVICE_URI + "acl/" + projectFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
        assertEquals("vfs:lastUpdateTime must not be updated", longLastUpdateTime, readUpdateTime());
    }
}
