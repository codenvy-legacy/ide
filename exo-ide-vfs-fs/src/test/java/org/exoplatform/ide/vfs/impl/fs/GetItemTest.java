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
package org.exoplatform.ide.vfs.impl.fs;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.*;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class GetItemTest extends LocalFileSystemTest {
    private Map<String, String[]> properties;

    private String fileId;
    private String filePath;

    private String protectedFileId;
    private String protectedFilePath;

    private String protectedParentId;
    private String protectedParentPath;

    private String folderId;
    private String folderPath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, "GetObjectTest_File", DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, "GetObjectTest_Folder");
        protectedFilePath = createFile(testRootPath, "GetObjectTest_ProtectedFile", DEFAULT_CONTENT_BYTES);
        String protectedParent = createDirectory(testRootPath, "GetObjectTest_ProtectedParent");
        protectedParentPath = createFile(protectedParent, "GetObjectTest_ProtectedChildFile", DEFAULT_CONTENT_BYTES);

        properties = new HashMap<String, String[]>(2);
        properties.put("MyProperty01", new String[]{"hello world"});
        properties.put("MyProperty02", new String[]{"to be or not to be"});
        writeProperties(filePath, properties);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(1);
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        writePermissions(protectedFilePath, permissions);
        writePermissions(protectedParent, permissions);

        fileId = pathToId(filePath);
        protectedFileId = pathToId(protectedFilePath);
        folderId = pathToId(folderPath);
        protectedParentId = pathToId(protectedParentPath);
    }

    public void testGetFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "item/" + fileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FILE, item.getItemType());
        assertEquals(fileId, item.getId());
        assertEquals(filePath, item.getPath());
        validateLinks(item);
    }

    public void testGetFileByPath() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "itembypath" + filePath;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FILE, item.getItemType());
        assertEquals(fileId, item.getId());
        assertEquals(filePath, item.getPath());
        validateLinks(item);
    }

   /*
    * --- Versions is not supported. Parameter 'versionId' must be absent or equals to '0'. ---
    */

    public void testGetFileByPathWithVersionId() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "itembypath" + filePath + '?' + "versionId=" + 0;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FILE, item.getItemType());
        assertEquals(fileId, item.getId());
        assertEquals(filePath, item.getPath());
        validateLinks(item);
    }

    public void testGetFileByPathWithVersionId2() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "itembypath" + filePath + '?' + "versionId=" + 1; // must fail
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(405, response.getStatus());
    }

   /*
    * ---
    */

    @SuppressWarnings("rawtypes")
    public void testGetFilePropertyFilter() throws Exception {
        Iterator<Map.Entry<String, String[]>> iterator = properties.entrySet().iterator();
        Map.Entry<String, String[]> e1 = iterator.next();
        Map.Entry<String, String[]> e2 = iterator.next();

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        // No filter - all properties
        String requestPath = SERVICE_URI + "item/" + fileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Item i = (Item)response.getEntity();

        assertEquals(e1.getValue()[0], i.getPropertyValue(e1.getKey()));
        assertEquals(e2.getValue()[0], i.getPropertyValue(e2.getKey()));

        // With filter
        requestPath = SERVICE_URI + "item/" + fileId + '?' + "propertyFilter=" + e1.getKey();

        response = launcher.service("GET", requestPath, BASE_URI, null, null, null);
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        i = (Item)response.getEntity();

        assertEquals(e1.getValue()[0], i.getPropertyValue(e1.getKey()));
        assertNull(i.getPropertyValue(e2.getKey()));
    }

    public void testGetFileNotFound() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "item/" + fileId + "_WRONG_ID_";
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(404, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetFileHavePermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "item/" + protectedFileId;
        // Replace default principal by principal who has read permission.
        ConversationState user = new ConversationState(new Identity("andrew"));
        ConversationState.setCurrent(user);
        // ---
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FILE, item.getItemType());
        assertEquals(protectedFileId, item.getId());
        assertEquals(protectedFilePath, item.getPath());
        validateLinks(item);
    }

    public void testGetFileNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "item/" + protectedFileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetFileParentNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "item/" + protectedParentId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetFileByPathNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "itembypath" + protectedFilePath;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testGetFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "item/" + folderId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FOLDER, item.getItemType());
        assertEquals(folderId, item.getId());
        assertEquals(folderPath, item.getPath());
        validateLinks(item);
    }

    public void testGetFolderByPath() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "itembypath" + folderPath;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Item item = (Item)response.getEntity();
        assertEquals(ItemType.FOLDER, item.getItemType());
        assertEquals(folderId, item.getId());
        assertEquals(folderPath, item.getPath());
        validateLinks(item);
    }

    public void testGetFolderByPathWithVersionId() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        // Parameter 'versionId' is not acceptable for folders, must be absent.
        String requestPath = SERVICE_URI + "itembypath" + folderPath + '?' + "versionId=" + 1;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
    }
}
