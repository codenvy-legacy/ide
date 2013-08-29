/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemImpl;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;

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
 * @version $Id: ChildrenTest.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class ChildrenTest extends JcrFileSystemTest
{
   private Node childrenTestNode;
   private String folderId;
   private String propertyFilter = "PropertyA";

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

      Node childFileNode = folderNode.addNode("ChildrenTest_FILE01", "nt:file");
      childFileNode.addMixin("exo:unstructuredMixin");
      childFileNode.setProperty("PropertyA", "A");
      childFileNode.setProperty("PropertyB", "B");
      Node childContentNode = childFileNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node childFolderNode1 = folderNode.addNode("ChildrenTest_FOLDER01", "nt:folder");
      childFolderNode1.addMixin("exo:unstructuredMixin");
      childFolderNode1.setProperty("PropertyA", "A");
      childFolderNode1.setProperty("PropertyB", "B");

      Node childFolderNode2 = folderNode.addNode("ChildrenTest_FOLDER02", "nt:folder");
      childFolderNode2.addMixin("exo:unstructuredMixin");
      childFolderNode2.setProperty("PropertyA", "A");
      childFolderNode2.setProperty("PropertyB", "B");

      session.save();

      folderId = ((ExtendedNode)folderNode).getIdentifier();
   }

   public void testGetChildren() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "children/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      @SuppressWarnings("unchecked")
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<String> list = new ArrayList<String>(3);
      for (Item i : children.getItems()){
         validateLinks(i);
         list.add(i.getName());
      }
      assertEquals(3, list.size());
      assertTrue(list.contains("ChildrenTest_FOLDER01"));
      assertTrue(list.contains("ChildrenTest_FOLDER02"));
      assertTrue(list.contains("ChildrenTest_FILE01"));
   }

   public void testGetChildrenNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)childrenTestNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "children/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   @SuppressWarnings("unchecked")
   public void testGetChildrenPagingSkipCount() throws Exception
   {
      // Get all children.
      String path = SERVICE_URI + "children/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<Object> all = new ArrayList<Object>(3);
      for (Item i : children.getItems())
         all.add(i.getName());

      Iterator<Object> iteratorAll = all.iterator();
      iteratorAll.next();
      iteratorAll.remove();

      // Skip first item in result.
      path = SERVICE_URI + "children/" + folderId + '?' + "skipCount=" + 1;
      checkPage(path, "GET", ItemImpl.class.getMethod("getName"), all);
   }

   @SuppressWarnings("unchecked")
   public void testGetChildrenPagingMaxItems() throws Exception
   {
      // Get all children.
      String path = SERVICE_URI + "children/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<Object> all = new ArrayList<Object>(3);
      for (Item i : children.getItems())
         all.add(i.getName());

      // Exclude last item from result.
      path = SERVICE_URI + "children/" + folderId + '?' + "maxItems=" + 2;
      all.remove(2);
      checkPage(path, "GET", ItemImpl.class.getMethod("getName"), all);
   }

   @SuppressWarnings("unchecked")
   public void testGetChildrenNoPropertyFilter() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      // Get children without filter.
      String path = SERVICE_URI + "children/" + folderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      assertEquals(3, children.getItems().size());
      for (Item i : children.getItems())
      {
         // No properties without filter. 'none' filter is used if nothing set by client.
         assertFalse(hasProperty(i, "PropertyA"));
         assertFalse(hasProperty(i, "PropertyB"));
      }
   }

   @SuppressWarnings("unchecked")
   public void testGetChildrenPropertyFilter() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      // Get children and apply filter for properties.
      String path = SERVICE_URI + "children/" + folderId + '?' + "propertyFilter=" + propertyFilter;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      assertEquals(3, children.getItems().size());
      for (Item i : children.getItems())
      {
         assertTrue(hasProperty(i, "PropertyA"));
         assertFalse(hasProperty(i, "PropertyB")); // must be excluded
      }
   }

   @SuppressWarnings("unchecked")
   public void testGetChildrenTypeFilter() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      // Get children and apply filter for properties.
      String path = SERVICE_URI + "children/" + folderId + '?' + "itemType=" + "folder";
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      assertEquals(2, children.getItems().size());
      for (Item i : children.getItems())
      {
         assertTrue(i.getItemType() == ItemType.FOLDER);
      }
   }

   @SuppressWarnings("rawtypes")
   private boolean hasProperty(Item i, String propertyName)
   {
      List<Property> properties = i.getProperties();
      if (properties.size() == 0)
         return false;
      for (Property p : properties)
         if (p.getName().equals(propertyName))
            return true;
      return false;
   }
}
