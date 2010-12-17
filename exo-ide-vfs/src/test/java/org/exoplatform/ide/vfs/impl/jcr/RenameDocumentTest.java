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

import org.exoplatform.ide.vfs.server.ObjectId;
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
public class RenameDocumentTest extends JcrFileSystemTest
{
   private Node renameDocumentTestNode;

   private String document;

   private String folder;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      renameDocumentTestNode = testRoot.addNode(name, "nt:unstructured");
      renameDocumentTestNode.addMixin("mix:lockable");
      renameDocumentTestNode.addMixin("exo:privilegeable");

      Node documentNode = renameDocumentTestNode.addNode("RenameDocumentTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:encoding", "utf8");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      document = documentNode.getPath();

      Node folderNode = renameDocumentTestNode.addNode("RenameDocumentTest_FOLDER", "nt:folder");
      folder = folderNode.getPath();

      session.save();
   }

   public void testRenameDocument() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/rename") //
         .append(document) //
         .append("?") //
         .append("newname=") //
         .append("_DOCUMENT_NEW_NAME_") //
         .append("&") //
         .append("mediaType=") //
         .append("text/*;charset=ISO-8859-1") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      ObjectId id = (ObjectId)response.getEntity();
      assertTrue(session.itemExists(id.getId()));
      Node doc = (Node)session.getItem(id.getId());
      assertEquals(DEFAULT_CONTENT, doc.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/*", doc.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("ISO-8859-1", doc.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testRenameDocumentLockedParent() throws Exception
   {
      Lock lock = renameDocumentTestNode.lock(true, false);
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/rename") //
         .append(document) //
         .append("?") //
         .append("newname=") //
         .append("_DOCUMENT_NEW_NAME_") //
         .append("&") //
         .append("mediaType=") //
         .append("text/*;charset=ISO-8859-1") //
         .append("&") //
         .append("lockTokens=") //
         .append(lock.getLockToken())
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      ObjectId id = (ObjectId)response.getEntity();
      assertTrue(session.itemExists(id.getId()));
      Node doc = (Node)session.getItem(id.getId());
      assertEquals(DEFAULT_CONTENT, doc.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/*", doc.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("ISO-8859-1", doc.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testRenameDocumentLockedParent_NoLockToke() throws Exception
   {
      renameDocumentTestNode.lock(true, false);
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/rename") //
         .append(document) //
         .append("?") //
         .append("newname=") //
         .append("_DOCUMENT_NEW_NAME_") //
         .append("&") //
         .append("mediaType=") //
         .append("text/*;charset=ISO-8859-1") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testRenameDocumentNoPermissions () throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      permissions.put("john", new String[]{PermissionType.READ});
      ((ExtendedNode)renameDocumentTestNode).setPermissions(permissions);
      session.save();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/rename") //
         .append(document) //
         .append("?") //
         .append("newname=") //
         .append("_DOCUMENT_NEW_NAME_") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testRenameFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/rename") //
         .append(folder) //
         .append("?") //
         .append("newname=") //
         .append("_FOLDER_NEW_NAME_") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(400, response.getStatus());
   }
}
