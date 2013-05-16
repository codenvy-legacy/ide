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
 * @version $Id: DeleteTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class DeleteTest extends MemoryFileSystemTest {
    private String     folderId;
    private String     fileId;
    private MemoryFile file;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder deleteTestFolder = new MemoryFolder(name);
        testRoot.addChild(deleteTestFolder);

        MemoryFolder folder = new MemoryFolder("DeleteTest_FOLDER");
        deleteTestFolder.addChild(folder);
        // add child in folder
        MemoryFile childFile = new MemoryFile("file", "text/plain", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        folder.addChild(childFile);
        folderId = folder.getId();

        file = new MemoryFile("DeleteTest_FILE", "text/plain",
                              new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        deleteTestFolder.addChild(file);
        fileId = file.getId();

        memoryContext.putItem(deleteTestFolder);
    }

    public void testDeleteFile() throws Exception {
        String path = SERVICE_URI + "delete/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertNull("File must be removed. ", memoryContext.getItem(fileId));
    }

    public void testDeleteFileLocked() throws Exception {
        String lockToken = file.lock();
        String path = SERVICE_URI + "delete/" + fileId + '?' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertNull("File must be removed. ", memoryContext.getItem(fileId));
    }

    public void testDeleteFileLocked_NoLockToken() throws Exception {
        file.lock();
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "delete/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
        assertNotNull("File must not be removed since it is locked. ", memoryContext.getItem(fileId));
    }

    public void testDeleteFileNoPermissionsFile() throws Exception {
        AccessControlEntry adminACE = new AccessControlEntryImpl();
        adminACE.setPrincipal(new PrincipalImpl("john", Principal.Type.USER));
        adminACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        AccessControlEntry userACE = new AccessControlEntryImpl();
        userACE.setPrincipal(new PrincipalImpl("john", Principal.Type.USER));
        adminACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.READ.value())));
        file.updateACL(Arrays.asList(adminACE, userACE), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "delete/" + fileId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
        assertNotNull("File must not be removed since permissions restriction. ", memoryContext.getItem(fileId));
    }

    public void testDeleteFileWrongID() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "delete/" + fileId + "_WRONG_ID";
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
        assertEquals(404, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testDeleteFolder() throws Exception {
        String path = SERVICE_URI + "delete/" + folderId;
        ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertNull("Folder must be removed. ", memoryContext.getItem(folderId));
    }
}
