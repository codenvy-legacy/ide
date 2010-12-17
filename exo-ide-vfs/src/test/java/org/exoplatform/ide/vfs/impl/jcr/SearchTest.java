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

import org.exoplatform.ide.vfs.server.Item;
import org.exoplatform.ide.vfs.server.ItemList;
import org.exoplatform.ide.vfs.server.OutputProperty;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class SearchTest extends JcrFileSystemTest
{
   private Node searchTestNode;

   private String resultPath;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      searchTestNode = testRoot.addNode(name, "nt:unstructured");

      Node documentNode01 = searchTestNode.addNode("SearchTest_DOCUMENT01", "nt:file");
      Node contentNode01 = documentNode01.addNode("jcr:content", "nt:resource");
      contentNode01.setProperty("jcr:mimeType", "text/plain");
      contentNode01.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode01.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      documentNode01.addMixin("exo:unstructuredMixin");
      documentNode01.setProperty("MyProperty", "Hello World");
      resultPath = documentNode01.getPath();

      Node documentNode02 = searchTestNode.addNode("SearchTest_DOCUMENT02", "nt:file");
      Node contentNode02 = documentNode02.addNode("jcr:content", "nt:resource");
      contentNode02.setProperty("jcr:mimeType", "text/plain");
      contentNode02.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode02.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node documentNode03 = searchTestNode.addNode("SearchTest_DOCUMENT03", "nt:file");
      Node contentNode03 = documentNode03.addNode("jcr:content", "nt:resource");
      contentNode03.setProperty("jcr:mimeType", "text/plain");
      contentNode03.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode03.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      session.save();
   }

   public void testSearchStatement() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/search") //
         .append("?") //
         .append("statement=") //
         //.append("SELECT%20*%20FROM%20nt:file%20WHERE%20MyProperty%20IS%20NOT%20NULL") //
         .append("SELECT%20MyProperty%20FROM%20nt:file%20WHERE%20MyProperty%20IS%20NOT%20NULL") //
         .toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      @SuppressWarnings("unchecked")
      ItemList<Item> items = (ItemList<Item>)response.getEntity();
      assertEquals(1, items.getItems().size());
      Item result = items.getItems().get(0);
      assertEquals(resultPath, result.getPath());
      List<OutputProperty> properties = result.getProperties();
      Map<String, Object[]> m = new HashMap<String, Object[]>(properties.size());
      for (OutputProperty o : properties)
         m.put(o.getName(), o.getValue());
      assertEquals("Hello World", m.get("MyProperty")[0]);
   }

   public void testSearchBuildStatement() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/search") //
         .toString();
      String sql = "properties=MyProperty&contains=Hello&path=" + searchTestNode.getPath();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
      ContainerResponse response = launcher.service("POST", path, "", h, sql.getBytes(), writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      @SuppressWarnings("unchecked")
      ItemList<Item> items = (ItemList<Item>)response.getEntity();
      assertEquals(1, items.getItems().size());
      Item result = items.getItems().get(0);
      assertEquals(resultPath, result.getPath());
      List<OutputProperty> properties = result.getProperties();
      Map<String, Object[]> m = new HashMap<String, Object[]>(properties.size());
      for (OutputProperty o : properties)
         m.put(o.getName(), o.getValue());
      assertEquals("Hello World", m.get("MyProperty")[0]);
   }
}
