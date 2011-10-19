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

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.lock.Lock;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class UpdateACLTest extends JcrFileSystemTest
{
   private Node updateAclTestNode;
   private String fileID;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      updateAclTestNode = testRoot.addNode(name, "nt:unstructured");

      Node fileNode = updateAclTestNode.addNode("UpdateACLTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("mix:lockable");
      fileID = ((ExtendedNode)fileNode).getIdentifier();

      session.save();
   }

   public void testUpdateAclFile() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("acl/") //
         .append(fileID).toString();
      String acl = "[{\"principal\":\"root\",\"permissions\":[\"all\"]}," + //
         "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, acl.getBytes(), null);
      assertEquals(204, response.getStatus());

      ExtendedNode node = (ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID);
      AccessControlList jcrAcl = node.getACL();

      List<String> root = jcrAcl.getPermissions("root");
      assertTrue(root.contains(PermissionType.READ));
      assertTrue(root.contains(PermissionType.SET_PROPERTY));
      assertTrue(root.contains(PermissionType.ADD_NODE));
      assertTrue(root.contains(PermissionType.REMOVE));

      List<String> john = jcrAcl.getPermissions("john");
      assertTrue(john.contains(PermissionType.READ));
      assertFalse(john.contains(PermissionType.SET_PROPERTY));
      assertFalse(john.contains(PermissionType.ADD_NODE));
      assertFalse(john.contains(PermissionType.REMOVE));
   }

   public void testUpdateAclFileOverride() throws Exception
   {
      ExtendedNode node = (ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID);
      node.addMixin("exo:privilegeable");
      node.setPermission("exo", PermissionType.ALL);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("acl/") //
         .append(fileID) //
         .append("?") //
         .append("override=") //
         .append(true) //
         .toString();
      String acl = "[{\"principal\":\"root\",\"permissions\":[\"all\"]}," + //
         "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      // Set root as current user since 'john' lose 'write' privileges. 
      ConversationState user = new ConversationState(new Identity("root"));
      ConversationState.setCurrent(user);
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, acl.getBytes(), writer, null);
      assertEquals(204, response.getStatus());

      node = (ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID);
      AccessControlList jcrAcl = node.getACL();
      List<String> root = jcrAcl.getPermissions("root");
      assertTrue(root.contains(PermissionType.READ));
      assertTrue(root.contains(PermissionType.SET_PROPERTY));
      assertTrue(root.contains(PermissionType.ADD_NODE));
      assertTrue(root.contains(PermissionType.REMOVE));

      List<String> john = jcrAcl.getPermissions("john");
      assertTrue(john.contains(PermissionType.READ));
      assertFalse(john.contains(PermissionType.SET_PROPERTY));
      assertFalse(john.contains(PermissionType.ADD_NODE));
      assertFalse(john.contains(PermissionType.REMOVE));

      // permissions for exo removed
      List<String> exo = jcrAcl.getPermissions("exo");
      assertTrue(exo.isEmpty());
   }

   public void testUpdateAclFileMerge() throws Exception
   {
      ExtendedNode node = (ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID);
      node.addMixin("exo:privilegeable");
      node.setPermission("exo", PermissionType.ALL);
      session.save();

      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("acl/") //
         .append(fileID) //
         .toString();
      String acl = "[{\"principal\":\"root\",\"permissions\":[\"all\"]}," + //
         "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, acl.getBytes(), null);
      assertEquals(204, response.getStatus());

      node = (ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID);
      AccessControlList jcrAcl = node.getACL();
      List<String> root = jcrAcl.getPermissions("root");
      assertTrue(root.contains(PermissionType.READ));
      assertTrue(root.contains(PermissionType.SET_PROPERTY));
      assertTrue(root.contains(PermissionType.ADD_NODE));
      assertTrue(root.contains(PermissionType.REMOVE));

      List<String> john = jcrAcl.getPermissions("john");
      assertTrue(john.contains(PermissionType.READ));
      assertFalse(john.contains(PermissionType.SET_PROPERTY));
      assertFalse(john.contains(PermissionType.ADD_NODE));
      assertFalse(john.contains(PermissionType.REMOVE));

      // permissions for exo saved
      List<String> exo = jcrAcl.getPermissions("exo");
      assertTrue(exo.contains(PermissionType.READ));
      assertTrue(exo.contains(PermissionType.SET_PROPERTY));
      assertTrue(exo.contains(PermissionType.ADD_NODE));
      assertTrue(exo.contains(PermissionType.REMOVE));
   }

   public void testUpdateAclFileLocked() throws Exception
   {
      Lock lock = ((ExtendedSession)session).getNodeByIdentifier(fileID).lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("acl/") //
         .append(fileID) //
         .append("?") //
         .append("lockToken=") //
         .append(lock.getLockToken()) //
         .toString();
      String acl = "[{\"principal\":\"root\",\"permissions\":[\"all\"]}," + //
         "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, acl.getBytes(), writer, null);
      assertEquals(204, response.getStatus());
      ExtendedNode node = (ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID);
      AccessControlList jcrAcl = node.getACL();

      List<String> root = jcrAcl.getPermissions("root");
      assertTrue(root.contains(PermissionType.READ));
      assertTrue(root.contains(PermissionType.SET_PROPERTY));
      assertTrue(root.contains(PermissionType.ADD_NODE));
      assertTrue(root.contains(PermissionType.REMOVE));

      List<String> john = jcrAcl.getPermissions("john");
      assertTrue(john.contains(PermissionType.READ));
      assertFalse(john.contains(PermissionType.SET_PROPERTY));
      assertFalse(john.contains(PermissionType.ADD_NODE));
      assertFalse(john.contains(PermissionType.REMOVE));
   }

   public void testUpdateAclFileLocked_NoLockToken() throws Exception
   {
      ((ExtendedSession)session).getNodeByIdentifier(fileID).lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("acl/") //
         .append(fileID).toString();
      String acl = "[{\"principal\":\"root\",\"permissions\":[\"all\"]}," + //
         "{\"principal\":\"john\",\"permissions\":[\"read\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, acl.getBytes(), writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
