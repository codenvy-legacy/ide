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

import org.exoplatform.ide.vfs.Item;
import org.exoplatform.ide.vfs.ItemList;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ChildrenTest extends JcrFileSystemTest
{
   private Node childrenTestNode;

   private String folder;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      childrenTestNode = testRoot.addNode(name, "nt:unstructured");
      childrenTestNode.addMixin("exo:privilegeable");

      Node folderNode = childrenTestNode.addNode("ChildrenTest_FOLDER", "nt:folder");

      Node childDocumentNode = folderNode.addNode("ChildrenTest_DOCUMENT01", "nt:file");
      Node childContentNode = childDocumentNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      folderNode.addNode("ChildrenTest_FOLDER01", "nt:folder");

      folderNode.addNode("ChildrenTest_FOLDER02", "nt:folder");

      session.save();

      folder = folderNode.getPath();
   }

   public void testGetChildren() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)childrenTestNode).setPermissions(permissions);
      session.save();
      
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/children") //
         .append(folder).toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetChildrenNoPermissions() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/children") //
         .append(folder).toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      @SuppressWarnings("unchecked")
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<String> list = new ArrayList<String>(3);
      for (Item i : children.getItems())
         list.add(i.getName());
      assertEquals(3, list.size());
      assertTrue(list.contains("ChildrenTest_FOLDER01"));
      assertTrue(list.contains("ChildrenTest_FOLDER02"));
      assertTrue(list.contains("ChildrenTest_DOCUMENT01"));
   }

   @SuppressWarnings("unchecked")
   public void testGetChildrenPagingSkipCount() throws Exception
   {
      // Get all children.
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/children") //
         .append(folder).toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<String> all = new ArrayList<String>(3);
      for (Item i : children.getItems())
         all.add(i.getName());

      // Skip first item in result.
      path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/children") //
         .append(folder) //
         .append("?") //
         .append("skipCount=") //
         .append("1") //
         .toString();
      response = launcher.service("GET", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      children = (ItemList<Item>)response.getEntity();
      List<String> page = new ArrayList<String>(2);
      for (Item i : children.getItems())
         page.add(i.getName());

      Iterator<String> iteratorAll = all.iterator();
      iteratorAll.next();
      iteratorAll.remove();

      assertEquals(all, page);
   }

   @SuppressWarnings("unchecked")
   public void testGetChildrenPagingMaxItems() throws Exception
   {
      // Get all children.
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/children") //
         .append(folder).toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<String> all = new ArrayList<String>(3);
      for (Item i : children.getItems())
         all.add(i.getName());

      // Exclude last item from result.
      path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/children") //
         .append(folder) //
         .append("?") //
         .append("maxItems=") //
         .append("2") //
         .toString();
      response = launcher.service("GET", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      children = (ItemList<Item>)response.getEntity();
      List<String> page = new ArrayList<String>(2);
      for (Item i : children.getItems())
         page.add(i.getName());

      all.remove(2);
      
      assertEquals(all, page);
   }
}
