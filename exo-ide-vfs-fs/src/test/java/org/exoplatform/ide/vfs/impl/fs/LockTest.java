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
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.LockToken;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class LockTest extends LocalFileSystemTest {
    private final String lockToken = "01234567890abcdef";

    private String folderId;
    private String folderPath;

    private String fileId;
    private String filePath;

    private String protectedFilePath;
    private String protectedFileId;

    private String lockedFileId;
    private String lockedFilePath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, "LockTest_File", DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, "LockTest_Folder");
        lockedFilePath = createFile(testRootPath, "LockTest_LockedFile", DEFAULT_CONTENT_BYTES);
        protectedFilePath = createFile(testRootPath, "LockTes_ProtectedFile", DEFAULT_CONTENT_BYTES);

        createLock(lockedFilePath, lockToken);

        Map<String, Set<BasicPermissions>> permissions = new HashMap<String, Set<BasicPermissions>>(2);
        permissions.put("andrew", EnumSet.of(BasicPermissions.ALL));
        permissions.put("admin", EnumSet.of(BasicPermissions.READ));
        writePermissions(protectedFilePath, permissions);

        fileId = pathToId(filePath);
        folderId = pathToId(folderPath);
        lockedFileId = pathToId(lockedFilePath);
        protectedFileId = pathToId(protectedFilePath);
    }

    public void testLockFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + fileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        LockToken result = (LockToken)response.getEntity();
        assertEquals("Lock file not found or lock token invalid. ", result.getLockToken(), readLock(filePath));
        assertTrue("File must be locked. ", ((File)getItem(fileId)).isLocked());
    }

    public void testLockFileAlreadyLocked() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        // lock file must not be updated.
        assertEquals("Lock file not found or lock token invalid. ", lockToken, readLock(lockedFilePath));
    }

    public void testLockFileNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + protectedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
        assertNull("Lock file must not be created. ", readLock(protectedFilePath));
        assertFalse("File must not be locked. ", ((File)getItem(protectedFileId)).isLocked());
    }

    public void testLockFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + folderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
        // Lock file must not be created
        assertNull(readLock(folderPath));
    }

    public void testUnlockFile() throws Exception {
        String requestPath = SERVICE_URI + "unlock/" + lockedFileId + '?' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertNull("Lock must be removed. ", readLock(lockedFilePath));
        assertFalse("File must be unlocked. ", ((File)getItem(lockedFileId)).isLocked());
    }

    public void testUnlockFileNoLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "unlock/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        assertEquals("Lock must be kept. ", lockToken, readLock(lockedFilePath));
        assertTrue("Lock must be kept.  ", ((File)getItem(lockedFileId)).isLocked());
    }

    public void testUnlockFileWrongLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "unlock/" + lockedFileId + '?' + "lockToken=" + lockToken + "_WRONG";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        assertEquals("Lock must be kept. ", lockToken, readLock(lockedFilePath));
        assertTrue("Lock must be kept.  ", ((File)getItem(lockedFileId)).isLocked());
    }

    public void testUnlockFileNotLocked() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "unlock/" + fileId + '?' + "lockToken=some_token";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        assertFalse(((File)getItem(fileId)).isLocked());
    }
}
