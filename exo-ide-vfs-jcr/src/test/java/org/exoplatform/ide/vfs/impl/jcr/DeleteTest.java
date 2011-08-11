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
import javax.jcr.lock.Lock;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DeleteTest extends JcrFileSystemTest
{
   private Node deleteTestNode;

   private String folderPath;

   private String filePath;

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
      folderPath = folderNode.getPath();

      fileNode = deleteTestNode.addNode("DeleteTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("mix:lockable");
      fileNode.addMixin("exo:privilegeable");
      filePath = fileNode.getPath();

      session.save();
   }

   public void testDeleteFile() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete") //
         .append(filePath).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      assertFalse("File must be removed. ", session.itemExists(filePath));
   }

   public void testDeleteFileLocked() throws Exception
   {
      Lock lock = fileNode.lock(true, false);
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete") //
         .append(filePath) //
         .append("?") //
         .append("lockToken=") //
         .append(lock.getLockToken()) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      assertFalse("File must be removed. ", session.itemExists(filePath));
   }

   public void testDeleteFileLocked_NoLockToken() throws Exception
   {
      fileNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete") //
         .append(filePath) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
      assertTrue("File must not be removed since locked parent. ", session.itemExists(filePath));
   }

   public void testDeleteFileNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(2);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)fileNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete") //
         .append(filePath).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
      assertTrue("File must not be removed since permissions restriction. ", session.itemExists(filePath));
   }

   public void testDeleteFileWrongPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete") //
         .append(filePath + "_WRONG_PATH").toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testDeleteFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete") //
         .append(folderPath).toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(204, response.getStatus());
      assertFalse("Folder must be removed. ", session.itemExists(folderPath));
   }
}
