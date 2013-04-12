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
