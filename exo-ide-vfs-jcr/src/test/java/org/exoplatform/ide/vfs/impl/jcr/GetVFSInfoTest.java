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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GetVFSInfoTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class GetVFSInfoTest extends JcrFileSystemTest
{
   public void testVFSInfo() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("GET", SERVICE_URI, BASE_URI, null, null, writer, null);
      assertNotNull(response.getEntity());
      assertEquals(response.getEntity().toString(), 200, response.getStatus());
      //log.info(new String(writer.getBody()));
      VirtualFileSystemInfo vfsInfo = (VirtualFileSystemInfo)response.getEntity();
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
