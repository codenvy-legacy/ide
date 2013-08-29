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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.lock.Lock;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: UpdateContentTest.java 75032 2011-10-13 15:24:34Z andrew00x $
 */
public class UpdateContentTest extends JcrFileSystemTest
{
   private Node updateContentTestNode;
   private String fileID;
   private String folderID;
   private String content = "__UpdateContentTest__";

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      updateContentTestNode = testRoot.addNode(name, "nt:unstructured");

      Node fileNode = updateContentTestNode.addNode("UpdateContentTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("exo:privilegeable");
      fileNode.addMixin("mix:lockable");
      fileID = ((ExtendedNode)fileNode).getIdentifier();

      Node folderNode = updateContentTestNode.addNode("UpdateContentTest_FOLDER", "nt:folder");
      folderID = ((ExtendedNode)folderNode).getIdentifier();

      session.save();
   }

   public void testUpdateContent() throws Exception
   {
      String path = SERVICE_URI + "content/" + fileID;
      
      Map <String, List <String>> headers = new HashMap <String, List <String>> ();
      List <String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);
      
      
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
      assertEquals(204, response.getStatus());
      Node file = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      assertEquals(content, file.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", file.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("utf8", file.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testUpdateContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + folderID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, content.getBytes(), writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testUpdateContentNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ExtendedNode file = (ExtendedNode)((ExtendedSession)session).getNodeByIdentifier(fileID);
      file.setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + fileID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testUpdateContentLocked() throws Exception
   {
      Node file = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      Lock lock = file.lock(true, false);
      String path = SERVICE_URI + "content/" + fileID + '?' + "lockToken=" + lock.getLockToken();
      
      Map <String, List <String>> headers = new HashMap <String, List <String>> ();
      List <String> contentType = new ArrayList<String>();
      contentType.add("text/plain;charset=utf8");
      headers.put("Content-Type", contentType);
      
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, content.getBytes(), null);
      assertEquals(204, response.getStatus());
      file = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      assertEquals(content, file.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", file.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("utf8", file.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testUpdateContentLocked_NoLockTokens() throws Exception
   {
      Node file = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      file.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + fileID;
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
