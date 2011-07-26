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
   private static final String TEST_FOLDER = PushTest.class.getSimpleName();

   private static final String TEST_FILE = "test1.html";

   private static final String NOT_GIT = "NotGit";

   private static final String REPOSITORY = "repository";

   private static final String REMOTE = "remote";

   private static final String TEST_BRANCH = "test";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/push-test.zip";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder(ZIP_PATH, WS_URL);
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER + "/" + NOT_GIT);
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
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test command is not available for remote in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testPushCommand() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      //Not Git repository:
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + NOT_GIT + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + NOT_GIT + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PUSH);
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

      //Check Push command is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PUSH);

      //Get error message - no remote repositories:
      IDE.ERROR_DIALOG.waitIsOpened();
      String errorMessage = IDE.ERROR_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NO_REMOTE_REPOSITORIES, errorMessage);
      IDE.ERROR_DIALOG.clickOk();
      IDE.ERROR_DIALOG.waitIsClosed();
   }

   /**
    * Test push view.
    * 
    * @throws Exception
    */
   @Test
   public void testPushView() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
         + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PUSH);
      IDE.GIT.PUSH.waitForViewOpened();

      Assert.assertEquals("refs/heads/master", IDE.GIT.PUSH.getLocalBranchValue());
      Assert.assertEquals("refs/heads/master", IDE.GIT.PUSH.getRemoteBranchValue());
      Assert.assertEquals("origin", IDE.GIT.PUSH.getRemoteRepositoryValue());

      Assert.assertTrue(IDE.GIT.PUSH.isPushButtonEnabled());
      Assert.assertTrue(IDE.GIT.PUSH.isCancelButtonEnabled());

      //Test Push button enabled state:
      IDE.GIT.PUSH.typeToRemoteBranch("");
      Assert.assertFalse(IDE.GIT.PUSH.isPushButtonEnabled());
      IDE.GIT.PUSH.typeToRemoteBranch(TEST_BRANCH);
      Assert.assertTrue(IDE.GIT.PUSH.isPushButtonEnabled());

      IDE.GIT.PUSH.clickCancelButton();
      IDE.GIT.PUSH.waitForViewClosed();
   }

   /**
    * Test pushing to remote repository.
    * 
    * @throws Exception
    */
   @Test
   public void testPushToRemote() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REMOTE + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/" + REMOTE + "/");
      waitForLoaderDissapeared();
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + REMOTE + "/" + TEST_FILE);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
         + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PUSH);
      IDE.GIT.PUSH.waitForViewOpened();

      //Push to test branch:
      IDE.GIT.PUSH.typeToRemoteBranch(TEST_BRANCH);

      IDE.GIT.PUSH.clickPushButton();
      IDE.GIT.PUSH.waitForViewClosed();

      //Check pushed message:
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(String.format(GIT.Messages.PUSH_SUCCESS, "git/" + REPO_NAME + "/" + WS_NAME + "/"
         + TEST_FOLDER + "/" + REMOTE), message);

      //Switch to test branch
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REMOTE + "/");
      IDE.GIT.BRANCHES.switchBranch(TEST_BRANCH);

      //Check file in browser tree
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REMOTE + "/");
      //Sleep is necessary for file to appear on file system:
      Thread.sleep(3000);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      waitForLoaderDissapeared();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REMOTE + "/" + TEST_FILE);
   }
}
