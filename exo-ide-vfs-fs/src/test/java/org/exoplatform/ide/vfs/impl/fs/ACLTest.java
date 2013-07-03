/*
 * Copyright (C) 2013 eXo Platform SAS.
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
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ACLTest extends LocalFileSystemTest {
    private final String lockToken = "01234567890abcdef";

    private String fileId;
    private String filePath;

    private String lockedFilePath;
    private String lockedFileId;

    private Map<Principal, Set<BasicPermissions>> permissions;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, "ACLTest_File", DEFAULT_CONTENT_BYTES);
        lockedFilePath = createFile(testRootPath, "ACLTest_LockedFile", DEFAULT_CONTENT_BYTES);

        permissions = new HashMap<Principal, Set<BasicPermissions>>(3);
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER),
                        EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE, BasicPermissions.UPDATE_ACL));
        permissions.put(new PrincipalImpl("john", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));

        writePermissions(filePath, permissions);
        createLock(lockedFilePath, lockToken);

        fileId = pathToId(filePath);
        lockedFileId = pathToId(lockedFilePath);
    }

    public void testGetACL() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "acl/" + fileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        @SuppressWarnings("unchecked")
        List<AccessControlEntry> acl = (List<AccessControlEntry>)response.getEntity();
        assertEquals(permissions, toMap(acl));
    }

    public void testGetACLNoPermissions() throws Exception {
        // Remove permissions for current user, see LocalFileSystemTest.setUp()
        permissions.remove(new PrincipalImpl("admin", Principal.Type.USER));
        writePermissions(filePath, permissions);
        // Request must fail since we have not permissions any more to read ACL.
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "acl/" + fileId;
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACL() throws Exception {
        String requestPath = SERVICE_URI + "acl/" + fileId;
        // Give write permission for john. No changes for other users.
        String acl = "[{\"principal\":{\"name\":\"john\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        permissions.get(new PrincipalImpl("john", Principal.Type.USER)).add(BasicPermissions.WRITE);
        // check backend
        Map<? extends Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(filePath);
        log.info(updatedAccessList);
        assertEquals(permissions, updatedAccessList);

        // check API
        assertEquals(permissions,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLOverride() throws Exception {
        String requestPath = SERVICE_URI + "acl/" + fileId + '?' + "override=" + true;
        // Give 'all' rights to admin and take away all rights for other users.
        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"all\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        permissions.remove(new PrincipalImpl("andrew", Principal.Type.USER));
        permissions.remove(new PrincipalImpl("john", Principal.Type.USER));

        // check backend
        Map<Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(filePath);
        log.info(updatedAccessList);
        assertEquals(permissions, updatedAccessList);

        // check API
        assertEquals(permissions,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testRemoveACL() throws Exception {
        // Send empty list of permissions. It means remove all restriction.
        String requestPath = SERVICE_URI + "acl/" + fileId + '?' + "override=" + true;
        String acl = "[]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        // check backend
        assertNull(readPermissions(filePath));

        // check API
        List<AccessControlEntry> updatedAcl =
                (List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity();
        // assertTrue(updatedAcl.isEmpty()); TODO
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLHavePermissions() throws Exception {
        // Remove permissions for current user, see LocalFileSystemTest.setUp()
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
        writePermissions(filePath, permissions);

        String requestPath = SERVICE_URI + "acl/" + fileId;
        // Give write permission for john. No changes for other users.
        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        // File is protected and default principal 'admin' has not update_acl permission.
        // Replace default principal by principal who has write permission.
        ConversationState user = new ConversationState(new Identity("andrew"));
        ConversationState.setCurrent(user);
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        permissions.get(new PrincipalImpl("admin", Principal.Type.USER)).add(BasicPermissions.WRITE);
        // check backend
        Map<Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(filePath);
        log.info(updatedAccessList);
        assertEquals(permissions, updatedAccessList);

        // check API
        assertEquals(permissions,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLNoPermissions() throws Exception {
        // Remove permissions for current user, see LocalFileSystemTest.setUp()
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));
        writePermissions(filePath, permissions);

        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"all\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));

        // Request must fail since we have not permissions any more to update ACL.
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "acl/" + fileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), writer, null);
        assertEquals(403, response.getStatus());
        log.info(new String(writer.getBody()));

        // ACL must not be changed.
        // check backend
        Map<Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(filePath);
        log.info(updatedAccessList);
        assertEquals(permissions, updatedAccessList);

        // check API
        assertEquals(permissions,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLLocked() throws Exception {
        String acl = "[{\"principal\":{\"name\":\"john\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}," +
                     "{\"principal\":{\"name\":\"any\",\"type\":\"USER\"},\"permissions\":null}," +
                     "{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));

        String requestPath = SERVICE_URI + "acl/" + lockedFileId + '?' + "lockToken=" + lockToken + "&override=" + true;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);

        assertEquals(204, response.getStatus());

        Map<Principal, Set<BasicPermissions>> thisTestAccessList = new HashMap<Principal, Set<BasicPermissions>>(2);
        thisTestAccessList.put(new PrincipalImpl("john", Principal.Type.USER), EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
        thisTestAccessList.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));

        // check backend
        Map<Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(lockedFilePath);
        log.info(updatedAccessList);
        assertEquals(thisTestAccessList, updatedAccessList);

        // check API
        assertEquals(thisTestAccessList,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLLocked_NoLockToken() throws Exception {
        String acl = "[{\"principal\":{\"name\":\"john\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}," +
                     "{\"principal\":{\"name\":\"any\",\"type\":\"USER\"},\"permissions\":null}," +
                     "{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));

        String requestPath = SERVICE_URI + "acl/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);

        assertEquals(423, response.getStatus());

        // ACL must not be updated.
        List<AccessControlEntry> updatedAcl =
                (List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity();
        // assertTrue(updatedAcl.isEmpty()); TODO
    }

    private Map<Principal, Set<BasicPermissions>> toMap(List<AccessControlEntry> acl) {
        Map<Principal, Set<BasicPermissions>> map = new HashMap<Principal, Set<BasicPermissions>>(acl.size());
        for (AccessControlEntry ace : acl) {
            Principal principal = new PrincipalImpl(ace.getPrincipal());
            Set<BasicPermissions> permissions = map.get(principal);
            if (permissions == null) {
                permissions = EnumSet.noneOf(BasicPermissions.class);
                map.put(principal, permissions);
            }
            Set<String> strPermissions = ace.getPermissions();
            if (!(strPermissions == null || strPermissions.isEmpty())) {
                for (String strPermission : strPermissions) {
                    permissions.add(BasicPermissions.fromValue(strPermission));
                }
            }
        }
        return map;
    }

    public void testReadWrite() throws Exception {
        AccessControlList accessControlList = new AccessControlList(permissions);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(bOut);
        accessControlList.write(dOut);
        AccessControlList newAccessControlList = AccessControlList.read(
                new DataInputStream(new ByteArrayInputStream(bOut.toByteArray())));
        assertEquals(accessControlList.getPermissionMap(), newAccessControlList.getPermissionMap());
    }
}
