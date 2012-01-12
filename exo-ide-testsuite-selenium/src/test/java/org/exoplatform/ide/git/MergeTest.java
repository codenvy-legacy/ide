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
   private static final String PROJECT = MergeTest.class.getSimpleName();

   private static final String TEST_FILE = "File.html";

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
         VirtualFileSystemUtils.importZipProject(PROJECT, "src/test/resources/org/exoplatform/ide/git/merge-test.zip");
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void afterTest()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * @throws Exception
    */
   @Test
   public void testMergeCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //Check Merge is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.MERGE));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitOpened();

      IDE.GIT.MERGE.clickCancelButton();
      IDE.GIT.MERGE.waitClosed();
   }

   @Test
   public void testMergeView() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitOpened();
      Assert.assertTrue(IDE.GIT.MERGE.isOpened());
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
      assertTrue(IDE.GIT.MERGE.isMergeButtonEnabled());

      //Select not the branch:
      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      assertFalse(IDE.GIT.MERGE.isMergeButtonEnabled());

      IDE.GIT.MERGE.clickCancelButton();
      IDE.GIT.MERGE.waitClosed();
   }

   @Test
   public void testMerge() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitOpened();

      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      IDE.GIT.MERGE.doubleClickReference(LOCAL_BRANCHES);

      IDE.GIT.MERGE.waitRererenceVisible(BRANCH1);
      //Select first branch:
      IDE.GIT.MERGE.selectReference(BRANCH1);

      IDE.GIT.MERGE.clickMergeButton();
      IDE.GIT.MERGE.waitClosed();

      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(message.contains(Merge.Messages.FAST_FORWARD));
      assertTrue(message.contains(Merge.Messages.MERGED_COMMITS));
      assertTrue(message.contains(Merge.Messages.NEW_HEAD_COMMIT));

      //Check file appeared:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE2);
      IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + TEST_FILE);
      IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + TEST_FILE2);
   }

   @Test
   public void testMergeConflict() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      IDE.PROJECT.EXPLORER.openItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE);

      //Modify file:
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.typeTextIntoEditor(0, "some");
      IDE.EDITOR.waitFileContentModificationMark(TEST_FILE);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(TEST_FILE);
      IDE.EDITOR.closeFile(TEST_FILE);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE);

      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      IDE.GIT.COMMIT.commit("change");
      IDE.OUTPUT.waitForMessageShow(2, 10);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitOpened();

      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      IDE.GIT.MERGE.doubleClickReference(LOCAL_BRANCHES);

      IDE.GIT.MERGE.waitRererenceVisible(BRANCH2);
      //Select branch:
      IDE.GIT.MERGE.selectReference(BRANCH2);

      IDE.GIT.MERGE.clickMergeButton();
      IDE.GIT.MERGE.waitClosed();

      IDE.OUTPUT.waitForMessageShow(3, 10);
      String message = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(message.contains(Merge.Messages.CONFLICTING));
      assertTrue(message.contains(String.format(Merge.Messages.CONFLICTS, "- " + TEST_FILE)));
   }

   @Test
   public void testMergeUptoDate() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.MERGE);
      IDE.GIT.MERGE.waitOpened();

      IDE.GIT.MERGE.selectReference(LOCAL_BRANCHES);
      IDE.GIT.MERGE.doubleClickReference(LOCAL_BRANCHES);

      IDE.GIT.MERGE.waitRererenceVisible(BRANCH3);
      //Select branch:
      IDE.GIT.MERGE.selectReference(BRANCH3);

      IDE.GIT.MERGE.clickMergeButton();
      IDE.GIT.MERGE.waitClosed();

      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertTrue(message.contains(Merge.Messages.UP_TO_DATE));
   }
}
