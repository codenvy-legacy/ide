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

import static org.junit.Assert.assertTrue;

import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.Add;
import org.exoplatform.ide.git.core.GIT;
import org.exoplatform.ide.git.core.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 23, 2011 6:01:47 PM anya $
 *
 */
public class AddTest extends BaseTest
{
   private static final String PROJECT = AddTest.class.getSimpleName();

   private static final String TEST_ADD_FILE = "TestAddFile";

   private static final String TEST_FILE2 = "TestFile2.txt";

   private static final String TEST_FILE_FOR_UPDATE = "TestFileForUpdate";

   private static final String TEST_ADD_FOLDER = "TestAddFolder";

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(PROJECT, "src/test/resources/org/exoplatform/ide/git/AddTest.zip");
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
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test command is not available for adding to index in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testAddCommand() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      waitForLoaderDissapeared();
      
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //Check Add to index is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.ADD));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitOpened();

      IDE.GIT.ADD.clickCancelButton();
      IDE.GIT.ADD.waitClosed();
   }

   /**
    * Test adding file to index.
    * 
    * @throws Exception
    */
   @Test
   public void testAddFile() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_ADD_FILE);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_ADD_FILE);
      IDE.EDITOR.closeFile(TEST_ADD_FILE);
      IDE.EDITOR.waitTabNotPresent(TEST_ADD_FILE);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_ADD_FILE);

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that file is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(String.format(Add.Messages.ADD_FILE, TEST_ADD_FILE), addMessage);

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1, 6);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessage(1));

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2, 6);
      String statusMessage = IDE.OUTPUT.getOutputMessage(2);
      //Get list of files in index:
      List<String> addedFiles = IDE.GIT.STATUS.getNotCommited(statusMessage);
      Assert.assertEquals(1, addedFiles.size());
      //Check list contains added file:
      Assert.assertTrue(addedFiles.contains(String.format(Status.Messages.NEW_FILE, TEST_ADD_FILE)));
   }

   /**
    * Test adding folder to index.
    * 
    * @throws Exception
    */
   @Test
   public void testAddFolder() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Create new folder:
      IDE.FOLDER.createFolder(TEST_ADD_FOLDER);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_ADD_FOLDER);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_ADD_FOLDER);

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that folder is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(String.format(Add.Messages.ADD_FOLDER, TEST_ADD_FOLDER), addMessage);

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1, 6);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessage(1));
   }

   /**
    * Test adding all changes in repository to index.
    * 
    * @throws Exception
    */
   @Test
   public void testAllChanges() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(TEST_FILE2);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE2);

      //Select Git work directory:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that folder is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(Add.Messages.ADD_ALL_CHANGES, addMessage);

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1, 6);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessage(1));

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2, 6);
      String statusMessage = IDE.OUTPUT.getOutputMessage(2);
      //Get list of files in index:
      List<String> addedFiles = IDE.GIT.STATUS.getNotCommited(statusMessage);
      Assert.assertEquals(1, addedFiles.size());
      Assert.assertTrue(addedFiles.contains(String.format(Status.Messages.NEW_FILE, TEST_FILE2)));
   }

   /**
    * Test adding only updates, not new files.
    * 
    * @throws Exception
    */
   @Test
   public void testAddOnlyUpdate() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE_FOR_UPDATE);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE_FOR_UPDATE);
      IDE.EDITOR.closeFile(TEST_FILE_FOR_UPDATE);

      //Select Git work directory:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that folder is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(Add.Messages.ADD_ALL_CHANGES, addMessage);

      //Make Update field checked:
      IDE.GIT.ADD.checkUpdateField();
      Assert.assertTrue(IDE.GIT.ADD.isUpdateFieldChecked());

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1, 6);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessage(1));

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2, 6);
      String statusMessage = IDE.OUTPUT.getOutputMessage(2);

      //Get list of untracked files:
      List<String> untrackedFiles = IDE.GIT.STATUS.getUntracked(statusMessage);
      Assert.assertEquals(1, untrackedFiles.size());
      //Check list contains added files:
      Assert.assertTrue(untrackedFiles.contains(TEST_FILE_FOR_UPDATE));
   }
}
