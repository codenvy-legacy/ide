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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class ContentTest extends LocalFileSystemTest
{
   private final String lockToken = "1234567890abcdef";
   private final byte[] content = "__ContentTest__".getBytes();
   private final byte[] updateContent = "__UpdateContentTest__".getBytes();

   private String filePath;
   private String fileId;

   private String protectedFilePath;
   private String protectedFileId;

   private String lockedFilePath;
   private String lockedFileId;

   private String folderId;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      filePath = createFile(testRootPath, "ContentTest_File", content);
      lockedFilePath = createFile(testRootPath, "ContentTest_LockedFile", content);
      protectedFilePath = createFile(testRootPath, "ContentTest_ProtectedFile", content);
      String folderPath = createDirectory(testRootPath, "ContentTest_Folder");

      createLock(lockedFilePath, lockToken);

      Map<String, Set<BasicPermissions>> accessList = new HashMap<String, Set<BasicPermissions>>(1);
      accessList.put("andrew", EnumSet.of(BasicPermissions.ALL));
      writeACL(protectedFilePath, accessList);

      fileId = pathToId(filePath);
      lockedFileId = pathToId(lockedFilePath);
      protectedFileId = pathToId(protectedFilePath);
      folderId = pathToId(folderPath);
   }

   public void testGetContent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "content/" + fileId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue(Arrays.equals(content, writer.getBody()));
      assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
   }

   public void testDownloadFile() throws Exception
   {
      // Expect the same as 'get content' plus header "Content-Disposition".
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "downloadfile/" + fileId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue(Arrays.equals(content, writer.getBody()));
      assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
      assertEquals(String.format("attachment; filename=\"%s\"", "ContentTest_File"),
         writer.getHeaders().getFirst("Content-Disposition"));
   }

   public void testGetContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "content/" + folderId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

   public void testGetContentNoPermissions() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "content/" + protectedFileId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
   }

   public void testGetContentByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "contentbypath" + filePath;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      log.info(new String(writer.getBody()));
      assertTrue(Arrays.equals(content, writer.getBody()));
      assertEquals("text/plain", writer.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
   }

   public void testGetContentByPathNoPermissions() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "contentbypath" + protectedFilePath;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
   }


   public void testUpdateContent() throws Exception
   {
      String requestPath = SERVICE_URI + "content/" + fileId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
      List<String> contentType = new ArrayList<String>(1);
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, null);
      assertEquals(204, response.getStatus());
      assertTrue(Arrays.equals(updateContent, readFile(filePath)));
      Map<String,String[]> expectedProperties = new HashMap<String, String[]>(1);
      expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
      validateProperties(filePath, expectedProperties);
   }

   public void testUpdateContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "content/" + folderId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, updateContent, writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testUpdateContentNoPermissions() throws Exception
   {
      Map<String, Set<BasicPermissions>> accessList = new HashMap<String, Set<BasicPermissions>>(1);
      // Restore 'read' permission for 'admin'.
      // All requests in test use this principal by default.
      accessList.put("admin", EnumSet.of(BasicPermissions.READ));
      writeACL(protectedFilePath, accessList);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "content/" + protectedFileId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
      List<String> contentType = new ArrayList<String>(1);
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, writer, null);
      // Request must fail since 'admin' has not 'write' permission (only 'read').
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
      assertTrue("Content must not be updated", Arrays.equals(content, readFile(protectedFilePath)));
      assertNull("Properties must not be updated", readProperties(filePath));
   }

   public void testUpdateContentLocked() throws Exception
   {
      Map <String, List <String>> headers = new HashMap <String, List <String>> ();
      List <String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);
      String requestPath = SERVICE_URI + "content/" + lockedFileId + '?' + "lockToken=" + lockToken;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, null);
      // File is locked.
      assertEquals(204, response.getStatus());
      assertTrue(Arrays.equals(updateContent, readFile(lockedFilePath))); // content updated
      // media type is set
      Map<String,String[]> expectedProperties = new HashMap<String, String[]>(1);
      expectedProperties.put("vfs:mimeType", new String[]{"text/plain;charset=utf8"});
      validateProperties(lockedFilePath, expectedProperties);
   }

   public void testUpdateContentLocked_NoLockToken() throws Exception
   {
      Map <String, List <String>> headers = new HashMap <String, List <String>> ();
      List <String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);
      String requestPath = SERVICE_URI + "content/" + lockedFileId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, updateContent, writer, null);
      // File is locked.
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
      assertTrue("Content must not be updated", Arrays.equals(content, readFile(lockedFilePath)));
      assertNull("Properties must not be updated", readProperties(lockedFilePath));
   }
}
