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

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.Calendar;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class UnlockTest extends JcrFileSystemTest
{
   private Node unlockTestNode;

   private String folder;

   private String document;

   private String documentLockToken;

   private String folderLockToken;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      unlockTestNode = testRoot.addNode(name, "nt:unstructured");
      unlockTestNode.addMixin("exo:privilegeable");

      Node folderNode = unlockTestNode.addNode("UnlockTest_FOLDER", "nt:folder");
      folderNode.addMixin("mix:lockable");
      folder = folderNode.getPath();

      Node documentNode = unlockTestNode.addNode("UnlockTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST_".getBytes()));
      documentNode.addMixin("mix:lockable");
      document = documentNode.getPath();

      session.save();
      
      folderLockToken = folderNode.lock(true, false).getLockToken();
      documentLockToken = documentNode.lock(true, false).getLockToken();
   }
   
   public void testUnlockDocument() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/unlock") //
         .append(document) //
         .append("?") //
         .append("lockTokens=") //
         .append(documentLockToken) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(204, response.getStatus());
      Node node = (Node)session.getItem(document);
      assertFalse("Lock must be removed. ", node.isLocked());
   }

   public void testUnlockDocumentWrongLockTokens() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/unlock") //
         .append(document) //
         .append("?") //
         .append("lockTokens=") //
         .append(documentLockToken + "_WRONG") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(423, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testUnlockFolder() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/unlock") //
         .append(folder) //
         .append("?") //
         .append("lockTokens=") //
         .append(folderLockToken) //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, null);
      assertEquals(204, response.getStatus());
      Node node = (Node)session.getItem(folder);
      assertFalse("Lock must be removed. ", node.isLocked());
   }
}
