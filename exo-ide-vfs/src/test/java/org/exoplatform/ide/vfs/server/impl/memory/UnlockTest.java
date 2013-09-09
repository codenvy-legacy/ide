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

import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: UnlockTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class UnlockTest extends MemoryFileSystemTest {
    private String lockedFileId;
    private String notLockedFileId;
    private String fileLockToken;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder unlockTestFolder = new MemoryFolder(name);
        testRoot.addChild(unlockTestFolder);

        MemoryFile lockedFile = new MemoryFile("UnlockTest_LOCKED", "text/plain",
                                               new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        unlockTestFolder.addChild(lockedFile);
        fileLockToken = lockedFile.lock();
        lockedFileId = lockedFile.getId();

        MemoryFile notLockedFile = new MemoryFile("UnlockTest_NOTLOCKED", "text/plain",
                                                  new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        unlockTestFolder.addChild(notLockedFile);
        notLockedFileId = notLockedFile.getId();

        memoryContext.putItem(unlockTestFolder);
    }

    public void testUnlockFile() throws Exception {
        String path = SERVICE_URI + "unlock/" + lockedFileId + '?' + "lockToken=" + fileLockToken;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        MemoryFile file = (MemoryFile)memoryContext.getItem(lockedFileId);
        assertFalse("Lock must be removed. ", file.isLocked());
    }

    public void testUnlockFileNoLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "unlock/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testUnlockFileWrongLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "unlock/" + lockedFileId + '?' + "lockToken=" + fileLockToken + "_WRONG";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
    }


    public void testUnlockFileNotLocked() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "unlock/" + notLockedFileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
    }
}
