/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.shared.dto.File;
import com.codenvy.api.vfs.shared.dto.Lock;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.dto.server.DtoFactory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;

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

        createLock(lockedFilePath, lockToken, Long.MAX_VALUE);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(user, EnumSet.of(BasicPermissions.ALL));
        permissions.put(admin, EnumSet.of(BasicPermissions.READ));
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
        Lock result = (Lock)response.getEntity();
        assertEquals("Lock file not found or lock token invalid. ", result.getLockToken(), readLock(filePath).getLockToken());
        assertTrue("File must be locked. ", ((File)getItem(fileId)).isLocked());
    }

    public void testLockFileAlreadyLocked() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        // lock file must not be updated.
        assertEquals("Lock file not found or lock token invalid. ", lockToken, readLock(lockedFilePath).getLockToken());
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
        assertEquals("Lock must be kept. ", lockToken, readLock(lockedFilePath).getLockToken());
        assertTrue("Lock must be kept.  ", ((File)getItem(lockedFileId)).isLocked());
    }

    public void testUnlockFileWrongLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "unlock/" + lockedFileId + '?' + "lockToken=" + lockToken + "_WRONG";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        assertEquals("Lock must be kept. ", lockToken, readLock(lockedFilePath).getLockToken());
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

    public void testLockTimeout() throws Exception {
        VirtualFile file = mountPoint.getVirtualFileById(fileId);
        file.lock(100);
        assertTrue(file.isLocked());
        Thread.sleep(200);
        assertFalse(file.isLocked());
    }
}
