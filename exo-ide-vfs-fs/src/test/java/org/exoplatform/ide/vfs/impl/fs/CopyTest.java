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
import org.exoplatform.ide.vfs.shared.ExitCodes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class CopyTest extends LocalFileSystemTest
{
   private final String fileName = "CopyTest_File";
   private final String folderName = "CopyTest_Folder";

   private String destinationPath;
   private String destinationId;

   private String protectedDestinationPath;
   private String protectedDestinationId;

   private String fileId;
   private String filePath;

   private String folderId;
   private String folderPath;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      filePath = createFile(testRootPath, fileName, DEFAULT_CONTENT_BYTES);
      folderPath = createDirectory(testRootPath, folderName);
      createTree(folderPath, 6, 4, null);
      destinationPath = createDirectory(testRootPath, "CopyTest_DestinationFolder");
      protectedDestinationPath = createDirectory(testRootPath, "CopyTest_ProtectedDestinationFolder");

      Map<String, Set<BasicPermissions>> accessList = new HashMap<String, Set<BasicPermissions>>(2);
      accessList.put("andrew", EnumSet.of(BasicPermissions.ALL));
      accessList.put("admin", EnumSet.of(BasicPermissions.READ));
      writeACL(protectedDestinationPath, accessList);

      fileId = pathToId(filePath);
      folderId = pathToId(folderPath);
      destinationId = pathToId(destinationPath);
      protectedDestinationId = pathToId(protectedDestinationPath);
   }

   public void testCopyFile() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + destinationId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = destinationPath + '/' + fileName;
      assertTrue("Source file not found. ", exists(filePath));
      assertTrue("Not found file in destination location. ", exists(expectedPath));
      assertTrue(Arrays.equals(DEFAULT_CONTENT_BYTES, readFile(expectedPath)));
   }

   public void testCopyFileAlreadyExist() throws Exception
   {
      byte[] existedFileContent = "existed file".getBytes();
      String existedFile = createFile(destinationPath, fileName, existedFileContent);
      String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + destinationId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals(400, response.getStatus());
      assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
      // untouched ??
      assertTrue(exists(existedFile));
      assertTrue(Arrays.equals(existedFileContent, readFile(existedFile)));
   }

   public void testCopyFileNoPermissionsDestination() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + protectedDestinationId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      String expectedPath = protectedDestinationPath + '/' + fileName;
      assertTrue("Source file not found. ", exists(filePath));
      assertFalse(exists(expectedPath));
   }

   public void testCopyFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + destinationId;
      final long start = System.currentTimeMillis();
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
      final long end = System.currentTimeMillis();
      log.info(">>>>> Copy tree time: {}ms", (end - start));
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      String expectedPath = destinationPath + '/' + folderName;
      assertTrue("Source folder not found. ", exists(folderPath));
      assertTrue("Not found file in destination location. ", exists(expectedPath));
      compareDirectories(folderPath, expectedPath);
   }

   public void testCopyFolderAlreadyExist() throws Exception
   {
      createDirectory(destinationPath, folderName);
      String requestPath = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + destinationId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals(400, response.getStatus());
      assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
      assertTrue("Source folder not found. ", exists(folderPath));
   }
}
