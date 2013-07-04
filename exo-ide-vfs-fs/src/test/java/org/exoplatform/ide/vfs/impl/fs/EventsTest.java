/*
 * Copyright (C) 2012 eXo Platform SAS.
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
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.ProjectUpdateListener;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codenvy.commons.lang.IoUtil.copy;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
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
        Map<String, String[]> fileProperties = new HashMap<String, String[]>(1);
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
        final List<ChangeEvent> events = new ArrayList<ChangeEvent>();

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
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
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
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
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
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
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
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
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

    public void _testProjectUpdateEventsIsolation() throws Exception {
      /* IDE-1768 */

        // Original issue is for JCR implementation but actual for plain file system backend also.

        // We already have one virtual filesystem 'fs'.
        // Create one more. Events from one filesystem must not be visible to another one.

        // convert regular folder to projects.
        Map<String, String[]> properties = new HashMap<String, String[]>(1);
        properties.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
        writeProperties(folderPath, properties);

        // Now create new root directory for virtual file system.
        java.io.File testFsIoRoot2 = EnvironmentContextLocalFSMountStrategy.calculateDirPath(root, "my-ws2");
        java.io.File testRoot2 = new java.io.File(testFsIoRoot2, testRootPath);
        assertTrue(testRoot2.mkdirs());
        // copy all items to new virtual filesystem
        copy(getIoFile(testRootPath), testRoot2, null);

        // Now have the same structure in two different workspaces (tenants).
        // This is the same what we have in cloud infrastructure.

        provider.mount(testFsIoRoot2);

        LocalFileSystem vfs1 = (LocalFileSystem)provider.newInstance(null, eventListenerList);
        LocalFileSystem vfs2 = (LocalFileSystem)provider.newInstance(null, eventListenerList);

        VirtualFile project1 = vfs1.getVirtualFileByPath(folderPath);
        VirtualFile project2 = vfs2.getVirtualFileByPath(folderPath);

        ChangeEventFilter filter1 = ProjectUpdateEventFilter.newFilter(vfs1, project1);
        ChangeEventFilter filter2 = ProjectUpdateEventFilter.newFilter(vfs2, project2);
        final boolean[] notified1 = {false};
        final boolean[] notified2 = {false};

        ProjectUpdateListener listener1 = new ProjectUpdateListener(vfs1.virtualFileToId(project1)) {
            @Override
            public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
                notified1[0] = true;
                super.handleEvent(event);
            }
        };

        ProjectUpdateListener listener2 = new ProjectUpdateListener(vfs2.virtualFileToId(project2)) {
            @Override
            public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
                notified2[0] = true;
                super.handleEvent(event);
            }
        };

        // Register listeners for both projects.
        assertTrue(eventListenerList.addEventListener(filter1, listener1));
        // This one must not be notified.
        assertTrue(eventListenerList.addEventListener(filter2, listener2));

        String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=file";
        Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
        headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));

        // Create file. As result only repository 'db1' get notification.
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, new byte[0], null);
        assertEquals(200, response.getStatus());

        assertTrue("Listener must be notified. ", notified1[0]);
        assertFalse("Listener must not be notified. ", notified2[0]);

        // test removing
        assertTrue(eventListenerList.removeEventListener(filter1, listener1));
        assertTrue(eventListenerList.removeEventListener(filter2, listener2));

        provider.close();
        assertFalse(provider.isMounted());
    }
}
