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
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CopyTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class CopyTest extends JcrFileSystemTest
{
   private Node copyTestNode;

   private Node copyTestDestinationNode;

   private String folderId;

   private String fileId;

   private Node fileNode;

   private Node folderNode;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      copyTestNode = testRoot.addNode(name, "nt:unstructured");

      folderNode = copyTestNode.addNode("CopyTest_FOLDER", "nt:folder");
      // add child in folder
      Node childFileNode = folderNode.addNode("file", "nt:file");
      Node childContentNode = childFileNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      folderId = ((ExtendedNode)folderNode).getIdentifier();

      copyTestDestinationNode = testRoot.addNode("CopyTest_DESTINATION_FOLDER", "nt:folder");
      copyTestDestinationNode.addMixin("mix:lockable");
      copyTestDestinationNode.addMixin("exo:privilegeable");

      fileNode = copyTestNode.addNode("CopyTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileId = ((ExtendedNode)fileNode).getIdentifier();

      session.save();
   }

   public void testCopyFile() throws Exception
   {
      String path = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + ((ExtendedNode)copyTestDestinationNode).getIdentifier();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = copyTestDestinationNode.getPath() + '/' + fileNode.getName();
      assertTrue("Source file not found. ", session.itemExists(fileNode.getPath()));
      assertTrue("Not found file in destination location. ", session.itemExists(expectedPath));
   }

   public void testCopyFileAlreadyExist() throws Exception
   {
      session.getWorkspace().copy(fileNode.getPath(), copyTestDestinationNode.getPath() + '/' + fileNode.getName());
      String path = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + ((ExtendedNode)copyTestDestinationNode).getIdentifier();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(400, response.getStatus());
      assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
   }

   public void testCopyFileDestination_NoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)copyTestDestinationNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "copy/" + fileId + '?' + "parentId=" + ((ExtendedNode)copyTestDestinationNode).getIdentifier();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      assertTrue("Source file not found. ", session.itemExists(fileNode.getPath()));
      assertFalse("File must not be copied since destination accessible for reading only. ",
         session.itemExists(copyTestDestinationNode.getPath() + "/CopyTest_FILE"));
   }

   public void testCopyFolder() throws Exception
   {
      String path = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + ((ExtendedNode)copyTestDestinationNode).getIdentifier();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = copyTestDestinationNode.getPath() + '/' + folderNode.getName();
      assertTrue("Source folder not found. ", session.itemExists(folderNode.getPath()));
      assertTrue("Not found folder in destination location. ", session.itemExists(expectedPath));
      assertTrue("Child of folder missing after coping. ", session.itemExists(expectedPath + "/file"));
   }

   public void testCopyFolderAlreadyExist() throws Exception
   {
      session.getWorkspace().copy(folderNode.getPath(), copyTestDestinationNode.getPath() + '/' + folderNode.getName());
      String path = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + ((ExtendedNode)copyTestDestinationNode).getIdentifier();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(400, response.getStatus());
      assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
   }
}
