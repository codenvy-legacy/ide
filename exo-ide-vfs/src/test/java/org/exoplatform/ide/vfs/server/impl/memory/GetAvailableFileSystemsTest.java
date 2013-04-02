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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GetAvailableFileSystemsTest extends MemoryFileSystemTest
{
   public void testAvailableFS() throws Exception
   {
      String path = BASE_URI + "/ide/vfs";
      ByteArrayContainerResponseWriter wr = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, wr, null);
      //log.info(new String(wr.getBody()));
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      @SuppressWarnings("unchecked")
      Collection<VirtualFileSystemInfo> entity = (Collection<VirtualFileSystemInfo>)response.getEntity();
      assertNotNull(entity);
      //assertEquals(1, entity.size());
      VirtualFileSystemInfo vfsInfo = null;
      for (VirtualFileSystemInfo e : entity)
      {
         if (e.getId().equals(MY_WORKSPACE_ID))
         {
            if (vfsInfo != null)
               fail("More then one VFS with the same ID found. ");
            vfsInfo = e;
         }
      }
      assertNotNull(vfsInfo);
      assertEquals(false, vfsInfo.isVersioningSupported());
      assertEquals(true, vfsInfo.isLockSupported());
      assertEquals(ACLCapability.MANAGE, vfsInfo.getAclCapability());
      assertEquals(QueryCapability.NONE, vfsInfo.getQueryCapability());
      assertEquals("anonymous", vfsInfo.getAnonymousPrincipal());
      assertEquals("any", vfsInfo.getAnyPrincipal());
      assertEquals(MY_WORKSPACE_ID, vfsInfo.getId());
      BasicPermissions[] basicPermissions = BasicPermissions.values();
      List<String> expectedPermissions = new ArrayList<String>(basicPermissions.length);
      for (BasicPermissions bp : basicPermissions)
         expectedPermissions.add(bp.value());
      Collection<String> permissions = vfsInfo.getPermissions();
      assertTrue(permissions.containsAll(expectedPermissions));
      assertNotNull(vfsInfo.getRoot());
      assertEquals("/", vfsInfo.getRoot().getPath());
      validateLinks(vfsInfo.getRoot());
      validateUrlTemplates(vfsInfo);
   }
}
