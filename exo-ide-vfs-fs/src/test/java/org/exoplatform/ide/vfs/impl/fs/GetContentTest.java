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

import java.io.FileOutputStream;

public class GetContentTest extends PlainFileSystemTest
{
   private String content = "__GetContentTest__";
   private String filePath;
   private String fileId;
   private String folderId;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      java.io.File thisTestRoot = new java.io.File(testRoot, name);
      if (!thisTestRoot.mkdirs())
      {
         fail();
      }
      java.io.File file = new java.io.File(thisTestRoot, "GetContentTest_FILE");
      FileOutputStream fOut = new FileOutputStream(file);
      fOut.write(content.getBytes());
      fOut.flush();
      fOut.close();
      filePath = '/' + thisTestRoot.getName() + '/' + file.getName();
      fileId = pathToId(filePath);
      java.io.File folder = new java.io.File(thisTestRoot, "GetContentTest_FOLDER");
      if (!folder.mkdir())
      {
         fail();
      }
      String folderPath = '/' + thisTestRoot.getName() + '/' + folder.getName();
      folderId = pathToId(folderPath);
   }

   public void testGetContent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + fileId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      assertEquals(content, new String(writer.getBody()));
      // TODO
//      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")).toString(), writer
//         .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
   }

//   public void testDownloadFile() throws Exception
//   {
//      // Expect the same as 'get content' plus header "Content-Disposition".
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("downloadfile/") //
//         .append(fileID).toString();
//      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
//      assertEquals(200, response.getStatus());
//      //log.info(new String(writer.getBody()));
//      assertEquals(content, new String(writer.getBody()));
//      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")).toString(), writer
//         .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
//      assertEquals("attachment; filename=\"" + fileName + "\"", writer.getHeaders().getFirst("Content-Disposition"));
//   }

   public void testGetContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }

//   public void testGetContentNoPermissions() throws Exception
//   {
//      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
//      permissions.put("root", PermissionType.ALL);
//      ((ExtendedNode)getContentTestNode).setPermissions(permissions);
//      session.save();
//
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("content/") //
//         .append(fileID).toString();
//      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
//      assertEquals(403, response.getStatus());
//      log.info(new String(writer.getBody()));
//   }

   public void testGetContentByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "contentbypath" + filePath;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      log.info(new String(writer.getBody()));
      assertEquals(content, new String(writer.getBody()));
      // TODO
//      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")).toString(), writer
//         .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
   }
}
