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
      renameTestNode = testRoot.addNode(name, "nt:unstructured");

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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("rename/") //
         .append(fileID) //
         .append("?") //
         .append("newname=") //
         .append("_FILE_NEW_NAME_") //
         .append("&") //
         .append("mediaType=") //
         .append("text/*;charset=ISO-8859-1") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = renameTestNode.getPath() + "/" + "_FILE_NEW_NAME_";
      assertTrue(session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals(DEFAULT_CONTENT, file.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/*", file.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("ISO-8859-1", file.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testRenameFileLocked() throws Exception
   {
      Lock lock = fileNode.lock(true, false);
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("rename/") //
         .append(fileID) //
         .append("?") //
         .append("newname=") //
         .append("_FILE_NEW_NAME_") //
         .append("&") //
         .append("mediaType=") //
         .append("text/*;charset=ISO-8859-1") //
         .append("&") //
         .append("lockToken=") //
         .append(lock.getLockToken()).toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("rename/") //
         .append(fileID) //
         .append("?") //
         .append("newname=") //
         .append("_FILE_NEW_NAME_") //
         .append("&") //
         .append("mediaType=") //
         .append("text/*;charset=ISO-8859-1") //
         .toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("rename/") //
         .append(fileID) //
         .append("?") //
         .append("newname=") //
         .append("_FILE_NEW_NAME_") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testRenameFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("rename/") //
         .append(folderID) //
         .append("?") //
         .append("newname=") //
         .append("_FOLDER_NEW_NAME_") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      String expectedPath = renameTestNode.getPath() + "/" + "_FOLDER_NEW_NAME_";
      assertTrue(session.itemExists(expectedPath));
   }

   //   public void testRenameFolderUpdateMimeType() throws Exception
   //   {
   //      String path = new StringBuilder() //
   //         .append(SERVICE_URI) //
   //         .append("rename") //
   //         .append(folderPath) //
   //         .append("?") //
   //         .append("newname=") //
   //         .append("_FOLDER_NEW_NAME_") //
   //         .append("&") //
   //         .append("mediaType=") //
   //         .append("text/directory%2BFOO") // text/directory+FOO
   //         .toString();
   //      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);
   //      assertEquals(200, response.getStatus());
   //      String expectedPath = renameTestNode.getPath() + "/" + "_FOLDER_NEW_NAME_";
   //      assertTrue(session.itemExists(expectedPath));
   //      Node folder = (Node)session.getItem(expectedPath);
   //      assertTrue(folder.isNodeType("vfs:folder"));
   //      assertEquals("text/directory+FOO", folder.getProperty("vfs:mimeType").getString());
   //   }
}
