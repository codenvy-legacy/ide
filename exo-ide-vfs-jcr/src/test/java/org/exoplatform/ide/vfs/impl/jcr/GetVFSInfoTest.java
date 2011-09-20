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

import org.exoplatform.ide.vfs.shared.Link;
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
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

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
      assertEquals(WORKSPACE_NAME, vfsInfo.getId());
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

   protected void validateUrlTemplates(VirtualFileSystemInfo info) throws Exception
   {
      Map<String, Link> templates = info.getUrlTemplates();
      //log.info(">>>>>>>>>\n" + templates);

      Link template = templates.get(Link.REL_COPY);
      assertNotNull("'" + Link.REL_COPY + "' template not found. ", template);
      assertEquals(null, template.getType());
      assertEquals(Link.REL_COPY, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("copy").path("[id]").queryParam("parentId", "[parentId]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_MOVE);
      assertNotNull("'" + Link.REL_MOVE + "' template not found. ", template);
      assertEquals(null, template.getType());
      assertEquals(Link.REL_MOVE, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("move").path("[id]").queryParam("parentId", "[parentId]")
         .queryParam("lockToken", "[lockToken]").build().toString(), template.getHref());

      template = templates.get(Link.REL_CREATE_FILE);
      assertNotNull("'" + Link.REL_CREATE_FILE + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_CREATE_FILE, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("file").path("[parentId]").queryParam("name", "[name]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_CREATE_FOLDER);
      assertNotNull("'" + Link.REL_CREATE_FOLDER + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_CREATE_FOLDER, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("folder").path("[parentId]").queryParam("name", "[name]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_CREATE_PROJECT);
      assertNotNull("'" + Link.REL_CREATE_PROJECT + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_CREATE_PROJECT, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("project").path("[parentId]").queryParam("name", "[name]")
         .queryParam("type", "[type]").build().toString(), template.getHref());

      template = templates.get(Link.REL_LOCK);
      assertNotNull("'" + Link.REL_LOCK + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_LOCK, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("lock").path("[id]").build().toString(), template.getHref());

      template = templates.get(Link.REL_UNLOCK);
      assertNotNull("'" + Link.REL_UNLOCK + "' template not found. ", template);
      assertEquals(null, template.getType());
      assertEquals(Link.REL_UNLOCK, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("unlock").path("[id]").queryParam("lockToken", "[lockToken]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_SEARCH);
      assertNotNull("'" + Link.REL_SEARCH + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_SEARCH, template.getRel());
      assertEquals(
         UriBuilder.fromPath(SERVICE_URI).path("search").queryParam("statement", "[statement]")
            .queryParam("maxItems", "[maxItems]").queryParam("skipCount", "[skipCount]").build().toString(),
         template.getHref());

      template = templates.get(Link.REL_SEARCH_FORM);
      assertNotNull("'" + Link.REL_SEARCH_FORM + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_SEARCH_FORM, template.getRel());
      assertEquals(
         UriBuilder.fromPath(SERVICE_URI).path("search").queryParam("maxItems", "[maxItems]")
            .queryParam("skipCount", "[skipCount]").queryParam("propertyFilter", "[propertyFilter]").build().toString(),
         template.getHref());
   }
}
