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
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CreateTest.java 75317 2011-10-19 15:02:05Z andrew00x $
 */
public class CreateTest extends MemoryFileSystemTest {
    private String       createTestFolderId;
    private String       createTestFolderPath;
    private MemoryFolder createTestFolder;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        createTestFolder = new MemoryFolder(name);
        testRoot.addChild(createTestFolder);
        createTestFolderId = createTestFolder.getId();
        createTestFolderPath = createTestFolder.getPath();
        memoryContext.putItem(createTestFolder);
    }

    public void testCreateFile() throws Exception {
        String name = "testCreateFile";
        String content = "test create file";
        String path = SERVICE_URI + "file/" + createTestFolderId + '?' + "name=" + name; //
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("text/plain;charset=utf8");
        headers.put("Content-Type", contentType);

        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
        assertEquals(200, response.getStatus());
        String expectedPath = createTestFolderPath + "/" + name;
        assertNotNull("File was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        checkFileContext(content, "text/plain;charset=utf8", file);
    }

    public void testCreateFileInRoot() throws Exception {
        String name = "testCreateFileInRoot";
        String content = "test create file";
        String path = SERVICE_URI + "file/" + memoryContext.getRoot().getId() + '?' + "name=" + name;
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> contentType = new ArrayList<String>();
        contentType.add("text/plain;charset=utf8");
        headers.put("Content-Type", contentType);

        ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
        assertEquals(200, response.getStatus());
        String expectedPath = "/" + name;
        assertNotNull("File was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        checkFileContext(content, "text/plain;charset=utf8", file);
    }

    public void testCreateFileNoContent() throws Exception {
        String name = "testCreateFileNoContent";
        String path = SERVICE_URI + "file/" + createTestFolderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);

        assertEquals(200, response.getStatus());
        String expectedPath = createTestFolderPath + "/" + name;
        assertNotNull("File was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        ContentStream contentStream = file.getContent();
        assertEquals(0, contentStream.getLength());
    }

    public void testCreateFileNoMediaType() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFileNoMediaType";
        String content = "test create file without media type";
        String path = SERVICE_URI + "file/" + createTestFolderId + '?' + "name=" + name;

        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, content.getBytes(), writer, null);
        assertEquals(200, response.getStatus());
        String expectedPath = createTestFolderPath + "/" + name;
        assertNotNull("File was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
        MemoryFile file = (MemoryFile)memoryContext.getItemByPath(expectedPath);
        checkFileContext(content, MediaType.APPLICATION_OCTET_STREAM, file);
    }

    public void testCreateFileNoName() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "file/" + createTestFolderId;
        ContainerResponse response =
                launcher.service("POST", path, BASE_URI, null, DEFAULT_CONTENT.getBytes(), writer, null);
        assertEquals(400, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testCreateFileNoPermissions() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        createTestFolder.updateACL(Arrays.asList(ace), true);

        String name = "testCreateFileNoPermissions";
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "file/" + createTestFolderId + '?' + "name=" + name;
        ContainerResponse response =
                launcher.service("POST", path, BASE_URI, null, DEFAULT_CONTENT.getBytes(), writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testCreateFileWrongParent() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFileWrongParent";
        String path = SERVICE_URI + "file/" + createTestFolderId + "_WRONG_ID" + '?' + "name=" + name;
        ContainerResponse response =
                launcher.service("POST", path, BASE_URI, null, DEFAULT_CONTENT.getBytes(), writer, null);
        assertEquals(404, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testCreateFolder() throws Exception {
        String name = "testCreateFolder";
        String path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = createTestFolderPath + "/" + name;
        assertNotNull("Folder was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
    }

    public void testCreateFolderInRoot() throws Exception {
        String name = "testCreateFolderInRoot";
        String path = SERVICE_URI + "folder/" + memoryContext.getRoot().getId() + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = "/" + name;
        assertNotNull("Folder was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
    }

    public void testCreateFolderNoName() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "folder/" + createTestFolderId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(400, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testCreateFolderNoPermissions() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        createTestFolder.updateACL(Arrays.asList(ace), true);

        String name = "testCreateFolderNoPermissions";
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testCreateFolderWrongParent() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String name = "testCreateFolderWrongParent";
        String path = SERVICE_URI + "folder/" + createTestFolderId + "_WRONG_ID" + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(404, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testCreateFolderHierarchy() throws Exception {
        String name = "testCreateFolderHierarchy/1/2/3/4/5";
        String path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = createTestFolderPath + "/" + name;
        assertNotNull("Folder was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
    }

    public void testCreateFolderHierarchy2() throws Exception {
        // create some items in path
        String name = "testCreateFolderHierarchy/1/2/3";
        String path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null, null);
        assertEquals(200, response.getStatus());
        // create the rest of path
        name += "/4/5";
        path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
        response = launcher.service("POST", path, BASE_URI, null, null, null, null);
        assertEquals(200, response.getStatus());
        String expectedPath = createTestFolderPath + "/" + name;
        assertNotNull("Folder was not created in expected location. ", memoryContext.getItemByPath(expectedPath));
    }

    public void testCreateProject() throws Exception {
        String name = "testCreateProject";
        String properties = "[{\"name\":\"vfs:projectType\", \"value\":[\"java\"]}]";
        //
        String path = SERVICE_URI + "project/" + createTestFolderId + '?' + "name=" + name + '&' + "type=" + "java";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, properties.getBytes(), null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String expectedPath = createTestFolderPath + "/" + name;
        MemoryFolder project = (MemoryFolder)memoryContext.getItemByPath(expectedPath);
        assertNotNull("File was not created in expected location. ", project);
        List<String> values = project.getProperties(PropertyFilter.valueOf("vfs:projectType")).get(0).getValue();
        assertEquals("java", values.get(0));
        assertEquals("text/vnd.ideproject+directory", project.getMediaType());
    }

    public void testCreateProjectInsideProject() throws Exception {
        String name = "testCreateProjectInsideProject";
        MemoryFolder parentFolder = (MemoryFolder)memoryContext.getItem(createTestFolderId);
        MemoryFolder parentProject = new MemoryFolder(name);
        parentProject.setMediaType("text/vnd.ideproject+directory");
        assertTrue(parentProject.isProject());
        parentFolder.addChild(parentProject);
        memoryContext.putItem(parentProject);

        String path = SERVICE_URI + "project/" + parentProject.getId() + '?' + "name=" + "childProject" + '&' +
                      "type=" + "java";

        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));

        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, null, null);
        log.info(response.getEntity());
        assertEquals("Unexpected status " + response.getStatus(), 200, response.getStatus());
        String expectedPath = parentProject.getPath() + "/childProject";
        MemoryFolder project = (MemoryFolder)memoryContext.getItemByPath(expectedPath);
        assertNotNull("File was not created in expected location. ", project);
        List<String> values = project.getProperties(PropertyFilter.valueOf("vfs:projectType")).get(0).getValue();
        assertEquals("java", values.get(0));
        assertEquals("text/vnd.ideproject+directory", project.getMediaType());
    }
}
