/*
 * Copyright (C) 2012 eXo Platform SAS.
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
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.MimeTypeFilter;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EventsTest extends JcrFileSystemTest
{
   private Node testNode;
   private String testNodeID;
   private String testNodePath;

   private Node destinationNode;
   private String destinationNodeID;
   private String destinationNodePath;

   private EventListenerList listeners;
   private ChangeEventFilter filter;
   private Listener listener;

   /** @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp() */
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      testNode = testRoot.addNode(name, "nt:unstructured");
      destinationNode = testRoot.addNode("EventsTest_DESTINATION_FOLDER", "nt:folder");

      session.save();

      testNodeID = ((ExtendedNode)testNode).getIdentifier();
      testNodePath = testNode.getPath();
      destinationNodeID = ((ExtendedNode)destinationNode).getIdentifier();
      destinationNodePath = destinationNode.getPath();

      listeners = (EventListenerList)container.getComponentInstanceOfType(EventListenerList.class);
      assertNotNull(listeners);
      listener = new Listener();
      filter = ChangeEventFilter.ANY_FILTER;
      listeners.addEventListener(filter, listener);
   }

   @Override
   protected void tearDown() throws Exception
   {
      assertTrue("Unable remove listener. ", listeners.removeEventListener(filter, listener));
      super.tearDown();
   }

   private class Listener implements EventListener
   {
      List<ChangeEvent> events = new ArrayList<ChangeEvent>();

      @Override
      public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
      {
         log.info(event);
         events.add(event);
      }
   }

   public void testCreateFile() throws Exception
   {
      String name = "testCreateFile";
      String content = "test create file";
      String path = SERVICE_URI + "file/" + testNodeID + '?' + "name=" + name;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
      assertEquals(200, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.CREATED, listener.events.get(0).getType());
      assertEquals(testNodePath + '/' + name, listener.events.get(0).getItemPath());
      assertEquals(contentType.get(0), listener.events.get(0).getMimeType());
   }

   public void testCreateFolder() throws Exception
   {
      String name = "testCreateFolder";
      String path = SERVICE_URI + "folder/" + testNodeID + '?' + "name=" + name;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.CREATED, listener.events.get(0).getType());
      assertEquals(testNodePath + '/' + name, listener.events.get(0).getItemPath());
   }

   public void testCopy() throws Exception
   {
      String fileId = createFile(testNode, "CopyTest_FILE", "text/plain", DEFAULT_CONTENT);

      String path = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + destinationNodeID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);

      assertEquals(200, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.CREATED, listener.events.get(0).getType());
      assertEquals(destinationNodePath + '/' + "CopyTest_FILE", listener.events.get(0).getItemPath());
      assertEquals("text/plain", listener.events.get(0).getMimeType());
   }

   public void testMove() throws Exception
   {
      String fileId = createFile(testNode, "MoveTest_FILE", "text/plain", DEFAULT_CONTENT);

      String path = SERVICE_URI + "move/" + fileId + '?' + "parentId=" + destinationNodeID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.MOVED, listener.events.get(0).getType());
      assertEquals(destinationNodePath + '/' + "MoveTest_FILE", listener.events.get(0).getItemPath());
      assertEquals("text/plain", listener.events.get(0).getMimeType());
   }

   public void testUpdateContent() throws Exception
   {
      String fileId = createFile(testNode, "UpdateContent_FILE", "text/plain", DEFAULT_CONTENT);

      String path = SERVICE_URI + "content/" + fileId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("application/xml");
      headers.put("Content-Type", contentType);
      String content = "<?xml version='1.0'><root/>";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
      assertEquals(204, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.CONTENT_UPDATED, listener.events.get(0).getType());
      assertEquals(testNodePath + '/' + "UpdateContent_FILE", listener.events.get(0).getItemPath());
      assertEquals("application/xml", listener.events.get(0).getMimeType());
   }

   public void testUpdateProperties() throws Exception
   {
      String fileId = createFile(testNode, "UpdateProperties_FILE", "text/plain", DEFAULT_CONTENT);

      String path = SERVICE_URI + "item/" + fileId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("application/json");
      headers.put("Content-Type", contentType);
      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, properties.getBytes(), null);
      assertEquals(200, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.PROPERTIES_UPDATED, listener.events.get(0).getType());
      assertEquals(testNodePath + '/' + "UpdateProperties_FILE", listener.events.get(0).getItemPath());
      assertEquals("text/plain", listener.events.get(0).getMimeType());
   }

   public void testDelete() throws Exception
   {
      String fileId = createFile(testNode, "Delete_FILE", "text/plain", DEFAULT_CONTENT);

      String path = SERVICE_URI + "delete/" + fileId;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.DELETED, listener.events.get(0).getType());
      assertEquals(testNodePath + '/' + "Delete_FILE", listener.events.get(0).getItemPath());
      assertEquals("text/plain", listener.events.get(0).getMimeType());
   }

   public void testRename() throws Exception
   {
      String fileId = createFile(testNode, "RenameTest_FILE", "text/plain", DEFAULT_CONTENT);

      String path = SERVICE_URI + "rename/" + fileId + '?' + "newname=" + "_FILE_NEW_NAME_";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);

      assertEquals(200, response.getStatus());

      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.RENAMED, listener.events.get(0).getType());
      assertEquals(testNodePath + '/' + "_FILE_NEW_NAME_", listener.events.get(0).getItemPath());
      assertEquals("text/plain", listener.events.get(0).getMimeType());
      VirtualFileSystem vfs = listener.events.get(0).getVirtualFileSystem();
      vfs.updateItem(fileId, Collections.<ConvertibleProperty>emptyList(), null);
   }

   public void testImport() throws Exception
   {
      URL testZipResource = Thread.currentThread().getContextClassLoader().getResource("spring-project.zip");
      java.io.File f = new java.io.File(testZipResource.toURI());
      FileInputStream in = new FileInputStream(f);
      byte[] zip = new byte[(int)f.length()];
      in.read(zip);
      in.close();
      String path = SERVICE_URI + "import/" + testNodeID;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      headers.put("Content-Type", Arrays.asList("application/zip"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zip, null, null);
      assertEquals(204, response.getStatus());
      VirtualFileSystemRegistry virtualFileSystemRegistry =
         (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
      VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider("ws").newInstance(null, null);

      Map<String, ChangeEvent> expectedEvents = new HashMap<String, ChangeEvent>();
      for (String ff : ZipUtils.getFileList(new ByteArrayInputStream(zip)))
      {
         // .project is not copied directly
         if (!".project".equals(ff))
         {
            String key = testNodePath + '/' + ff + ChangeEvent.ChangeType.CREATED;
            expectedEvents.put(key, new ChangeEvent(vfs, "", testNodePath + '/' + ff, null, ChangeEvent.ChangeType.CREATED));
         }
      }
      // additional actions
      expectedEvents.put(testNodePath + ChangeEvent.ChangeType.RENAMED,
         new ChangeEvent(vfs, "", testNodePath, null, ChangeEvent.ChangeType.RENAMED));
      expectedEvents.put(testNodePath + ChangeEvent.ChangeType.PROPERTIES_UPDATED,
         new ChangeEvent(vfs, "", testNodePath, null, ChangeEvent.ChangeType.PROPERTIES_UPDATED));

      assertEquals(expectedEvents.size(), listener.events.size());

      for (ChangeEvent event : listener.events)
      {
         expectedEvents.remove(event.getItemPath() + event.getType());
      }
      if (!expectedEvents.isEmpty())
      {
         StringBuilder msg = new StringBuilder("Missed events:\n");
         for (ChangeEvent event : expectedEvents.values())
         {
            msg.append(event.getItemPath());
            msg.append(" -> ");
            msg.append(event.getType());
            msg.append('\n');
         }
         fail(msg.toString());
      }
   }

   public void testImportWithEventsFilter() throws Exception
   {
      // remove listener first
      listeners.removeEventListener(filter, listener);
      // re-add it with filter
      filter = ChangeEventFilter.createAndFilter(
         new VfsIDFilter("ws"),
         new TypeFilter(ChangeEvent.ChangeType.CREATED),
         new PathFilter("^(.*/)?pom\\.xml"));
      listeners.addEventListener(filter, listener);
      // do import
      URL testZipResource = Thread.currentThread().getContextClassLoader().getResource("spring-project.zip");
      java.io.File f = new java.io.File(testZipResource.toURI());
      FileInputStream in = new FileInputStream(f);
      byte[] zip = new byte[(int)f.length()];
      in.read(zip);
      in.close();
      String path = SERVICE_URI + "import/" + testNodeID;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      headers.put("Content-Type", Arrays.asList("application/zip"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zip, null, null);
      assertEquals(204, response.getStatus());
      // just one event expected
      assertEquals(1, listener.events.size());
      assertEquals(ChangeEvent.ChangeType.CREATED, listener.events.get(0).getType());
      assertEquals(testNodePath + '/' + "pom.xml", listener.events.get(0).getItemPath());
   }

   private String createFile(Node parent, String name, String mediaType, String data) throws Exception
   {
      Node fileNode = parent.addNode(name, "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", mediaType);
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(data.getBytes()));
      fileNode.addMixin("exo:unstructuredMixin");
      session.save();
      return ((ExtendedNode)fileNode).getIdentifier();
   }
}
