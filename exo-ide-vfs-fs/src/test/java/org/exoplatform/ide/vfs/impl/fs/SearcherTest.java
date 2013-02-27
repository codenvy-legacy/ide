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
package org.exoplatform.ide.vfs.impl.fs;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearcherTest extends LocalFileSystemTest
{
   private Pair<String[], String>[] queryToResult;

   @SuppressWarnings("unchecked")
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      System.setProperty("java.io.tmpdir", root.getParent());

      String searchTestPath = createDirectory(testRootPath, "SearchTest_Folder");

      String file1 = createFile(searchTestPath, "SearchTest_File01", "to be or not to be".getBytes());
      writeProperties(file1, Collections.singletonMap("vfs:mimeType", new String[]{"text/xml"})); // text/xml just for test, it is not xml content

      String file2 = createFile(searchTestPath, "SearchTest_File02", "to be or not to be".getBytes());
      writeProperties(file2, Collections.singletonMap("vfs:mimeType", new String[]{"text/plain"}));

      String folder1 = createDirectory(searchTestPath, "folder01");
      String file3 = createFile(folder1, "SearchTest_File03", "to be or not to be".getBytes());

      queryToResult = new Pair[11];
      queryToResult[0] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or%20not%20to%20be");
      queryToResult[1] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or");
      // text + media type
      queryToResult[2] = new Pair<String[], String>(new String[]{file2, file3}, "text=to%20be%20or&mediaType=text/plain");
      queryToResult[3] = new Pair<String[], String>(new String[]{file1}, "text=to%20be%20or&mediaType=text/xml");
      // text + name
      queryToResult[4] = new Pair<String[], String>(new String[]{file2}, "text=to%20be%20or&name=*File02");
      queryToResult[5] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or&name=SearcherTest*");
      // text + path
      queryToResult[6] = new Pair<String[], String>(new String[]{file3}, "text=to%20be%20or&path=" + folder1);
      queryToResult[7] = new Pair<String[], String>(new String[]{file1, file2, file3}, "text=to%20be%20or&path=" + searchTestPath);
      // name + media type
      queryToResult[8] = new Pair<String[], String>(new String[]{file2, file3}, "name=SearcherTest*&mediaType=text/plain");
      queryToResult[9] = new Pair<String[], String>(new String[]{file1}, "name=SearcherTest*&mediaType=text/xml");

      MountPoint mountPoint = provider.getMounts().iterator().next();
      Searcher searcher = Searcher.getInstance(mountPoint);
      // Wait util searcher initialized.
      while (!searcher.isInitialized())
      {
         Thread.sleep(100);
      }
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public void testSearchBuildStatement() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "search";
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
      for (Pair<String[], String> pair : queryToResult)
      {
         ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, h, pair.b.getBytes(), writer, null);
         log.info(new String(writer.getBody()));
         assertEquals(200, response.getStatus());
         List<Item> result = ((ItemList)response.getEntity()).getItems();
         assertEquals(String.format(
            "Expected %d but found %d for query %s", pair.a.length, result.size(), pair.b),
            pair.a.length,
            result.size());
         List<String> resultPaths = new ArrayList<String>(result.size());
         for (Item item : result)
         {
            resultPaths.add(item.getPath());
         }
         List<String> copy = new ArrayList<String>(resultPaths);
         copy.removeAll(Arrays.asList(pair.a));
         assertTrue(String.format("Expected result is %s but found %s", Arrays.toString(pair.a), resultPaths), copy.isEmpty());
         writer.reset();
      }
   }

//   public void _testSearchPagingSkipCount() throws Exception
//   {
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = SERVICE_URI + "search";
//      String query = "name=SearchTest&mediaType=text/plain&text=__TEST__&path=" + searchTestPath;
//      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
//      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, query.getBytes(), writer, null);
//      //log.info(new String(writer.getBody()));
//      assertEquals(200, response.getStatus());
//      @SuppressWarnings("unchecked")
//      ItemList<Item> items = (ItemList<Item>)response.getEntity();
//      assertEquals(3, items.getItems().size());
//      List<Object> all = new ArrayList<Object>(3);
//      for (Item i : items.getItems())
//      {
//         validateLinks(i);
//         all.add(i.getId());
//      }
//      Iterator<Object> iteratorAll = all.iterator();
//      iteratorAll.next();
//      iteratorAll.remove();
//
//      path = SERVICE_URI + "search" + '?' + "skipCount=" + 1;
//
//      checkPage(path, "POST", h, query.getBytes(), FileImpl.class.getMethod("getId"), all);
//   }
//
//   public void _testSearchPagingMaxItems() throws Exception
//   {
//      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
//      String path = SERVICE_URI + "search";
//      String query = "name=SearchTest&mediaType=text/plain&text=__TEST__&path=" + searchTestPath;
//      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
//      h.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
//      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, query.getBytes(), writer, null);
//      //log.info(new String(writer.getBody()));
//      assertEquals(200, response.getStatus());
//      @SuppressWarnings("unchecked")
//      ItemList<Item> items = (ItemList<Item>)response.getEntity();
//      List<Object> all = new ArrayList<Object>(3);
//      for (Item i : items.getItems())
//      {
//         validateLinks(i);
//         all.add(i.getId());
//      }
//      all.remove(2);
//
//      path = SERVICE_URI + "search" + '?' + "maxItems=" + 2;
//
//      checkPage(path, "POST", h, query.getBytes(), FileImpl.class.getMethod("getId"), all);
//   }
}
