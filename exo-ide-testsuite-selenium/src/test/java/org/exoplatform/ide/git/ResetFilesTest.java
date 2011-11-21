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

import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 24, 2011 4:07:02 PM anya $
 *
 */
public class ResetFilesTest extends BaseTest
{
   private static final String TEST_FOLDER = ResetFilesTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   private static final String TEST_FILE3 = "TestFile3";

   private static final String SRC_FOLDER = "src";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER + "/" + SRC_FOLDER);
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
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
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
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES, false);

      //Not Git repository:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.WARNING_DIALOG.waitOpened();
      String message = IDE.WARNING_DIALOG.getWarningMessage();
      Assert.assertEquals(GIT.Messages.NOT_GIT_REPO, message);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      //Init repository:
      IDE.GIT.INIT_REPOSITORY.initRepository();
      IDE.OUTPUT.waitForMessageShow(1);
      message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));

      //Check Reset files is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);

      IDE.INFORMATION_DIALOG.waitOpened();
      message = IDE.INFORMATION_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NOTHING_TO_COMMIT, message);
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
      IDE.EDITOR.closeFile(0);

      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.RESET_FILES.isViewComponentsPresent());
      Assert.assertEquals(1, IDE.GIT.RESET_FILES.getFilesCount());

      IDE.GIT.RESET_FILES.clickCancelButton();
      IDE.GIT.RESET_FILES.waitForViewClosed();
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE2);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(0);

      //Create file in sub folder
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + SRC_FOLDER + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE3);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + SRC_FOLDER + "/" + TEST_FILE3);
      IDE.EDITOR.closeFile(0);

      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.RESET_FILES.isViewComponentsPresent());
      Assert.assertEquals(2, IDE.GIT.RESET_FILES.getFilesCount());

      //Uncheck file contained in folder:
      IDE.GIT.RESET_FILES.checkFileByName(SRC_FOLDER + "/" + TEST_FILE3);
      IDE.GIT.RESET_FILES.clickResetButton();

      IDE.GIT.RESET_FILES.waitForViewClosed();

      //Check reset files sucess message:
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessageText(2);
      Assert.assertEquals(GIT.Messages.RESET_FILES_SUCCESS, message);

      //Get status message:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(3);
      message = IDE.OUTPUT.getOutputMessageText(3);

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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.RESET_FILES.isViewComponentsPresent());
      Assert.assertEquals(3, IDE.GIT.RESET_FILES.getFilesCount());

      //Check file contained in folder:
      IDE.GIT.RESET_FILES.checkFileByName(TEST_FILE2);
      IDE.GIT.RESET_FILES.clickCancelButton();

      IDE.GIT.RESET_FILES.waitForViewClosed();

      //Open Reset files view and check index doesn't contain folder with file:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.RESET_FILES.isViewComponentsPresent());
      Assert.assertEquals(3, IDE.GIT.RESET_FILES.getFilesCount());

      IDE.GIT.RESET_FILES.clickCancelButton();
      IDE.GIT.RESET_FILES.waitForViewClosed();
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.GIT.RESET_FILES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.RESET_FILES.isViewComponentsPresent());
      Assert.assertEquals(3, IDE.GIT.RESET_FILES.getFilesCount());

      //Check all files to reset:
      IDE.GIT.RESET_FILES.checkFileByName(TEST_FILE1);
      IDE.GIT.RESET_FILES.checkFileByName(TEST_FILE2);
      IDE.GIT.RESET_FILES.checkFileByName(SRC_FOLDER + "/" + TEST_FILE3);
      IDE.GIT.RESET_FILES.clickResetButton();

      IDE.GIT.RESET_FILES.waitForViewClosed();

      //Check success reset files message:
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessageText(2);
      Assert.assertEquals(GIT.Messages.RESET_FILES_SUCCESS, message);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET_FILES);
      IDE.INFORMATION_DIALOG.waitOpened();
      message = IDE.INFORMATION_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NOTHING_TO_COMMIT, message);
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();
   }
}
