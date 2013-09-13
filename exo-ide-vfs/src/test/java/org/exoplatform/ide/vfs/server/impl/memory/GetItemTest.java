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
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.shared.*;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GetItemTest.java 77587 2011-12-13 10:42:02Z andrew00x $
 */
public class GetItemTest extends MemoryFileSystemTest {
    private String folderId;
    private String folderPath;
    private String fileId;
    private String filePath;
    private String projectId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder getItemTestFolder = new MemoryFolder(name);
        testRoot.addChild(getItemTestFolder);

        MemoryFolder folder = new MemoryFolder("GetObjectTest_FOLDER");
        getItemTestFolder.addChild(folder);
        folderId = folder.getId();
        folderPath = folder.getPath();

        MemoryFile file = new MemoryFile("GetObjectTest_FILE", "text/plain",
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        getItemTestFolder.addChild(file);
        file.updateProperties(Arrays.<Property>asList(
                new PropertyImpl("MyProperty01", "hello world"),
                new PropertyImpl("MyProperty02", "to be or not to be"),
                new PropertyImpl("MyProperty03", "123"),
                new PropertyImpl("MyProperty04", "true"),
                new PropertyImpl("MyProperty05", Calendar.getInstance().toString()),
                new PropertyImpl("MyProperty06", "123.456")
                                                     ));
        fileId = file.getId();
        filePath = file.getPath();

        MemoryFolder project = new MemoryFolder("GetObjectTest_PROJECT");
        project.setMediaType("text/vnd.ideproject+directory");
        project.updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:projectType", "java"),
                                                         new PropertyImpl("prop1", "val1")));
        assertTrue(project.isProject());
        getItemTestFolder.addChild(project);
        projectId = project.getId();

        memoryContext.putItem(getItemTestFolder);
    }

    public void testGetFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "item/" + fileId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        //log.info(new String(writer.getBody()));
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FILE, item.getItemType());
        assertEquals(fileId, item.getId());
        assertEquals(filePath, item.getPath());
        validateLinks(item);
    }

    public void testGetFileByPath() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "itembypath" + filePath;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FILE, item.getItemType());
        assertEquals(fileId, item.getId());
        assertEquals(filePath, item.getPath());
        validateLinks(item);
    }

    @SuppressWarnings("rawtypes")
    public void testGetFilePropertyFilter() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        // No filter - all properties
        String path = SERVICE_URI + "item/" + fileId;

        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        //log.info(new String(writer.getBody()));
        assertEquals(200, response.getStatus());
        List<Property> properties = ((Item)response.getEntity()).getProperties();
        Map<String, List> m = new HashMap<String, List>(properties.size());
        for (Property p : properties) {
            m.put(p.getName(), p.getValue());
        }
        assertTrue(m.size() >= 6);
        assertTrue(m.containsKey("MyProperty01"));
        assertTrue(m.containsKey("MyProperty02"));
        assertTrue(m.containsKey("MyProperty03"));
        assertTrue(m.containsKey("MyProperty04"));
        assertTrue(m.containsKey("MyProperty05"));
        assertTrue(m.containsKey("MyProperty06"));

        // With filter
        path = SERVICE_URI + "item/" + fileId + '?' + "propertyFilter=" + "MyProperty02";

        response = launcher.service("GET", path, BASE_URI, null, null, null);
        assertEquals(200, response.getStatus());
        m.clear();
        properties = ((Item)response.getEntity()).getProperties();
        for (Property p : properties) {
            m.put(p.getName(), p.getValue());
        }
        assertEquals(1, m.size());
        assertEquals("to be or not to be", m.get("MyProperty02").get(0));
    }

    public void testGetFileNotFound() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "item/" + fileId + "_WRONG_ID_";
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(404, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetFileNoPermissions() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        memoryContext.getItem(fileId).updateACL(Arrays.asList(ace), true);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "item/" + fileId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "item/" + folderId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        //log.info(new String(writer.getBody()));
        assertEquals(200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FOLDER, item.getItemType());
        assertEquals(folderId, item.getId());
        assertEquals(folderPath, item.getPath());
        validateLinks(item);
    }

    public void testGetFolderByPath() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "itembypath" + folderPath;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        //log.info(new String(writer.getBody()));
        assertEquals(200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FOLDER, item.getItemType());
        assertEquals(folderId, item.getId());
        assertEquals(folderPath, item.getPath());
        validateLinks(item);
    }

    public void testGetFolderByPathWithVersionID() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "itembypath" + folderPath + '?' + "versionId=" + "0";
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }

    public void testGetProjectItem() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "item/" + projectId;
        ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);

        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        assertEquals("application/json", response.getContentType().toString());

        ProjectImpl project = (ProjectImpl)response.getEntity();
        validateLinks(project);
        assertEquals("GetObjectTest_PROJECT", project.getName());
        assertEquals(Project.PROJECT_MIME_TYPE, project.getMimeType());
        assertEquals("java", project.getProjectType());
        assertEquals("val1", project.getPropertyValue("prop1"));
        assertEquals(Project.PROJECT_MIME_TYPE, project.getPropertyValue("vfs:mimeType"));
    }
}
