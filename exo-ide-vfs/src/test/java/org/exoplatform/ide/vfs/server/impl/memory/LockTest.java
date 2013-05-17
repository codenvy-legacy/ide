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
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: LockTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class LockTest extends MemoryFileSystemTest {
    private String folderId;
    private String fileId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder lockTestFolder = new MemoryFolder(name);
        testRoot.addChild(lockTestFolder);

        MemoryFolder folder = new MemoryFolder("LockTest_FOLDER");
        lockTestFolder.addChild(folder);
        folderId = folder.getId();

        MemoryFile file = new MemoryFile("LockTest_FILE", "text/plain",
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        lockTestFolder.addChild(file);
        fileId = file.getId();

        memoryContext.putItem(lockTestFolder);
    }

    public void testLockFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "lock/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(200, response.getStatus());
        log.info(new String(writer.getBody()));
        MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
        assertTrue("File must be locked. ", file.isLocked());
        validateLinks(getItem(fileId));
    }

    public void testLockFileAlreadyLocked() throws Exception {
        MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
        file.lock();
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "lock/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testLockFileNoPermissions() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal(new PrincipalImpl("admin", Principal.Type.USER));
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        memoryContext.getItem(fileId).updateACL(Arrays.asList(ace), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "lock/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
        assertFalse("File must not be locked. ", file.isLocked());
    }

    public void testLockFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "lock/" + folderId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(400, response.getStatus());
    }
}
