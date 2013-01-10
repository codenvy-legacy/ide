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
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.io.FileOutputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GetItemTest.java 77587 2011-12-13 10:42:02Z andrew00x $
 */
public class GetItemTest extends PlainFileSystemTest
{
   //   private Node getObjectTestNode;
   private String folderId;
   private String folderPath;
   private String fileId;
   private String filePath;

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
      java.io.File file = new java.io.File(thisTestRoot, "GetObjectTest_FILE");
      FileOutputStream fOut = new FileOutputStream(file);
      fOut.write(DEFAULT_CONTENT.getBytes());
      fOut.flush();
      fOut.close();

      java.io.File folder = new java.io.File(thisTestRoot, "GetObjectTest_FOLDER");
      if (!folder.mkdirs())
      {
         fail();
      }

//      fileNode.setProperty("MyProperty01", "hello world");
//      fileNode.setProperty("MyProperty02", "to be or not to be");
//      fileNode.setProperty("MyProperty03", 123);
//      fileNode.setProperty("MyProperty04", true);
//      fileNode.setProperty("MyProperty05", Calendar.getInstance());
//      fileNode.setProperty("MyProperty06", 123.456);
//      fileId = ((ExtendedNode)fileNode).getIdentifier();
//      filePath = fileNode.getPath();
//
//      session.save();
      filePath = '/' + thisTestRoot.getName() + '/' + file.getName();
      fileId = pathToId(filePath);
      folderPath = '/' + thisTestRoot.getName() + '/' + folder.getName();
      folderId = pathToId(folderPath);
   }

   public void testGetFile() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "item/" + fileId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FILE, item.getItemType());
      assertEquals(fileId, item.getId());
      assertEquals(filePath, item.getPath());
//      validateLinks(item);
   }

   public void testGetFileByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "itembypath" + filePath;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FILE, item.getItemType());
      assertEquals(fileId, item.getId());
      assertEquals(filePath, item.getPath());
//      validateLinks(item);
   }

//   @SuppressWarnings("rawtypes")
//   public void testGetFilePropertyFilter() throws Exception
//   {
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      // No filter - all properties
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("item/") //
//         .append(fileId) //
//         .toString();
//
//      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
//      //log.info(new String(writer.getBody()));
//      assertEquals(200, response.getStatus());
//      List<Property> properties = ((Item)response.getEntity()).getProperties();
//      Map<String, List> m = new HashMap<String, List>(properties.size());
//      for (Property p : properties)
//      {
//         m.put(p.getName(), p.getValue());
//      }
//      assertTrue(m.size() >= 6);
//      assertTrue(m.containsKey("MyProperty01"));
//      assertTrue(m.containsKey("MyProperty02"));
//      assertTrue(m.containsKey("MyProperty03"));
//      assertTrue(m.containsKey("MyProperty04"));
//      assertTrue(m.containsKey("MyProperty05"));
//      assertTrue(m.containsKey("MyProperty06"));
//
//      // With filter
//      path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("item/") //
//         .append(fileId) //
//         .append("?") //
//         .append("propertyFilter=") //
//         .append("MyProperty02") //
//         .toString();
//
//      response = launcher.service("GET", path, BASE_URI, null, null, null);
//      assertEquals(200, response.getStatus());
//      m.clear();
//      properties = ((Item)response.getEntity()).getProperties();
//      for (Property p : properties)
//      {
//         m.put(p.getName(), p.getValue());
//      }
//      assertEquals(1, m.size());
//      assertEquals("to be or not to be", m.get("MyProperty02").get(0));
//   }
//
//   public void testGetFileNotFound() throws Exception
//   {
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("item/") //
//         .append(fileId + "_WRONG_ID_").toString();
//      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
//      assertEquals(404, response.getStatus());
//      log.info(new String(writer.getBody()));
//   }
//
//   public void testGetFileNoPermissions() throws Exception
//   {
//      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
//      permissions.put("root", PermissionType.ALL);
//      ((ExtendedNode)getObjectTestNode).setPermissions(permissions);
//      session.save();
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("item/") //
//         .append(fileId).toString();
//      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
//      assertEquals(403, response.getStatus());
//      log.info(new String(writer.getBody()));
//   }

   public void testGetFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "item/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FOLDER, item.getItemType());
      assertEquals(folderId, item.getId());
      assertEquals(folderPath, item.getPath());
//      validateLinks(item);
   }

   public void testGetFolderByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "itembypath" + folderPath;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FOLDER, item.getItemType());
      assertEquals(folderId, item.getId());
      assertEquals(folderPath, item.getPath());
//      validateLinks(item);
   }

//   public void testGetFolderByPathWithVersionID() throws Exception
//   {
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = new StringBuilder() //
//         .append(SERVICE_URI) //
//         .append("itembypath") //
//         .append(folderPath) //
//         .append("?") //
//         .append("versionId=") //
//         .append("1").toString();
//      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
//      log.info(new String(writer.getBody()));
//      assertEquals(400, response.getStatus());
//   }
}
