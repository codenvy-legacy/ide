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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.core.user.User;
import com.codenvy.api.core.user.UserImpl;
import com.codenvy.api.core.user.UserState;
import com.codenvy.api.vfs.shared.dto.AccessControlEntry;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.dto.server.DtoFactory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

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

import static com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
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

        permissions = new HashMap<>(3);
        Principal user1 = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal user2 = DtoFactory.getInstance().createDto(Principal.class).withName("john").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(admin, EnumSet.of(BasicPermissions.ALL));
        permissions.put(user1, EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE, BasicPermissions.UPDATE_ACL));
        permissions.put(user2, EnumSet.of(BasicPermissions.READ));

        writePermissions(filePath, permissions);
        createLock(lockedFilePath, lockToken, Long.MAX_VALUE);

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
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.remove(principal);

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
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("john").withType(Principal.Type.USER);
        permissions.get(principal).add(BasicPermissions.WRITE);
        // check backend
        Map<? extends Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(filePath);
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
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        Principal user1 = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal user2 = DtoFactory.getInstance().createDto(Principal.class).withName("john").withType(Principal.Type.USER);
        permissions.remove(user1);
        permissions.remove(user2);

        // check backend
        Map<Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(filePath);
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
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        // check backend
        assertNull(readPermissions(filePath));

        // check API
        List<AccessControlEntry> updatedAcl =
                (List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity();
        // TODO: test files because we always provide "default ACL" at the moment.
        // It is temporary solution before we get client side tool to manage ACL.
        // assertTrue(updatedAcl.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLHavePermissions() throws Exception {
        // Remove permissions for current user, see LocalFileSystemTest.setUp()
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(principal, EnumSet.of(BasicPermissions.READ));
        writePermissions(filePath, permissions);

        String requestPath = SERVICE_URI + "acl/" + fileId;
        // Give write permission for john. No changes for other users.
        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}]";
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        // File is protected and default principal 'andrew' has not update_acl permission.
        // Replace default principal by principal who has write permission.
        User user = new UserImpl("andrew");
        UserState.set(new UserState(user));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        principal = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.get(principal).add(BasicPermissions.WRITE);
        // check backend
        Map<Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(filePath);
        assertEquals(permissions, updatedAccessList);

        // check API
        assertEquals(permissions,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLNoPermissions() throws Exception {
        // Remove permissions for current user, see LocalFileSystemTest.setUp()
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(principal, EnumSet.of(BasicPermissions.READ));
        writePermissions(filePath, permissions);

        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"all\"]}]";
        Map<String, List<String>> h = new HashMap<>(1);
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
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));

        String requestPath = SERVICE_URI + "acl/" + lockedFileId + '?' + "lockToken=" + lockToken + "&override=" + true;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);

        assertEquals(204, response.getStatus());

        Map<Principal, Set<BasicPermissions>> thisTestAccessList = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("john").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        thisTestAccessList.put(user, EnumSet.of(BasicPermissions.READ, BasicPermissions.WRITE));
        thisTestAccessList.put(admin, EnumSet.of(BasicPermissions.READ));

        // check backend
        Map<Principal, Set<BasicPermissions>> updatedAccessList = readPermissions(lockedFilePath);
        assertEquals(thisTestAccessList, updatedAccessList);

        // check API
        assertEquals(thisTestAccessList,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLLockedNoLockToken() throws Exception {
        String acl = "[{\"principal\":{\"name\":\"john\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}," +
                     "{\"principal\":{\"name\":\"any\",\"type\":\"USER\"},\"permissions\":null}," +
                     "{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"read\"]}]";
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));

        String requestPath = SERVICE_URI + "acl/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);

        assertEquals(423, response.getStatus());

        // ACL must not be updated.
        List<AccessControlEntry> updatedAcl =
                (List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity();
        assertTrue(updatedAcl.isEmpty()); // TODO
    }

    private Map<Principal, Set<BasicPermissions>> toMap(List<AccessControlEntry> acl) {
        Map<Principal, Set<BasicPermissions>> map = new HashMap<>(acl.size());
        for (AccessControlEntry ace : acl) {
            Principal principal = DtoFactory.getInstance().clone(ace.getPrincipal());
            Set<BasicPermissions> permissions = map.get(principal);
            if (permissions == null) {
                permissions = EnumSet.noneOf(BasicPermissions.class);
                map.put(principal, permissions);
            }
            List<String> strPermissions = ace.getPermissions();
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
