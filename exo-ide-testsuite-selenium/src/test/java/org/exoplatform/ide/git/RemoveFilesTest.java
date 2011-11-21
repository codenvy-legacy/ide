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
 * @version $Id:  Jun 29, 2011 11:55:41 AM anya $
 *
 */
public class RemoveFilesTest extends BaseTest
{
   private static final String TEST_FOLDER = RemoveFilesTest.class.getSimpleName();

   private static final String TEST_FILE1 = "TestFile1";

   private static final String TEST_FILE2 = "TestFile2";

   private static final String TEST_FILE3 = "TestFile3";

   private static final String TEST_FOLDER2 = "testFolder";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(new byte[]{1}, MimeType.TEXT_CSS, URL + TEST_FILE1);
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER + "/" + TEST_FOLDER2);
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
    * Test command is not available for removing files in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testRemoveFilesCommand() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE, false);

      //Not Git repository:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE, true);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);
      IDE.WARNING_DIALOG.waitOpened();
      String message = IDE.WARNING_DIALOG.getWarningMessage();
      Assert.assertEquals(GIT.Messages.NOT_GIT_REPO, message);
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      //Init repository:
      IDE.GIT.INIT_REPOSITORY.initRepository();
      IDE.OUTPUT.waitForMessageShow(1);
      message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));

      //Check Remove files is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);

      IDE.INFORMATION_DIALOG.waitOpened();
      message = IDE.INFORMATION_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NOTHING_TO_COMMIT, message);
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE2);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);
      IDE.EDITOR.closeFile(0);

      //Add file to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Remove files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);
      IDE.GIT.REMOVE_FILES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.REMOVE_FILES.isViewComponentsPresent());
      Assert.assertEquals(1, IDE.GIT.REMOVE_FILES.getFilesCount());

      IDE.GIT.REMOVE_FILES.clickCancelButton();
      IDE.GIT.REMOVE_FILES.waitForViewClosed();
   }

   /**
    * Test Remove files.
    * 
    * @throws Exception
    */
   @Test
   public void testRemoveFiles() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FOLDER2 + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_FOLDER2 + "/");

      //Create new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(TEST_FILE3);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FOLDER2 + "/" + TEST_FILE3);
      IDE.EDITOR.closeFile(0);

      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Open Remove files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);
      IDE.GIT.REMOVE_FILES.waitForViewOpened();
      Assert.assertEquals(2, IDE.GIT.REMOVE_FILES.getFilesCount());

      //Remove file in sub folder:
      IDE.GIT.REMOVE_FILES.checkFileByName(TEST_FOLDER2 + "/" + TEST_FILE3);
      IDE.GIT.REMOVE_FILES.clickRemoveButton();
      IDE.GIT.REMOVE_FILES.waitForViewClosed();

      //Check files in Browser tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + TEST_FOLDER2 + "/");
   }

   /**
    * Test Remove edited file.
    * 
    * @throws Exception
    */
   @Test
   public void testRemoveEditedFiles() throws Exception
   {
      selenium().refresh();

      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      //Open and edit file:
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + TEST_FILE1, true);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.typeTextIntoEditor(0, "somithing");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      waitForLoaderDissapeared();
      IDE.EDITOR.closeFile(0);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Add folder to index
      IDE.GIT.ADD.addToIndex();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertEquals(GIT.Messages.ADD_SUCCESS, message);

      //Open Remove files view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOVE);
      IDE.GIT.REMOVE_FILES.waitForViewOpened();
      Assert.assertEquals(2, IDE.GIT.REMOVE_FILES.getFilesCount());

      //Remove edited file:
      IDE.GIT.REMOVE_FILES.checkFileByName(TEST_FILE1);
      IDE.GIT.REMOVE_FILES.clickRemoveButton();
      IDE.GIT.REMOVE_FILES.waitForViewClosed();

      //Check files in Browser tree:
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE2);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + TEST_FILE1);
   }
}
