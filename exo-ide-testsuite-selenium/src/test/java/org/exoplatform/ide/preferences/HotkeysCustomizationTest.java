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
package org.exoplatform.ide.preferences;

import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * IDE-156:HotKeys customization.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class HotkeysCustomizationTest extends BaseTest
{
   private final static String PROJECT = HotkeysCustomizationTest.class.getSimpleName();

   private final static String FILE_NAME = HotkeysCustomizationTest.class.getSimpleName();

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, WS_URL + PROJECT + "/" + FILE_NAME);
      }
      catch (IOException e)
      {

      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(ENTRY_POINT_URL_IDE + PRODUCTION_SERVICE_PREFIX);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   /**
    * IDE-156:HotKeys customization ----- 1-2 ------------
    * 
    * @throws Exception
    */

   @Test
   public void testDefaultHotkeys() throws Exception
   {

      // step 1 create new project, open default xml file and check hotkey
      // ctrl+N.
      // change xml file, press Ctrl+S and check ask for value dialog
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.New.NEW);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.deleteFileContent();
      IDE.EDITOR.typeTextIntoEditor("change file");
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "s");
      IDE.ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE.ASK_FOR_VALUE_DIALOG.clickNoButton();
      IDE.ASK_DIALOG.waitClosed();
      IDE.LOADER.waitClosed();
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @Test
   public void testHotkeysInSeveralTabs() throws Exception
   {

      // step 1 open
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      openCustomizeHotkeyForm();

      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("New HTML");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "h");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("Save As Template...");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.ALT.toString() + "n");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();

      // step 2 tabs and check in first tab new hotkeys. Selecting second tab,
      // and checking new hotkey here
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitTabPresent(2);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.typeTextIntoEditor(Keys.ALT.toString() + "n");
      IDE.SAVE_AS_TEMPLATE.waitOpened();
      IDE.SAVE_AS_TEMPLATE.clickCancelButton();
      IDE.SAVE_AS_TEMPLATE.waitClosed();
      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL + "h");
      IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.html *");
      IDE.selectMainFrame();
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeTabIgnoringChanges(1);

   }

   @Test
   public void testHotkeysAfterRefresh() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.New.NEW);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);

      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();

      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
      closeAllTabs();
   }

   @Test
   public void testResettingToDefaults() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      // step 1: create new hotkey for upload file (Alt+U)
      openCustomizeHotkeyForm();

      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("Open File By Path...");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "m");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();

      // step 2: check new hotkey
      IDE.selectMainFrame();
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.New.NEW);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL + "m");
      IDE.OPEN_FILE_BY_PATH.waitOpened();
      IDE.OPEN_FILE_BY_PATH.clickCancelButton();
      IDE.OPEN_FILE_BY_PATH.waitClosed();

      // step 3: resetting the hotkeys to the default values and checking
      openCustomizeHotkeyForm();
      IDE.CUSTOMIZE_HOTKEYS.defaultsButtonClick();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL + "m");
      assertFalse(IDE.UPLOAD.isOpened());
   }

   // close opened files in this test
   private void closeAllTabs() throws Exception
   {
      for (int i = 0; i < 2; i++)
      {
         IDE.EDITOR.closeTabIgnoringChanges(1);
      }
   }

   //sequence actions for calling Hotkeys form
   private void openCustomizeHotkeyForm() throws Exception, InterruptedException
   {
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.PREFERNCESS);
      IDE.PREFERENCES.waitPreferencesOpen();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
   }

}