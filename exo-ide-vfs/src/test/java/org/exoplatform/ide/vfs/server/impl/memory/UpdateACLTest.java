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
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: UpdateACLTest.java 75317 2011-10-19 15:02:05Z andrew00x $
 */
public class UpdateACLTest extends MemoryFileSystemTest {
    private String objectId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String name = getClass().getName();
        MemoryFolder updateAclTestFolder = new MemoryFolder(name);
        testRoot.addChild(updateAclTestFolder);

        MemoryFile file = new MemoryFile("UpdateACLTest_FILE", "text/plain",
                                         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
        updateAclTestFolder.addChild(file);
        objectId = file.getId();

        memoryContext.putItem(updateAclTestFolder);
    }

    public void testUpdateAcl() throws Exception {
        String path = SERVICE_URI + "acl/" + objectId;
        String body = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}," + //
                      "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, body.getBytes(), null);
        assertEquals(204, response.getStatus());
        List<AccessControlEntry> acl = memoryContext.getItem(objectId).getACL();
        Map<String, Set<String>> m = toMap(acl);
        assertEquals(m.get("admin"), new HashSet<String>(Arrays.asList("all")));
        assertEquals(m.get("john"), new HashSet<String>(Arrays.asList("read")));
    }

    public void testUpdateAclOverride() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal("anonymous");
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        memoryContext.getItem(objectId).updateACL(Arrays.asList(ace), false);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "acl/" + objectId + '?' + "override=" + true;
        String body = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}," + //
                      "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, body.getBytes(), writer, null);
        assertEquals(204, response.getStatus());

        List<AccessControlEntry> acl = memoryContext.getItem(objectId).getACL();
        Map<String, Set<String>> m = toMap(acl);
        assertEquals(m.get("admin"), new HashSet<String>(Arrays.asList("all")));
        assertEquals(m.get("john"), new HashSet<String>(Arrays.asList("read")));
        assertNull("Anonymous permissions must be removed.", m.get("anonymous"));
    }

    public void testUpdateAclMerge() throws Exception {
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal("anonymous");
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        memoryContext.getItem(objectId).updateACL(Arrays.asList(ace), false);

        String path = SERVICE_URI + "acl/" + objectId;
        String body = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}," + //
                      "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, body.getBytes(), null);
        assertEquals(204, response.getStatus());

        List<AccessControlEntry> acl = memoryContext.getItem(objectId).getACL();
        Map<String, Set<String>> m = toMap(acl);
        assertEquals(m.get("admin"), new HashSet<String>(Arrays.asList("all")));
        assertEquals(m.get("john"), new HashSet<String>(Arrays.asList("read")));
        assertEquals(m.get("anonymous"), new HashSet<String>(Arrays.asList("all")));
    }

    public void testUpdateAclLocked() throws Exception {
        String lockToken = ((MemoryFile)memoryContext.getItem(objectId)).lock();
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "acl/" + objectId + '?' + "lockToken=" + lockToken;
        String body = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}," + //
                      "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, body.getBytes(), writer, null);
        assertEquals(204, response.getStatus());

        List<AccessControlEntry> acl = memoryContext.getItem(objectId).getACL();
        Map<String, Set<String>> m = toMap(acl);
        assertEquals(m.get("admin"), new HashSet<String>(Arrays.asList("all")));
        assertEquals(m.get("john"), new HashSet<String>(Arrays.asList("read")));
    }

    public void testUpdateAclLocked_NoLockToken() throws Exception {
        ((MemoryFile)memoryContext.getItem(objectId)).lock();
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "acl/" + objectId;
        String body = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}," + //
                      "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, body.getBytes(), writer, null);
        assertEquals(423, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    public void testUpdateAclNoPermissions() throws Exception {
        // Remove permissions for any users except 'admin'.
        AccessControlEntry ace = new AccessControlEntryImpl();
        ace.setPrincipal("admin");
        ace.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
        memoryContext.getItem(objectId).updateACL(Arrays.asList(ace), true);

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String path = SERVICE_URI + "acl/" + objectId;
        String body = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}," + //
                      "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", path, BASE_URI, h, body.getBytes(), writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));
    }

    private Map<String, Set<String>> toMap(List<AccessControlEntry> acl) {
        Map<String, Set<String>> m = new HashMap<String, Set<String>>();
        for (AccessControlEntry e : acl) {
            m.put(e.getPrincipal(), e.getPermissions());
        }
        return m;
    }
}
