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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 29, 2011 11:55:41 AM anya $
 *
 */
public class RemoveFilesTest extends BaseTest
{
   private static final String PROJECT = RemoveFilesTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   private static final String TEST_FILE3 = "TestFile3";

   private static final String TEST_FOLDER = "testFolder";

   private static final String EMPTY_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/empty-repository.zip";

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(PROJECT, EMPTY_ZIP_PATH);
         VirtualFileSystemUtils.put(new byte[]{1}, MimeType.TEXT_CSS, WS_URL + PROJECT + "/" + TEST_FILE1);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + TEST_FOLDER);
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
    * Test command is not available for removing files in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testRemoveFilesCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      //Check Remove files is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);

      IDE.INFORMATION_DIALOG.waitOpened();
      String message = IDE.INFORMATION_DIALOG.getMessage();
      assertEquals(GIT.Messages.NOTHING_TO_COMMIT, message);
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();
   }

   /**
    * Test the Remove files view.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testRemoveFilesView() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(TEST_FILE2);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE2);

      //Add file to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Remove files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);
      IDE.GIT.REMOVE_FILES.waitOpened();

      assertTrue(IDE.GIT.REMOVE_FILES.isOpened());
      assertEquals(2, IDE.GIT.REMOVE_FILES.getFilesCount());

      IDE.GIT.REMOVE_FILES.clickCancelButton();
      IDE.GIT.REMOVE_FILES.waitClosed();
   }

   /**
    * Test Remove files.
    * 
    * @throws Exception
    */
   @Test
   public void testRemoveFiles() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_FOLDER);

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FOLDER + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE3);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER + "/" + TEST_FILE3);
      IDE.EDITOR.closeFile(TEST_FILE3);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE3);

      //Add folder to index
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Remove files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);
      IDE.GIT.REMOVE_FILES.waitOpened();
      assertEquals(2, IDE.GIT.REMOVE_FILES.getFilesCount());

      //Remove file in sub folder:
      IDE.GIT.REMOVE_FILES.checkFileByName(TEST_FOLDER + "/" + TEST_FILE3);
      IDE.GIT.REMOVE_FILES.clickRemoveButton();
      IDE.GIT.REMOVE_FILES.waitClosed();

      //Check files in Browser tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + TEST_FOLDER));
   }

   /**
    * Test Remove edited file.
    * 
    * @throws Exception
    */
   @Test
   public void testRemoveEditedFiles() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.LOADER.waitClosed();
      
      //Add to index:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open and edit file:
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FILE1);
      IDE.EDITOR.typeTextIntoEditor(0, "something");
      IDE.EDITOR.waitFileContentModificationMark(TEST_FILE1);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(TEST_FILE1);
      IDE.EDITOR.closeFile(TEST_FILE1);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE1);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(1);
      assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Remove files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);
      IDE.GIT.REMOVE_FILES.waitOpened();
      assertEquals(1, IDE.GIT.REMOVE_FILES.getFilesCount());

      //Remove edited file:
      IDE.GIT.REMOVE_FILES.checkFileByName(TEST_FILE1);
      IDE.GIT.REMOVE_FILES.clickRemoveButton();
      IDE.GIT.REMOVE_FILES.waitClosed();

      //Check files in Browser tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER);
      assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + TEST_FILE1));
   }
}
