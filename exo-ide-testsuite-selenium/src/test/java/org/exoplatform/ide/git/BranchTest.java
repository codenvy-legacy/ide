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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 27, 2011 3:23:35 PM anya $
 *
 */
public class BranchTest extends BaseTest
{
   private static final String TEST_FOLDER = BranchTest.class.getSimpleName();

   private static final String REPOSITORY = "repository";

   private static final String FIRST_BRANCH_FILE = "File1.txt";

   private static final String SECOND_BRANCH_FILE = "File2.txt";

   private static final String BRANCH1 = "branch1";

   private static final String BRANCH2 = "branch2";

   private static final String BRANCH3 = "branch3";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/branch-test.zip";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder(ZIP_PATH, WS_URL);
         Thread.sleep(2000);
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
    * Test Branches command is not available in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testBranchesCommand() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES, false);

      //Not Git repository:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.WARNING_DIALOG.waitOpened();
      String message = IDE.WARNING_DIALOG.getWarningMessage();
      Assert.assertEquals(GIT.Messages.NOT_GIT_REPO, message);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      //Select repository:
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      //Check branches is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitOpened();

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitClosed();

   }

   /**
    * Test Branches view elements.
    * @throws Exception 
    */
   @Test
   public void testBranchesView() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitOpened();

      IDE.GIT.BRANCHES.selectBranchByName("master");
      Assert.assertTrue(IDE.GIT.BRANCHES.isOpened());
      Assert.assertFalse(IDE.GIT.BRANCHES.isCheckoutButtonEnabled());
      Assert.assertFalse(IDE.GIT.BRANCHES.isDeleteButtonEnabled());
      Assert.assertTrue(IDE.GIT.BRANCHES.isCreateButtonEnabled());
      Assert.assertTrue(IDE.GIT.BRANCHES.isCloseButtonEnabled());
      Assert.assertEquals(3, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitClosed();
   }

   /**
    * Test creation of new branch.
    * @throws Exception 
    */
   @Test
   public void testCreateBranch() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitOpened();

      //Create new branch:
      IDE.GIT.BRANCHES.clickCreateButton();
      IDE.GIT.BRANCHES.waitNewBranchViewOpened();
      IDE.GIT.BRANCHES.setNewBranchName(BRANCH3);
      IDE.GIT.BRANCHES.clickNewBranchOkButton();
      IDE.GIT.BRANCHES.waitNewBranchViewClosed();
      IDE.GIT.BRANCHES.waitForBranchesCount(4);

      Assert.assertEquals(4, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitClosed();
      Thread.sleep(2000);
   }

   /**
     * Test deleting existing branch.
     * @throws Exception 
     */
   @Test
   public void testDeleteBranch() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitOpened();
      Assert.assertEquals(3, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

      //Select checked branch:
      IDE.GIT.BRANCHES.selectBranchByName("master");
      Assert.assertFalse(IDE.GIT.BRANCHES.isDeleteButtonEnabled());

      //Select not checked branch:
      IDE.GIT.BRANCHES.selectBranchByName(BRANCH1);

      //Try delete and click "No"
      IDE.GIT.BRANCHES.clickDeleteButton();
      IDE.ASK_DIALOG.waitOpened();
      Assert.assertEquals(String.format(GIT.Messages.DELETE_BRANCH_QUESTION, "refs/heads/" + BRANCH1),
         IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickNo();
      IDE.ASK_DIALOG.waitClosed();

      //Check branch is not deleted:
      Assert.assertEquals(3, IDE.GIT.BRANCHES.getBranchesCount());

      //Try delete and click "Yes"
      IDE.GIT.BRANCHES.clickDeleteButton();
      IDE.ASK_DIALOG.waitOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();
      IDE.GIT.BRANCHES.waitForBranchesCount(2);

      //Check branch is deleted:
      Assert.assertEquals(2, IDE.GIT.BRANCHES.getBranchesCount());

      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitClosed();
   }

   /**
    * Test checkout branch.
    * @throws Exception 
    */
   @Test
   public void testCheckoutBranch() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitOpened();
      Assert.assertEquals(3, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertTrue(IDE.GIT.BRANCHES.isBranchChecked("master"));

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
      IDE.GIT.BRANCHES.waitClosed();
   }

   /**
    * Test switch branches.
    * @throws Exception 
    */
   @Test
   public void testSwitchBranches() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      //Open Branches view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitOpened();
      Assert.assertEquals(3, IDE.GIT.BRANCHES.getBranchesCount());
      Assert.assertFalse(IDE.GIT.BRANCHES.isBranchChecked("master"));

      //Check first branch:
      IDE.GIT.BRANCHES.selectBranchByName(BRANCH1);
      IDE.GIT.BRANCHES.clickCheckoutButton();
      IDE.GIT.BRANCHES.waitBranchChecked(BRANCH1);
      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitClosed();

      Thread.sleep(2000);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + FIRST_BRANCH_FILE);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + SECOND_BRANCH_FILE);

      //Check second branch:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      IDE.GIT.BRANCHES.waitOpened();
      IDE.GIT.BRANCHES.selectBranchByName(BRANCH2);
      IDE.GIT.BRANCHES.clickCheckoutButton();
      IDE.GIT.BRANCHES.waitBranchChecked(BRANCH2);
      IDE.GIT.BRANCHES.clickCloseButton();
      IDE.GIT.BRANCHES.waitClosed();

      //Check file from first branch is not present:
      Thread.sleep(2000);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + SECOND_BRANCH_FILE);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + FIRST_BRANCH_FILE);
   }
}
