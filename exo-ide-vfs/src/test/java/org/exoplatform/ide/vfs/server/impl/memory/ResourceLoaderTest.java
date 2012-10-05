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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceLoaderTest extends MemoryFileSystemTest
{
   private String folderId;
   private String folderPath;
   private String fileId;
   private String filePath;

   private String vfsId = "memory";

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      MemoryFolder resourceLoaderTestFolder = new MemoryFolder(name);
      testRoot.addChild(resourceLoaderTestFolder);

      MemoryFolder folder = new MemoryFolder("GetResourceTest_FOLDER");
      resourceLoaderTestFolder.addChild(folder);
      MemoryFile childFile = new MemoryFile("file1", "text/plain",
         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      folder.addChild(childFile);
      folderId = folder.getId();
      folderPath = folder.getPath();

      MemoryFile file = new MemoryFile("GetResourceTest_FILE", "text/plain",
         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      resourceLoaderTestFolder.addChild(file);
      fileId = file.getId();
      filePath = file.getPath();

      memoryContext.putItem(resourceLoaderTestFolder);
   }

   public void testLoadFileByID() throws Exception
   {
      URL file = new URI("ide+vfs", '/' + vfsId, fileId).toURL();
      final String expectedURL = "ide+vfs:/" + vfsId + '#' + fileId;
      assertEquals(expectedURL, file.toString());
      byte[] b = new byte[128];
      InputStream in = file.openStream();
      int num = in.read(b);
      in.close();
      assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
   }

   public void testLoadFileByPath() throws Exception
   {
      URL file = new URI("ide+vfs", '/' + vfsId, filePath).toURL();
      final String expectedURL = "ide+vfs:/" + vfsId + '#' + filePath;
      assertEquals(expectedURL, file.toString());
      byte[] b = new byte[128];
      InputStream in = file.openStream();
      int num = in.read(b);
      in.close();
      assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
   }

   public void testLoadFolderByID() throws Exception
   {
      URL folder = new URI("ide+vfs", '/' + vfsId, folderId).toURL();
      final String expectedURL = "ide+vfs:/" + vfsId + '#' + folderId;
      assertEquals(expectedURL, folder.toString());
      byte[] b = new byte[128];
      InputStream in = folder.openStream();
      int num = in.read(b);
      in.close();
      assertEquals("file1\n", new String(b, 0, num));
   }

   public void testLoadFolderByPath() throws Exception
   {
      URL folder = new URI("ide+vfs", '/' + vfsId, folderPath).toURL();
      final String expectedURL = "ide+vfs:/" + vfsId + '#' + folderPath;
      assertEquals(expectedURL, folder.toString());
      byte[] b = new byte[128];
      InputStream in = folder.openStream();
      int num = in.read(b);
      in.close();
      assertEquals("file1\n", new String(b, 0, num));
   }
}
