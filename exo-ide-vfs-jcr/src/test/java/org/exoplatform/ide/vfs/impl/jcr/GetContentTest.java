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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GetContentTest.java 77587 2011-12-13 10:42:02Z andrew00x $
 */
public class GetContentTest extends JcrFileSystemTest
{
   private Node getContentTestNode;
   private String fileID;
   private String fileName;
   private String folderID;
   private String content = "__GetContentTest__";
   private String filePath;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      getContentTestNode = testRoot.addNode(name, "nt:unstructured");
      getContentTestNode.addMixin("exo:privilegeable");

      Node fileNode = getContentTestNode.addNode("GetContentTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:encoding", "utf8");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(content.getBytes()));
      fileID = ((ExtendedNode)fileNode).getIdentifier();
      fileName = fileNode.getName();
      filePath = fileNode.getPath();

      Node folderNode = getContentTestNode.addNode("GetContentTest_FOLDER", "nt:folder");
      folderID = ((ExtendedNode)folderNode).getIdentifier();

      session.save();
   }

   public void testGetContent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + fileID;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      assertEquals(content, new String(writer.getBody()));
      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")).toString(), writer
         .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
   }

   public void testDownloadFile() throws Exception
   {
      // Expect the same as 'get content' plus header "Content-Disposition". 
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "downloadfile/" + fileID;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      assertEquals(content, new String(writer.getBody()));
      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")).toString(), writer
         .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
      assertEquals("attachment; filename=\"" + fileName + "\"", writer.getHeaders().getFirst("Content-Disposition"));
   }

   public void testGetContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + folderID;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetContentNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)getContentTestNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "content/" + fileID;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetContentByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "contentbypath" + filePath;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      assertEquals(content, new String(writer.getBody()));
      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")).toString(), writer
         .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
   }

   public void testGetContentByPathWithVersionID() throws Exception
   {
      Node fileNode = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      fileNode.addMixin("mix:versionable");
      session.save();
      fileNode.checkin();
      fileNode.checkout();
      Node contentNode = fileNode.getNode("jcr:content");
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__GetContentTest__UPDATED".getBytes()));
      session.save();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "contentbypath" + filePath + "?" + "versionId=" + "1";
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      // Still have original content, version '1'. Latest version has ID '0'. 
      assertEquals(content, new String(writer.getBody()));
      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")).toString(), writer
         .getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
   }
}
