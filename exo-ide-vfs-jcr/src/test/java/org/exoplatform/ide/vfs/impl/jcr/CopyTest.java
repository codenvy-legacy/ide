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

import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class CopyTest extends JcrFileSystemTest
{
   private Node copyTestNode;

   private Node copyTestDestinationNode;

   private String folderPath;

   private String filePath;

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
      folderPath = folderNode.getPath();

      copyTestDestinationNode = testRoot.addNode("CopyTest_DESTINATION_FOLDER", "nt:folder");
      copyTestDestinationNode.addMixin("mix:lockable");
      copyTestDestinationNode.addMixin("exo:privilegeable");

      fileNode = copyTestNode.addNode("CopyTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      filePath = fileNode.getPath();

      session.save();
   }

   public void testCopyFile() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("copy") //
         .append(filePath) //
         .append("?") //
         .append("parentId=") //
         .append(copyTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(201, response.getStatus());
      String expectedPath = copyTestDestinationNode.getPath() + "/" + fileNode.getName();
      String expectedLocation = SERVICE_URI + "item" + expectedPath;
      String location = response.getHttpHeaders().getFirst("Location").toString();
      assertEquals(expectedLocation, location);
      assertTrue("Source file not found. ", session.itemExists(filePath));
      assertTrue("Not found file in destination location. ", session.itemExists(expectedPath));
   }

   public void testCopyFileDestination_NoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)copyTestDestinationNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("copy") //
         .append(filePath) //
         .append("?") //
         .append("parentId=") //
         .append(copyTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      log.info(new String(writer.getBody()));
      assertEquals(403, response.getStatus());
      assertTrue("Source file not found. ", session.itemExists(filePath));
      assertFalse("File must not be copied since destination folder is locked. ",
         session.itemExists(copyTestDestinationNode.getPath() + "/CopyTest_FILE"));
   }

   public void testCopyFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("copy") //
         .append(folderPath) //
         .append("?") //
         .append("parentId=") //
         .append(copyTestDestinationNode.getPath()).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(201, response.getStatus());
      String expectedPath = copyTestDestinationNode.getPath() + "/" + folderNode.getName();
      String expectedLocation = SERVICE_URI + "item" + expectedPath;
      String location = response.getHttpHeaders().getFirst("Location").toString();
      assertEquals(expectedLocation, location);
      assertTrue("Source folder not found. ", session.itemExists(folderPath));
      assertTrue("Not found folder in destination location. ", session.itemExists(expectedPath));
      assertTrue("Child of folder missing after coping. ", session.itemExists(expectedPath + "/file"));
   }
}
