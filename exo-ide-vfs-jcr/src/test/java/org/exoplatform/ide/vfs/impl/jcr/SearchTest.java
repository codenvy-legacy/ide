/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Property;

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
 * @version $Id: SearchTest.java 79905 2012-02-27 15:50:25Z vitalka $
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
      
      Node fileNode04 = searchTestNode.addNode("SearchTest_FILE04", "nt:file");
      Node contentNode04 = fileNode04.addNode("jcr:content", "nt:resource");
      contentNode04.setProperty("jcr:mimeType", "application/java");
      contentNode04.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode04.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      session.save();
   }

   @SuppressWarnings("rawtypes")
   public void testSearchStatement() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "search" + '?' +
         "statement=SELECT%20MyProperty%20FROM%20nt:file%20WHERE%20MyProperty%20IS%20NOT%20NULL";
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      //log.info(new String(writer.getBody()));
      @SuppressWarnings("unchecked")
      ItemList<Item> items = (ItemList<Item>)response.getEntity();
      assertEquals(1, items.getItems().size());
      Item result = items.getItems().get(0);
      assertEquals(resultPath, result.getPath());
      List<Property> properties = result.getProperties();
      Map<String, List> m = new HashMap<String, List>(properties.size());
      for (Property o : properties)
      {
         m.put(o.getName(), o.getValue());
      }
      assertEquals("Hello World", m.get("MyProperty").get(0));
   }

   @SuppressWarnings("rawtypes")
   public void testSearchBuildStatement() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "search";
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
      validateLinks(result);
      assertEquals(resultPath, result.getPath());
      List<Property> properties = result.getProperties();
      Map<String, List> m = new HashMap<String, List>(properties.size());
      for (Property o : properties)
      {
         m.put(o.getName(), o.getValue());
      }
      assertEquals("Hello World", m.get("MyProperty").get(0));
   }

   public void testSearchPagingSkipCount() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "search";
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
      {
         validateLinks(i);
         all.add(i.getId());
      }
      Iterator<Object> iteratorAll = all.iterator();
      iteratorAll.next();
      iteratorAll.remove();

      path = SERVICE_URI + "search" + '?' + "skipCount=" + 1;
      
      checkPage(path, "POST", h, sql.getBytes(), File.class.getMethod("getId"), all);
   }

   public void testSearchPagingMaxItems() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "search";
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
      {
         validateLinks(i);
         all.add(i.getId());
      }
      all.remove(2);

      path = SERVICE_URI + "search" + '?' + "maxItems=" + 2;
      
      checkPage(path, "POST", h, sql.getBytes(), File.class.getMethod("getId"), all);
   }
}
