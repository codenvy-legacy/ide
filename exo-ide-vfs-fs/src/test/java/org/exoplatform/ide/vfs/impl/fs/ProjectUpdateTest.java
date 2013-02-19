/*
 * Copyright (C) 2013 eXo Platform SAS.
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
import org.exoplatform.ide.vfs.shared.Project;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProjectUpdateTest extends LocalFileSystemTest
{
   private String projectId;

   private String projectFileId;
   private String projectFilePath;

   private String projectFolderId;
   private String projectFolderPath;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();

      String projectPath = createDirectory(testRootPath, "ProjectUpdateTest_Project");
      Map<String, String[]> props = new HashMap<String, String[]>(1);
      props.put("vfs:mimeType", new String[]{Project.PROJECT_MIME_TYPE});
      writeProperties(projectPath, props);
      createTree(projectPath, 6, 4, null);

      List<String> l = flattenDirectory(projectPath);
      // Find one child in the list and lock it.
      for (int i = 0, size = l.size(); i < size && (projectFilePath == null || projectFolderPath == null); i++)
      {
         String s = l.get(i);
         String path = projectPath + '/' + s;
         File f = getIoFile(path);
         if (f.isFile())
         {
            projectFilePath = path;
         }
         else if (f.isDirectory())
         {
            projectFolderPath = path;
         }
      }

      projectFileId = pathToId(projectFilePath);
      projectFolderId = pathToId(projectFolderPath);
      projectId = pathToId(projectPath);

      String requestPath = SERVICE_URI + "watch/start/" + projectId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
   }

   @Override
   public void tearDown() throws Exception
   {
      String requestPath = SERVICE_URI + "watch/stop/" + projectId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
      super.tearDown();
   }

   private long readUpdateTime() throws Exception
   {
      String lastUpdateTime = getItem(projectId).getPropertyValue("vfs:lastUpdateTime");
      assertNotNull(lastUpdateTime); // must be set
      return Long.parseLong(lastUpdateTime);
   }

   public void testUpdateContent() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      byte[] newContent = "test update content".getBytes();
      String requestPath = SERVICE_URI + "content/" + projectFileId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
      headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, newContent, null);
      assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   public void testCreateNewFile() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String name = "some_file.txt";
      byte[] newContent = "test create new file".getBytes();
      String requestPath = SERVICE_URI + "file/" + projectId + '?' + "name=" + name;
      Map<String, List<String>> headers = new HashMap<String, List<String>>(1);
      headers.put("Content-Type", Arrays.asList("text/plain;charset=utf8"));
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, headers, newContent, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   public void testDeleteFile() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String requestPath = SERVICE_URI + "delete/" + projectFileId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   public void testMoveFile() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String newFolderId = pathToId(createDirectory(testRootPath, "dest_folder"));
      String requestPath = SERVICE_URI + "move/" + projectFileId + '?' + "parentId=" + newFolderId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   public void testRenameFile() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String newName = "_new_name_";
      String newMediaType = "text/plain";
      String requestPath = SERVICE_URI + "rename/" + projectFileId + '?' + "newname=" + newName + '&' +
         "mediaType=" + newMediaType;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }


   public void testCreateNewFolder() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String name = "some_folder";
      String requestPath = SERVICE_URI + "folder/" + projectId + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   public void testDeleteFolder() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String requestPath = SERVICE_URI + "delete/" + projectFolderId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   public void testMoveFolder() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String newFolderId = pathToId(createDirectory(testRootPath, "dest_folder2"));
      String requestPath = SERVICE_URI + "move/" + projectFolderId + '?' + "parentId=" + newFolderId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   public void testRenameFolder() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String newName = "_new_name_";
      String newMediaType = "text/plain";
      String requestPath = SERVICE_URI + "rename/" + projectFolderId + '?' + "newname=" + newName + '&' +
         "mediaType=" + newMediaType;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertTrue("vfs:lastUpdateTime must be updated", readUpdateTime() > longLastUpdateTime);
   }

   // Update metadata and ACL. Project update listener must not be notified.

   public void testUpdateItem() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      String requestPath = SERVICE_URI + "item/" + projectFileId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, properties.getBytes(), null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertEquals("vfs:lastUpdateTime must not be updated", longLastUpdateTime, readUpdateTime());
   }

   public void testUpdateAcl() throws Exception
   {
      long longLastUpdateTime = readUpdateTime();
      String acl = "[{\"principal\":\"admin\",\"permissions\":[\"all\"]}," +
         "{\"principal\":\"john\",\"permissions\":[\"read\", \"write\"]}]";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      String requestPath = SERVICE_URI + "acl/" + projectFileId;
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, acl.getBytes(), null);
      assertEquals("Error: " + response.getEntity(), 204, response.getStatus());
      assertEquals("vfs:lastUpdateTime must not be updated", longLastUpdateTime, readUpdateTime());
   }
}
