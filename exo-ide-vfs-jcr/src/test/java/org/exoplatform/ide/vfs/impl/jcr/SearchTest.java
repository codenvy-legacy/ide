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

import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.server.OutputProperty;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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

      Node fileNode01 = searchTestNode.addNode("SearchTest_FILE01", "nt:file");
      Node contentNode01 = fileNode01.addNode("jcr:content", "nt:resource");
      contentNode01.setProperty("jcr:mimeType", "text/plain");
      contentNode01.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode01.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode01.addMixin("exo:unstructuredMixin");
      fileNode01.setProperty("MyProperty", "Hello World");
      resultPath = fileNode01.getPath();

      Node fileNode02 = searchTestNode.addNode("SearchTest_FILE02", "nt:file");
      Node contentNode02 = fileNode02.addNode("jcr:content", "nt:resource");
      contentNode02.setProperty("jcr:mimeType", "text/plain");
      contentNode02.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode02.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node fileNode03 = searchTestNode.addNode("SearchTest_FILE03", "nt:file");
      Node contentNode03 = fileNode03.addNode("jcr:content", "nt:resource");
      contentNode03.setProperty("jcr:mimeType", "text/plain");
      contentNode03.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode03.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      session.save();
   }

   public void testSearchStatement() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("search") //
         .append("?") //
         .append("statement=") //
         //.append("SELECT%20*%20FROM%20nt:file%20WHERE%20MyProperty%20IS%20NOT%20NULL") //
         .append("SELECT%20MyProperty%20FROM%20nt:file%20WHERE%20MyProperty%20IS%20NOT%20NULL") //
         .toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
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
         .append(SERVICE_URI) //
         .append("search") //
         .toString();
      String sql = "name=SearchTest_FILE01&mediaType=text/plain&text=__TEST__&path=" + searchTestNode.getPath();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, sql.getBytes(), writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
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

   public void testSearchPagingSkipCount() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("search") //
         .toString();
      String sql = "name=SearchTest&mediaType=text/plain&text=__TEST__&path=" + searchTestNode.getPath();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, sql.getBytes(), writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      ItemList<Item> items = (ItemList<Item>)response.getEntity();
      assertEquals(3, items.getItems().size());
      List<Object> all = new ArrayList<Object>(3);
      for (Item i : items.getItems())
         all.add(i.getId());
      Iterator<Object> iteratorAll = all.iterator();
      iteratorAll.next();
      iteratorAll.remove();

      path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("search") //
         .append("?") //
         .append("skipCount=") //
         .append("1") //
         .toString();
      
      checkPage(path, "POST", h, sql.getBytes(), File.class.getMethod("getId"), all);
   }

   public void testSearchPagingMaxItems() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("search") //
         .toString();
      String sql = "name=SearchTest&mediaType=text/plain&text=__TEST__&path=" + searchTestNode.getPath();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, sql.getBytes(), writer, null);
      //log.info(new String(writer.getBody()));
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      ItemList<Item> items = (ItemList<Item>)response.getEntity();
      List<Object> all = new ArrayList<Object>(3);
      for (Item i : items.getItems())
         all.add(i.getId());
      all.remove(2);

      path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("search") //
         .append("?") //
         .append("maxItems=") //
         .append("2") //
         .toString();
      
      checkPage(path, "POST", h, sql.getBytes(), File.class.getMethod("getId"), all);
   }
}
