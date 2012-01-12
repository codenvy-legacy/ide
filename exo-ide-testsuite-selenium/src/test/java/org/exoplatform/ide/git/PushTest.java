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
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 1, 2011 11:08:46 AM anya $
 *
 */
public class PushTest extends BaseTest
{
   private static final String PROJECT = PushTest.class.getSimpleName();

   private static final String TEST_FILE = "test1.html";

   private static final String REMOTE = "remote";

   private static final String TEST_BRANCH = "test";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/push-test.zip";

   private static final String EMPTY_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/empty-repository.zip";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(REMOTE, EMPTY_ZIP_PATH);
         VirtualFileSystemUtils.importZipProject(PROJECT, ZIP_PATH);
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         VirtualFileSystemUtils.delete(WS_URL + REMOTE);
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Test command is not available for remote in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testPushCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE));

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PUSH);

      //Get error message - no remote repositories:
      IDE.WARNING_DIALOG.waitOpened();
      String errorMessage = IDE.WARNING_DIALOG.getWarningMessage();
      Assert.assertEquals(GIT.Messages.NO_REMOTE_REPOSITORIES, errorMessage);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();
   }

   /**
    * Test push view.
    * 
    * @throws Exception
    */
   @Test
   public void testPushView() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PUSH);
      IDE.GIT.PUSH.waitOpened();

      assertEquals("refs/heads/master", IDE.GIT.PUSH.getLocalBranchValue());
      assertEquals("refs/heads/master", IDE.GIT.PUSH.getRemoteBranchValue());
      assertEquals("origin", IDE.GIT.PUSH.getRemoteRepositoryValue());

      assertTrue(IDE.GIT.PUSH.isPushButtonEnabled());
      assertTrue(IDE.GIT.PUSH.isCancelButtonEnabled());

      //Test Push button enabled state:
      IDE.GIT.PUSH.clearRemoteBranch();
      Assert.assertFalse(IDE.GIT.PUSH.isPushButtonEnabled());
      IDE.GIT.PUSH.setRemoteBranch(TEST_BRANCH);
      Assert.assertTrue(IDE.GIT.PUSH.isPushButtonEnabled());

      IDE.GIT.PUSH.clickCancelButton();
      IDE.GIT.PUSH.waitClosed();
   }

   /**
    * Test pushing to remote repository.
    * 
    * @throws Exception
    */
   @Test
   public void testPushToRemote() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PUSH);
      IDE.GIT.PUSH.waitOpened();

      //Push to test branch:
      IDE.GIT.PUSH.setRemoteBranch(TEST_BRANCH);

      IDE.GIT.PUSH.clickPushButton();
      IDE.GIT.PUSH.waitClosed();

      //Check pushed message:
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(String.format(GIT.Messages.PUSH_SUCCESS, "git/" + REPO_NAME + "/" + WS_NAME + "/" + REMOTE), message);

      //Open project with remote repository
      IDE.PROJECT.OPEN.openProject(REMOTE);
      IDE.PROJECT.EXPLORER.waitForItem(REMOTE);
      IDE.LOADER.waitClosed();

      IDE.GIT.BRANCHES.switchBranch(TEST_BRANCH);
      //Necessary for refreshing davfs:
      Thread.sleep(3000);

      //Check file in browser tree
      IDE.PROJECT.EXPLORER.selectItem(REMOTE);
      //Sleep is necessary for file to appear on file system:
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(REMOTE + "/" + TEST_FILE);
   }
}
