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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 1, 2011 3:26:43 PM anya $
 *
 */
public class PullTest extends BaseTest
{
   private static final String PROJECT = PullTest.class.getSimpleName();

   private static final String FOLDER1 = "folder1";

   private static final String FILE1 = "file1.txt";

   private static final String FILE2 = "file2.txt";

   private static final String REMOTE = "remote";

   private static final String BRANCH = "master";

   private static final String REMOTE_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/pull-test.zip";

   private static final String EMPTY_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/empty-repository.zip";

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(REMOTE, REMOTE_ZIP_PATH);
         VirtualFileSystemUtils.importZipProject(PROJECT, EMPTY_ZIP_PATH);
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
         VirtualFileSystemUtils.delete(WS_URL + REMOTE);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test command is not available for pull in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testPullCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      //Check Pull command is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PULL);

      //Get error message - no remote repositories:
      IDE.WARNING_DIALOG.waitOpened();
      String errorMessage = IDE.WARNING_DIALOG.getWarningMessage();
      Assert.assertEquals(GIT.Messages.NO_REMOTE_REPOSITORIES, errorMessage);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
   }

   /**
    * Test Pull view.
    * 
    * @throws Exception
    */
   @Test
   public void testPullView() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PULL);
      IDE.GIT.PULL.waitOpened();

      assertEquals(BRANCH, IDE.GIT.PULL.getLocalBranchValue());
      assertEquals(BRANCH, IDE.GIT.PULL.getRemoteBranchValue());
      assertEquals("origin", IDE.GIT.PULL.getRemoteRepositoryValue());

      assertTrue(IDE.GIT.PULL.isPullButtonEnabled());
      assertTrue(IDE.GIT.PULL.isCancelButtonEnabled());

      //Test Pull button enabled state:
      IDE.GIT.PULL.clearLocalBranchValue();
      assertTrue(IDE.GIT.PULL.isPullButtonEnabled());

      IDE.GIT.PULL.clearRemoteBranchValue();
      assertFalse(IDE.GIT.PULL.isPullButtonEnabled());
      IDE.GIT.PULL.typeToRemoteBranch(BRANCH);
      assertTrue(IDE.GIT.PULL.isPullButtonEnabled());

      IDE.GIT.PULL.clickCancelButton();
      IDE.GIT.PULL.waitClosed();
   }

   /**
    * Test pulling from remote repository.
    * 
    * @throws Exception
    */
   @Test
   public void testPullFromRemote() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + FOLDER1));

      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PULL);
      IDE.GIT.PULL.waitOpened();

      //Pull from remote:
      IDE.GIT.PULL.typeToRemoteBranch(BRANCH);
      IDE.GIT.PULL.typeToLocalBranch(BRANCH);

      IDE.GIT.PULL.waitPullButtonEnabled();
      IDE.GIT.PULL.clickPullButton();
      IDE.GIT.PULL.waitClosed();

      //Check pulled message:
      IDE.OUTPUT.waitForMessageShow(1, 15);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(String.format(GIT.Messages.PULL_SUCCESS, "git/" + REPO_NAME + "/" + WS_NAME + "/" + REMOTE), message);

      //Check file in browser tree
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //Sleep is necessary for file to appear on file system:
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER1);

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1 + "/" + FILE1);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE2);
   }
}
