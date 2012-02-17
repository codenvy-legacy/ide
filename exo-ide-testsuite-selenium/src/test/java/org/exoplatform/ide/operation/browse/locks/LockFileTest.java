/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.operation.browse.locks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.gwt.user.client.Command;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Check the work of Lock/Unlock feature.
 * 
 * Test is Lock/Unlock button correctly changes state,
 * while changing tabs in editor.
 * 
 * Test is Lick/Unlock button saves its state after refresh.
 * 
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2010 $
 *
 */
public class LockFileTest extends LockFileAbstract
{

   private static String PROJECT = LockFileTest.class.getSimpleName();

   private static String FOLDER_NAME = LockFileTest.class.getSimpleName() + "_dir";

   private static final String FILE_NAME_1 = "file-" + LockFileTest.class.getSimpleName() + "_1";

   private static final String FILE_NAME_2 = "file-" + LockFileTest.class.getSimpleName() + "_2";

   @Before
   public void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testLockFileManually() throws Exception
   {

      //--------------------------------------------------------------------------------
      //step 1 open project, select folder, check state lock menu on toolbar and Edit
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME);
      assertFalse(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.LOCK_FILE));
      IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.LOCK_FILE);

      //step 2 open xml file and chek state of menu
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabPresent(1);

      //need for parsing text in editor and redraw all menu edit
      Thread.sleep(1000);
      checkAllLockButonNotActive();

      IDE.EDITOR.clickCloseEditorButton(0);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent("Welcome");

      //step 2 save xml file and check button state
      IDE.EDITOR.saveAs(0, FILE_NAME_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_1);
      checkAllLockButtonIsActive();

      //step 3 lock file and check state buttons  
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.LOCK_FILE);
      IDE.LOADER.waitClosed();
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.Edit.UNLOCK_FILE);
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.UNLOCK_FILE));
      IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNLOCK_FILE);

      //step 4 open html file and chek state of menu
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabPresent(1);
      //need for parsing text in editor and redraw all menu edit
      Thread.sleep(1000);
      checkAllLockButonNotActive();

      //step 5 select XML file and check button state
      IDE.EDITOR.selectTab(FILE_NAME_1);

      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      assertTrue(IDE.LOCK_FILE.isUnLockCommandActive());
      IDE.MENU.clickOnLockLayer();
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.UNLOCK_FILE));
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.UNLOCK_FILE));

      //step 6
      //      //TODO after fix issue IDE- 1473 ucommit code unlock XML file and check state button ()
      //      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNLOCK_FILE);
      //      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      //      assertFalse(IDE.LOCK_FILE.isUnLockCommandActive());
      //      assertTrue(IDE.LOCK_FILE.isLockCommandActive());
      //      IDE.MENU.clickOnLockLayer();
      //      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.Edit.LOCK_FILE);
      //      IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.LOCK_FILE);

      //step 7 select file, lock check button states and close
      IDE.EDITOR.selectTab(1);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.saveAs(1, FILE_NAME_2);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_2);
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.LOCK_FILE);
      IDE.LOADER.waitClosed();
      checkAllUnlockStateButtons();
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.waitTabNotPresent(1);

      //step 8 reopen Html File and check lock     
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_2);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_2);
      checkAllUnlockStateButtons();

      // step 9 create new file and check lock button state
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      IDE.EDITOR.waitTabPresent(2);
      //need for parsing text in editor and redraw all menu edit
      Thread.sleep(1000);
      checkAllLockButonNotActive();

      IDE.EDITOR.forcedClosureFile(2);
      IDE.EDITOR.waitTabNotPresent(2);

      checkAllUnlockStateButtons();
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_1);

      //unlock after fix issue TODO after fix issue IDE-1473
      //checkAllLockButtonIsActive();

   }

   @Test
   public void testLockFileStaysAfterRefresh() throws Exception
   {
      //step one refresh an open project
      createFileViaWebDav(FILE_NAME_1);
      createFileViaWebDav(FILE_NAME_2);

      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_2);

      //close welcome tab
      IDE.EDITOR.clickCloseEditorButton(0);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabNotPresent("Welcome");

      //step 2 open files
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_1);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_2);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_2);

      //step 3 lock first file and check button state
      IDE.EDITOR.selectTab(FILE_NAME_1);
      IDE.LOADER.waitClosed();
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.LOCK_FILE);
      IDE.LOADER.waitClosed();
      checkAllUnlockStateButtons();
      
      //step 4 refresh browser and chek state button of second file
      driver.navigate().refresh();
      IDE.EDITOR.waitTabPresent(2);
      IDE.EDITOR.selectTab(FILE_NAME_2);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME_2);
      Thread.sleep(1000);
      checkAllLockButtonIsActive();

   }

   /**
    * check enabled icon and button on toolbar and Edit menu
    * @throws Exception
    */
   private void checkAllLockButtonIsActive() throws Exception
   {
      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      assertTrue(IDE.LOCK_FILE.isLockCommandActive());
      IDE.MENU.clickOnLockLayer();
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.Edit.LOCK_FILE);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.LOCK_FILE));
      assertFalse(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.UNLOCK_FILE));
   }

   /**
    * check disabled icon and button on toolbar and Edit menu
    * @throws Exception
    */
   private void checkAllLockButonNotActive() throws Exception
   {
      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      IDE.LOADER.waitClosed();
      assertTrue(IDE.LOCK_FILE.isLockCommandNotActive());
      IDE.MENU.clickOnLockLayer();
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME);
      IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.LOCK_FILE);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.LOCK_FILE));
   }

   /**
    * check enabled ulock icon and button on toolbar and Edit menu
    * @throws Exception
    */
   private void checkAllUnlockStateButtons() throws Exception
   {
      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      assertTrue(IDE.LOCK_FILE.isUnLockCommandActive());
      IDE.MENU.clickOnLockLayer();
      IDE.LOADER.waitClosed();
      assertTrue(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.UNLOCK_FILE));
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(MenuCommands.Edit.UNLOCK_FILE));
   }

   private void createFileViaWebDav(String fileName)
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/browse/locks/test.html";
      try
      {
         VirtualFileSystemUtils
            .put(filePath, MimeType.TEXT_HTML, WS_URL + PROJECT + "/" + FOLDER_NAME + "/" + fileName);
      }
      catch (Exception e)
      {
         fail("Can't put file to webdav");
      }
   }

}
