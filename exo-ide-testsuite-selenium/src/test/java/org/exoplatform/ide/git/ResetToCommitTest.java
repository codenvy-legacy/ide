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
   private static final String INIT_COMMIT_COMMENT = "init";
   
   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/ResetToCommitTest.zip";

   private static final String TEST_FOLDER = ResetToCommitTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1.txt";

   private static final String TEST_FILE2 = "TestFile2.txt";

   private static final String FIRST_COMMIT = "Commit 1";

   private static final String SECOND_COMMIT = "Commit 2";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder(ZIP_PATH, WS_URL);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
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
    * Test command is not available for reseting commit in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testResetCommitCommand() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.RESET, false);

      //Check Reset commit is available:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.RESET, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);

      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.RESET_TO_COMMIT.isViewComponentsPresent());
      Assert.assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();
   }

   /**
    * Test the reset commit in soft mode.
    * 
    * @throws Exception
    */
   @Test
   public void testSoftReset() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitForViewOpened();
      Assert.assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      //Choose "soft" mode, init commit and click "Revert":
      IDE.GIT.RESET_TO_COMMIT.selectSoftMode();
      IDE.GIT.RESET_TO_COMMIT.selectRevisionByComment(INIT_COMMIT_COMMENT);
      IDE.GIT.RESET_TO_COMMIT.clickRevertButton();

      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();

      //Check successfully reverted message:
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertEquals(GIT.Messages.RESET_COMMIT_SUCCESS, message);

      //Check file in tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessage(2);
      List<String> notCommited = IDE.GIT.STATUS.getNotCommited(message);
      Assert.assertEquals(1, notCommited.size());
      Assert.assertTrue(notCommited.contains(String.format(Status.Messages.NEW_FILE, TEST_FILE1)));

      //Check number of commits:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitForViewOpened();
      Assert.assertEquals(1, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());
      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();
   }

   /**
    * Test the reset commit in mixed mode.
    * 
    * @throws Exception
    */
   @Test
   public void testMixedReset() throws Exception
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

      //Add changes to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Commit file:
      IDE.GIT.COMMIT.commit(SECOND_COMMIT);
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessage(2);
      Assert.assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitForViewOpened();
      Assert.assertEquals(3, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      //Choose "mixed" mode, first commit and click "Revert":
      IDE.GIT.RESET_TO_COMMIT.selectMixedMode();
      IDE.GIT.RESET_TO_COMMIT.selectRevisionByComment(FIRST_COMMIT);
      IDE.GIT.RESET_TO_COMMIT.clickRevertButton();

      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();

      //Check successfully reverted message:
      IDE.OUTPUT.waitForMessageShow(3);
      message = IDE.OUTPUT.getOutputMessage(3);
      Assert.assertEquals(GIT.Messages.RESET_COMMIT_SUCCESS, message);

      //Check file in tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(4);
      message = IDE.OUTPUT.getOutputMessage(4);
      List<String> untracked = IDE.GIT.STATUS.getUntracked(message);
      Assert.assertEquals(1, untracked.size());
      Assert.assertTrue(untracked.contains(TEST_FILE2));

      //Check number of commits:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitForViewOpened();
      Assert.assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());
      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();
   }

   /**
    * Test the reset commit in hard mode.
    * 
    * @throws Exception
    */
   @Test
   public void testHardReset() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Reset files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitForViewOpened();
      Assert.assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());

      //Choose "hard" mode, first commit and click "Revert":
      IDE.GIT.RESET_TO_COMMIT.selectHardMode();
      IDE.GIT.RESET_TO_COMMIT.selectRevisionByComment(FIRST_COMMIT);
      IDE.GIT.RESET_TO_COMMIT.clickRevertButton();

      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();

      //Check successfully reverted message:
      IDE.OUTPUT.waitForMessageShow(1);
     String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertEquals(GIT.Messages.RESET_COMMIT_SUCCESS, message);

      //Check file in tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessage(2);
      Assert.assertTrue(message.contains(Status.Messages.NOTHING_TO_COMMIT));

      //Check number of commits:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.RESET);
      IDE.GIT.RESET_TO_COMMIT.waitForViewOpened();
      Assert.assertEquals(2, IDE.GIT.RESET_TO_COMMIT.getRevisionsCount());
      IDE.GIT.RESET_TO_COMMIT.clickCancelButton();
      IDE.GIT.RESET_TO_COMMIT.waitForViewClosed();
   }
}
