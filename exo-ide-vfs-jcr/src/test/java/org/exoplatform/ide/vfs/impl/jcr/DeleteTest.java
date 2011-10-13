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
import java.util.HashMap;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.lock.Lock;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete/") //
         .append(fileID).toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete/") //
         .append(fileID) //
         .append("?") //
         .append("lockToken=") //
         .append(lock.getLockToken()) //
         .toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete/") //
         .append(fileID) //
         .toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete/") //
         .append(fileID).toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete/") //
         .append(fileID + "_WRONG_ID").toString();
      ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testDeleteFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("delete/") //
         .append(folderID).toString();
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
