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

import org.exoplatform.services.rest.impl.ContainerResponse;

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
   private String CREATE_TEST_PATH;

   private Node createTestNode;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      createTestNode = testRoot.addNode(name, "nt:unstructured");
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
         .append(name)
         .append("&")
         .append("type=") //
         .append("java").toString();
      
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, 
         properties.getBytes(), null);

      assertEquals("Error: "+response.getEntity(), 201, response.getStatus());
      String expectedPath = CREATE_TEST_PATH + "/" + name;
      String expectedLocation = SERVICE_URI + "item" + expectedPath;
      String location = response.getHttpHeaders().getFirst("Location").toString();
      assertEquals(expectedLocation, location);

      assertTrue("Project was not created in expected location. ", session.itemExists(expectedPath));
      Node project = (Node)session.getItem(expectedPath);
      assertTrue("vfs:project node type expected", project.getPrimaryNodeType().isNodeType("vfs:project"));
      
      assertEquals("java", project.getProperty("type").getString());
      assertEquals("MyValue", project.getProperty("MyProperty").getString());
   }

}
