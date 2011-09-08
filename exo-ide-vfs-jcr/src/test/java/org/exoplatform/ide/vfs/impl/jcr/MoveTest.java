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

   private String folderPath;

   private String filePath;

   private Node folderNode;

   private Node fileNode;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      moveTestNode = testRoot.addNode(name, "nt:unstructured");

      folderNode = moveTestNode.addNode("MoveTest_FOLDER", "nt:folder");
      // add child in folder
      Node childFileNode = folderNode.addNode("file", "nt:file");
      Node childContentNode = childFileNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      folderPath = folderNode.getPath();

      moveTestDestinationNode = testRoot.addNode("MoveTest_DESTINATION_FOLDER", "nt:folder");
      moveTestDestinationNode.addMixin("exo:privilegeable");

      fileNode = moveTestNode.addNode("MoveTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("mix:lockable");
      fileNode.addMixin("exo:privilegeable");
      filePath = fileNode.getPath();

      session.save();
   }

   public void testMoveFile() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("move") //
         .append(filePath) //
         .append("?") //
         .append("parentId=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(201, response.getStatus());
      String expectedPath = moveTestDestinationNode.getPath() + "/" + fileNode.getName();
      String expectedLocation = SERVICE_URI + "item" + expectedPath;
      String location = response.getHttpHeaders().getFirst("Location").toString();
      assertEquals(expectedLocation, location);
      assertFalse("File must be moved. ", session.itemExists(filePath));
      assertTrue("Not found file in destination location. ", session.itemExists(expectedPath));
   }

   public void testMoveLockedFile() throws Exception
   {
      Lock lock = fileNode.lock(true, false);
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("move") //
         .append(filePath) //
         .append("?") //
         .append("parentId=") //
         .append(moveTestDestinationNode.getPath()) //
         .append("&") //
         .append("lockToken=") //
         .append(lock.getLockToken()) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(201, response.getStatus());
      String expectedPath = moveTestDestinationNode.getPath() + "/" + fileNode.getName();
      String expectedLocation = SERVICE_URI + "item" + expectedPath;
      String location = response.getHttpHeaders().getFirst("Location").toString();
      assertEquals(expectedLocation, location);
      assertFalse("File must be moved. ", session.itemExists(filePath));
      assertTrue("Not found file in destination location. ", session.itemExists(expectedPath));
   }

   public void testMoveLockedFile_NoLockToken() throws Exception
   {
      fileNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("move") //
         .append(filePath) //
         .append("?") //
         .append("parentId=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(423, response.getStatus());
      assertTrue("File must not be moved since its parent is locked. ", session.itemExists(filePath));
   }

   public void testMoveFileNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)fileNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("move") //
         .append(filePath) //
         .append("?") //
         .append("parentId=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      assertTrue("File must not be moved since permissions restriction. ", session.itemExists(filePath));
   }

   public void testMoveFileDestination_NoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)moveTestDestinationNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("move") //
         .append(filePath) //
         .append("?") //
         .append("parentId=") //
         .append(moveTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      assertTrue("Source file not found. ", session.itemExists(filePath));
      assertFalse("File must not be moved since destination folder is locked. ",
         session.itemExists(moveTestDestinationNode.getPath() + "/MoveTest_FILE"));
   }

   public void testMoveFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("move") //
         .append(folderPath) //
         .append("?") //
         .append("parentId=") //
         .append(moveTestDestinationNode.getPath()).toString();
      
      
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(201, response.getStatus());
      
      System.out.println("MoveTest.testMoveFolder()" + folderPath + " :: " + moveTestDestinationNode.getPath());
      
      String expectedPath = moveTestDestinationNode.getPath() + "/" + folderNode.getName();
      String expectedLocation = SERVICE_URI + "item" + expectedPath;
      String location = response.getHttpHeaders().getFirst("Location").toString();
      assertEquals(expectedLocation, location);
      assertFalse("Folder must be moved. ", session.itemExists(folderPath));
      assertTrue("Not found folder in destination location. ", session.itemExists(expectedPath));
      assertTrue("Child of folder missing after moving. ", session.itemExists(expectedPath + "/file"));
   }
}
