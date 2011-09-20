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

import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CreateTest.java 65533 2011-01-26 12:31:23Z andrew00x $
 */
public class ProjectTest extends JcrFileSystemTest
{

   private static final String PROJECT_NT = "vfs:project";
   private static final String PROJECT_MT = "text/vnd.ideproject+directory";

   private String CREATE_TEST_PATH;

   //private Node readTestNode;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      testRoot.addNode(name, "nt:unstructured");

      session.save();
      CREATE_TEST_PATH = "/" + TEST_ROOT_NAME + "/" + name;
   }

   public void testCreateProject() throws Exception
   {
      String name = "testCreateProject";

      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";

      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("project") //
         .append(CREATE_TEST_PATH) //
         .append("?") //
         .append("name=") //
         .append(name).append("&").append("type=") //
         .append("java").toString();

      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));

      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, properties.getBytes(), null);

      assertEquals("Error: " + response.getEntity(), 201, response.getStatus());
      String expectedPath = CREATE_TEST_PATH + "/" + name;
      String expectedLocation = SERVICE_URI + "item" + expectedPath;
      String location = response.getHttpHeaders().getFirst("Location").toString();
      assertEquals(expectedLocation, location);

      assertTrue("Project was not created in expected location. ", session.itemExists(expectedPath));
      Node project = (Node)session.getItem(expectedPath);
      assertTrue("vfs:project node type expected", project.getPrimaryNodeType().isNodeType(PROJECT_NT));

      assertEquals("java", project.getProperty("type").getString());
      assertEquals("MyValue", project.getProperty("MyProperty").getString());
   }

   public void testGetProjectItem() throws Exception
   {
      Node readRoot = testRoot.addNode("testGetProjectItem", "nt:unstructured");
      Node proj1 = readRoot.addNode("project1", PROJECT_NT);
      proj1.setProperty("type", "java");
      proj1.setProperty("prop1", "val1");
      readRoot.getSession().save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item") //
         .append(proj1.getPath()).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);

      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertEquals("application/json", response.getContentType().toString());

      Project p = (Project)response.getEntity();
      assertEquals("project1", p.getName());
      assertEquals(PROJECT_MT, p.getMimeType());
      assertEquals("java", p.getProjectType());

      assertEquals(2, p.getProperties().size());
      assertEquals("val1", p.getPropertyValue("prop1"));

   }

   public void testProjectAsChild() throws Exception
   {

      Node readRoot = testRoot.addNode("testProjectAsChild", "nt:unstructured");
      Node proj1 = readRoot.addNode("project1", PROJECT_NT);
      Node proj2 = readRoot.addNode("project2", PROJECT_NT);
      readRoot.addNode("f1", "nt:folder");
      readRoot.addNode("f2", "nt:folder");
      proj1.setProperty("type", "java");
      proj1.setProperty("prop1", "val1");
      proj2.setProperty("type", "groovy");
      readRoot.getSession().save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("children") //
         .append(readRoot.getPath()).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<Item> list = new ArrayList<Item>();
      for (Item i : children.getItems())
      {
         validateLinks(i);
         if (i.getMimeType().equals(PROJECT_MT))
         {
            assertTrue(i instanceof Project);
            assertNotNull(((Project)i).getProjectType());
            list.add(i);
         }
      }

      assertEquals(2, list.size());
   }

   public void testUpdateProject() throws Exception
   {
      Node readRoot = testRoot.addNode("testUpdateProject", "nt:unstructured");
      Node proj1 = readRoot.addNode("project1", PROJECT_NT);
      proj1.setProperty("type", "java");
      readRoot.getSession().save();

      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item") //
         .append(proj1.getPath()) //
         .toString();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, properties.getBytes(), null);
      assertEquals(204, response.getStatus());
      Node file = (Node)session.getItem(proj1.getPath());
      assertEquals("MyValue", file.getProperty("MyProperty").getString());

   }

}
