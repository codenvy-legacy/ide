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

import org.exoplatform.ide.vfs.server.ObjectId;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.lock.Lock;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class CreateTest extends JcrFileSystemTest
{
   private String CREATE_TEST_PATH;

   private Node createTestNode;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      createTestNode = testRoot.addNode(name, "nt:unstructured");
      session.save();
      CREATE_TEST_PATH = "/" + TEST_ROOT_NAME + "/" + name;
   }

   public void testCreateDocument() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateDocument";
      String content = "test create document";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(CREATE_TEST_PATH) //
         .append("?") //
         .append("name=") //
         .append(name) //
         .append("&") //
         .append("mediaType=") //
         .append("text/plain;charset%3Dutf8").toString();
      ContainerResponse response = launcher.service("POST", path, "", null, content.getBytes(), writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      ObjectId id = (ObjectId)response.getEntity();
      assertEquals(CREATE_TEST_PATH + "/" + name, id.getId());

      assertTrue("Document was not created in expected location. ", session.itemExists(id.getId()));
      Node document = (Node)session.getItem(id.getId());
      assertEquals("text/plain", document.getNode("jcr:content").getProperty("jcr:mimeType").getString());
      assertEquals("utf8", document.getNode("jcr:content").getProperty("jcr:encoding").getString());
      assertEquals(content, document.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testCreateDocumentInLockedParent() throws Exception
   {
      Node parent = createTestNode.addNode("testCreateDocumentInLockedParent_PARENT", "nt:folder");
      String parentPath = parent.getPath();
      parent.addMixin("mix:lockable");
      session.save();
      Lock lock = parent.lock(true, false);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateDocumentInLockedParent";
      String content = "test create document in locked parent";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(parentPath) //
         .append("?") //
         .append("name=") //
         .append(name) //
         .append("&") //
         .append("lockTokens=") //
         .append(lock.getLockToken()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, content.getBytes(), writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      ObjectId id = (ObjectId)response.getEntity();
      assertEquals(parentPath + "/" + name, id.getId());

      assertTrue("Document was not created in expected location. ", session.itemExists(id.getId()));
      Node document = (Node)session.getItem(id.getId());
      assertEquals(MediaType.APPLICATION_OCTET_STREAM, document.getNode("jcr:content").getProperty("jcr:mimeType")
         .getString());
      assertEquals(content, document.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testCreateDocumentInLockedParent_NoLockToken() throws Exception
   {
      Node parent = createTestNode.addNode("testCreateDocumentInLockedParent_NoLockToken_PARENT", "nt:folder");
      String parentPath = parent.getPath();
      parent.addMixin("mix:lockable");
      session.save();
      parent.lock(true, false);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateDocumentInLockedParent_NoLockToken";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(parentPath) //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, DEFAULT_CONTENT.getBytes(), writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testCreateDocumentNoContent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateDocumentNoContent";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(CREATE_TEST_PATH) //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      ObjectId id = (ObjectId)response.getEntity();
      assertEquals(CREATE_TEST_PATH + "/" + name, id.getId());

      assertTrue("Document was not created in expected location. ", session.itemExists(id.getId()));
      Node document = (Node)session.getItem(id.getId());
      assertEquals(MediaType.APPLICATION_OCTET_STREAM, document.getNode("jcr:content").getProperty("jcr:mimeType")
         .getString());
      assertFalse(document.getNode("jcr:content").hasProperty("jcr:encoding"));
      assertEquals("", document.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testCreateDocumentNoMediaType() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateDocumentNoMediaType";
      String content = "test create document without media type";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(CREATE_TEST_PATH) //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, content.getBytes(), writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      ObjectId id = (ObjectId)response.getEntity();
      assertEquals(CREATE_TEST_PATH + "/" + name, id.getId());

      assertTrue("Document was not created in expected location. ", session.itemExists(id.getId()));
      Node document = (Node)session.getItem(id.getId());
      assertEquals(MediaType.APPLICATION_OCTET_STREAM, document.getNode("jcr:content").getProperty("jcr:mimeType")
         .getString());
      assertFalse(document.getNode("jcr:content").hasProperty("jcr:encoding"));
      assertEquals(content, document.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testCreateDocumentNoName() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(CREATE_TEST_PATH).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, DEFAULT_CONTENT.getBytes(), writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testCreateDocumentNoPermissions() throws Exception
   {
      Node parent = createTestNode.addNode("testCreateDocumentNoPermissions_PARENT", "nt:folder");
      parent.addMixin("exo:privilegeable");
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)parent).setPermissions(permissions);
      String parentPath = parent.getPath();
      session.save();

      String name = "testCreateDocumentNoPermissions";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(parentPath) //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, DEFAULT_CONTENT.getBytes(), writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testCreateDocumentWrongParent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateDocumentWrongParent";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/document") //
         .append(CREATE_TEST_PATH + "_WRONG_PATH") //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, DEFAULT_CONTENT.getBytes(), writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testCreateFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolder";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/folder") //
         .append(CREATE_TEST_PATH) //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      ObjectId id = (ObjectId)response.getEntity();
      assertEquals(CREATE_TEST_PATH + "/" + name, id.getId());

      assertTrue("Folder was not created in expected location. ", session.itemExists(id.getId()));
      Node folder = (Node)session.getItem(id.getId());
      assertTrue("nt:folder node type expected", folder.getPrimaryNodeType().isNodeType("nt:folder"));
   }

   public void testCreateFolderInLockedParent() throws Exception
   {
      Node parent = createTestNode.addNode("testCreateFolderInLockedParent_PARENT", "nt:folder");
      String parentPath = parent.getPath();
      parent.addMixin("mix:lockable");
      session.save();
      Lock lock = parent.lock(true, false);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderInLockedParent";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/folder") //
         .append(parentPath) //
         .append("?") //
         .append("name=") //
         .append(name) //
         .append("&") //
         .append("lockTokens=") //
         .append(lock.getLockToken()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      ObjectId id = (ObjectId)response.getEntity();
      assertEquals(parentPath + "/" + name, id.getId());

      assertTrue("Folder was not created in expected location. ", session.itemExists(id.getId()));
      Node folder = (Node)session.getItem(id.getId());
      assertTrue("nt:folder node type expected", folder.getPrimaryNodeType().isNodeType("nt:folder"));
   }

   public void testCreateFolderInLockedParent_NoLockToken() throws Exception
   {
      Node parent = createTestNode.addNode("testCreateFolderInLockedParent_PARENT", "nt:folder");
      String parentPath = parent.getPath();
      parent.addMixin("mix:lockable");
      session.save();
      parent.lock(true, false);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderInLockedParent";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/folder") //
         .append(parentPath) //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testCreateFolderNoName() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/folder") //
         .append(CREATE_TEST_PATH).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testCreateFolderNoPermissions() throws Exception
   {
      Node parent = createTestNode.addNode("testCreateFolderNoPermissions_PARENT", "nt:folder");
      parent.addMixin("exo:privilegeable");
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)parent).setPermissions(permissions);
      String parentPath = parent.getPath();
      session.save();

      String name = "testCreateFolderNoPermissions";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/folder") //
         .append(parentPath) //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testCreateFolderWrongParent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderWrongParent";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/folder") //
         .append(CREATE_TEST_PATH + "_WRONG_PATH") //
         .append("?") //
         .append("name=") //
         .append(name).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
