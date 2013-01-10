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

public class DeleteTest extends PlainFileSystemTest
{
   private String folderPath;
   private String folderId;
   private String notEmptyFolderPath;
   private String notEmptyFolderId;
   private String filePath;
   private String fileId;

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
      java.io.File file = new java.io.File(thisTestRoot, "DeleteTest_FILE");
      FileOutputStream fOut = new FileOutputStream(file);
      fOut.write(DEFAULT_CONTENT.getBytes());
      fOut.flush();
      fOut.close();
      filePath = '/' + thisTestRoot.getName() + '/' + file.getName();
      fileId = pathToId(filePath);
      java.io.File folder = new java.io.File(thisTestRoot, "DeleteTest_FOLDER");
      if (!folder.mkdir())
      {
         fail();
      }
      folderPath = '/' + thisTestRoot.getName() + '/' + folder.getName();
      folderId = pathToId(folderPath);

      java.io.File tree = new java.io.File(thisTestRoot, "DeleteTest_TREE");
      if (!tree.mkdir())
      {
         fail();
      }
      notEmptyFolderPath = '/' + thisTestRoot.getName() + '/' + folder.getName();
      notEmptyFolderId = pathToId(notEmptyFolderPath);
      createTree(tree, 3);
   }

   /** Create file tree. Each level contain 5 folders and 5 empty files. */
   private void createTree(java.io.File root, int depth) throws Exception
   {
      if (depth < 0)
      {
         return;
      }
      for (int i = 0; i < 10; i++)
      {
         java.io.File f = new java.io.File(root, Integer.toString(i));
         if (!(i % 2 == 0 ? f.mkdirs() : f.createNewFile()))
         {
            fail();
         }
         if (depth >= 0 && f.isDirectory())
         {
            createTree(f, depth - 1);
         }

         --depth;
      }
   }

   public void testDeleteFile() throws Exception
   {
      String path = SERVICE_URI + "delete/" + fileId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      if (new java.io.File(testRoot, filePath).exists())
      {
         fail("File must be removed. ");
      }
   }

//   public void testDeleteFileLocked() throws Exception
//   {
//      Lock lock = fileNode.lock(true, false);
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("delete/") //
//         .append(fileID) //
//         .append("?") //
//         .append("lockToken=") //
//         .append(lock.getLockToken()) //
//         .toString();
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
//      assertEquals(204, response.getStatus());
//      try
//      {
//         ((ExtendedSession)session).getNodeByIdentifier(fileID);
//         fail("File must be removed. ");
//      }
//      catch (ItemNotFoundException e)
//      {
//      }
//   }
//
//   public void testDeleteFileLocked_NoLockToken() throws Exception
//   {
//      fileNode.lock(true, false);
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("delete/") //
//         .append(fileID) //
//         .toString();
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
//      assertEquals(423, response.getStatus());
//      log.info(new String(writer.getBody()));
//      try
//      {
//         ((ExtendedSession)session).getNodeByIdentifier(fileID);
//      }
//      catch (ItemNotFoundException e)
//      {
//         fail("File must not be removed since locked parent. ");
//      }
//   }
//
//   public void testDeleteFileNoPermissions() throws Exception
//   {
//      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
//      permissions.put("root", PermissionType.ALL);
//      permissions.put("john", new String[]{PermissionType.READ});
//      ((ExtendedNode)fileNode).setPermissions(permissions);
//      session.save();
//
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("delete/") //
//         .append(fileID).toString();
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
//      assertEquals(403, response.getStatus());
//      log.info(new String(writer.getBody()));
//      try
//      {
//         ((ExtendedSession)session).getNodeByIdentifier(fileID);
//      }
//      catch (ItemNotFoundException e)
//      {
//         fail("File must not be removed since permissions restriction. ");
//      }
//   }

   public void testDeleteFileWrongID() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "delete/" + fileId + "_WRONG_ID";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(404, response.getStatus());
   }

   public void testDeleteFolder() throws Exception
   {
      String path = SERVICE_URI + "delete/" + folderId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      if (new java.io.File(testRoot, folderPath).exists())
      {
         fail("Folder must be removed. ");
      }
   }

   public void testDeleteTree() throws Exception
   {
      String path = SERVICE_URI + "delete/" + notEmptyFolderId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      if (new java.io.File(testRoot, notEmptyFolderPath).exists())
      {
         fail("Folder must be removed. ");
      }
   }
}
