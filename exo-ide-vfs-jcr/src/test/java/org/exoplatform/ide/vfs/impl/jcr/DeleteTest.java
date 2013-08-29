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
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.lock.Lock;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: DeleteTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class DeleteTest extends JcrFileSystemTest
{
   private Node deleteTestNode;
   private String folderID;
   private String fileID;
   private Node folderNode;
   private Node fileNode;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      deleteTestNode = testRoot.addNode(name, "nt:unstructured");
      deleteTestNode.addMixin("exo:privilegeable");

      folderNode = deleteTestNode.addNode("DeleteTest_FOLDER", "nt:folder");
      // add child in folder
      Node childFileNode = folderNode.addNode("file", "nt:file");
      Node childContentNode = childFileNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      folderNode.addMixin("exo:privilegeable");
      folderID = ((ExtendedNode)folderNode).getIdentifier();

      fileNode = deleteTestNode.addNode("DeleteTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("mix:lockable");
      fileNode.addMixin("exo:privilegeable");
      fileID = ((ExtendedNode)fileNode).getIdentifier();

      session.save();
   }

   public void testDeleteFile() throws Exception
   {
      String path = SERVICE_URI + "delete/" + fileID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      try
      {
         ((ExtendedSession)session).getNodeByIdentifier(fileID);
         fail("File must be removed. ");
      }
      catch (ItemNotFoundException e)
      {
      }
   }

   public void testDeleteFileLocked() throws Exception
   {
      Lock lock = fileNode.lock(true, false);
      String path = SERVICE_URI + "delete/" + fileID + '?' + "lockToken=" + lock.getLockToken();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      try
      {
         ((ExtendedSession)session).getNodeByIdentifier(fileID);
         fail("File must be removed. ");
      }
      catch (ItemNotFoundException e)
      {
      }
   }

   public void testDeleteFileLocked_NoLockToken() throws Exception
   {
      fileNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "delete/" + fileID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
      try
      {
         ((ExtendedSession)session).getNodeByIdentifier(fileID);
      }
      catch (ItemNotFoundException e)
      {
         fail("File must not be removed since locked parent. ");
      }
   }

   public void testDeleteFileNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)fileNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "delete/" + fileID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
      try
      {
         ((ExtendedSession)session).getNodeByIdentifier(fileID);
      }
      catch (ItemNotFoundException e)
      {
         fail("File must not be removed since permissions restriction. ");
      }
   }

   public void testDeleteFileWrongID() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "delete/" + fileID + "_WRONG_ID";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testDeleteFolder() throws Exception
   {
      String path = SERVICE_URI + "delete/" + folderID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      try
      {
         ((ExtendedSession)session).getNodeByIdentifier(folderID);
         fail("Folder must be removed. ");
      }
      catch (ItemNotFoundException e)
      {
      }
   }
}
