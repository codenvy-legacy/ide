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
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;
import org.exoplatform.services.security.IdentityConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GetAvailableFileSystemsTest extends JcrFileSystemTest
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
         if (e.getId().equals(REPOSITORY_NAME))
         {
            if (vfsInfo != null)
               fail("More then one VFS with the same ID found. ");
            vfsInfo = e;
         }
      }
      assertNotNull(vfsInfo);
      assertEquals(true, vfsInfo.isVersioningSupported());
      assertEquals(true, vfsInfo.isLockSupported());
      assertEquals(ACLCapability.MANAGE, vfsInfo.getAclCapability());
      assertEquals(QueryCapability.BOTHCOMBINED, vfsInfo.getQueryCapability());
      assertEquals(IdentityConstants.ANONIM, vfsInfo.getAnonymousPrincipal());
      assertEquals(IdentityConstants.ANY, vfsInfo.getAnyPrincipal());
      assertEquals(REPOSITORY_NAME, vfsInfo.getId());
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
