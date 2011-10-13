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
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GetItemTest extends JcrFileSystemTest
{
   private Node getObjectTestNode;
   private String folderId;
   private String folderPath;
   private String fileId;
   private String filePath;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      getObjectTestNode = testRoot.addNode(name, "nt:unstructured");
      getObjectTestNode.addMixin("exo:privilegeable");

      Node folderNode = getObjectTestNode.addNode("GetObjectTest_FOLDER", "nt:folder");
      folderId = ((ExtendedNode)folderNode).getIdentifier();
      folderPath = folderNode.getPath();

      Node fileNode = getObjectTestNode.addNode("GetObjectTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("exo:unstructuredMixin");
      fileNode.setProperty("MyProperty01", "hello world");
      fileNode.setProperty("MyProperty02", "to be or not to be");
      fileNode.setProperty("MyProperty03", 123);
      fileNode.setProperty("MyProperty04", true);
      fileNode.setProperty("MyProperty05", Calendar.getInstance());
      fileNode.setProperty("MyProperty06", 123.456);
      fileId = ((ExtendedNode)fileNode).getIdentifier();
      filePath = fileNode.getPath();

      session.save();
   }

   public void testGetFile() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(fileId).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FILE, item.getItemType());
      assertEquals(fileId, item.getId());
      assertEquals(filePath, item.getPath());
      validateLinks(item);
   }

   public void testGetFileByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("itembypath") //
         .append("?") //
         .append("path=") //
         .append(filePath).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FILE, item.getItemType());
      assertEquals(fileId, item.getId());
      assertEquals(filePath, item.getPath());
      validateLinks(item);
   }

   @SuppressWarnings("rawtypes")
   public void testGetFilePropertyFilter() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      // No filter - all properties
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(fileId) //
         .toString();

      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      List<Property> properties = ((Item)response.getEntity()).getProperties();
      Map<String, List> m = new HashMap<String, List>(properties.size());
      for (Property p : properties)
         m.put(p.getName(), p.getValue());
      assertEquals(6, m.size());
      assertTrue(m.containsKey("MyProperty01"));
      assertTrue(m.containsKey("MyProperty02"));
      assertTrue(m.containsKey("MyProperty03"));
      assertTrue(m.containsKey("MyProperty04"));
      assertTrue(m.containsKey("MyProperty05"));
      assertTrue(m.containsKey("MyProperty06"));

      // With filter
      path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(fileId) //
         .append("?") //
         .append("propertyFilter=") //
         .append("MyProperty02") //
         .toString();

      response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      m.clear();
      properties = ((Item)response.getEntity()).getProperties();
      for (Property p : properties)
         m.put(p.getName(), p.getValue());
      assertEquals(1, m.size());
      assertEquals("to be or not to be", m.get("MyProperty02").get(0));
   }

   public void testGetFileNotFound() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(fileId + "_WRONG_ID_").toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetFileNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)getObjectTestNode).setPermissions(permissions);
      session.save();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(fileId).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(folderId).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FOLDER, item.getItemType());
      assertEquals(folderId, item.getId());
      assertEquals(folderPath, item.getPath());
      validateLinks(item);
   }

   public void testGetFolderByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("itembypath") //
         .append("?") //
         .append("path=") //
         .append(folderPath).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      Item item = (Item)response.getEntity();
      assertEquals(ItemType.FOLDER, item.getItemType());
      assertEquals(folderId, item.getId());
      assertEquals(folderPath, item.getPath());
      validateLinks(item);
   }

   public void testGetFolderByPathWithVersionID() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("itembypath") //
         .append("?") //
         .append("path=") //
         .append(folderPath) //
         .append("&") //
         .append("versionId=") //
         .append("1").toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(400, response.getStatus());
   }
}
