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

import org.exoplatform.ide.vfs.shared.ObjectId;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.lock.Lock;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class MoveTest extends JcrFileSystemTest
{
   private Node moveTestNode;

   private Node moveTestDestinationNode;

   private String folder;

   private String document;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      moveTestNode = testRoot.addNode(name, "nt:unstructured");
      moveTestNode.addMixin("mix:lockable");
      moveTestNode.addMixin("exo:privilegeable");

      Node folderNode = moveTestNode.addNode("MoveTest_FOLDER", "nt:folder");
      // add child in folder
      Node childDocumentNode = folderNode.addNode("document", "nt:file");
      Node childContentNode = childDocumentNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      folder = folderNode.getPath();

      moveTestDestinationNode = testRoot.addNode("MoveTest_DESTINATION_FOLDER", "nt:folder");
      moveTestDestinationNode.addMixin("mix:lockable");
      moveTestDestinationNode.addMixin("exo:privilegeable");

      Node documentNode = moveTestNode.addNode("MoveTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      document = documentNode.getPath();

      session.save();
   }

   public void testMoveDocument() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(document) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      ObjectId id = (ObjectId)response.getEntity();
      assertFalse("Document must be moved. ", session.itemExists(document));
      assertTrue("Not found document in destination location. ", session.itemExists(id.getId()));
   }

   public void testMoveLockedDocument() throws Exception
   {
      Lock lock = moveTestNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(document) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()) //
         .append("&") //
         .append("lockTokens=") //
         .append(lock.getLockToken()) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      ObjectId id = (ObjectId)response.getEntity();
      assertFalse("Document must be moved. ", session.itemExists(document));
      assertTrue("Not found document in destination location. ", session.itemExists(id.getId()));
   }

   public void testMoveLockedDocument_NoLockToken() throws Exception
   {
      moveTestNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(document) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(423, response.getStatus());
      assertTrue("Document must not be moved since its parent is locked. ", session.itemExists(document));
   }

   public void testMoveDocumentNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)moveTestNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(document) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      assertTrue("Document must not be moved since permissions restriction. ", session.itemExists(document));
   }

   public void testMoveDocumentLockedDestination() throws Exception
   {
      Lock lock = moveTestDestinationNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(document) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()) //
         .append("&") //
         .append("lockTokens=") //
         .append(lock.getLockToken()) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      ObjectId id = (ObjectId)response.getEntity();
      assertFalse("Source document must be moved. ", session.itemExists(document));
      assertTrue("Not found document in destination location. ", session.itemExists(id.getId()));
   }

   public void testMoveDocumentLockedDestination_NoLockToken() throws Exception
   {
      moveTestDestinationNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(document) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(423, response.getStatus());
      assertTrue("Source document not found. ", session.itemExists(document));
      assertFalse("Document must not be moved since destination folder is locked. ",
         session.itemExists(moveTestDestinationNode.getPath() + "/MoveTest_DOCUMENT"));
   }

   public void testMoveDocumentDestination_NoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)moveTestDestinationNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(document) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      assertTrue("Source document not found. ", session.itemExists(document));
      assertFalse("Document must not be moved since destination folder is locked. ",
         session.itemExists(moveTestDestinationNode.getPath() + "/MoveTest_DOCUMENT"));
   }

   public void testMoveFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/move") //
         .append(folder) //
         .append("?") //
         .append("newparent=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      ObjectId id = (ObjectId)response.getEntity();
      assertFalse("Folder must be moved. ", session.itemExists(folder));
      assertTrue("Not found folder in destination location. ", session.itemExists(id.getId()));
      assertTrue("Children of folder missing after moving. ", session.itemExists(id.getId() + "/document"));
   }
}
