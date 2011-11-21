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
import org.exoplatform.ide.git.core.Merge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 22, 2011 12:48:26 PM anya $
 *
 */
public class MergeTest extends BaseTest
{
   private static final String TEST_FOLDER = MergeTest.class.getSimpleName();

   private static final String TEST_FILE = "File.html";

   private static final String REPOSITORY = "repository";

   private static final String TEST_FILE2 = "branch1.xml";

   private static final String BRANCH1 = "branch1";

   private static final String BRANCH2 = "branch2";

   private static final String BRANCH3 = "branch3";

   private static final String LOCAL_BRANCHES = "Local Branches";

   private static final String REMOTE_BRANCHES = "Remote Branches";

   @Before
   public void beforeTest() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder("src/test/resources/org/exoplatform/ide/git/merge-test.zip", WS_URL);
         Thread.sleep(2000);
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
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @throws Exception
    */
   @Test
   public void testMergeCommand() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.MERGE, false);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Check Merge is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.MERGE, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);

      //Not Git repository:
      IDE.WARNING_DIALOG.waitOpened();
      String message = IDE.WARNING_DIALOG.getWarningMessage();
      Assert.assertEquals(GIT.Messages.NOT_GIT_REPO, message);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitForViewOpened();

      IDE.GIT.MERGE.clickCancelButton();
      IDE.GIT.MERGE.waitForViewClosed();
   }

   @Test
   public void testMergeView() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitForViewOpened();
      Assert.assertTrue(IDE.GIT.MERGE.isViewComponentsPresent());
      Assert.assertFalse(IDE.GIT.MERGE.isMergeButtonEnabled());

      Assert.assertTrue(IDE.GIT.MERGE.isRererencePresent(LOCAL_BRANCHES));
      Assert.assertTrue(IDE.GIT.MERGE.isRererencePresent(REMOTE_BRANCHES));
      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      IDE.GIT.MERGE.doubleClickReference(LOCAL_BRANCHES);

      IDE.GIT.MERGE.waitRererenceVisible(BRANCH1);
      Assert.assertTrue(IDE.GIT.MERGE.isRererencePresent(BRANCH1));
      Assert.assertTrue(IDE.GIT.MERGE.isRererencePresent(BRANCH2));
      Assert.assertTrue(IDE.GIT.MERGE.isRererencePresent(BRANCH3));

      //Select branch:
      IDE.GIT.MERGE.selectReference(BRANCH1);
      Assert.assertTrue(IDE.GIT.MERGE.isMergeButtonEnabled());

      //Select not the branch:
      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      Assert.assertFalse(IDE.GIT.MERGE.isMergeButtonEnabled());

      IDE.GIT.MERGE.clickCancelButton();
      IDE.GIT.MERGE.waitForViewClosed();
   }

   @Test
   public void testMerge() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitForViewOpened();

      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      IDE.GIT.MERGE.doubleClickReference(LOCAL_BRANCHES);

      IDE.GIT.MERGE.waitRererenceVisible(BRANCH1);
      //Select first branch:
      IDE.GIT.MERGE.selectReference(BRANCH1);

      IDE.GIT.MERGE.clickMergeButton();
      IDE.GIT.MERGE.waitForViewClosed();

      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(message.contains(Merge.Messages.FAST_FORWARD));
      Assert.assertTrue(message.contains(Merge.Messages.MERGED_COMMITS));
      Assert.assertTrue(message.contains(Merge.Messages.NEW_HEAD_COMMIT));

      //Check file appeared:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      Thread.sleep(2000);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + TEST_FILE2);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + TEST_FILE);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + TEST_FILE2);
   }

   @Test
   public void testMergeConfilct() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      //Change file:
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(
         WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/" + TEST_FILE, false);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.typeTextIntoEditor(0, "some");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      waitForLoaderDissapeared();
      IDE.EDITOR.closeFile(0);
      IDE.GIT.ADD.addToIndex();
      IDE.GIT.COMMIT.commit("change");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitForViewOpened();

      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      IDE.GIT.MERGE.doubleClickReference(LOCAL_BRANCHES);

      IDE.GIT.MERGE.waitRererenceVisible(BRANCH2);
      //Select branch:
      IDE.GIT.MERGE.selectReference(BRANCH2);

      IDE.GIT.MERGE.clickMergeButton();
      IDE.GIT.MERGE.waitForViewClosed();

      IDE.OUTPUT.waitForMessageShow(3);
      String message = IDE.OUTPUT.getOutputMessageText(3);
      Assert.assertTrue(message.contains(Merge.Messages.CONFLICTING));
      Assert.assertTrue(message.contains(String.format(Merge.Messages.CONFLICTS, "- " + TEST_FILE)));
   }

   @Test
   public void testMergeUptoDate() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitForViewOpened();

      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      IDE.GIT.MERGE.doubleClickReference(LOCAL_BRANCHES);

      IDE.GIT.MERGE.waitRererenceVisible(BRANCH3);
      //Select branch:
      IDE.GIT.MERGE.selectReference(BRANCH3);

      IDE.GIT.MERGE.clickMergeButton();
      IDE.GIT.MERGE.waitForViewClosed();

      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(message.contains(Merge.Messages.UP_TO_DATE));
   }
}
