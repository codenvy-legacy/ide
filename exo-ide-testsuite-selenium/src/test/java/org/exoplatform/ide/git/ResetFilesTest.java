/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 24, 2011 4:07:02 PM anya $
 *
 */
public class ResetFilesTest extends BaseTest
{
   private static final String PROJECT = ResetFilesTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   private static final String TEST_FILE3 = "TestFile3";

   private static final String SRC_FOLDER = "src";

   private static final String EMPTY_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/empty-repository.zip";

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(PROJECT, EMPTY_ZIP_PATH);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + SRC_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void afterTest()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test command is not available for reseting files in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testResetFilesCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER);
      IDE.LOADER.waitClosed();
      
      //Check Reset files is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);

      IDE.INFORMATION_DIALOG.waitOpened();
      String message = IDE.INFORMATION_DIALOG.getMessage();
      assertEquals(GIT.Messages.NOTHING_TO_COMMIT, message);
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();
   }

   /**
    * Test the Reset files view.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testResetFilesView() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER);
      IDE.LOADER.waitClosed();
      
      //Create new file:
      createFile(TEST_FILE1);

      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitOpened();

      Assert.assertTrue(IDE.GIT.RESET_FILES.isOpened());
      Assert.assertEquals(1, IDE.GIT.RESET_FILES.getFilesCount());

      IDE.GIT.RESET_FILES.clickCancelButton();
      IDE.GIT.RESET_FILES.waitClosed();
   }

   /**
    * Test the reset files, contained in folder from index.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testResetFolderFiles() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER);
      IDE.LOADER.waitClosed();
      
      createFile(TEST_FILE1);
      createFile(TEST_FILE2);

      //Create file in sub folder
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + SRC_FOLDER);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + SRC_FOLDER + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE3);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER + "/" + TEST_FILE3);
      IDE.EDITOR.closeFile(TEST_FILE3);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE3);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      
      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitOpened();

      assertTrue(IDE.GIT.RESET_FILES.isOpened());
      assertEquals(3, IDE.GIT.RESET_FILES.getFilesCount());

      //Uncheck file contained in folder:
      IDE.GIT.RESET_FILES.checkFileByName(SRC_FOLDER + "/" + TEST_FILE3);
      IDE.GIT.RESET_FILES.clickResetButton();

      IDE.GIT.RESET_FILES.waitClosed();

      //Check reset files sucess message:
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessage(2);
      Assert.assertEquals(GIT.Messages.RESET_FILES_SUCCESS, message);

      //Get status message:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(3);
      message = IDE.OUTPUT.getOutputMessage(3);

      //Check untracked files after reset
      List<String> untracked = IDE.GIT.STATUS.getUntracked(message);
      Assert.assertEquals(1, untracked.size());
      Assert.assertTrue(untracked.contains(SRC_FOLDER + "/" + TEST_FILE3));
   }

   /**
    * Test the canceling reset files from index.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testCancelResetFiles() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER);
      IDE.LOADER.waitClosed();
      
      createFile(TEST_FILE1);
      createFile(TEST_FILE2);

      //Create file in sub folder
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + SRC_FOLDER);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + SRC_FOLDER + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE3);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER + "/" + TEST_FILE3);
      IDE.EDITOR.closeFile(TEST_FILE3);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE3);
      
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //Add to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);
      
      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitOpened();

      assertTrue(IDE.GIT.RESET_FILES.isOpened());
      assertEquals(3, IDE.GIT.RESET_FILES.getFilesCount());

      //Check file contained in folder:
      IDE.GIT.RESET_FILES.checkFileByName(TEST_FILE2);
      IDE.GIT.RESET_FILES.clickCancelButton();

      IDE.GIT.RESET_FILES.waitClosed();

      //Open Reset files view and check index doesn't contain folder with file:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitOpened();

      assertTrue(IDE.GIT.RESET_FILES.isOpened());
      assertEquals(3, IDE.GIT.RESET_FILES.getFilesCount());

      IDE.GIT.RESET_FILES.clickCancelButton();
      IDE.GIT.RESET_FILES.waitClosed();
   }

   /**
    * Test the reset files from index.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testResetFiles() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER);
      IDE.LOADER.waitClosed();
      
      createFile(TEST_FILE1);
      createFile(TEST_FILE2);

      //Create file in sub folder
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + SRC_FOLDER);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + SRC_FOLDER + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE3);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SRC_FOLDER + "/" + TEST_FILE3);
      IDE.EDITOR.closeFile(TEST_FILE3);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE3);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      
      //Add to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitOpened();

      Assert.assertTrue(IDE.GIT.RESET_FILES.isOpened());
      Assert.assertEquals(3, IDE.GIT.RESET_FILES.getFilesCount());

      //Check all files to reset:
      IDE.GIT.RESET_FILES.checkFileByName(TEST_FILE1);
      IDE.GIT.RESET_FILES.checkFileByName(TEST_FILE2);
      IDE.GIT.RESET_FILES.checkFileByName(SRC_FOLDER + "/" + TEST_FILE3);
      IDE.GIT.RESET_FILES.clickResetButton();

      IDE.GIT.RESET_FILES.waitClosed();

      //Check success reset files message:
      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(2);
      Assert.assertEquals(GIT.Messages.RESET_FILES_SUCCESS, message);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.INFORMATION_DIALOG.waitOpened();
      message = IDE.INFORMATION_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NOTHING_TO_COMMIT, message);
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();
   }

   private void createFile(String fileName) throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, fileName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + fileName);
      IDE.EDITOR.closeFile(fileName);
      IDE.EDITOR.waitTabNotPresent(fileName);
   }
}
