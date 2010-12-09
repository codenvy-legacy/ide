/**
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
 *
 */

package org.exoplatform.ide.vfs.webdav;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.jcr.access.AccessControlEntry;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.jcr.webdav.util.TextUtil;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestAclCommand extends BaseStandaloneTest
{

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
   }

   private void checkPermissionSet(NodeImpl node, String identity, String permission) throws RepositoryException
   {
      for (AccessControlEntry entry : node.getACL().getPermissionEntries())
      {
         if (entry.getIdentity().equals(identity) && entry.getPermission().equals(permission))
         {
            return;
         }
      }

      fail();
   }

   private void checkPermissionRemoved(NodeImpl node, String identity, String permission) throws RepositoryException
   {
      for (AccessControlEntry entry : node.getACL().getPermissionEntries())
      {
         if (entry.getIdentity().equals(identity) && entry.getPermission().equals(permission))
         {
            fail();
         }
      }
   }

   @Test
   public void testSetACLForTwoUsersOnNonPrivilegableResource() throws Exception
   {
      NodeImpl testNode = (NodeImpl)root.addNode("test_set_acl_node2", "nt:folder");
      session.save();

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "0");
      headers.putSingle("Content-Type", "text/xml; charset=\"utf-8\"");
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + "<D:acl xmlns:D=\"DAV:\">" + "<D:ace>" + "<D:principal>"
            + "<D:href>Anya</D:href>" + "</D:principal>" + "<D:grant>" + "<D:privilege><D:write/></D:privilege>"
            + "</D:grant>" + "</D:ace>" + "<D:ace>" + "<D:principal>" + "<D:href>Oksana</D:href>" + "</D:principal>"
            + "<D:grant>" + "<D:privilege><D:write/></D:privilege>" + "</D:grant>" + "</D:ace>" + "</D:acl>";

      ContainerResponse response =
         launcher.service("ACL", "/ide-vfs-webdav/db1/ws" + testNode.getPath(), "", headers,
            request.getBytes(), null, ctx);

      assertEquals(HTTPStatus.OK, response.getStatus());

      session.refresh(false);
      NodeImpl node = (NodeImpl)root.getNode("test_set_acl_node2");

      checkPermissionSet(node, "Anya", PermissionType.ADD_NODE);
      checkPermissionSet(node, "Anya", PermissionType.SET_PROPERTY);
      checkPermissionSet(node, "Anya", PermissionType.REMOVE);

      checkPermissionSet(node, "Oksana", PermissionType.ADD_NODE);
      checkPermissionSet(node, "Oksana", PermissionType.SET_PROPERTY);
      checkPermissionSet(node, "Oksana", PermissionType.REMOVE);

      checkPermissionRemoved(node, "any", PermissionType.ADD_NODE);
      checkPermissionRemoved(node, "any", PermissionType.SET_PROPERTY);
      checkPermissionRemoved(node, "any", PermissionType.REMOVE);
      checkPermissionRemoved(node, "any", PermissionType.READ);
   }

   @Test
   public void testSetAllPermissionsForAllUsersOnNonPrivilegableResource() throws Exception
   {
      NodeImpl testNode = (NodeImpl)root.addNode("test_set_acl_node_1", "nt:folder");
      session.save();

      testNode.addMixin("exo:owneable");
      testNode.addMixin("exo:privilegeable");
      session.save();

      Map<String, String[]> defaultPermissions = new HashMap<String, String[]>();
      String[] initPermissions =
         new String[]{PermissionType.ADD_NODE, PermissionType.READ, PermissionType.SET_PROPERTY};
      defaultPermissions.put("Vetal", initPermissions);
      testNode.setPermissions(defaultPermissions);
      session.save();

      System.out.println("NODE >> " + testNode);

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "0");
      headers.putSingle("Content-Type", "text/xml; charset=\"utf-8\"");
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + "<D:acl xmlns:D=\"DAV:\">" + "<D:ace>" + "<D:principal>"
            + "<D:all />" + "</D:principal>" + "<D:grant>" + "<D:privilege><D:all/></D:privilege>" + "</D:grant>"
            + "</D:ace>" + "</D:acl>";

      ContainerResponse response =
         launcher.service("ACL", "/ide-vfs-webdav/db1/ws" + testNode.getPath(), "http://localhost", headers,
            request.getBytes(), null, ctx);

      assertEquals(HTTPStatus.OK, response.getStatus());

      session.refresh(false);
      NodeImpl node = (NodeImpl)root.getNode("test_set_acl_node_1");

      System.out.println("Node after > " + node);

      checkPermissionSet(node, "any", PermissionType.ADD_NODE);
      checkPermissionSet(node, "any", PermissionType.SET_PROPERTY);
      checkPermissionSet(node, "any", PermissionType.REMOVE);
      checkPermissionSet(node, "any", PermissionType.READ);

   }

   @Test
   public void testWrongAclXml() throws Exception
   {
      NodeImpl testNode = (NodeImpl)root.addNode("test_set_wrong_acl_node", "nt:folder");
      session.save();

      testNode.addMixin("exo:owneable");
      testNode.addMixin("exo:privilegeable");
      session.save();

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "0");
      headers.putSingle("Content-Type", "text/xml; charset=\"utf-8\"");

      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + "<D:acl xmlns:D=\"DAV:\">" + "<D:ace>" + "<D:principal>"
            + "<D:all />" + "</D:principal>" + "<D:grant>" + "<D:privilege><D:read /><D:write /></D:privilege>"
            + "</D:grant>" + "</D:ace>" + "</D:acl>";

      ContainerResponse response =
         launcher.service("ACL", "/ide-vfs-webdav/db1/ws" + testNode.getPath(), "http://localhost", headers,
            request.getBytes(), null, ctx);

      assertEquals(HTTPStatus.BAD_REQUEST, response.getStatus());

   }
   
   @Test
   public void testSetAclForVersioningNode() throws Exception
   {
      
      String content = getFileContent();
      String path = getFileName();
      ContainerResponse containerResponse =
         service("PUT", "/ide-vfs-webdav/db1/ws" + path, "", null, content.getBytes());
      assertEquals(HTTPStatus.CREATED, containerResponse.getStatus());
      assertTrue(session.getRootNode().hasNode(TextUtil.relativizePath(path)));
      
      //create new version
      content = getFileContent();
      containerResponse =
         service("PUT", "/ide-vfs-webdav/db1/ws"+ path, "", null, content.getBytes());
      assertEquals(HTTPStatus.CREATED, containerResponse.getStatus());
      assertTrue(session.getRootNode().hasNode(TextUtil.relativizePath(path)));

      //try set acl
      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + "<D:acl xmlns:D=\"DAV:\">" + "<D:ace>" + "<D:principal>"
            + "<D:all />" + "</D:principal>" + "<D:grant>" + "<D:privilege><D:all/></D:privilege>" + "</D:grant>"
            + "</D:ace>" + "</D:acl>";
      
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "0");
      headers.putSingle("Content-Type", "text/xml; charset=\"utf-8\"");

      
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);
      
      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);
      ContainerResponse response =
         launcher.service("ACL", "/ide-vfs-webdav/db1/ws" + path, "", headers,
            request.getBytes(), null, ctx);
      
      assertEquals(HTTPStatus.OK, response.getStatus());
      
   }

   private static String getFileContent()
   {
      String content = new String();
      for (int i = 0; i < 10; i++)
      {
         content += UUID.randomUUID().toString();
      }
      return content;
   }
   
   private static String getFileName()
   {
      return "/test-file-" + System.currentTimeMillis() + ".txt";
   }

   
   @Override
   protected String getRepositoryName()
   {
      return null;
   }

}
