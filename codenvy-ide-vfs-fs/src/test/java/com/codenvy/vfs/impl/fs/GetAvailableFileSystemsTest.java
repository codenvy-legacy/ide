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

import com.codenvy.api.vfs.shared.VirtualFileSystemInfo;
import com.codenvy.api.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import com.codenvy.api.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import com.codenvy.api.vfs.shared.VirtualFileSystemInfo.QueryCapability;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetAvailableFileSystemsTest extends LocalFileSystemTest {
    @SuppressWarnings("unchecked")
    public void testAvailableFS() throws Exception {
        String requestPath = BASE_URI + "/api/my-ws/vfs";
        ByteArrayContainerResponseWriter wr = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, wr, null);
        //log.info(new String(wr.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        Collection<VirtualFileSystemInfo> entity = (Collection<VirtualFileSystemInfo>)response.getEntity();
        assertNotNull(entity);
        //assertEquals(1, entity.size());
        VirtualFileSystemInfo vfsInfo = null;
        for (VirtualFileSystemInfo e : entity) {
            if (e.getId().equals(MY_WORKSPACE_ID)) {
                if (vfsInfo != null) {
                    fail("More then one VFS with the same ID found. ");
                }
                vfsInfo = e;
            }
        }
        assertNotNull(vfsInfo);
        assertEquals(false, vfsInfo.isVersioningSupported());
        assertEquals(true, vfsInfo.isLockSupported());
        assertEquals(ACLCapability.MANAGE, vfsInfo.getAclCapability());
        assertEquals(QueryCapability.NONE, vfsInfo.getQueryCapability()); // TODO : update when implement search
        assertEquals(VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL, vfsInfo.getAnonymousPrincipal());
        assertEquals(VirtualFileSystemInfo.ANY_PRINCIPAL, vfsInfo.getAnyPrincipal());
        assertEquals(MY_WORKSPACE_ID, vfsInfo.getId());
        BasicPermissions[] basicPermissions = BasicPermissions.values();
        List<String> expectedPermissions = new ArrayList<>(basicPermissions.length);
        for (BasicPermissions bp : basicPermissions) {
            expectedPermissions.add(bp.value());
        }
        Collection<String> permissions = vfsInfo.getPermissions();
        assertTrue(permissions.containsAll(expectedPermissions));
        assertNotNull(vfsInfo.getRoot());
        assertEquals("/", vfsInfo.getRoot().getPath());
        validateLinks(vfsInfo.getRoot());
        validateUrlTemplates(vfsInfo);
    }
}
