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
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.ProjectUpdateListener;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CreateTest.java 65533 2011-01-26 12:31:23Z andrew00x $
 */
public class ProjectTest extends JcrFileSystemTest
{
   private String projectTestPath;
   private String projectTestId;

   /** @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp() */
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      Node createTestNode = testRoot.addNode(name, "nt:unstructured");
      session.save();
      projectTestPath = createTestNode.getPath();
      projectTestId = ((ExtendedNode)createTestNode).getIdentifier();
   }

   public void testCreateProject() throws Exception
   {
      String name = "testCreateProject";
      String properties = "[{\"name\":\"vfs:projectType\", \"value\":[\"java\"]}]";
      // 
      String path = SERVICE_URI + "project/" + projectTestId + '?' + "name=" + name + "&" + "type=" + "java";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, properties.getBytes(), null);
      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      String expectedPath = projectTestPath + '/' + name;
      assertTrue("Project was not created in expected location. ", session.itemExists(expectedPath));
      Node project = (Node)session.getItem(expectedPath);
      assertEquals("java", project.getProperty(".project/vfs:projectType").getString());
      assertEquals("text/vnd.ideproject+directory", project.getProperty(".project/vfs:mimeType").getString());
   }

   public void testCreateProjectInsideProject() throws Exception
   {
      String name = "testCreateProjectInsideProject";
      Node parentProject = testRoot.addNode(name, "nt:folder");
      parentProject.addMixin("vfs:project");
      Node projectData = parentProject.getNode(".project");
      projectData.setProperty("vfs:projectType", "java");
      projectData.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      session.save();

      String path = SERVICE_URI + "project/" + ((ExtendedNode)parentProject).getIdentifier() + '?' + "name=childProject&type=java";

      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));

      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, null, null);
      log.info(response.getEntity());
      assertEquals("Unexpected status " + response.getStatus(), 200, response.getStatus());
      String expectedPath = parentProject.getPath() + "/childProject";
      Node project = (Node)session.getItem(expectedPath);
      assertEquals("java", project.getProperty(".project/vfs:projectType").getString());
      assertEquals("text/vnd.ideproject+directory", project.getProperty(".project/vfs:mimeType").getString());
   }

   public void testCopyProjectToProject() throws Exception
   {
      Node destProject = testRoot.addNode("testCopyProjectToProject_DESTINATION", "nt:folder");
      destProject.addMixin("vfs:project");
      Node destProjectData = destProject.getNode(".project");
      destProjectData.setProperty("vfs:projectType", "java");
      destProjectData.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);

      String projectName = "testCopyProjectToProject";
      Node project = testRoot.addNode(projectName, "nt:folder");
      project.addMixin("vfs:project");
      Node projectData = project.getNode(".project");
      projectData.setProperty("vfs:projectType", "java");
      projectData.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);

      session.save();

      String path = SERVICE_URI + "copy/" + ((ExtendedNode)project).getIdentifier() + '?' +
         "parentId=" + ((ExtendedNode)destProject).getIdentifier();

      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      log.info(response.getEntity());
      assertEquals("Unexpected status " + response.getStatus(), 200, response.getStatus());
      String expectedPath = destProject.getPath() + '/' + projectName;
      project = (Node)session.getItem(expectedPath);
      assertEquals("java", project.getProperty(".project/vfs:projectType").getString());
      assertEquals("text/vnd.ideproject+directory", project.getProperty(".project/vfs:mimeType").getString());
   }

   public void testMoveProjectToProject() throws Exception
   {
      Node destProject = testRoot.addNode("testMoveProjectToProject_DESTINATION", "nt:folder");
      destProject.addMixin("vfs:project");
      Node destProjectData = destProject.getNode(".project");
      destProjectData.setProperty("vfs:projectType", "java");
      destProjectData.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);

      String projectName = "testMoveProjectToProject";
      Node project = testRoot.addNode(projectName, "nt:folder");
      project.addMixin("vfs:project");
      Node projectData = project.getNode(".project");
      projectData.setProperty("vfs:projectType", "java");
      projectData.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);

      session.save();

      String path = SERVICE_URI + "move/" + ((ExtendedNode)project).getIdentifier() + '?' + "parentId=" +
         ((ExtendedNode)destProject).getIdentifier();

      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      log.info(response.getEntity());
      assertEquals("Unexpected status " + response.getStatus(), 200, response.getStatus());
      String expectedPath = destProject.getPath() + '/' + projectName;
      project = (Node)session.getItem(expectedPath);
      assertEquals("java", project.getProperty(".project/vfs:projectType").getString());
      assertEquals("text/vnd.ideproject+directory", project.getProperty(".project/vfs:mimeType").getString());
   }

   public void testGetProjectItem() throws Exception
   {
      Node projectNode = testRoot.addNode("testGetProjectItem_PROJECT1", "nt:folder");
      projectNode.addMixin("vfs:project");
      Node projectData = projectNode.getNode(".project");
      projectData.setProperty("vfs:projectType", "java");
      projectData.setProperty("prop1", "val1");
      projectData.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);

      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "item/" + ((ExtendedNode)projectNode).getIdentifier();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);

      assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
      assertEquals("application/json", response.getContentType().toString());

      Project project = (Project)response.getEntity();
      validateLinks(project);
      assertEquals("testGetProjectItem_PROJECT1", project.getName());
      assertEquals(Project.PROJECT_MIME_TYPE, project.getMimeType());
      assertEquals("java", project.getProjectType());

      assertEquals(3, project.getProperties().size());
      assertEquals("val1", project.getPropertyValue("prop1"));
      assertEquals(Project.PROJECT_MIME_TYPE, project.getPropertyValue("vfs:mimeType"));
   }

   public void testProjectAsChild() throws Exception
   {
      Node theTestRoot = testRoot.addNode("testProjectAsChild", "nt:folder");
      Node project1 = theTestRoot.addNode("testProjectAsChild_PROJECT1", "nt:folder");
      project1.addMixin("vfs:project");
      Node projectData1 = project1.getNode(".project");
      projectData1.setProperty("vfs:projectType", "java");
      projectData1.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
      projectData1.setProperty("prop1", "val1");

      Node project2 = theTestRoot.addNode("testProjectAsChild_PROJECT2", "nt:folder");
      project2.addMixin("vfs:project");
      Node projectData2 = project1.getNode(".project");
      projectData2.setProperty("vfs:projectType", "groovy");
      projectData2.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);

      theTestRoot.addNode("testProjectAsChild_FOLDER1", "nt:folder");
      theTestRoot.addNode("testProjectAsChild_FOLDER2", "nt:folder");

      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "children/" + ((ExtendedNode)theTestRoot).getIdentifier();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      ItemList<Item> children = (ItemList<Item>)response.getEntity();
      List<Item> list = new ArrayList<Item>();
      for (Item i : children.getItems())
      {
         validateLinks(i);
         if (Project.PROJECT_MIME_TYPE.equals(i.getMimeType()))
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
      Node project = testRoot.addNode("testUpdateProject", "nt:folder");
      project.addMixin("vfs:project");
      Node projectData = project.getNode(".project");
      projectData.setProperty("vfs:projectType", "java");
      projectData.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);

      session.save();

      String projectPath = project.getPath();
      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      String path = SERVICE_URI + "item/" + ((ExtendedNode)project).getIdentifier();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, properties.getBytes(), null);
      assertEquals(200, response.getStatus());
      project = (Node)session.getItem(projectPath);
      assertEquals("MyValue", project.getProperty(".project/MyProperty").getString());
   }

   public void testConvertFolderToProject() throws Exception
   {
      Node folder = testRoot.addNode("testConvertFolderToProject", "nt:folder");
      session.save();
      String folderPath = folder.getPath();
      String path = SERVICE_URI + "rename/" + ((ExtendedNode)folder).getIdentifier() + '?' + "mediaType=text/vnd.ideproject%2Bdirectory";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      folder = (Node)session.getItem(folderPath);
      assertEquals("text/vnd.ideproject+directory", folder.getProperty(".project/vfs:mimeType").getString());

      path = SERVICE_URI + "item/" + ((ExtendedNode)folder).getIdentifier();
      response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      Folder project = (Folder)response.getEntity();
      assertEquals("text/vnd.ideproject+directory", project.getMimeType());
      assertTrue("Folder must be converted to Project. ", project instanceof Project);
   }

   public void testConvertProjectToFolder() throws Exception
   {
      Node project = testRoot.addNode("testConvertProjectToFolder", "nt:folder");
      project.addMixin("vfs:project");
      Node projectData = project.getNode(".project");
      projectData.setProperty("vfs:mimeType", "text/vnd.ideproject+directory");
      projectData.setProperty("vfs:projectType", "default");

      session.save();

      String path = SERVICE_URI + "rename/" + ((ExtendedNode)project).getIdentifier() + '?' + "mediaType=" + "text/directory";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());

      path = SERVICE_URI + "item/" + ((ExtendedNode)project).getIdentifier();
      response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      Folder folder = (Folder)response.getEntity();
      assertEquals("text/directory", folder.getMimeType());
      assertFalse("Project must be converted to Folder. ", folder instanceof Project);
   }

