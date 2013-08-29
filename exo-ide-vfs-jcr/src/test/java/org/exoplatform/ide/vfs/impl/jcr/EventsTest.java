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
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.services.jcr.core.ExtendedNode;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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
      vfs.updateItem(fileId, Collections.<Property>emptyList(), null);
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
