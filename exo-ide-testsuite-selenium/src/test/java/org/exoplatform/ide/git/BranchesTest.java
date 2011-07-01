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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 27, 2011 3:23:35 PM anya $
 *
 */
public class BranchesTest extends BaseTest
{
   private static final String TEST_FOLDER = BranchesTest.class.getSimpleName();

   private static final String TEST_FILE = "TestFile";

   private static final String FIRST_BRANCH_FILE = "InFirstBranch";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   private static final String BRANCH1 = "branch1";

   private static final String BRANCH2 = "branch2";

   @BeforeClass
   public static void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(new byte[]{1}, MimeType.GROOVY_SERVICE, URL + TEST_FILE);
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
    * Test Branches command is not available in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testBranchesCommand() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES, false);

      //Not Git repository:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
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

      //Check branches is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitForViewOpened();

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitForViewClosed();
   }

   /**
    * Test Branches view elements.
    * @throws Exception 
    */
   @Test
   public void testBranchesView() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitForViewOpened();
      Assert.assertTrue(IDE.GIT.BRANCHES.isViewComponentsPresent());
      Assert.assertFalse(IDE.GIT.BRANCHES.isCheckoutButtonEnabled());
      Assert.assertFalse(IDE.GIT.BRANCHES.isDeleteButtonEnabled());
      Assert.assertTrue(IDE.GIT.BRANCHES.isCreateButtonEnabled());
      Assert.assertTrue(IDE.GIT.BRANCHES.isCloseButtonEnabled());
      Assert.assertEquals(1, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitForViewClosed();
   }

   /**
    * Test creation of new branch.
    * @throws Exception 
    */
   @Test
   public void testCreateBranch() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitForViewOpened();

      //Create new branch:
      IDE.GIT.BRANCHES.clickCreateButton();
      IDE.GIT.BRANCHES.waitForNewBranchViewOpened();
      IDE.GIT.BRANCHES.typeNewBranchName(BRANCH1);
      IDE.GIT.BRANCHES.clickNewBranchOkButton();
      IDE.GIT.BRANCHES.waitForNewBranchViewClosed();
      IDE.GIT.BRANCHES.waitForBranchesCount(2);
      
      Assert.assertEquals(2, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitForViewClosed();
   }

   /**
    * Test deleting existing branch.
    * @throws Exception 
    */
   @Test
   public void testDeleteBranch() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitForViewOpened();
      Assert.assertEquals(2, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

      //Select checked branch:
      IDE.GIT.BRANCHES.selectBranchByName("master");
      Assert.assertFalse(IDE.GIT.BRANCHES.isDeleteButtonEnabled());

      //Select not checked branch:
      IDE.GIT.BRANCHES.selectBranchByName(BRANCH1);

      //Try delete and click "No"
      IDE.GIT.BRANCHES.clickDeleteButton();
      IDE.ASK_DIALOG.waitForAskDialogOpened();
      Assert.assertEquals(String.format(GIT.Messages.DELETE_BRANCH_QUESTION, "refs/heads/" + BRANCH1),
         IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickNo();
      IDE.ASK_DIALOG.waitForDialogNotPresent();

      //Check branch is not deleted:
      Assert.assertEquals(2, IDE.GIT.BRANCHES.getBranchesCount());

      //Try delete and click "Yes"
      IDE.GIT.BRANCHES.clickDeleteButton();
      IDE.ASK_DIALOG.waitForAskDialogOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitForDialogNotPresent();
      IDE.GIT.BRANCHES.waitForBranchesCount(1);
      
      //Check branch is deleted:
      Assert.assertEquals(1, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));
      Assert.assertFalse(IDE.GIT.BRANCHES.isDeleteButtonEnabled());

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitForViewClosed();
   }

   /**
    * Test checkout branch.
    * @throws Exception 
    */
   @Test
   public void testCheckoutBranch() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitForViewOpened();
      Assert.assertEquals(1, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

      createBranches();

      //Select checked branch:
      IDE.GIT.BRANCHES.selectBranchByName("master");
      Assert.assertFalse(IDE.GIT.BRANCHES.isCheckoutButtonEnabled());

      //Select not checked branch:
      IDE.GIT.BRANCHES.selectBranchByName(BRANCH1);
      Assert.assertTrue(IDE.GIT.BRANCHES.isCheckoutButtonEnabled());

      //Checkout branch:
      IDE.GIT.BRANCHES.clickCheckoutButton();

      IDE.GIT.BRANCHES.waitBranchChecked(BRANCH1);
      Assert.assertFalse(IDE.GIT.BRANCHES.isBranchChecked("master"));

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitForViewClosed();
   }

   /**
    * Test switch branches.
    * @throws Exception 
    */
   @Test
   public void testSwitchBranches() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create file in first branch
      createFile(FIRST_BRANCH_FILE);
      //Add file to index:
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);
      //Commit file:
      IDE.GIT.COMMIT.commit("Comment 1");
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessageText(2);
      Assert.assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitForViewOpened();
      Assert.assertEquals(3, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked(BRANCH1));

      //Select checked branch:
      IDE.GIT.BRANCHES.selectBranchByName(BRANCH2);
      //Checkout branch:
      IDE.GIT.BRANCHES.clickCheckoutButton();
      IDE.GIT.BRANCHES.waitBranchChecked(BRANCH2);
      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitForViewClosed();

      //Check file from first branch is not present:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + FIRST_BRANCH_FILE);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/" + TEST_FILE);
   }

   private void createFile(String fileName) throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(fileName);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + fileName);
      IDE.EDITOR.closeFile(0);
   }

   private void createBranches() throws Exception
   {
      //Create first branch:
      IDE.GIT.BRANCHES.clickCreateButton();
      IDE.GIT.BRANCHES.waitForNewBranchViewOpened();
      IDE.GIT.BRANCHES.typeNewBranchName(BRANCH1);
      IDE.GIT.BRANCHES.clickNewBranchOkButton();
      IDE.GIT.BRANCHES.waitForNewBranchViewClosed();
      waitForLoaderDissapeared();

      //Create second branch:
      IDE.GIT.BRANCHES.clickCreateButton();
      IDE.GIT.BRANCHES.waitForNewBranchViewOpened();
      IDE.GIT.BRANCHES.typeNewBranchName(BRANCH2);
      IDE.GIT.BRANCHES.clickNewBranchOkButton();
      IDE.GIT.BRANCHES.waitForNewBranchViewClosed();
      waitForLoaderDissapeared();
   }
}
