/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.exoplatform.ide.vfs.server.AccessControlEntry;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GetACLTest extends JcrFileSystemTest
{
   private Node getAclTestNode;

   private String document;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      getAclTestNode = testRoot.addNode(name, "nt:unstructured");
      getAclTestNode.addMixin("exo:privilegeable");

      Node documentNode = getAclTestNode.addNode("GetACLTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      documentNode.addMixin("exo:privilegeable");
      ((ExtendedNode)documentNode).setPermission("root", PermissionType.ALL);
      ((ExtendedNode)documentNode).setPermission("john", new String[]{PermissionType.READ});
      document = documentNode.getPath();

      session.save();
   }

   public void testGetACL() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/acl") //
         .append(document) //
         .toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      List<AccessControlEntry> acl = (List<AccessControlEntry>)response.getEntity();
      for (AccessControlEntry ace : acl)
      {
         if ("root".equals(ace.getPrincipal()))
            ace.getPermissions().contains("all");
         if ("john".equals(ace.getPrincipal()))
            ace.getPermissions().contains("read");
      }
   }

   public void testGetACLNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)session.getItem(document)).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/acl") //
         .append(document) //
         .toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
