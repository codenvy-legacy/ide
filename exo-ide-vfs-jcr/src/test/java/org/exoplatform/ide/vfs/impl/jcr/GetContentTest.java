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
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GetContentTest extends JcrFileSystemTest
{
   private Node getContentTestNode;
   private String fileID;
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
      filePath = fileNode.getPath();

      Node folderNode = getContentTestNode.addNode("GetContentTest_FOLDER", "nt:folder");
      folderID = ((ExtendedNode)folderNode).getIdentifier();

      session.save();
   }

   public void testGetContent() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("content/") //
         .append(fileID).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      assertEquals(content, new String(writer.getBody()));
      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")),
         response.getContentType());
   }

   public void testGetContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("content/") //
         .append(folderID).toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("content/") //
         .append(fileID).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetContentByPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("contentbypath") //
         .append("?path=") //
         .append(filePath).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      assertEquals(content, new String(writer.getBody()));
      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")),
         response.getContentType());
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("contentbypath") //
         .append("?path=") //
         .append(filePath) //
         .append("&versionId=") //
         .append("1").toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      // Still have original content, version '1'. Latest version has ID '0'. 
      assertEquals(content, new String(writer.getBody()));
      assertEquals(new MediaType("text", "plain", Collections.singletonMap("charset", "utf8")),
         response.getContentType());
   }

}
