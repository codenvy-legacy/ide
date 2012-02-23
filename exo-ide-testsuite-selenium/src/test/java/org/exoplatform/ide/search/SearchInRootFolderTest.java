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
package org.exoplatform.ide.search;

import static org.junit.Assert.assertEquals;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class SearchInRootFolderTest extends BaseTest
{
   private static final String FOLDER_1 = "Users";

   private static final String FOLDER_2 = "Test";

   private static final String PROJECT = SearchInRootFolderTest.class.getSimpleName();

   private final static String restFileName = "Example.groovy";

   private final String restFileMimeType = MimeType.TEXT_PLAIN;

   private final static String copyofRestFileName = "CopyofExample.groovy";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/search/Example.groovy";

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + FOLDER_1 + "/"
            + restFileName);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + FOLDER_2 + "/"
            + restFileName);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + FOLDER_2 + "/"
            + copyofRestFileName);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * @throws Exception
    */
   @Test
   public void testSearchInRootFolder() throws Exception
   {
      //step 1 open project an folders
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2);

      //step 1 first search and check results
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.SEARCH.performSearch("/" + PROJECT, "Hello", "text/html\n");
      IDE.SEARCH.waitSearchResultsOpened();
      assertEquals(1, IDE.SEARCH.getResultsCount());

      //step 2 return to project explorer and run new search. Check search results
      IDE.PROJECT.EXPLORER.selectProjectTab(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.SEARCH.performSearch("/" + PROJECT, "Hello", restFileMimeType + "\n");
      IDE.SEARCH.waitSearchResultsOpened();
      assertEquals(4, IDE.SEARCH.getResultsCount());
      IDE.SEARCH.isFilePresent(PROJECT + "/" + FOLDER_1 + "/" + restFileName);
      IDE.SEARCH.isFilePresent(PROJECT + "/" + FOLDER_2 + "/" + restFileName);
      IDE.SEARCH.isFilePresent(PROJECT + "/" + FOLDER_2 + "/" + copyofRestFileName);

      //step 3 open files from search three and check opening
      IDE.SEARCH.selectItem(PROJECT + "/" + FOLDER_1 + "/" + restFileName);
      IDE.SEARCH.doubleClickOnFile(PROJECT + "/" + FOLDER_1 + "/" + restFileName);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_1 + "/" + restFileName);
      IDE.PERSPECTIVE.selectTabsOnExplorer("Search");
      IDE.SEARCH.waitSearchResultsOpened();
      IDE.SEARCH.selectItem(PROJECT + "/" + FOLDER_2 + "/" + copyofRestFileName);
      IDE.SEARCH.doubleClickOnFile(PROJECT + "/" + FOLDER_2 + "/" + copyofRestFileName);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_2 + "/" + copyofRestFileName);
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

}
