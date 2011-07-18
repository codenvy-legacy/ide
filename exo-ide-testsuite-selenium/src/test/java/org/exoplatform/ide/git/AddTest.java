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
   private static final String TEST_FOLDER = AddTest.class.getSimpleName();

   private static final String TEST_ADD_FILE = "TestAddFile";

   private static final String TEST_FILE2 = "TestFile2.txt";

   private static final String TEST_FILE_FOR_UPDATE = "TestFileForUpdate";

   private static final String TEST_ADD_FOLDER = "TestAddFolder";

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder("src/test/resources/org/exoplatform/ide/git/AddTest.zip", WS_URL);
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
    * Test command is not available for adding to index in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testAddCommand() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.ADD, false);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Check Add to index is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.ADD, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitForViewOpened();

      IDE.GIT.ADD.clickCancelButton();
      IDE.GIT.ADD.waitForViewClosed();
   }

   /**
    * Test adding file to index.
    * 
    * @throws Exception
    */
   @Test
   public void testAddFile() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_ADD_FILE);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_ADD_FILE);
      IDE.EDITOR.closeFile(0);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_ADD_FILE);

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that file is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(String.format(Add.Messages.ADD_FILE, TEST_ADD_FILE), addMessage);

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitForViewClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessageText(1));

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2);
      String statusMessage = IDE.OUTPUT.getOutputMessageText(2);
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
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create new folder:
      IDE.NAVIGATION.createFolder(TEST_ADD_FOLDER);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_ADD_FOLDER + "/");

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_ADD_FOLDER + "/");

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that folder is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(String.format(Add.Messages.ADD_FOLDER, TEST_ADD_FOLDER), addMessage);

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitForViewClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessageText(1));
   }

   /**
    * Test adding all changes in repository to index.
    * 
    * @throws Exception
    */
   @Test
   public void testAllChanges() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE2);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(0);

      //Select Git work directory:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that folder is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(Add.Messages.ADD_ALL_CHANGES, addMessage);

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitForViewClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessageText(1));

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2);
      String statusMessage = IDE.OUTPUT.getOutputMessageText(2);
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
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE_FOR_UPDATE);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE_FOR_UPDATE);
      IDE.EDITOR.closeFile(0);

      //Select Git work directory:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Add file to Git index:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      IDE.GIT.ADD.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.ADD.isAddButtonEnabled());
      Assert.assertTrue(IDE.GIT.ADD.isCancelButtonEnabled());

      //Check Add message, that folder is gonna to be added:
      String addMessage = IDE.GIT.ADD.getAddMessage();
      Assert.assertEquals(Add.Messages.ADD_ALL_CHANGES, addMessage);

      //Make Update field checked:
      IDE.GIT.ADD.checkUpdateField();
      Assert.assertTrue(IDE.GIT.ADD.isUpdateFieldChecked());

      IDE.GIT.ADD.clickAddButton();
      IDE.GIT.ADD.waitForViewClosed();

      //Check successfully added:
      IDE.OUTPUT.waitForMessageShow(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, IDE.OUTPUT.getOutputMessageText(1));

      //Check status:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.STATUS);
      IDE.OUTPUT.waitForMessageShow(2);
      String statusMessage = IDE.OUTPUT.getOutputMessageText(2);

      //Get list of untracked files:
      List<String> untrackedFiles = IDE.GIT.STATUS.getUntracked(statusMessage);
      Assert.assertEquals(1, untrackedFiles.size());
      //Check list contains added files:
      Assert.assertTrue(untrackedFiles.contains(TEST_FILE_FOR_UPDATE));
   }
}
