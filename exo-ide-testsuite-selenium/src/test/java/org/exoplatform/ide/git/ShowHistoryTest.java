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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 29, 2011 4:49:44 PM anya $
 *
 */
public class ShowHistoryTest extends BaseTest
{
   private static final String INIT_COMMIT_COMMENT = "init";

   private static final String PROJECT = ShowHistoryTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   private static final String COMMIT1 = "Commit 1";

   private static final String COMMIT2 = "Commit 2";

   private static final String EMPTY_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/empty-repository.zip";

   @Before
   public void beforeTest() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(PROJECT, EMPTY_ZIP_PATH);
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
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Test command is not available for show history in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testShowHistoryCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      //Check show history is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();

      IDE.GIT.SHOW_HISTORY.closeView();
   }

   /**
    * Test Show history view elements.
    * @throws Exception 
    */
   @Test
   public void testShowHistoryView() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();
      assertTrue(IDE.GIT.SHOW_HISTORY.isOpened());
      assertTrue(IDE.GIT.SHOW_HISTORY.isChangesInProjectButtonSelected());
      assertTrue(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isChangesOfResourceButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      assertEquals(1, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.closeView();
   }

   /**
    * Test Show history for selected resource.
    * @throws Exception 
    */
   @Test
   public void testRefreshRevisionList() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();
      assertEquals(1, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      //Make commit:
      createFileAndCommit(TEST_FILE1, COMMIT1);
      assertEquals(1, IDE.GIT.SHOW_HISTORY.getRevisionsCount());
      IDE.GIT.SHOW_HISTORY.clickRefreshRevisionListButton();
      IDE.GIT.SHOW_HISTORY.waitForRevisionsCount(2);
      assertEquals(2, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      //Make one more commit:
      createFileAndCommit(TEST_FILE2, COMMIT2);
      assertEquals(2, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.clickRefreshRevisionListButton();
      IDE.GIT.SHOW_HISTORY.waitForRevisionsCount(3);
      assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

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
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      createFileAndCommit(TEST_FILE1, COMMIT1);
      IDE.OUTPUT.clickClearButton();
      createFileAndCommit(TEST_FILE2, COMMIT2);

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();
      IDE.GIT.SHOW_HISTORY.waitForRevisionsCount(3);
      assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      //Select first file on the first commit:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_FILE1);
      IDE.GIT.SHOW_HISTORY.clickChangesOfResourceButton();
      IDE.LOADER.waitClosed();
      assertTrue(IDE.GIT.SHOW_HISTORY.isChangesOfResourceButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isChangesInProjectButtonSelected());
      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.isEmpty());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.contains(TEST_FILE1));
      assertFalse(diffContent.contains(TEST_FILE2));

      //Select second file on the second commit:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_FILE2);
      IDE.GIT.SHOW_HISTORY.clickRefreshRevisionListButton();
      IDE.LOADER.waitClosed();

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertFalse(diffContent.contains(TEST_FILE1));
      assertTrue(diffContent.contains(TEST_FILE2));

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.isEmpty());

      //Changes in whole project
      IDE.GIT.SHOW_HISTORY.clickChangesInProjectButton();
      assertFalse(IDE.GIT.SHOW_HISTORY.isChangesOfResourceButtonSelected());
      assertTrue(IDE.GIT.SHOW_HISTORY.isChangesInProjectButtonSelected());

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertFalse(diffContent.contains(TEST_FILE1));
      assertTrue(diffContent.contains(TEST_FILE2));

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.contains(TEST_FILE1));
      assertFalse(diffContent.contains(TEST_FILE2));

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
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      createFileAndCommit(TEST_FILE1, COMMIT1);
      IDE.OUTPUT.clickClearButton();
      createFileAndCommit(TEST_FILE2, COMMIT2);

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();
      IDE.GIT.SHOW_HISTORY.waitForRevisionsCount(3);
      assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());
      //Check states of diff modes:
      assertTrue(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      IDE.LOADER.waitClosed();
      //Test buttons states
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.contains(TEST_FILE1));
      assertFalse(diffContent.contains(TEST_FILE2));

      //Select init commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(INIT_COMMIT_COMMENT);
      IDE.LOADER.waitClosed();
      //Test buttons states
      assertTrue(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.trim().isEmpty());

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      IDE.LOADER.waitClosed();
      //Test buttons states
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertFalse(diffContent.contains(TEST_FILE1));
      assertTrue(diffContent.contains(TEST_FILE2));
   }

   /**
    * Test diff with index mode.
    * 
    * @throws Exception
    */
   @Test
   public void testDiffWithIndex() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      createFileAndCommit(TEST_FILE1, COMMIT1);
      IDE.OUTPUT.clickClearButton();
      createFileAndCommit(TEST_FILE2, COMMIT2);

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();
      IDE.GIT.SHOW_HISTORY.waitForRevisionsCount(3);
      assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.clickDiffIndexButton();
      //Check states of diff modes:
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      assertTrue(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertTrue(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertFalse(diffContent.contains(TEST_FILE1));
      assertTrue(diffContent.contains(TEST_FILE2));

      //Select init commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(INIT_COMMIT_COMMENT);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertTrue(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.contains(TEST_FILE1));
      assertTrue(diffContent.contains(TEST_FILE2));

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertTrue(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.isEmpty());
   }

   /**
    * Test diff with working tree mode.
    * 
    * @throws Exception
    */
   @Test
   public void testDiffWithWorkingTreeMode() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      createFileAndCommit(TEST_FILE1, COMMIT1);
      IDE.OUTPUT.clickClearButton();
      createFileAndCommit(TEST_FILE2, COMMIT2);

      //Open Show history view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();
      IDE.GIT.SHOW_HISTORY.waitForRevisionsCount(3);
      assertEquals(3, IDE.GIT.SHOW_HISTORY.getRevisionsCount());

      IDE.GIT.SHOW_HISTORY.clickDiffWorkTreeStatusButton();
      //Check states of diff modes:
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffPrevRevisionButtonSelected());
      assertFalse(IDE.GIT.SHOW_HISTORY.isDiffIndexButtonSelected());
      assertTrue(IDE.GIT.SHOW_HISTORY.isDiffWorkTreeButtonSelected());

      //Select first commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT1);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertTrue(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      String diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertFalse(diffContent.contains(TEST_FILE1));
      assertTrue(diffContent.contains(TEST_FILE2));

      //Select init commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(INIT_COMMIT_COMMENT);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertTrue(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.contains(TEST_FILE1));
      assertTrue(diffContent.contains(TEST_FILE2));

      //Select second commit:
      IDE.GIT.SHOW_HISTORY.selectRevisionByComment(COMMIT2);
      IDE.LOADER.waitClosed();
      assertFalse(IDE.GIT.SHOW_HISTORY.isNothingForComparanceState());
      assertFalse(IDE.GIT.SHOW_HISTORY.isCompareWithIndexState());
      assertTrue(IDE.GIT.SHOW_HISTORY.isCompareWithWorkingTree());
      //Check diff content:
      diffContent = IDE.GIT.SHOW_HISTORY.getDiffText();
      assertTrue(diffContent.isEmpty());
   }

   /**
    * Creates new file with pointed name..
    * @throws Exception 
    */
   private void createFileAndCommit(String fileName, String commitMessage) throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, fileName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + fileName);
      IDE.EDITOR.closeFile(fileName);
      IDE.EDITOR.waitTabNotPresent(fileName);

      //Add to index:
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Commit file:
      IDE.GIT.COMMIT.commit(commitMessage);
      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.startsWith(GIT.Messages.COMMIT_SUCCESS));
   }
}
