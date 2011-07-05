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
import org.exoplatform.ide.git.core.ShowHistory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 29, 2011 4:49:44 PM anya $
 *
 */
public class ShowHistoryTest extends BaseTest
{
   private static final String INIT_COMMIT_COMMENT = "init";

   private static final String TEST_FOLDER = ShowHistoryTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   private static final String COMMIT1 = "Commit 1";

   private static final String COMMIT2 = "Commit 2";

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
    * Test command is not available for show history in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testShowHistoryCommand() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY, false);

      //Not Git repository:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
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

      //Check show history is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitForViewOpened();

      IDE.GIT.SHOW_HISTORY.closeView();
   }

   /**
    * Test Show history view elements.
    * @throws Exception 
    */
   @Test
   public void testShowHistoryView() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitForViewOpened();
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isViewComponentsPresent());
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isChangesInProjectButtonSelected());
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isChangesOfResourceButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      Assert.assertEquals(1, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.closeView();
   }

   /**
    * Test Show history for selected resource.
    * @throws Exception 
    */
   @Test
   public void testRefreshRevisionList() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitForViewOpened();
      Assert.assertEquals(1, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      //Make commit:
      createFileAndCommit(TEST_FILE1, COMMIT1);
      Assert.assertEquals(1, IDE.GIT.SHOW_HISTORY.getRevisionsCount());
      IDE.GIT.SHOW_HISTORY.clickRefreshRevisionListButton();
      IDE.GIT.SHOW_HISTORY.waitForCommitsCount(2);
      Assert.assertEquals(2, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      //Make one more commit:
      createFileAndCommit(TEST_FILE2, COMMIT2);
      Assert.assertEquals(2, IDE.GIT.SHOW_HISTORY.getRevisionsCount());
      Thread.sleep(2000);
      
      IDE.GIT.SHOW_HISTORY.clickRefreshRevisionListButton();
      IDE.GIT.SHOW_HISTORY.waitForCommitsCount(3);
      Assert.assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.closeView();
   }

   /**
    * Test diff changes in whole project and of the selected resource.
    * 
    * @throws Exception
    */
   @Test
   public void testChangesMode() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitForViewOpened();
      IDE.GIT.SHOW_HISTORY.waitForCommitsCount(3);
      Assert.assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      //Select first file on the first commit:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
      IDE.GIT.SHOW_HISTORY.clickChangesOfResourceButton();
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isChangesOfResourceButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isChangesInProjectButtonSelected());
      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.isEmpty());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.contains(TEST_FILE1));
      Assert.assertFalse(diffContent.contains(TEST_FILE2));

      //Select second file on the second commit:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);
      IDE.GIT.SHOW_HISTORY.clickRefreshRevisionListButton();

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertFalse(diffContent.contains(TEST_FILE1));
      Assert.assertTrue(diffContent.contains(TEST_FILE2));

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.isEmpty());

      //Changes in whole project
      IDE.GIT.SHOW_HISTORY.clickChangesInProjectButton();
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isChangesOfResourceButtonSelected());
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isChangesInProjectButtonSelected());

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertFalse(diffContent.contains(TEST_FILE1));
      Assert.assertTrue(diffContent.contains(TEST_FILE2));

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.contains(TEST_FILE1));
      Assert.assertFalse(diffContent.contains(TEST_FILE2));

      IDE.GIT.SHOW_HISTORY.closeView();
   }

   /**
    * Test diff with previous version mode.
    * 
    * @throws Exception
    */
   @Test
   public void testDiffWithPrevVersion() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitForViewOpened();
      IDE.GIT.SHOW_HISTORY.waitForCommitsCount(3);
      Assert.assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());
      //Check states of diff modes:
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      waitForLoaderDissapeared();
      //Test buttons states
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.contains(TEST_FILE1));
      Assert.assertFalse(diffContent.contains(TEST_FILE2));

      //Select init commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(INIT_COMMIT_COMMENT);
      waitForLoaderDissapeared();
      //Test buttons states
      Assert.assertTrue(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.isEmpty());

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      waitForLoaderDissapeared();
      //Test buttons states
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertFalse(diffContent.contains(TEST_FILE1));
      Assert.assertTrue(diffContent.contains(TEST_FILE2));
   }

   /**
    * Test diff with index mode.
    * 
    * @throws Exception
    */
   @Test
   public void testDiffWithIndex() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitForViewOpened();
      IDE.GIT.SHOW_HISTORY.waitForCommitsCount(3);
      Assert.assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.clickDiffIndexButton();
      //Check states of diff modes:
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      waitForLoaderDissapeared();
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertTrue(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertFalse(diffContent.contains(TEST_FILE1));
      Assert.assertTrue(diffContent.contains(TEST_FILE2));

      //Select init commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(INIT_COMMIT_COMMENT);
      waitForLoaderDissapeared();
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertTrue(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.contains(TEST_FILE1));
      Assert.assertTrue(diffContent.contains(TEST_FILE2));

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      waitForLoaderDissapeared();
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertTrue(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.isEmpty());
   }

   /**
    * Test diff with working tree mode.
    * 
    * @throws Exception
    */
   @Test
   public void testDiffWithWorkingTreeMode() throws Exception
   {
      selenium.refresh();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitForViewOpened();
      IDE.GIT.SHOW_HISTORY.waitForCommitsCount(3);
      Assert.assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.clickDiffWorkTreeStatusButton();
      //Check states of diff modes:
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      Assert.assertFalse(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      Assert.assertTrue(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      waitForLoaderDissapeared();
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertTrue(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertFalse(diffContent.contains(TEST_FILE1));
      Assert.assertTrue(diffContent.contains(TEST_FILE2));

      //Select init commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(INIT_COMMIT_COMMENT);
      waitForLoaderDissapeared();
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertTrue(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.contains(TEST_FILE1));
      Assert.assertTrue(diffContent.contains(TEST_FILE2));

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      waitForLoaderDissapeared();
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.NOTHING_FOR_COMPARANCE));
      Assert.assertFalse(selenium.isTextPresent(ShowHistory.Locators.INDEX_STATE));
      Assert.assertTrue(selenium.isTextPresent(ShowHistory.Locators.WORKING_TREE_STATE));
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      Assert.assertTrue(diffContent.isEmpty());
   }

   /**
    * Creates new file with pointed name..
    * @throws Exception 
    */
   private void createFileAndCommit(String fileName, String commitMessage) throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(fileName);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + fileName);
      IDE.EDITOR.closeFile(0);

      //Add to index:
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Commit file:
      IDE.GIT.COMMIT.commit(commitMessage);
      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessageText(2);
      Assert.assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));
   }

}
