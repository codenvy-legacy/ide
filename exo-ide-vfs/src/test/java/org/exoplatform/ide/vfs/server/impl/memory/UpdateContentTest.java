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
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFile;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: UpdateContentTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class UpdateContentTest extends MemoryFileSystemTest
{
   private String fileId;
   private String folderId;
   private String content = "__UpdateContentTest__";

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      MemoryFolder updateContentTestFolder = new MemoryFolder(name);
      testRoot.addChild(updateContentTestFolder);

      MemoryFile file = new MemoryFile("UpdateContentTest_FILE", "text/plain",
         new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      updateContentTestFolder.addChild(file);
      fileId = file.getId();

      MemoryFolder folder = new MemoryFolder("UpdateContentTest_FOLDER");
      updateContentTestFolder.addChild(folder);
      folderId = folder.getId();

      memoryContext.putItem(updateContentTestFolder);
   }

   public void testUpdateContent() throws Exception
   {
      String path = SERVICE_URI + "content/" + fileId;

      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);

      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
      assertEquals(204, response.getStatus());

      MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
      checkFileContext(content, "text/plain;charset=utf8", file);
   }

   public void testUpdateContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + folderId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, content.getBytes(), writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testUpdateContentNoPermissions() throws Exception
   {
      AccessControlEntry adminACE = new AccessControlEntryImpl();
      adminACE.setPrincipal("admin");
      adminACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
      AccessControlEntry userACE = new AccessControlEntryImpl();
      userACE.setPrincipal("john");
      userACE.setPermissions(new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.READ.value())));
      memoryContext.getItem(fileId).updateACL(Arrays.asList(adminACE, userACE), true);

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + fileId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testUpdateContentLocked() throws Exception
   {
      MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
      String lockToken = file.lock();

      String path = SERVICE_URI + "content/" + fileId + '?' + "lockToken=" + lockToken;

      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);

      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
      assertEquals(204, response.getStatus());

      file = (MemoryFile)memoryContext.getItem(fileId);
      checkFileContext(content, "text/plain;charset=utf8", file);
   }

   public void testUpdateContentLocked_NoLockTokens() throws Exception
   {
      MemoryFile file = (MemoryFile)memoryContext.getItem(fileId);
      file.lock();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + fileId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
