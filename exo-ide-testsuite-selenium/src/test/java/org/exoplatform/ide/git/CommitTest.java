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
   private static final String TEST_FOLDER = CommitTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   @BeforeClass
   public static void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
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
    * Test command is not available for commit in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testCommitCommand() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT, false);

      //Not Git repository:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.ERROR_DIALOG.waitIsOpened();
      String message = IDE.ERROR_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NOT_GIT_REPO, message);
      IDE.ERROR_DIALOG.clickOk();
      IDE.ERROR_DIALOG.waitIsClosed();

      //Init repository:
      IDE.GIT.INIT_REPOSITORY.initRepository();
      IDE.OUTPUT.waitForMessageShow(1);
      message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));

      //Check commit is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitForViewOpened();

      IDE.GIT.COMMIT.clickCancelButton();
      IDE.GIT.COMMIT.waitForViewClosed();
   }

   /**
    * Test Commit view elements.
    * @throws Exception 
    */
   @Test
   public void testCommitView() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Commit view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitForViewOpened();
      Assert.assertTrue(IDE.GIT.COMMIT.isViewComponentsPresent());
      Assert.assertFalse(IDE.GIT.COMMIT.isAddFieldChecked());
      Assert.assertFalse(IDE.GIT.COMMIT.isCommitButtonEnabled());
      Assert.assertTrue(IDE.GIT.COMMIT.isCancelButtonEnabled());

      //Test Commit button state:
      IDE.GIT.COMMIT.typeToMessageField("test");
      Assert.assertTrue(IDE.GIT.COMMIT.isCommitButtonEnabled());
      IDE.GIT.COMMIT.typeToMessageField("");
      Assert.assertFalse(IDE.GIT.COMMIT.isCommitButtonEnabled());

      IDE.GIT.COMMIT.clickCancelButton();
      IDE.GIT.COMMIT.waitForViewClosed();
   }

   /**
    * Test commit new files.
    * 
    * @throws Exception 
    */
   @Test
   public void testCommitNewFiles() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      createFiles();

      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Check status before commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      message = IDE.OUTPUT.getOutputMessageText(2);
      List<String> notCommited = IDE.GIT.STATUS.getNotCommited(message);
      Assert.assertEquals(2, notCommited.size());

      //Open Commit view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitForViewOpened();

      //Commit:
      String commitMessage = "First commit";
      IDE.GIT.COMMIT.typeToMessageField(commitMessage);
      IDE.GIT.COMMIT.clickCommitButton();
      IDE.GIT.COMMIT.waitForViewClosed();

      IDE.OUTPUT.waitForMessageShow(3);
      message = IDE.OUTPUT.getOutputMessageText(3);
      Assert.assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));

      //Check status after commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(4);
      message = IDE.OUTPUT.getOutputMessageText(4);
      Assert.assertTrue(message.contains(Status.Messages.NOTHING_TO_COMMIT));
   }

   /**
    * Test commit edited files.
    * 
    * @throws Exception 
    */
   @Test
   public void testCommitWithoutAdd() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + TEST_FILE1, false);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.typeTextIntoEditor(0, "Some chages");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      waitForLoaderDissapeared();
      IDE.EDITOR.closeFile(0);
      
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Check status before commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      List<String> notUpdated = IDE.GIT.STATUS.getNotUdated(message);
      Assert.assertEquals(1, notUpdated.size());
      Assert.assertTrue(notUpdated.contains(String.format(Status.Messages.MODIFIED, TEST_FILE1)));

      //Open Commit view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      IDE.GIT.COMMIT.waitForViewOpened();

      //Commit:
      String commitMessage = "Second commit";
      IDE.GIT.COMMIT.typeToMessageField(commitMessage);
      IDE.GIT.COMMIT.checkAddField();
      IDE.GIT.COMMIT.clickCommitButton();
      IDE.GIT.COMMIT.waitForViewClosed();

      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessageText(2);
      Assert.assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));

      //Check status after commit:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(3);
      message = IDE.OUTPUT.getOutputMessageText(3);
      Assert.assertTrue(message.contains(Status.Messages.NOTHING_TO_COMMIT));
   }

   /**
    * Creates new files.
    * @throws Exception 
    */
   private void createFiles() throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
      IDE.EDITOR.closeFile(0);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE2);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(0);
   }
}
