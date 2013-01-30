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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class CreateTest extends LocalFileSystemTest
{
   private String folderId;
   private String folderPath;

   private String protectedFolderPath;
   private String protectedFolderId;

   private String fileId;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      folderPath = createDirectory(testRootPath, "CreateTest_Folder");
      protectedFolderPath = createDirectory(testRootPath, "CreateTest_ProtectedFolder");
      String filePath = createFile(testRootPath, "CreateTest_File", DEFAULT_CONTENT_BYTES);

      Map<String, Set<BasicPermissions>> accessList = new HashMap<String, Set<BasicPermissions>>(2);
      accessList.put("andrew", EnumSet.of(BasicPermissions.ALL));
      accessList.put("admin", EnumSet.of(BasicPermissions.READ));
      writeACL(protectedFolderPath, accessList);

      folderId = pathToId(folderPath);
      protectedFolderId = pathToId(protectedFolderPath);
      fileId = pathToId(filePath);
   }

   public void testCreateFile() throws Exception
   {
      String name = "testCreateFile";
      String content = "test create file";
      String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);

      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, content.getBytes(), null);
      assertEquals(200, response.getStatus());
      String expectedPath = folderPath + '/' + name;
      assertTrue("File was not created in expected location. ", exists(expectedPath));
      assertEquals(content, new String(readFile(expectedPath)));
      Map<String,String[]> expectedProperties = new HashMap<String, String[]>(1);
      expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
      validateProperties(expectedPath, expectedProperties);
   }

   public void testCreateFileAlreadyExists() throws Exception
   {
      String name = "testCreateFileAlreadyExists";
      createFile(folderPath, name, null);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

   public void testCreateFileInRoot() throws Exception
   {
      String name = "FileInRoot";
      String content = "test create file";
      String requestPath = SERVICE_URI + "file/" + ROOT_ID + '?' + "name=" + name;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, content.getBytes(), writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = '/' + name;
      assertTrue("File was not created in expected location. ", exists(expectedPath));
      assertEquals(content, new String(readFile(expectedPath)));
      Map<String,String[]> expectedProperties = new HashMap<String, String[]>(1);
      expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
      validateProperties(expectedPath, expectedProperties);
   }

   public void testCreateFileNoContent() throws Exception
   {
      String name = "testCreateFileNoContent";
      String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = folderPath + '/' + name;
      assertTrue("File was not created in expected location. ", exists(expectedPath));
      assertTrue(readFile(expectedPath).length == 0);
      Map<String,String[]> expectedProperties = new HashMap<String, String[]>(1);
      expectedProperties.put("vfs:mimeType", new String[]{"application/octet-stream"});
      validateProperties(expectedPath, expectedProperties);
   }

   public void testCreateFileNoMediaType() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFileNoMediaType";
      String content = "test create file without media type";
      String requestPath = SERVICE_URI + "file/" + folderId + '?' + "name=" + name;
      ContainerResponse response =
         launcher.service("POST", requestPath, BASE_URI, null, content.getBytes(), writer, null);
      assertEquals(200, response.getStatus());
      String expectedPath = folderPath + '/' + name;
      assertTrue("File was not created in expected location. ", exists(expectedPath));
      assertEquals(content, new String(readFile(expectedPath)));
      Map<String,String[]> expectedProperties = new HashMap<String, String[]>(1);
      expectedProperties.put("vfs:mimeType", new String[]{"application/octet-stream"});
      validateProperties(expectedPath, expectedProperties);
   }

   public void testCreateFileNoName() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "file/" + folderId;
      ContainerResponse response =
         launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

   public void testCreateFileNoPermissions() throws Exception
   {
      String name = "testCreateFileNoPermissions";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "file/" + protectedFolderId + '?' + "name=" + name;
      ContainerResponse response =
         launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT.getBytes(), writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      assertFalse(exists(protectedFolderPath + '/' + name));
   }

   public void testCreateFileWrongParent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFileWrongParent";
      // Try to create new file in other file.
      String requestPath = SERVICE_URI + "file/" + fileId + '?' + "name=" + name;
      ContainerResponse response =
         launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

   public void testCreateFileWrongParentId() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFileWrongParentId";
      String requestPath = SERVICE_URI + "file/" + folderId + "_WRONG_ID" + '?' + "name=" + name;
      ContainerResponse response =
         launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(404, response.getStatus());
   }

   public void testCreateFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolder";
      String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = folderPath + '/' + name;
      assertTrue("Folder was not created in expected location. ", exists(expectedPath));
   }

   public void testCreateFolderInRoot() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "FolderInRoot";
      String requestPath = SERVICE_URI + "folder/" + ROOT_ID + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = '/' + name;
      assertTrue("Folder was not created in expected location. ", exists(expectedPath));
   }

   public void testCreateFolderNoName() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "folder/" + folderId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

   public void testCreateFolderNoPermissions() throws Exception
   {
      String name = "testCreateFolderNoPermissions";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "folder/" + protectedFolderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      String expectedPath = protectedFolderPath + '/' + name;
      assertFalse(exists(expectedPath));
   }

   public void testCreateFolderWrongParentId() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderWrongParentId";
      String requestPath = SERVICE_URI + "folder/" + folderId + "_WRONG_ID" + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(404, response.getStatus());
   }

   public void testCreateFolderHierarchy() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderHierarchy/1/2/3/4/5";
      String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = folderPath + '/' + name;
      assertTrue("Folder was not created in expected location. ", exists(expectedPath));
   }

   public void testCreateFolderHierarchyExists() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderHierarchyExists/1/2/3/4/5";
      createDirectory(folderPath, name);
      String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, DEFAULT_CONTENT_BYTES, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

   public void testCreateFolderHierarchy2() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String name = "testCreateFolderHierarchy2/1/2/3";
      createDirectory(folderPath, name);
      name += "/4/5";
      String requestPath = SERVICE_URI + "folder/" + folderId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      // Expect path of first folder in hierarchy.
      // testCreateFolderHierarchy2/1/2/3 already exists create only 4/5
      assertEquals(folderPath + "/testCreateFolderHierarchy2/1/2/3/4", ((Folder)response.getEntity()).getPath());
      String expectedPath = folderPath + '/' + name;
      assertTrue("Folder was not created in expected location. ", exists(expectedPath));
   }
}
