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
package org.exoplatform.ide.vfs.impl.fs;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.Folder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTest extends PlainFileSystemTest
{
   private String createTestFolderId;
   private String createTestFolderPath;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      java.io.File thisTestRoot = new java.io.File(testRoot, name);
      if (!thisTestRoot.mkdirs())
      {
         fail();
      }
      createTestFolderPath = '/' + thisTestRoot.getName();
      createTestFolderId = pathToId(createTestFolderPath);
   }

   public void testCreateFile() throws Exception
   {
      String name = "testCreateFile";
      String content = "test create file";
      String path = SERVICE_URI + "file/" + createTestFolderId + '?' + "name=" + name; //
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);

      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
      assertEquals(200, response.getStatus());
      String expectedPath = createTestFolderPath + '/' + name;
      assertTrue("File was not created in expected location. ", new java.io.File(testRoot, expectedPath).exists());
      // TODO
//      assertEquals("text/plain", file.getNode("jcr:content").getProperty("jcr:mimeType").getString());
      assertEquals(content, new String(readFile(new java.io.File(testRoot, expectedPath))));
   }

      public void testCreateFileInRoot() throws Exception
   {
      String name = "testCreateFileInRoot";
      String content = "test create file";
      String path = SERVICE_URI + "file/" + PlainFileSystemContext.ROOT_ID + '?' + "name=" + name;
      Map <String, List <String>> headers = new HashMap <String, List <String>> ();
      List <String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = '/' + name;
      assertTrue("File was not created in expected location. ", new java.io.File(testRoot, expectedPath).exists());
      // TODO
//      assertEquals("text/plain", file.getNode("jcr:content").getProperty("jcr:mimeType").getString());
//      assertEquals("utf8", file.getNode("jcr:content").getProperty("jcr:encoding").getString());

      assertEquals(content, new String(readFile(new java.io.File(testRoot, expectedPath))));
   }

   public void testCreateFileNoContent() throws Exception
   {
      String name = "testCreateFileNoContent";
      String path = SERVICE_URI + "file/" + createTestFolderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);

      assertEquals(200, response.getStatus());
      String expectedPath = createTestFolderPath + '/' + name;
      assertTrue("File was not created in expected location. ", new java.io.File(testRoot, expectedPath).exists());
      // TODO
//      assertEquals(MediaType.APPLICATION_OCTET_STREAM, file.getNode("jcr:content").getProperty("jcr:mimeType")
//         .getString());
//      assertFalse(file.getNode("jcr:content").hasProperty("jcr:encoding"));
//      assertEquals("", file.getNode("jcr:content").getProperty("jcr:data").getString());
      assertTrue(readFile(new java.io.File(testRoot, expectedPath)).length == 0);
   }

//   public void testCreateFileNoMediaType() throws Exception
//   {
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String name = "testCreateFileNoMediaType";
//      String content = "test create file without media type";
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("file/") //
//         .append(createTestNodeID) //
//         .append("?") //
//         .append("name=") //
//         .append(name).toString();
//
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, content.getBytes(), writer, null);
//      assertEquals(200, response.getStatus());
//      String expectedPath = createTestNodePath + "/" + name;
//      assertTrue("File was not created in expected location. ", session.itemExists(expectedPath));
//      Node file = (Node)session.getItem(expectedPath);
//      assertEquals(MediaType.APPLICATION_OCTET_STREAM, file.getNode("jcr:content").getProperty("jcr:mimeType")
//         .getString());
//      assertFalse(file.getNode("jcr:content").hasProperty("jcr:encoding"));
//      assertEquals(content, file.getNode("jcr:content").getProperty("jcr:data").getString());
//   }

   public void testCreateFileNoName() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "file/" + createTestFolderId;
      ContainerResponse response =
         launcher.service("POST", path, BASE_URI, null, DEFAULT_CONTENT.getBytes(), writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

//   public void testCreateFileNoPermissions() throws Exception
//   {
//      Node parent = createTestNode.addNode("testCreateFileNoPermissions_PARENT", "nt:folder");
//      parent.addMixin("exo:privilegeable");
//      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
//      permissions.put("root", PermissionType.ALL);
//      ((ExtendedNode)parent).setPermissions(permissions);
//      session.save();
//      String parentID = ((ExtendedNode)parent).getIdentifier();
//
//      String name = "testCreateFileNoPermissions";
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("file/") //
//         .append(parentID) //
//         .append("?") //
//         .append("name=") //
//         .append(name).toString();
//      ContainerResponse response =
//         launcher.service("POST", path, BASE_URI, null, DEFAULT_CONTENT.getBytes(), writer, null);
//      assertEquals(403, response.getStatus());
//      log.info(new String(writer.getBody()));
//   }

   public void testCreateFileWrongParent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFileWrongParent";
      String path = SERVICE_URI + "file/" + createTestFolderId + "_WRONG_ID" + "?" + "name=" + name;
      ContainerResponse response =
         launcher.service("POST", path, BASE_URI, null, DEFAULT_CONTENT.getBytes(), writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(404, response.getStatus());
   }

   public void testCreateFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolder";
      String path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = createTestFolderPath + '/' + name;
      assertTrue("Folder was not created in expected location. ", new java.io.File(testRoot, expectedPath).exists());
   }

   public void testCreateFolderInRoot() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderInRoot";
      String path = SERVICE_URI + "folder/" + PlainFileSystemContext.ROOT_ID + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = '/' + name;
      assertTrue("Folder was not created in expected location. ", new java.io.File(testRoot, expectedPath).exists());
   }

   public void testCreateFolderNoName() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "folder/" + createTestFolderId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

//   public void testCreateFolderNoPermissions() throws Exception
//   {
//      Node parent = createTestNode.addNode("testCreateFolderNoPermissions_PARENT", "nt:folder");
//      parent.addMixin("exo:privilegeable");
//      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
//      permissions.put("root", PermissionType.ALL);
//      ((ExtendedNode)parent).setPermissions(permissions);
//      session.save();
//      String parentID = ((ExtendedNode)parent).getIdentifier();
//
//      String name = "testCreateFolderNoPermissions";
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("folder/") //
//         .append(parentID) //
//         .append("?") //
//         .append("name=") //
//         .append(name).toString();
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
//      assertEquals(403, response.getStatus());
//      log.info(new String(writer.getBody()));
//   }
//
   public void testCreateFolderWrongParent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderWrongParent";
      String path = SERVICE_URI + "folder/" + createTestFolderId + "_WRONG_ID" + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(404, response.getStatus());
   }

   public void testCreateFolderHierarchy() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderHierarchy/1/2/3/4/5";
      String path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = createTestFolderPath + '/' + name;
      assertTrue("Folder was not created in expected location. ", new java.io.File(testRoot, expectedPath).exists());
   }

   public void testCreateFolderHierarchy2() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderHierarchy2/1/2/3";
      if (!new java.io.File(new java.io.File(testRoot, createTestFolderPath), name).mkdirs())
      {
         fail();
      }
      name += "/4/5";
      String path = SERVICE_URI + "folder/" + createTestFolderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      // Expect path of first folder in hierarchy.
      // testCreateFolderHierarchy2/1/2/3 already exists create only 4/5
      assertEquals(createTestFolderPath + "/testCreateFolderHierarchy2/1/2/3/4", ((Folder)response.getEntity()).getPath());
      String expectedPath = createTestFolderPath + '/' + name;
      assertTrue("Folder was not created in expected location. ", new java.io.File(testRoot, expectedPath).exists());
   }
}
