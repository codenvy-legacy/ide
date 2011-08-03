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
package org.exoplatform.ide.operation.browse;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-96 Go to folder test
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GoToFolderTest extends BaseTest
{

   private final static String FOLDER_1 = "GoToFolderTest1";

   private final static String FOLDER_2 = "GoToFolderTest2";

   private final static String FILE_1 = "GoToFolderTestFile1.xml";

   private final static String FILE_2 = "GoToFolderTestFile2.xml";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/empty.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_2);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, WS_URL + FOLDER_1 + "/" + FILE_1);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, WS_URL + FOLDER_2 + "/" + FILE_2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_1);
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testGoToFolder() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);

      //Open first folder and file in it
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_1 + "/");
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_1 + "/" + FILE_1, false);

      //Close first folder
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + FOLDER_1 + "/");
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_1 + "/" + FILE_1);

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + FOLDER_2 + "/");

      //Select second file
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_2 + "/" + FILE_2);

      //Go to folder with first file
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);

      //Check file is shown in tree
      //TODO check selected state
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_1 + "/" + FILE_1);

      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommand("Refresh Selected Folder");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + FOLDER_2 + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_2 + "/" + FILE_2);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_2 + "/" + FILE_2, false);

      //Go to folder with first file
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);

      //TODO check selected state
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_2 + "/" + FILE_2);

      //Close opened tabs
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.closeFile(0);

      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);
   }

   @Test
   public void testGoToFolderSearchPanel() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_1 + "/" + FILE_1);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_2 + "/" + FILE_2);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_1 + "/");
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_2 + "/");

      IDE.SEARCH.performSearch("/", "", "");
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      
      //Check files are found
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL + FOLDER_1 + "/" + FILE_1);
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL + FOLDER_2 + "/" + FILE_2);
      //Open second file
      IDE.WORKSPACE.doubleClickOnFileFromSearchTab(WS_URL + FOLDER_2 + "/" + FILE_2);
      //Go to folder with second file
      IDE.WORKSPACE.waitForRootItem();
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      IDE.WORKSPACE.waitForRootItem();

      //TODO check selected
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_2 + "/" + FILE_2);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_1 + "/" + FILE_1);
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_1 + "/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_2 + "/");
   }

}
