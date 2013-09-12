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

import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.observation.ChangeEvent;
import com.codenvy.api.vfs.server.observation.ChangeEvent.ChangeType;
import com.codenvy.api.vfs.server.observation.ChangeEventFilter;
import com.codenvy.api.vfs.server.observation.EventListener;

import org.everrest.core.impl.ContainerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class EventsTest extends LocalFileSystemTest {
    final String fileName   = "EventsTest_File";
    final String folderName = "EventsTest_Folder";

    private String folderId;
    private String folderPath;

    private String fileId;
    private String filePath;

    private String destinationFolderId;
    private String destinationFolderPath;

    private ChangeEventFilter filter;
    private Listener          listener;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        folderPath = createDirectory(testRootPath, folderName);
        filePath = createFile(testRootPath, fileName, DEFAULT_CONTENT_BYTES);
        Map<String, String[]> fileProperties = new HashMap<>(1);
        fileProperties.put("vfs:mimeType", new String[]{"text/plain"});
        writeProperties(filePath, fileProperties);
        destinationFolderPath = createDirectory(testRootPath, "EventsTest_DestinationFolder");

        listener = new Listener();
        filter = ChangeEventFilter.ANY_FILTER;
        eventListenerList.addEventListener(filter, listener);

        folderId = pathToId(folderPath);
        fileId = pathToId(filePath);
        destinationFolderId = pathToId(destinationFolderPath);
    }

    @Override
    protected void tearDown() throws Exception {
        assertTrue("Unable remove listener. ", eventListenerList.removeEventListener(filter, listener));
        super.tearDown();
    }

    private class Listener implements EventListener {
        final List<ChangeEvent> events = new ArrayList<>();

        @Override
        public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
            log.info(event);
            events.add(event);
        }
    }

    public void testCreateFile() throws Exception {
        String name = "testCreateFile";
        String content = "test create file";
        String contentType = "text/plain;charset=utf8";
        String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, content.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        String expectedPath = folderPath + '/' + name;
        assertEquals(content, new String(readFile(expectedPath)));
        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.CREATED, event.getType());
        assertEquals(expectedPath, event.getItemPath());
        assertEquals(contentType, event.getMimeType());
    }

    public void testCreateFolder() throws Exception {
        String name = "testCreateFolder";
        String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        String expectedPath = folderPath + '/' + name;
        assertTrue(exists(expectedPath));
        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.CREATED, event.getType());
        assertEquals(expectedPath, event.getItemPath());
    }

    public void testCopy() throws Exception {
        String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + destinationFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);

        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        String expectedPath = destinationFolderPath + '/' + fileName;
        assertTrue(exists(expectedPath));
        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.CREATED, event.getType());
        assertEquals(expectedPath, event.getItemPath());
        assertEquals("text/plain", event.getMimeType());
    }

    public void testMove() throws Exception {
        String requestPath = SERVICE_URI + "move/" + fileId + '?' + "parentId=" + destinationFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);

        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        String expectedPath = destinationFolderPath + '/' + fileName;
        assertTrue(exists(expectedPath));
        assertFalse(exists(filePath));
        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.MOVED, event.getType());
        assertEquals(expectedPath, event.getItemPath());
        assertEquals(filePath, event.getOldItemPath());
        assertEquals("text/plain", event.getMimeType());
    }

    public void testUpdateContent() throws Exception {
        String contentType = "application/xml";
        String requestPath = SERVICE_URI + "content/" + fileId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList(contentType));
        String content = "<?xml version='1.0'><root/>";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, content.getBytes(), null);
        assertEquals(204, response.getStatus());

        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.CONTENT_UPDATED, event.getType());
        assertEquals(filePath, event.getItemPath());
        assertEquals("application/xml", event.getMimeType());
    }

    public void testUpdateProperties() throws Exception {
        String requestPath = SERVICE_URI + "item/" + fileId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("application/json"));
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, properties.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.PROPERTIES_UPDATED, event.getType());
        assertEquals(filePath, event.getItemPath());
        assertEquals("text/plain", event.getMimeType());
    }

    public void testUpdateAcl() throws Exception {
        String requestPath = SERVICE_URI + "acl/" + fileId;
        Map<String, List<String>> headers = new HashMap<>(1);
        headers.put("Content-Type", Arrays.asList("application/json"));
        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"all\"]}," +
                     "{\"principal\":{\"name\":\"john\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}]";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, acl.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());

        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.ACL_UPDATED, event.getType());
        assertEquals(filePath, event.getItemPath());
    }

    public void testDelete() throws Exception {
        String requestPath = SERVICE_URI + "delete/" + fileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 204, response.getStatus());

        assertFalse(exists(filePath));
        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.DELETED, event.getType());
        assertEquals(filePath, event.getItemPath());
        assertEquals("text/plain", event.getMimeType());
    }

    public void testRename() throws Exception {
        String requestPath = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);

        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());

        String expectedPath = testRootPath + '/' + "_FILE_NEW_NAME_";
        assertTrue(exists(expectedPath));
        assertFalse(exists(filePath));
        assertEquals(1, listener.events.size());
        ChangeEvent event = listener.events.get(0);
        assertEquals(ChangeType.RENAMED, event.getType());
        assertEquals(expectedPath, event.getItemPath());
        assertEquals("text/plain", event.getMimeType());
        assertEquals(filePath, event.getOldItemPath());
    }
}
