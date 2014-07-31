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

import com.codenvy.api.vfs.shared.dto.AccessControlEntry;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.user.UserImpl;
import com.codenvy.dto.server.DtoFactory;
import com.google.common.collect.Sets;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

    private Map<Principal, Set<String>> permissions;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, "ACLTest_File", DEFAULT_CONTENT_BYTES);
        lockedFilePath = createFile(testRootPath, "ACLTest_LockedFile", DEFAULT_CONTENT_BYTES);

        permissions = new HashMap<>(3);
        Principal user1 = DtoFactory.getInstance().createDto(Principal.class).withName("andrew").withType(Principal.Type.USER);
        Principal user2 = DtoFactory.getInstance().createDto(Principal.class).withName("john").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(admin, Sets.newHashSet(BasicPermissions.ALL.value()));
        permissions.put(user1, Sets.newHashSet(BasicPermissions.READ.value(), BasicPermissions.WRITE.value(),
                                               BasicPermissions.UPDATE_ACL.value()));
        permissions.put(user2, Sets.newHashSet(BasicPermissions.READ.value()));

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
        permissions.get(principal).add(BasicPermissions.WRITE.value());
        // check backend
        Map<? extends Principal, Set<String>> updatedAccessList = readPermissions(filePath);
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
        Map<Principal, Set<String>> updatedAccessList = readPermissions(filePath);
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
        permissions.put(principal, Sets.newHashSet(BasicPermissions.READ.value()));
        writePermissions(filePath, permissions);

        String requestPath = SERVICE_URI + "acl/" + fileId;
        // Give write permission for john. No changes for other users.
        String acl = "[{\"principal\":{\"name\":\"admin\",\"type\":\"USER\"},\"permissions\":[\"read\", \"write\"]}]";
        Map<String, List<String>> h = new HashMap<>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        // File is protected and default principal 'andrew' has not update_acl permission.
        // Replace default principal by principal who has write permission.
        EnvironmentContext.getCurrent().setUser(new UserImpl("andrew", "andrew", null, Arrays.asList("workspace/developer")));
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
        assertEquals(204, response.getStatus());

        principal = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.get(principal).add(BasicPermissions.WRITE.value());
        // check backend
        Map<Principal, Set<String>> updatedAccessList = readPermissions(filePath);
        assertEquals(permissions, updatedAccessList);

        // check API
        assertEquals(permissions,
                     toMap((List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity()));
    }

    @SuppressWarnings("unchecked")
    public void testUpdateACLNoPermissions() throws Exception {
        // Remove permissions for current user, see LocalFileSystemTest.setUp()
        Principal principal = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        permissions.put(principal, Sets.newHashSet(BasicPermissions.READ.value()));
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
        Map<Principal, Set<String>> updatedAccessList = readPermissions(filePath);
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

        Map<Principal, Set<String>> thisTestAccessList = new HashMap<>(2);
        Principal user = DtoFactory.getInstance().createDto(Principal.class).withName("john").withType(Principal.Type.USER);
        Principal admin = DtoFactory.getInstance().createDto(Principal.class).withName("admin").withType(Principal.Type.USER);
        thisTestAccessList.put(user, Sets.newHashSet(BasicPermissions.READ.value(), BasicPermissions.WRITE.value()));
        thisTestAccessList.put(admin, Sets.newHashSet(BasicPermissions.READ.value()));

        // check backend
        Map<Principal, Set<String>> updatedAccessList = readPermissions(lockedFilePath);
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

        assertEquals(403, response.getStatus());

        // ACL must not be updated.
        List<AccessControlEntry> updatedAcl =
                (List<AccessControlEntry>)launcher.service("GET", requestPath, BASE_URI, null, null, null).getEntity();
        assertTrue(updatedAcl.isEmpty()); // TODO
    }

    private Map<Principal, Set<String>> toMap(List<AccessControlEntry> acl) {
        Map<Principal, Set<String>> map = new HashMap<>(acl.size());
        for (AccessControlEntry ace : acl) {
            Principal principal = DtoFactory.getInstance().clone(ace.getPrincipal());
            Set<String> permissions = map.get(principal);
            if (permissions == null) {
                map.put(principal, permissions = new HashSet<>(4));
            }
            List<String> acePermissions = ace.getPermissions();
            if (!(acePermissions == null || acePermissions.isEmpty())) {
                permissions.addAll(acePermissions);
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
