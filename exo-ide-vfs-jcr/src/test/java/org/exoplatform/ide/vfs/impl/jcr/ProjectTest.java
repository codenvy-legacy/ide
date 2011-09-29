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

import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.jcr.core.ExtendedNode;
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
   private String createTestPath;
   private String createTestID;
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
      createTestPath = createTestNode.getPath();
      createTestID = ((ExtendedNode)createTestNode).getIdentifier();
   }

   public void testCreateProject() throws Exception
   {
      String name = "testCreateProject";
      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("project/") //
         .append(createTestID) //
         .append("?") //
         .append("name=") //
         .append(name).append("&").append("type=") //
         .append("java").toString();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, properties.getBytes(), null);

      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      String expectedPath = createTestPath + "/" + name;

      assertTrue("Project was not created in expected location. ", session.itemExists(expectedPath));
      Node project = (Node)session.getItem(expectedPath);

      assertEquals("java", project.getProperty("vfs:projectType").getString());
      assertEquals("MyValue", project.getProperty("MyProperty").getString());
   }

   public void testCreateProjectInsideProject() throws Exception
   {
      String name = "testCreateProjectInsideProject";
      Node parentProject = testRoot.addNode(name, "nt:folder");
      parentProject.addMixin("vfs:mixunstructured");
      parentProject.setProperty("vfs:projectType", "java");
      parentProject.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      parentProject.getSession().save();

      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("project/") //
         .append(((ExtendedNode)parentProject).getIdentifier()) //
         .append("?") //
         .append("name=").append("childProject") //
         .append("&") //
         .append("type=").append("java").toString();

      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));

      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, null, null);
      log.info(response.getEntity());
      assertEquals("Unexpected status " + response.getStatus(), 400, response.getStatus());
   }

   public void testCopyProjectToProject() throws Exception
   {
      String destName = "testCopyProjectToProject_DESTINATION";
      Node destProject = testRoot.addNode(destName, "nt:folder");
      destProject.addMixin("vfs:mixunstructured");
      destProject.setProperty("vfs:projectType", "java");
      destProject.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      String projectName = "testCopyProjectToProject";
      Node project = testRoot.addNode(projectName, "nt:folder");
      project.addMixin("vfs:mixunstructured");
      project.setProperty("vfs:projectType", "java");
      project.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      project.getSession().save();

      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("copy/") //
         .append(((ExtendedNode)project).getIdentifier()) //
         .append("?") //
         .append("parentId=").append(((ExtendedNode)destProject).getIdentifier()).toString();

      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      log.info(response.getEntity());
      assertEquals("Unexpected status " + response.getStatus(), 400, response.getStatus());
   }

   public void testMoveProjectToProject() throws Exception
   {
      String destName = "testCopyProjectToProject_DESTINATION";
      Node destProject = testRoot.addNode(destName, "nt:folder");
      destProject.addMixin("vfs:mixunstructured");
      destProject.setProperty("vfs:projectType", "java");
      destProject.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      String projectName = "testCopyProjectToProject";
      Node project = testRoot.addNode(projectName, "nt:folder");
      project.addMixin("vfs:mixunstructured");
      project.setProperty("vfs:projectType", "java");
      project.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      project.getSession().save();

      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("move/") //
         .append(((ExtendedNode)project).getIdentifier()) //
         .append("?") //
         .append("parentId=").append(((ExtendedNode)destProject).getIdentifier()).toString();

      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      log.info(response.getEntity());
      assertEquals("Unexpected status " + response.getStatus(), 400, response.getStatus());
   }

   public void testGetProjectItem() throws Exception
   {
      Node getTestRoot = testRoot.addNode("testGetProjectItem", "nt:unstructured");
      Node proj1 = getTestRoot.addNode("project1", "nt:folder");
      proj1.addMixin("vfs:mixunstructured");
      proj1.setProperty("vfs:projectType", "java");
      proj1.setProperty("prop1", "val1");
      proj1.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      getTestRoot.getSession().save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(((ExtendedNode)proj1).getIdentifier()).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);

      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertEquals("application/json", response.getContentType().toString());

      Project p = (Project)response.getEntity();
      validateLinks(p);
      assertEquals("project1", p.getName());
      assertEquals(Project.PROJECT_MIME_TYPE, p.getMimeType());
      assertEquals("java", p.getProjectType());

      assertEquals(3, p.getProperties().size());
      assertEquals("val1", p.getPropertyValue("prop1"));
      assertEquals(Project.PROJECT_MIME_TYPE, p.getPropertyValue("vfs:mimeType"));
   }

   public void testProjectAsChild() throws Exception
   {
      Node readRoot = testRoot.addNode("testProjectAsChild", "nt:unstructured");
      Node proj1 = readRoot.addNode("project1", "nt:folder");
      proj1.addMixin("vfs:mixunstructured");
      Node proj2 = readRoot.addNode("project2", "nt:folder");
      proj2.addMixin("vfs:mixunstructured");
      readRoot.addNode("f1", "nt:folder");
      readRoot.addNode("f2", "nt:folder");
      proj1.setProperty("vfs:projectType", "java");
      proj1.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      proj1.setProperty("prop1", "val1");
      proj2.setProperty("vfs:projectType", "groovy");
      proj2.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      readRoot.getSession().save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("children/") //
         .append(((ExtendedNode)readRoot).getIdentifier()).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<Item> list = new ArrayList<Item>();
      for (Item i : children.getItems())
      {
         validateLinks(i);
         if (i.getMimeType().equals(Project.PROJECT_MIME_TYPE))
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
      Node proj1 = readRoot.addNode("project1", "nt:folder");
      proj1.addMixin("vfs:mixunstructured");
      proj1.setProperty("vfs:projectType", "java");
      proj1.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      readRoot.getSession().save();

      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(((ExtendedNode)proj1).getIdentifier()) //
         .toString();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, properties.getBytes(), null);
      assertEquals(204, response.getStatus());
      Node file = (Node)session.getItem(proj1.getPath());
      assertEquals("MyValue", file.getProperty("MyProperty").getString());
   }

   public void testConvertFolderToProject() throws Exception
   {
      Node convertRoot = testRoot.addNode("testConvertFolderToProject", "nt:unstructured");
      Node folder = convertRoot.addNode("project1", "nt:folder");
      convertRoot.getSession().save();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("rename/") //
         .append(((ExtendedNode)folder).getIdentifier()) //
         .append("?") //
         .append("mediaType=") //
         .append("text/vnd.ideproject%2Bdirectory") // text/vnd.ideproject+directory
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("text/vnd.ideproject+directory", folder.getProperty("vfs:mimeType").getString());

      path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(((ExtendedNode)folder).getIdentifier()) //
         .toString();
      response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      Folder project = (Folder)response.getEntity();
      assertEquals("text/vnd.ideproject+directory", project.getMimeType());
      assertTrue("Folder must be converted to Project. ", project instanceof Project);
   }

   public void testConvertProjectToFolder() throws Exception
   {
      Node convertRoot = testRoot.addNode("testConvertProjectToFolder", "nt:unstructured");
      Node project = convertRoot.addNode("project1", "nt:folder");
      project.addMixin("vfs:mixunstructured");
      project.setProperty("vfs:mimeType", "text/vnd.ideproject+directory");
      project.setProperty("vfs:projectType", "default");
      convertRoot.getSession().save();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("rename/") //
         .append(((ExtendedNode)project).getIdentifier()) //
         .append("?") //
         .append("mediaType=") //
         .append("text/directory") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      assertEquals("text/directory", project.getProperty("vfs:mimeType").getString());

      path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(((ExtendedNode)project).getIdentifier()) //
         .toString();
      response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      Folder folder = (Folder)response.getEntity();
      assertEquals("text/directory", folder.getMimeType());
      assertFalse("Project must be converted to Folder. ", folder instanceof Project);
   }
}
