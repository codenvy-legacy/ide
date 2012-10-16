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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

/**
 * Check, is can open locked file in CK editor.
 * If open if CK editor locked file, you can't save it.
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 15, 2010 $
 *
 */
public class OpenLockedFileInCKEditorTest extends LockFileAbstract
{

   private static final String FOLDER_NAME = OpenLockedFileInCKEditorTest.class.getSimpleName();

   private static String PROJECT = OpenLockedFileInCKEditorTest.class.getSimpleName();

   private static final String FILE_NAME = "file-" + OpenLockedFileInCKEditorTest.class.getSimpleName();

   @Before
   public void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME);

         VirtualFileSystemUtils.put("src/test/resources/org/exoplatform/ide/operation/browse/locks/test.html",
            MimeType.TEXT_HTML, WS_URL + PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testOpenLockedFile() throws Exception
   {
      //step 1 open project
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);

      //step 2 lock file an logout
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.clickDesignButton();
      IDE.LOADER.waitClosed();
      //open file save content 
      IDE.CK_EDITOR.switchToCkEditorIframe(1);
      IDE.selectMainFrame();
      IDE.CK_EDITOR.waitCkEditorOpened(1);
      String contentEditor = IDE.CK_EDITOR.getTextFromCKEditor(1);
     
      //save file for correct logout, popup ask window can't appearance
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "s");
      IDE.EDITOR.waitNoContentModificationMark(FILE_NAME);
      //lock file
      
      IDE.TOOLBAR.waitButtonPresentAtLeft(ToolbarCommands.Editor.LOCK_FILE);
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.LOCK_FILE);
      IDE.LOADER.waitClosed();
      //check state buttons and logout
      checkAllUnlockStateButtons();
      IDE.LOGIN.logout();

      //step 3 login as invite user, open an check lock project 
      IDE.LOGIN.waitTenantLoginPage();
      IDE.LOGIN.loginAsUser();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.EDITOR.waitTabPresent(0);
      IDE.WELCOME_PAGE.close();
      IDE.WELCOME_PAGE.waitClose();

      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      
      
      IDE.EDITOR.clickDesignButton();
      IDE.LOADER.waitClosed();
      IDE.CK_EDITOR.waitCkEditorOpened(1);
      IDE.TOOLBAR.waitButtonPresentAtLeft(ToolbarCommands.Editor.LOCK_FILE);
      checkLockStateButtonsOnUserCredentinal();
      IDE.CK_EDITOR.deleteFileContentInCKEditor(1);
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, "i try is change content");
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "s");
      IDE.EDITOR.waitNoContentModificationMark(FILE_NAME);
      IDE.EDITOR.closeFile(0);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.CK_EDITOR.clickDesignButton(1);
      assertEquals(contentEditor, IDE.CK_EDITOR.getTextFromCKEditor(2));

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
   
   
   /**
    * check enabled ulock icon and button on toolbar and Edit menu
    * @throws Exception
    */
   private void checkLockStateButtonsOnUserCredentinal() throws Exception
   {
      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      assertFalse(IDE.LOCK_FILE.isUnLockCommandActive());
      IDE.MENU.clickOnLockLayer();
      IDE.LOADER.waitClosed();
      assertFalse(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.UNLOCK_FILE));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(MenuCommands.Edit.UNLOCK_FILE));
   }
   

}
