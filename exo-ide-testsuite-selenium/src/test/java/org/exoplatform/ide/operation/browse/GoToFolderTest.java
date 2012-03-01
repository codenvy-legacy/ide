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
 * @version $Id: ${date} ${time}
 * 
 */
public class GoToFolderTest extends BaseTest
{

   private final static String PROJECT = GoToFolderTest.class.getSimpleName();

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
         VirtualFileSystemUtils.createDefaultProject(PROJECT);

         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_2);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, WS_URL + PROJECT + "/" + FOLDER_1 + "/" + FILE_1);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, WS_URL + PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testGoToFolder() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2);

      // Open first folder and file in it
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);

      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);

      // Select second file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      // Go to folder and go
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);

      // close all folders, refresh Project Explorer Three. And reproduce goto folder operations with
      // second file
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.TOOLBAR.runCommand(MenuCommands.File.REFRESH_TOOLBAR);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_2);
      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);

      IDE.EDITOR.closeFile(FILE_1);
      IDE.EDITOR.waitTabNotPresent(FILE_1);

      IDE.EDITOR.closeFile(FILE_2);
      IDE.EDITOR.waitTabNotPresent(FILE_2);
   }

   @Test
   public void testGoToFolderSearchPanel() throws Exception
   {

      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2);

      IDE.TOOLBAR.runCommand(MenuCommands.File.SEARCH);
      IDE.SEARCH.waitPerformSearchOpened();
      IDE.SEARCH.clickSearchButton();
      IDE.SEARCH_RESULT.waitOpened();

      IDE.SEARCH_RESULT.openItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      // TODO After fix issue-IDE-1458 should be work
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_2 + "/" + FILE_2);
      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + FOLDER_1 + "/" + FILE_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_1);

   }

}
