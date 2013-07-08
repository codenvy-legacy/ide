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
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.util.*;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class DeleteTest extends LocalFileSystemTest {
    private final String lockToken = "01234567890abcdef";

    private String filePath;
    private String fileId;

    private String lockedFileId;
    private String lockedFilePath;

    private String protectedFilePath;
    private String protectedFileId;

    private String protectedFolderPath;
    private String protectedFolderId;

    private String protectedChildFolderPath;
    private String protectedChildFolderId;

    private String lockedChildFolderPath;
    private String lockedChildFolderId;

    private String folderPath;
    private String folderId;

    private String notEmptyFolderPath;
    private String notEmptyFolderId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(2);
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));

        filePath = createFile(testRootPath, "DeleteTest_File", DEFAULT_CONTENT_BYTES);
        lockedFilePath = createFile(testRootPath, "DeleteTest_LockedFile", DEFAULT_CONTENT_BYTES);
        protectedFilePath = createFile(testRootPath, "DeleteTest_ProtectedFile", DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, "DeleteTest_Folder");
        protectedFolderPath = createDirectory(testRootPath, "DeleteTest_ProtectedFolder");
        createTree(protectedFolderPath, 6, 4, null);
        notEmptyFolderPath = createDirectory(testRootPath, "DeleteTest_NotEmptyFolder");
        createTree(notEmptyFolderPath, 6, 4, null);
        protectedChildFolderPath = createDirectory(testRootPath, "DeleteTest_ProtectedChildFolder");
        createTree(protectedChildFolderPath, 6, 4, null);
        lockedChildFolderPath = createDirectory(testRootPath, "DeleteTest_LockedChildFolder");
        createTree(lockedChildFolderPath, 6, 4, null);

        List<String> l = flattenDirectory(protectedChildFolderPath);
        // Find one child in the list and remove write permission for 'admin'.
        writePermissions(protectedChildFolderPath + '/' + l.get(new Random().nextInt(l.size())), permissions);

        l = flattenDirectory(lockedChildFolderPath);
        // Find one child in the list and lock it.
        for (String s : l) {
            if (createLock(lockedChildFolderPath + '/' + s, lockToken)) {
                break;
            }
        }

        writePermissions(protectedFilePath, permissions);
        writePermissions(protectedFolderPath, permissions);

        createLock(lockedFilePath, lockToken);

        Map<String, String[]> properties = new HashMap<String, String[]>(2);
        properties.put("MyProperty01", new String[]{"foo"});
        properties.put("MyProperty02", new String[]{"bar"});
        writeProperties(filePath, properties);


        fileId = pathToId(filePath);
        lockedFileId = pathToId(lockedFilePath);
        protectedFileId = pathToId(protectedFilePath);
        folderId = pathToId(folderPath);
        protectedFolderId = pathToId(protectedFolderPath);
        protectedChildFolderId = pathToId(protectedChildFolderPath);
        lockedChildFolderId = pathToId(lockedChildFolderPath);
        notEmptyFolderId = pathToId(notEmptyFolderPath);
    }

    public void testDeleteFile() throws Exception {
        String requestPath = SERVICE_URI + "delete/" + fileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertFalse("File must be removed. ", exists(filePath));
        assertNull("Properties must be removed. ", readProperties(filePath));
    }

    public void testDeleteFileLocked() throws Exception {
        String requestPath = SERVICE_URI + "delete/" + lockedFileId + '?' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertFalse("File must be removed. ", exists(lockedFilePath));
        assertNull("Lock file must be removed. ", readLock(lockedFilePath));
    }

    public void testDeleteFileLocked_NoLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "delete/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("File must not be removed. ", exists(lockedFilePath));
        assertEquals(lockToken, readLock(lockedFilePath)); // lock file must not be removed
    }

    public void testDeleteFileHavePermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "delete/" + protectedFileId;
        // File is protected and default principal 'admin' has not write permission.
        // Replace default principal by principal who has write permission.
        ConversationState user = new ConversationState(new Identity("andrew"));
        ConversationState.setCurrent(user);
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(204, response.getStatus());
        assertFalse("File must not be removed. ", exists(protectedFilePath));
        assertNull("ACL file must be removed. ", readPermissions(protectedFilePath)); // file which stored ACL must be removed
    }

    public void testDeleteFileNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "delete/" + protectedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
        assertTrue("File must not be removed. ", exists(protectedFilePath));
    }

    public void testDeleteFileWrongId() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "delete/" + fileId + "_WRONG_ID";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(404, response.getStatus());
        assertTrue(exists(filePath));
    }

    public void testDeleteFolder() throws Exception {
        String requestPath = SERVICE_URI + "delete/" + folderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertFalse("Folder must be removed. ", exists(folderPath));
    }

    public void testDeleteRootFolder() throws Exception {
        String requestPath = SERVICE_URI + "delete/" + ROOT_ID;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(400, response.getStatus()); // must not be able delete root folder
        assertTrue("Folder must not be removed. ", exists("/"));
    }

    public void testDeleteFolderNoPermissions() throws Exception {
        List<String> before = flattenDirectory(protectedFolderPath);
        String requestPath = SERVICE_URI + "delete/" + protectedFolderId;
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Folder must not be removed. ", exists(protectedFolderPath));
        List<String> after = flattenDirectory(protectedFolderPath);
        before.removeAll(after);
        assertTrue(String.format("Missed items: %s", before), before.isEmpty());
    }

    public void testDeleteFolderChildNoPermissions() throws Exception {
        List<String> before = flattenDirectory(protectedChildFolderPath);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "delete/" + protectedChildFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Folder must not be removed. ", exists(protectedChildFolderPath));
        List<String> after = flattenDirectory(protectedChildFolderPath);
        before.removeAll(after);
        assertTrue(String.format("Missed items: %s", before), before.isEmpty());
    }

    public void testDeleteFolderChildLocked() throws Exception {
        List<String> before = flattenDirectory(lockedChildFolderPath);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "delete/" + lockedChildFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        assertTrue("Folder must not be removed. ", exists(lockedChildFolderPath));
        List<String> after = flattenDirectory(lockedChildFolderPath);
        before.removeAll(after);
        assertTrue(String.format("Missed items: %s", before), before.isEmpty());
    }

    public void testDeleteTree() throws Exception {
        String requestPath = SERVICE_URI + "delete/" + notEmptyFolderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertFalse("Folder must be removed. ", exists(notEmptyFolderPath));
    }
}
