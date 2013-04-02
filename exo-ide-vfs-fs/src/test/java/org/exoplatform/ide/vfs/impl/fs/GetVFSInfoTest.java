/*
 * Copyright (C) 2011 eXo Platform SAS.
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
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetVFSInfoTest extends LocalFileSystemTest {
    public void testVFSInfo() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("GET", SERVICE_URI, BASE_URI, null, null, writer, null);
        assertNotNull(response.getEntity());
        assertEquals(response.getEntity().toString(), 200, response.getStatus());
        //log.info(new String(writer.getBody()));
        VirtualFileSystemInfo vfsInfo = (VirtualFileSystemInfo)response.getEntity();
        assertNotNull(vfsInfo);
        assertEquals(false, vfsInfo.isVersioningSupported());
        assertEquals(true, vfsInfo.isLockSupported());
        assertEquals(ACLCapability.MANAGE, vfsInfo.getAclCapability());
        assertEquals(QueryCapability.NONE, vfsInfo.getQueryCapability()); // TODO : update when implement search
        assertEquals(VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL, vfsInfo.getAnonymousPrincipal());
        assertEquals(VirtualFileSystemInfo.ANY_PRINCIPAL, vfsInfo.getAnyPrincipal());
        assertEquals(MY_WORKSPACE_ID, vfsInfo.getId());
        BasicPermissions[] basicPermissions = BasicPermissions.values();
        List<String> expectedPermissions = new ArrayList<String>(basicPermissions.length);
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
