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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.exoplatform.ide.git.core.Status;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 27, 2011 11:48:04 AM anya $
 *
 */
public class CommitTest extends BaseTest
{
   private static final String PROJECT = CommitTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   @BeforeClass
   public static void setUp() throws Exception
   {
      VirtualFileSystemUtils.createDefaultProject(PROJECT);
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
         e.printStackTrace();
      }
   }

   /**
    * Test command is not available for commit in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testCommitCommand() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      if (!PROJECT.equals(IDE.PROJECT.EXPLORER.getCurrentProject()))
      {
         IDE.PROJECT.OPEN.openProject(PROJECT);
      }
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Not Git repository:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT));

      //Init repository:
      IDE.GIT.INIT_REPOSITORY.initRepository();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));
      IDE.LOADER.waitClosed();

      //Check commit is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitOpened();

      IDE.GIT.COMMIT.clickCancelButton();
      IDE.GIT.COMMIT.waitClosed();
   }

   /**
    * Test Commit view elements.
    * @throws Exception 
    */
   @Test
   public void testCommitView() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      if (!PROJECT.equals(IDE.PROJECT.EXPLORER.getCurrentProject()))
      {
         IDE.PROJECT.OPEN.openProject(PROJECT);
      }
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      //Open Commit view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitOpened();
      assertTrue(IDE.GIT.COMMIT.isOpened());
      assertFalse(IDE.GIT.COMMIT.isAddFieldChecked());
      assertFalse(IDE.GIT.COMMIT.isCommitButtonEnabled());
      assertTrue(IDE.GIT.COMMIT.isCancelButtonEnabled());

      //Test Commit button state:
      IDE.GIT.COMMIT.typeToMessageField("test");
      assertTrue(IDE.GIT.COMMIT.isCommitButtonEnabled());
      IDE.GIT.COMMIT.typeToMessageField("");
      assertFalse(IDE.GIT.COMMIT.isCommitButtonEnabled());

      IDE.GIT.COMMIT.clickCancelButton();
      IDE.GIT.COMMIT.waitClosed();
   }

   /**
    * Test commit new files.
    * 
    * @throws Exception 
    */
   @Test
   public void testCommitNewFiles() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      if (!PROJECT.equals(IDE.PROJECT.EXPLORER.getCurrentProject()))
      {
         IDE.PROJECT.OPEN.openProject(PROJECT);
      }
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      createFiles();

      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Check status before commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2, 5);
      message = IDE.OUTPUT.getOutputMessage(2);
      List<String> notCommited = IDE.GIT.STATUS.getNotCommited(message);
      Assert.assertEquals(2, notCommited.size());

      //Open Commit view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitOpened();

      //Commit:
      String commitMessage = "First commit";
      IDE.GIT.COMMIT.typeToMessageField(commitMessage);
      IDE.GIT.COMMIT.clickCommitButton();
      IDE.GIT.COMMIT.waitClosed();

      IDE.OUTPUT.waitForMessageShow(3, 5);
      message = IDE.OUTPUT.getOutputMessage(3);
      Assert.assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));

      //Check status after commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(4, 5);
      message = IDE.OUTPUT.getOutputMessage(4);
      Assert.assertTrue(message.contains(Status.Messages.NOTHING_TO_COMMIT));
   }

   /**
    * Test commit edited files.
    * 
    * @throws Exception 
    */
   //@Test
   public void testCommitWithoutAdd() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      if (!PROJECT.equals(IDE.PROJECT.EXPLORER.getCurrentProject()))
      {
         IDE.PROJECT.OPEN.openProject(PROJECT);
      }
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FILE1);
      IDE.EDITOR.typeTextIntoEditor(0, "Some chages");
      IDE.EDITOR.waitFileContentModificationMark(TEST_FILE1);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(TEST_FILE1);
      IDE.EDITOR.closeFile(1);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //Check status before commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(1, 5);
      String message = IDE.OUTPUT.getOutputMessage(1);
      List<String> notUpdated = IDE.GIT.STATUS.getNotUdated(message);
      Assert.assertEquals(1, notUpdated.size());
      Assert.assertTrue(notUpdated.contains(String.format(Status.Messages.MODIFIED, TEST_FILE1)));

      //Open Commit view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitOpened();

      //Commit:
      String commitMessage = "Second commit";
      IDE.GIT.COMMIT.typeToMessageField(commitMessage);
      IDE.GIT.COMMIT.checkAddField();
      IDE.GIT.COMMIT.clickCommitButton();
      IDE.GIT.COMMIT.waitClosed();

      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(2);
      Assert.assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));

      //Check status after commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(3, 5);
      message = IDE.OUTPUT.getOutputMessage(3);
      Assert.assertTrue(message.contains(Status.Messages.NOTHING_TO_COMMIT));
   }

   /**
    * Creates new files.
    * @throws Exception 
    */
   private void createFiles() throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE1);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE2);
   }
}
