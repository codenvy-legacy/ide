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

   private String folder;

   private String document;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      deleteTestNode = testRoot.addNode(name, "nt:unstructured");
      deleteTestNode.addMixin("mix:lockable");
      deleteTestNode.addMixin("exo:privilegeable");

      Node folderNode = deleteTestNode.addNode("DeleteTest_FOLDER", "nt:folder");
      // add child in folder
      Node childDocumentNode = folderNode.addNode("document", "nt:file");
      Node childContentNode = childDocumentNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST_".getBytes()));
      folder = folderNode.getPath();

      Node documentNode = deleteTestNode.addNode("DeleteTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST_".getBytes()));
      document = documentNode.getPath();

      session.save();
   }

   public void testDeleteDocument() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/delete") //
         .append(document).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(204, response.getStatus());
      assertFalse("Document must be removed. ", session.itemExists(document));
   }

   public void testDeleteDocumentLockedParent() throws Exception
   {
      Lock lock = deleteTestNode.lock(true, false);
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/delete") //
         .append(document) //
         .append("?") //
         .append("lockTokens=") //
         .append(lock.getLockToken()) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(204, response.getStatus());
      assertFalse("Document must be removed. ", session.itemExists(document));
   }

   public void testDeleteDocumentLockedParent_NoLockToken() throws Exception
   {
      deleteTestNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/delete") //
         .append(document).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
      assertTrue("Document must not be removed since locked parent. ", session.itemExists(document));
   }

   public void testDeleteDocumentNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)deleteTestNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/delete") //
         .append(document).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
      assertTrue("Document must not be removed since permissions restriction. ", session.itemExists(document));
   }

   public void testDeleteDocumentWrongPath() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/delete") //
         .append(document + "_WRONG_PATH").toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testDeleteFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/delete") //
         .append(folder).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(204, response.getStatus());
      assertFalse("Folder must be removed. ", session.itemExists(folder));
   }
}
