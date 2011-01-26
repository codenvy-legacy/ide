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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.services.security.IdentityConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GetVFSInfoTest extends JcrFileSystemTest
{
   public void testVFSInfo() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", SERVICE_URI, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      VirtualFileSystemInfo vfsInfo = (VirtualFileSystemInfo)response.getEntity();
      assertNotNull(vfsInfo);
      assertEquals("", vfsInfo.getRootFolderId());
      vfsInfo.getUriTemplates();
      assertEquals(true, vfsInfo.isVersioningSupported());
      assertEquals(true, vfsInfo.isLockSupported());
      assertEquals(ACLCapability.MANAGE, vfsInfo.getAclCapability());
      assertEquals(QueryCapability.BOTHCOMBINED, vfsInfo.getQueryCapability());
      assertEquals(IdentityConstants.ANONIM, vfsInfo.getAnonymousPrincipal());
      assertEquals(IdentityConstants.ANY, vfsInfo.getAnyPrincipal());
      BasicPermissions[] basicPermissions = BasicPermissions.values();
      List<String> expectedPermissions = new ArrayList<String>(basicPermissions.length);
      for (BasicPermissions bp : basicPermissions)
         expectedPermissions.add(bp.value());
      Collection<String> permissions = vfsInfo.getPermissions();
      assertTrue(permissions.containsAll(expectedPermissions));
      //log.info(">>>>>>>>> "+vfsInfo.getUrlTemplates());
   }
}
