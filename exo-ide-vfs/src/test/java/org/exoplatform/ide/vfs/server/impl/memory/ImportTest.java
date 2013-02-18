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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.everrest.core.impl.ContainerResponse;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: ImportTest.java Nov 21, 2012 vetal $
 */
public class ImportTest extends MemoryFileSystemTest
{
   private String importTestRootId;
   private byte[] zipFolder;
   private byte[] zipProject;

   private final String projectProperties = "[{\"name\":\"vfs:projectType\",\"value\":[\"java\"]}," +
      "{\"name\":\"vfs:mimeType\",\"value\":[\"text/vnd.ideproject+directory\"]}]";

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      MemoryFolder importTestRoot = new MemoryFolder(name);
      testRoot.addChild(importTestRoot);
      memoryContext.putItem(importTestRoot);
      importTestRootId = importTestRoot.getId();

      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ZipOutputStream zipOut = new ZipOutputStream(bout);
      zipOut.putNextEntry(new ZipEntry("folder1/"));
      zipOut.putNextEntry(new ZipEntry("folder2/"));
      zipOut.putNextEntry(new ZipEntry("folder3/"));
      zipOut.putNextEntry(new ZipEntry("folder1/file1.txt"));
      zipOut.write(DEFAULT_CONTENT_BYTES);
      zipOut.putNextEntry(new ZipEntry("folder2/file2.txt"));
      zipOut.write(DEFAULT_CONTENT_BYTES);
      zipOut.putNextEntry(new ZipEntry("folder3/file3.txt"));
      zipOut.write(DEFAULT_CONTENT_BYTES);
      zipOut.close();
      zipFolder = bout.toByteArray();

      bout.reset();
      zipOut = new ZipOutputStream(bout);
      zipOut.putNextEntry(new ZipEntry(".project"));
      zipOut.write(projectProperties.getBytes());
      zipOut.putNextEntry(new ZipEntry("readme.txt"));
      zipOut.write(DEFAULT_CONTENT_BYTES);
      zipOut.close();
      zipProject = bout.toByteArray();
   }

   public void testImportFolder() throws Exception
   {
      String path = SERVICE_URI + "import/" + importTestRootId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, zipFolder, null);
      assertEquals(204, response.getStatus());
      MemoryFolder parent = (MemoryFolder)memoryContext.getItem(importTestRootId);
      MemoryFolder folder1 = (MemoryFolder)parent.getChild("folder1");
      assertNotNull(folder1);
      MemoryFolder folder2 = (MemoryFolder)parent.getChild("folder2");
      assertNotNull(folder2);
      MemoryFolder folder3 = (MemoryFolder)parent.getChild("folder3");
      assertNotNull(folder3);
      MemoryFile file1 = (MemoryFile)folder1.getChild("file1.txt");
      assertNotNull(file1);
      checkFileContext(DEFAULT_CONTENT, "text/plain", file1);
      MemoryFile file2 = (MemoryFile)folder2.getChild("file2.txt");
      assertNotNull(file2);
      checkFileContext(DEFAULT_CONTENT, "text/plain", file2);
      MemoryFile file3 = (MemoryFile)folder3.getChild("file3.txt");
      assertNotNull(file3);
      checkFileContext(DEFAULT_CONTENT, "text/plain", file3);
   }

   public void testImportProject() throws Exception
   {
      String path = SERVICE_URI + "import/" + importTestRootId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, zipProject, null);
      assertEquals(204, response.getStatus());
      MemoryFolder parent = (MemoryFolder)memoryContext.getItem(importTestRootId);
      List<Property> properties = parent.getProperties(PropertyFilter.ALL_FILTER);
      for (Property property : properties)
      {
         if ("vfs:projectType".equals(property.getName()))
         {
            assertEquals("java", property.getValue().get(0));
         }
         else if ("vfs:mimeType".equals(property.getName()))
         {
            assertEquals("text/vnd.ideproject+directory", property.getValue().get(0));
         }
      }
      assertEquals(1, parent.getChildren().size()); // file .project must be store as project properties not like a file
      MemoryFile readme = (MemoryFile)parent.getChild("readme.txt");
      assertNotNull(readme);
      checkFileContext(DEFAULT_CONTENT, "text/plain", readme);
   }
}
