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
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
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

   private final static String copyofRestFileName = "Copy of Example.groovy";

   private final String restFileContent = "// simple groovy script\n" + "import javax.ws.rs.Path\n"
      + "import javax.ws.rs.GET\n" + "import javax.ws.rs.PathParam\n" + "@Path(\"/\")\n"
      + "public class HelloWorld {\n" + "@GET\n" + "@Path(\"helloworld/{name}\")\n"
      + "public String hello(@PathParam(\"name\") String name) {\n" + "return \"Hello \" + name\n" + "}\n" + "}\n";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/search/Example.groovy";
      String filePath2 = "src/test/resources/org/exoplatform/ide/search/Copy of Example.groovy";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + FOLDER_1 + "/" + restFileName);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + FOLDER_2 + "/" + restFileName);
         VirtualFileSystemUtils.put(filePath2, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + FOLDER_2 + "/" + copyofRestFileName);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * IDE-31:Searching file from root folder test.
    * 
    * @throws Exception
    */
   @Test
   public void testSearchInRootFolder() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2);

      
      
      Thread.sleep(10000);

      //      IDE.WORKSPACE.waitForRootItem();
      //      IDE.NAVIGATION.createFolder(folder1Name);
      //      //Create and save 
      //      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      //      IDE.EDITOR.waitTabPresent(0);
      //      IDE.EDITOR.deleteLinesInEditor(0, 14);
      //      IDE.EDITOR.typeTextIntoEditor(0, restFileContent);
      //      IDE.NAVIGATION.saveFileAs(restFileName);
      //      Thread.sleep(TestConstants.SLEEP);
      //      IDE.NAVIGATION.assertItemVisible(WS_URL + folder1Name + "/" + restFileName);
      //
      //      //Create second folder
      //      IDE.WORKSPACE.selectRootItem();
      //      IDE.NAVIGATION.createFolder(folder2Name);
      //      IDE.WORKSPACE.waitForItem(WS_URL + folder2Name + "/");
      //      //Save in second folder first time
      //      saveAsUsingToolbarButton(copyofRestFileName);
      //      Thread.sleep(TestConstants.SLEEP);
      //      IDE.NAVIGATION.assertItemVisible(WS_URL + folder2Name + "/" + copyofRestFileName);
      //      //Save in second folder second time
      //      saveAsUsingToolbarButton(restFileName);
      //      Thread.sleep(TestConstants.SLEEP);
      //      IDE.NAVIGATION.assertItemVisible(WS_URL + folder2Name + "/" + restFileName);
      //
      //      IDE.EDITOR.closeFile(0);
      //
      //      IDE.WORKSPACE.selectRootItem();
      //
      //      IDE.SEARCH.performSearch("/", "Hello", "text/html");
      //      IDE.SEARCH.waitSearchResultsOpened();
      //
      //      assertEquals(0, IDE.SEARCH.getResultsCount());
      //
      //      selectWorkspaceTab();
      //      IDE.SEARCH.performSearch("/", "Hello", restFileMimeType);
      //      IDE.SEARCH.waitSearchResultsOpened();
      //      assertEquals(3, IDE.SEARCH.getResultsCount());
      //
      //      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + folder1Name + "/" + restFileName);
      //      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + folder2Name + "/" + restFileName);
      //      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + folder2Name + "/" + copyofRestFileName);
      //      //Open first file from search results
      //      IDE.WORKSPACE.doubleClickOnFileFromSearchTab(WS_URL + folder1Name + "/" + restFileName);
      //      IDE.EDITOR.waitTabPresent(0);
      //      assertEquals(restFileName, IDE.EDITOR.getTabTitle(0));
      //
      //      //Open second file from search results
      //      IDE.WORKSPACE.doubleClickOnFileFromSearchTab(WS_URL + folder2Name + "/" + copyofRestFileName);
      //      IDE.EDITOR.waitTabPresent(1);
      //      assertEquals(copyofRestFileName, IDE.EDITOR.getTabTitle(1));
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

}
