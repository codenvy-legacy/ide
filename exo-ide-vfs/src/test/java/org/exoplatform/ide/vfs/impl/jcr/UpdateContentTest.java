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
public class UpdateContentTest extends JcrFileSystemTest
{
   private Node updateContentTestNode;

   private String document;

   private String folder;

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
      updateContentTestNode.addMixin("exo:privilegeable");

      Node documentNode = updateContentTestNode.addNode("UpdateContentTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST__".getBytes()));
      document = documentNode.getPath();

      Node folderNode = updateContentTestNode.addNode("UpdateContentTest_FOLDER", "nt:folder");
      folder = folderNode.getPath();

      session.save();
   }

   public void testUpdateContent() throws Exception
   {
      String path = new StringBuilder() //
         .append("/vfs/db1/ws/content") //
         .append(document) //
         .append("?") //
         .append("mediaType=") //
         .append("text/plain;charset=utf8") //
         .toString();
      ContainerResponse response = launcher.service("POST", path, "", null, content.getBytes(), null);
      assertEquals(204, response.getStatus());
      Node doc = (Node)session.getItem(document);
      assertEquals(content, doc.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", doc.getProperty("jcr:content/jcr:mimeType").getString());
      assertEquals("utf8", doc.getProperty("jcr:content/jcr:encoding").getString());
   }

   public void testUpdateContentFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/db1/ws/content") //
         .append(folder).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, content.getBytes(), writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }
   
   public void testGetContentNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)updateContentTestNode).setPermissions(permissions);
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/db1/ws/content") //
         .append(document).toString();
      ContainerResponse response = launcher.service("POST", path, "", null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
