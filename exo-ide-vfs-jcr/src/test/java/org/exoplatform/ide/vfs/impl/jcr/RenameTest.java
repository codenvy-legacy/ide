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
import javax.jcr.lock.Lock;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RenameTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class RenameTest extends JcrFileSystemTest
{
   private Node renameTestNode;
   private String fileID;
   private String folderID;
   private Node fileNode;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      renameTestNode = testRoot.addNode(name, "nt:folder");

      fileNode = renameTestNode.addNode("RenameFileTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:encoding", "utf8");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("mix:lockable");
      fileNode.addMixin("exo:privilegeable");
      fileID = ((ExtendedNode)fileNode).getIdentifier();

      Node folderNode = renameTestNode.addNode("RenameFileTest_FOLDER", "nt:folder");
      folderID = ((ExtendedNode)folderNode).getIdentifier();

      session.save();
   }

   public void testRenameFile() throws Exception
   {
      String path = SERVICE_URI + "rename/" + fileID + '?' + "newname=_FILE_NEW_NAME_&mediaType=text/*;charset=ISO-8859-1";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = renameTestNode.getPath() + "/" + "_FILE_NEW_NAME_";
      assertTrue(session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals(DEFAULT_CONTENT, file.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/*", file.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("ISO-8859-1", file.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testRenameFileAlreadyExists() throws Exception
   {
      Node existedFile = renameTestNode.addNode("_FILE_NEW_NAME_", "nt:file");
      Node existedFileContent = existedFile.addNode("jcr:content", "nt:resource");
      existedFileContent.setProperty("jcr:mimeType", "text/plain");
      existedFileContent.setProperty("jcr:encoding", "utf8");
      existedFileContent.setProperty("jcr:lastModified", Calendar.getInstance());
      existedFileContent.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      session.save();
      
      String path = SERVICE_URI + "rename/" + fileID + '?' + "newname=_FILE_NEW_NAME_&mediaType=text/*;charset=ISO-8859-1";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(400, response.getStatus());
      assertEquals(ExitCodes.ITEM_EXISTS, Integer.parseInt((String)response.getHttpHeaders().getFirst("X-Exit-Code")));
   }

   public void testRenameFileLocked() throws Exception
   {
      Lock lock = fileNode.lock(true, false);
      String path = SERVICE_URI + "rename/" + fileID + '?' +
         "newname=_FILE_NEW_NAME_&mediaType=text/*;charset=ISO-8859-1&lockToken=" + lock.getLockToken();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = renameTestNode.getPath() + "/" + "_FILE_NEW_NAME_";
      assertTrue(session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals(DEFAULT_CONTENT, file.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/*", file.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("ISO-8859-1", file.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testRenameFileLocked_NoLockToke() throws Exception
   {
      fileNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "rename/" + fileID + '?' + "newname=_FILE_NEW_NAME_&mediaType=text/*;charset=ISO-8859-1";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testRenameFileNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)fileNode).setPermissions(permissions);
      session.save();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "rename/" + fileID + '?' + "newname=_FILE_NEW_NAME_";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testRenameFolder() throws Exception
   {
      String path = SERVICE_URI + "rename/" + folderID + '?' + "newname=_FOLDER_NEW_NAME_";
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = renameTestNode.getPath() + "/" + "_FOLDER_NEW_NAME_";
      assertTrue(session.itemExists(expectedPath));
   }
}
