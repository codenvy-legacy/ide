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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.exoplatform.ide.git.core.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 29, 2011 9:44:16 AM anya $
 *
 */
public class ResetToCommitTest extends BaseTest
{
   private static final String PROJECT = ResetToCommitTest.class.getSimpleName();

   private static final String INIT_COMMIT_COMMENT = "init";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/reset-to-commit.zip";

   private static final String TEST_FILE1 = "TestFile1.txt";

   private static final String TEST_FILE2 = "TestFile2.txt";

   private static final String FIRST_COMMIT = "Commit 1";

   private static final String SECOND_COMMIT = "Commit 2";

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(PROJECT, ZIP_PATH);
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
    * Test command is not available for reseting commit in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testResetCommitCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      //Check Reset commit is available:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.RESET));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);

      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitClosed();
   }

   /**
    * Test the Reset commit view.
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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitOpened();

      assertTrue(IDE.GIT.RESET_TO_COMMIT.isOpened());
      assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitClosed();
   }

   /**
    * Test the reset commit in soft mode.
    * 
    * @throws Exception
    */
   @Test
   public void testSoftReset() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitOpened();
      assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      //Choose "soft" mode, init commit and click "Revert":
      IDE.GIT.RESET_TO_COMMIT.selectSoftMode();
      IDE.GIT.RESET_TO_COMMIT.selectRevisionByComment(INIT_COMMIT_COMMENT);
      IDE.GIT.RESET_TO_COMMIT.clickRevertButton();

      IDE.GIT.RESET_TO_COMMIT.waitClosed();

      //Check successfully reverted message:
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.RESET_COMMIT_SUCCESS, message);

      //Check file in tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(2);
      List<String> notCommited = IDE.GIT.STATUS.getNotCommited(message);
      assertEquals(1, notCommited.size());
      assertTrue(notCommited.contains(String.format(Status.Messages.NEW_FILE, TEST_FILE1)));

      //Check number of commits:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitOpened();
      assertEquals(1, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());
      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitClosed();
   }

   /**
    * Test the reset commit in mixed mode.
    * 
    * @throws Exception
    */
   @Test
   public void testMixedReset() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(TEST_FILE2);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE2);

      //Add changes to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Commit file:
      IDE.GIT.COMMIT.commit(SECOND_COMMIT);
      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitOpened();
      assertEquals(3, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      //Choose "mixed" mode, first commit and click "Revert":
      IDE.GIT.RESET_TO_COMMIT.selectMixedMode();
      IDE.GIT.RESET_TO_COMMIT.selectRevisionByComment(FIRST_COMMIT);
      IDE.GIT.RESET_TO_COMMIT.clickRevertButton();

      IDE.GIT.RESET_TO_COMMIT.waitClosed();

      //Check successfully reverted message:
      IDE.OUTPUT.waitForMessageShow(3, 10);
      message = IDE.OUTPUT.getOutputMessage(3);
      assertEquals(GIT.Messages.RESET_COMMIT_SUCCESS, message);

      //Check file in tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE2);
      IDE.LOADER.waitClosed();
      
      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(4, 10);
      message = IDE.OUTPUT.getOutputMessage(4);
      List<String> untracked = IDE.GIT.STATUS.getUntracked(message);
      assertEquals(1, untracked.size());
      assertTrue(untracked.contains(TEST_FILE2));

      //Check number of commits:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitOpened();
      assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());
      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitClosed();
   }

   /**
    * Test the reset commit in hard mode.
    * 
    * @throws Exception
    */
   @Test
   public void testHardReset() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitOpened();
      assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      //Choose "hard" mode, first commit and click "Revert":
      IDE.GIT.RESET_TO_COMMIT.selectHardMode();
      IDE.GIT.RESET_TO_COMMIT.selectRevisionByComment(FIRST_COMMIT);
      IDE.GIT.RESET_TO_COMMIT.clickRevertButton();

      IDE.GIT.RESET_TO_COMMIT.waitClosed();

      //Check successfully reverted message:
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.RESET_COMMIT_SUCCESS, message);

      //Check file in tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + TEST_FILE2);

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.contains(Status.Messages.NOTHING_TO_COMMIT));

      //Check number of commits:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitOpened();
      assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());
      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitClosed();
   }
}