//   public void testProjectUpdateEventsRepositoryIsolation() throws Exception
//   {
//      /* IDE-1768 */
//
//      String name = "testProjectUpdateEvents";
//      // JCR backend repository: db1, workspace: ws
//      Node projectNode = testRoot.addNode(name, "nt:folder");
//      projectNode.addMixin("vfs:project");
//      Node projectCfgNode = projectNode.getNode(".project");
//      projectCfgNode.setProperty("vfs:projectType", "java");
//      projectCfgNode.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
//      session.save();
//
//      // JCR backend repository: db2, workspace: ws
//      RepositoryService repositoryService =
//         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
//      ManageableRepository repository1 = repositoryService.getRepository("db2");
//      Session session1 = repository1.login(new CredentialsImpl("root", "exo".toCharArray()), "ws");
//      // create the same structure.
//      Node testRoot1 = session1.getRootNode().addNode(TEST_ROOT_NAME, "nt:unstructured");
//      Node projectNode1 = testRoot1.addNode(name, "nt:folder");
//      projectNode1.addMixin("vfs:project");
//      Node projectCfgNode1 = projectNode1.getNode(".project");
//      projectCfgNode1.setProperty("vfs:projectType", "java");
//      projectCfgNode1.setProperty("vfs:mimeType", Project.PROJECT_MIME_TYPE);
//      session1.save();
//
//      // Now have the same structure in two different repositories.
//      // This is the same what we have in exo-cloud.
//
//      EventListenerList listeners = (EventListenerList)container.getComponentInstanceOfType(EventListenerList.class);
//
//      ProjectData project = (ProjectData)ItemData.fromNode(projectNode, "/");
//      ProjectData project1 = (ProjectData)ItemData.fromNode(projectNode1, "/");
//      final boolean[] notified = {false};
//      final boolean[] notified1 = {false};
//
//      ChangeEventFilter filter = ProjectUpdateEventFilter.newFilter(new JcrFileSystem(
//         session.getRepository(), "ws", "/", "ws", new MediaType2NodeTypeResolver()), project);
//      ChangeEventFilter filter1 = ProjectUpdateEventFilter.newFilter(new JcrFileSystem(
//         repository1, "ws", "/", "ws", new MediaType2NodeTypeResolver()), project);
//
//      ProjectUpdateListener listener = new ProjectUpdateListener(project.getId())
//      {
//         @Override
//         public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
//         {
//            notified[0] = true;
//            super.handleEvent(event);
//         }
//      };
//
//      ProjectUpdateListener listener1 = new ProjectUpdateListener(project1.getId())
//      {
//         @Override
//         public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
//         {
//            notified1[0] = true;
//            super.handleEvent(event);
//         }
//      };
//
//      // Register listeners for both projects.
//      assertTrue(listeners.addEventListener(filter, listener));
//      // This one must not be notified.
//      assertTrue(listeners.addEventListener(filter1, listener1));
//
//      String path = SERVICE_URI + "file/" + project.getId() + '?' + "name=file";
//      Map<String, List<String>> headers = new HashMap<String, List<String>>();
//      List<String> contentType = new ArrayList<String>();
//      contentType.add("text/plain;charset=utf8");
//      headers.put("Content-Type", contentType);
//
//      // Create file. As result only repository 'db1' get notification.
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, new byte[0], null);
//      assertEquals(200, response.getStatus());
//
//      assertTrue("Listener must be notified. ", notified[0]);
//      assertFalse("Listener must not be notified. ", notified1[0]);
//
//      // test removing
//      assertTrue(listeners.removeEventListener(filter, listener));
//      assertTrue(listeners.removeEventListener(filter1, listener1));
//   }
}
