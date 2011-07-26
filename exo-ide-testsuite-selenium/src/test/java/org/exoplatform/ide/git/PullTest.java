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
 * @version $Id:  Jul 1, 2011 3:26:43 PM anya $
 *
 */
public class PullTest extends BaseTest
{
   private static final String TEST_FOLDER = PullTest.class.getSimpleName();

   private static final String FOLDER1 = "folder1";

   private static final String FILE1 = "file1.txt";

   private static final String FILE2 = "file2.txt";

   private static final String NOT_GIT = "NotGit";

   private static final String REPOSITORY = "repository";

   private static final String REMOTE = "remote";

   private static final String BRANCH = "master";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/pull-test.zip";

   @Before
   public void beforeTest()
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
   public void afterTest()
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
    * Test command is not available for pull in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testPullCommand() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      //Not Git repository:
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + NOT_GIT + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + NOT_GIT + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PULL);
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

      //Check Pull command is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PULL);

      //Get error message - no remote repositories:
      IDE.ERROR_DIALOG.waitIsOpened();
      String errorMessage = IDE.ERROR_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NO_REMOTE_REPOSITORIES, errorMessage);
      IDE.ERROR_DIALOG.clickOk();
      IDE.ERROR_DIALOG.waitIsClosed();
   }

   /**
    * Test Pull view.
    * 
    * @throws Exception
    */
   @Test
   public void testPullView() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
         + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PULL);
      IDE.GIT.PULL.waitForViewOpened();

      Assert.assertEquals(BRANCH, IDE.GIT.PULL.getLocalBranchValue());
      Assert.assertEquals(BRANCH, IDE.GIT.PULL.getRemoteBranchValue());
      Assert.assertEquals("origin", IDE.GIT.PULL.getRemoteRepositoryValue());

      Assert.assertTrue(IDE.GIT.PULL.isPullButtonEnabled());
      Assert.assertTrue(IDE.GIT.PULL.isCancelButtonEnabled());

      //Test Pull button enabled state:
      IDE.GIT.PULL.typeToLocalBranch("");
      Assert.assertTrue(IDE.GIT.PULL.isPullButtonEnabled());

      IDE.GIT.PULL.typeToRemoteBranch("");
      Assert.assertFalse(IDE.GIT.PULL.isPullButtonEnabled());
      IDE.GIT.PULL.typeToRemoteBranch(BRANCH);
      Assert.assertTrue(IDE.GIT.PULL.isPullButtonEnabled());

      IDE.GIT.PULL.clickCancelButton();
      IDE.GIT.PULL.waitForViewClosed();
   }

   /**
    * Test pulling from remote repository.
    * 
    * @throws Exception
    */
   @Test
   public void testPullFromRemote() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      waitForLoaderDissapeared();
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + FOLDER1);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.GIT.REMOTES.addRemoteRepository("origin", GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
         + "/" + REMOTE);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.PULL);
      IDE.GIT.PULL.waitForViewOpened();

      //Pull from remote:
      IDE.GIT.PULL.typeToRemoteBranch(BRANCH);
      IDE.GIT.PULL.typeToLocalBranch(BRANCH);

      IDE.GIT.PULL.clickPullButton();
      IDE.GIT.PULL.waitForViewClosed();

      //Check pulled message:
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(String.format(GIT.Messages.PULL_SUCCESS, "git/" + REPO_NAME + "/" + WS_NAME + "/"
         + TEST_FOLDER + "/" + REMOTE), message);

      //Check file in browser tree
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      //Sleep is necessary for file to appear on file system:
      Thread.sleep(3000);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      waitForLoaderDissapeared();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + FOLDER1 + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + FOLDER1 + "/");

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + FOLDER1 + "/" + FILE1);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + FILE2);
   }
}
