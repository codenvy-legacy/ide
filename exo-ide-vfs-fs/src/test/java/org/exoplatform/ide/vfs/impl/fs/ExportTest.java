/*
 * Copyright (C) 2011 eXo Platform SAS.
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
import org.exoplatform.ide.commons.ZipUtils;

import java.io.ByteArrayInputStream;

public class ExportTest extends LocalFileSystemTest
{
   private String folderId;
   private String folderPath;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      folderPath = createDirectory(testRootPath, "ExportTest");
      createTree(folderPath, 6, 4, null);
      folderId = pathToId(folderPath);
   }

   public void testExportFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "export/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));

      java.io.File unzip = getIoFile(createDirectory(testRootPath, "__unzip__"));
      ZipUtils.unzip(new ByteArrayInputStream(writer.getBody()), unzip);
      compareDirectories(getIoFile(folderPath), unzip);
   }

   public void testExportProject() throws Exception
   {
      // TODO
   }

   public void testExportMultiProject() throws Exception
   {
      // TODO
   }

   public void testDownloadZip() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "downloadzip/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));
      assertEquals("attachment; filename=\"" + getIoFile(folderPath).getName() + ".zip" + '"',
         writer.getHeaders().getFirst("Content-Disposition"));
   }
}
