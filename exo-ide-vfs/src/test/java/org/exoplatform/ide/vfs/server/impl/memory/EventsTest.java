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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.everrest.core.impl.ContainerResponse;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.ProjectUpdateListener;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EventsTest extends MemoryFileSystemTest {
    private MemoryFolder testEventsFolder;
    private String       testFolderId;
    private String       testFolderPath;

    private String destinationFolderID;
    private String destinationFolderPath;

    private ChangeEventFilter filter;
    private Listener          listener;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        testEventsFolder = new MemoryFolder(name);
        testRoot.addChild(testEventsFolder);
        MemoryFolder destinationFolder = new MemoryFolder("EventsTest_DESTINATION_FOLDER");
        testRoot.addChild(destinationFolder);

        memoryContext.putItem(testEventsFolder);
        memoryContext.putItem(destinationFolder);

        testFolderId = testEventsFolder.getId();
        testFolderPath = testEventsFolder.getPath();
        destinationFolderID = destinationFolder.getId();
        destinationFolderPath = destinationFolder.getPath();

        assertNotNull(eventListenerList);
        listener = new Listener();
        filter = ChangeEventFilter.ANY_FILTER;
        eventListenerList.addEventListener(filter, listener);
    }

    @Override
    protected void tearDown() throws Exception {
        assertTrue("Unable remove listener. ", eventListenerList.removeEventListener(filter, listener));
        super.tearDown();
    }

    private class Listener implements EventListener {
        List<ChangeEvent> events = new ArrayList<ChangeEvent>();

        @Override
        public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
            log.info(event);
            events.add(event);
        }
    }

    public void testCreateFile() throws Exception {
        String name = "testCreateFile";
        String content = "test create file";
        String path = SERVICE_URI + "file/" + testFolderId + '?' + "name=" + name;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("text/plain;charset=utf8");
        headers.put("Content-Type", contentType);
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
        assertEquals(200, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.CREATED, listener.events.get(0).getType());
        assertEquals(testFolderPath + '/' + name, listener.events.get(0).getItemPath());
        assertEquals(contentType.get(0), listener.events.get(0).getMimeType());
    }

    public void testCreateFolder() throws Exception {
        String name = "testCreateFolder";
        String path = SERVICE_URI + "folder/" + testFolderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.CREATED, listener.events.get(0).getType());
        assertEquals(testFolderPath + '/' + name, listener.events.get(0).getItemPath());
    }

    public void testCopy() throws Exception {
        String fileId = createFile(testEventsFolder, "CopyTest_FILE", "text/plain", DEFAULT_CONTENT);

        String path = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + destinationFolderID;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);

        assertEquals(200, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.CREATED, listener.events.get(0).getType());
        assertEquals(destinationFolderPath + '/' + "CopyTest_FILE", listener.events.get(0).getItemPath());
        assertEquals("text/plain", listener.events.get(0).getMimeType());
    }

    public void testMove() throws Exception {
        String fileId = createFile(testEventsFolder, "MoveTest_FILE", "text/plain", DEFAULT_CONTENT);

        String path = SERVICE_URI + "move/" + fileId + '?' + "parentId=" + destinationFolderID;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.MOVED, listener.events.get(0).getType());
        assertEquals(destinationFolderPath + '/' + "MoveTest_FILE", listener.events.get(0).getItemPath());
        assertEquals("text/plain", listener.events.get(0).getMimeType());
    }

    public void testUpdateContent() throws Exception {
        String fileId = createFile(testEventsFolder, "UpdateContent_FILE", "text/plain", DEFAULT_CONTENT);

        String path = SERVICE_URI + "content/" + fileId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("application/xml");
        headers.put("Content-Type", contentType);
        String content = "<?xml version='1.0'><root/>";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
        assertEquals(204, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.CONTENT_UPDATED, listener.events.get(0).getType());
        assertEquals(testFolderPath + '/' + "UpdateContent_FILE", listener.events.get(0).getItemPath());
        assertEquals("application/xml", listener.events.get(0).getMimeType());
    }

    public void testUpdateProperties() throws Exception {
        String fileId = createFile(testEventsFolder, "UpdateProperties_FILE", "text/plain", DEFAULT_CONTENT);

        String path = SERVICE_URI + "item/" + fileId;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("application/json");
        headers.put("Content-Type", contentType);
        String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, properties.getBytes(), null);
        assertEquals(200, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.PROPERTIES_UPDATED, listener.events.get(0).getType());
        assertEquals(testFolderPath + '/' + "UpdateProperties_FILE", listener.events.get(0).getItemPath());
        assertEquals("text/plain", listener.events.get(0).getMimeType());
    }

    public void testDelete() throws Exception {
        String fileId = createFile(testEventsFolder, "Delete_FILE", "text/plain", DEFAULT_CONTENT);

        String path = SERVICE_URI + "delete/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.DELETED, listener.events.get(0).getType());
        assertEquals(testFolderPath + '/' + "Delete_FILE", listener.events.get(0).getItemPath());
        assertEquals("text/plain", listener.events.get(0).getMimeType());
    }

    public void testRename() throws Exception {
        String fileId = createFile(testEventsFolder, "RenameTest_FILE", "text/plain", DEFAULT_CONTENT);

        String path = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);

        assertEquals(200, response.getStatus());

        assertEquals(1, listener.events.size());
        assertEquals(ChangeEvent.ChangeType.RENAMED, listener.events.get(0).getType());
        assertEquals(testFolderPath + '/' + "_FILE_NEW_NAME_", listener.events.get(0).getItemPath());
        assertEquals("text/plain", listener.events.get(0).getMimeType());
        VirtualFileSystem vfs = listener.events.get(0).getVirtualFileSystem();
        vfs.updateItem(fileId, Collections.<Property>emptyList(), null);
    }

    public void testStartProjectUpdateListener() throws Exception {
        MemoryFolder project = new MemoryFolder("project");
        project.updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:mimeType", "text/vnd.ideproject+directory")));
        assertTrue(project.isProject());
        testEventsFolder.addChild(project);
        memoryContext.putItem(project);

        int configuredListeners = eventListenerList.size();
        String path = SERVICE_URI + "watch/start/" + project.getId();
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);

        assertEquals(204, response.getStatus());
        assertEquals("Project update listener must be added. ", configuredListeners + 1, eventListenerList.size());
    }

    public void testStopProjectUpdateListener() throws Exception {
        MemoryFolder project = new MemoryFolder("project");
        project.updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:mimeType", "text/vnd.ideproject+directory")));
        assertTrue(project.isProject());
        testEventsFolder.addChild(project);
        memoryContext.putItem(project);

        eventListenerList.addEventListener(ProjectUpdateEventFilter.newFilter(MY_WORKSPACE_ID, project),
                                           new ProjectUpdateListener(project.getId()));

        int configuredListeners = eventListenerList.size();
        String path = SERVICE_URI + "watch/stop/" + project.getId();
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);

        assertEquals(204, response.getStatus());
        assertEquals("Project update listener must be removed. ", configuredListeners - 1, eventListenerList.size());
    }

    public void testProjectUpdateListener() throws Exception {
        MemoryFolder project = new MemoryFolder("project");
        project.updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:mimeType", "text/vnd.ideproject+directory")));
        assertTrue(project.isProject());
        testEventsFolder.addChild(project);
        memoryContext.putItem(project);

        eventListenerList.addEventListener(ProjectUpdateEventFilter.newFilter(MY_WORKSPACE_ID, project),
                                           new ProjectUpdateListener(project.getId()));

        String name = "testProjectUpdateListenerFolder";
        String path = SERVICE_URI + "folder/" + project.getId() + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        List<Property> properties = project.getProperties(PropertyFilter.valueOf("vfs:lastUpdateTime"));
        assertEquals(1, properties.size());
        assertFalse("Lst update time must be changed. ", "0".equals(properties.get(0).getValue().get(0)));
    }

    private String createFile(MemoryFolder parent, String name, String mediaType, String data) throws Exception {
        MemoryFile file = new MemoryFile(name, mediaType,
                                         new ByteArrayInputStream(data.getBytes()));
        parent.addChild(file);
        memoryContext.putItem(file);
        return file.getId();
    }
}
