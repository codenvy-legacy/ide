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

import org.exoplatform.ide.vfs.Item;
import org.exoplatform.ide.vfs.OutputProperty;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class GetObjectTest extends JcrFileSystemTest
{
   private Node getObjectTestNode;

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
      getObjectTestNode = testRoot.addNode(name, "nt:unstructured");
      getObjectTestNode.addMixin("exo:privilegeable");

      Node folderNode = getObjectTestNode.addNode("GetObjectTest_FOLDER", "nt:folder");
      folder = folderNode.getPath();

      Node documentNode = getObjectTestNode.addNode("GetObjectTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      documentNode.addMixin("exo:unstructuredMixin");
      documentNode.setProperty("MyProperty01", "hello world");
      documentNode.setProperty("MyProperty02", "to be or not to be");
      document = documentNode.getPath();

      session.save();
   }

   public void testGetDocument() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/item") //
         .append(document).toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      assertEquals(document, ((Item)response.getEntity()).getPath());
   }

   public void testGetDocumentPropertyFilter() throws Exception
   {
      // No filter - all properties
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/item") //
         .append(document) //
         .toString();

      ContainerResponse response = launcher.service("GET", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      List<OutputProperty> properties = ((Item)response.getEntity()).getProperties();
      Map<String, Object[]> m = new HashMap<String, Object[]>(properties.size());
      for (OutputProperty p : properties)
         m.put(p.getName(), p.getValue());
      assertEquals(2, m.size());
      assertEquals("hello world", m.get("MyProperty01")[0]);
      assertEquals("to be or not to be", m.get("MyProperty02")[0]);

      // With filter
      path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/item") //
         .append(document) //
         .append("?") //
         .append("propertyFilter=") //
         .append("MyProperty02") //
         .toString();

      response = launcher.service("GET", path, "", null, null, null);
      assertEquals(200, response.getStatus());
      m.clear();
      properties = ((Item)response.getEntity()).getProperties();
      for (OutputProperty p : properties)
         m.put(p.getName(), p.getValue());
      assertEquals(1, m.size());
      assertEquals("to be or not to be", m.get("MyProperty02")[0]);
   }

   public void testGetDocumentNotFound() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/item") //
         .append(document + "_WRONG_IDENTIFIER").toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(404, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetDocumentNoPermissions() throws Exception
   {
      Map<String, String[]> permissions = new HashMap<String, String[]>(1);
      permissions.put("root", PermissionType.ALL);
      ((ExtendedNode)getObjectTestNode).setPermissions(permissions);
      session.save();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/item") //
         .append(document).toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(403, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/item") //
         .append(folder).toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      log.info(new String(writer.getBody()));
   }
}
