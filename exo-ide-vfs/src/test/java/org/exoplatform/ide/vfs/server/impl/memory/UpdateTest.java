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

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: UpdateTest.java 77229 2011-12-03 16:56:34Z andrew00x $
 */
public class UpdateTest extends MemoryFileSystemTest
{
   private String fileId;
   private String folderId;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();

      MemoryFolder updateTestFolder = new MemoryFolder(name);
      testRoot.addChild(updateTestFolder);

      MemoryFile file = new MemoryFile("UpdateTest_FILE", "text/plain",
         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      updateTestFolder.addChild(file);
      fileId = file.getId();

      MemoryFolder folder = new MemoryFolder("UpdateTest_FOLDER");
      updateTestFolder.addChild(folder);
      folderId = folder.getId();

      memoryContext.putItem(updateTestFolder);
   }

   public void testUpdatePropertiesFile() throws Exception
   {
      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      doUpdate(fileId, properties);
      MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
      List<String> values = file.getProperties(PropertyFilter.valueOf("MyProperty")).get(0).getValue();
      assertEquals("MyValue", values.get(0));
   }

   public void testUpdatePropertiesAndChangeFolderType() throws Exception
   {
      MemoryFolder folder = (MemoryFolder)memoryContext.getItem(folderId);
      assertFalse(folder.isProject());
      String properties = "[{\"name\":\"vfs:mimeType\", \"value\":[\"text/vnd.ideproject+directory\"]}]";
      doUpdate(folderId, properties);
      folder = (MemoryFolder)memoryContext.getItem(folderId);
      assertTrue("Regular folder must be converted to project. ", folder.isProject());
   }

   public void testUpdatePropertiesAndChangeFolderType2() throws Exception
   {
      MemoryFolder folder = (MemoryFolder)memoryContext.getItem(folderId);
      folder.updateProperties(Arrays.asList(new Property("vfs:mimeType", "text/vnd.ideproject+directory")));
      assertTrue(folder.isProject());
      String properties = "[{\"name\":\"vfs:mimeType\", \"value\":[\"text/directory\"]}]";
      doUpdate(folderId, properties);
      folder = (MemoryFolder)memoryContext.getItem(folderId);
      assertFalse("Project must be converted to regular folder . ", folder.isProject());
   }

   public void doUpdate(String id, String rawData) throws Exception
   {
      String path = SERVICE_URI + "item/" + id;
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, rawData.getBytes(), null);
      assertEquals(200, response.getStatus());
   }
}
