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

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.preferences.AbstractHotkeysTest.Commands;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;

/**
 * IDE-156:HotKeys customization.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class HotkeysFormTest extends BaseTest
{
   private final static String PRODUCTION_SERVICE_PREFIX = "production/ide-home/users/" + USER_NAME
      + "/settings/userSettings";

   private final static String PROJECT = HotkeysFormTest.class.getSimpleName();

   @AfterClass
   public static void tearDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(ENTRY_POINT_URL_IDE + PRODUCTION_SERVICE_PREFIX);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @After
   public void restoreDefault()
   {
      try
      {
         VirtualFileSystemUtils.delete(ENTRY_POINT_URL_IDE + PRODUCTION_SERVICE_PREFIX);
      }
      catch (Exception e)
      {
      }
   }

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testFormAndButtons() throws Exception
   {
      //step 1 create new project, open Customize Hotkey form
      //select CSS file and checks status of buttons 
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.EDITOR.waitTabPresent(0);
      openCustomizeHotkeyForm();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isKeyFieldActive(true);

      //step2 deselect row and check elements state

      //reset all selection in hotkey form
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.CUSTOMIZE_TOOLBAR);
      IDE.CUSTOMIZE_TOOLBAR.waitOpened();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();

      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkDisabled();

      //step3 select 'Save' raw and check state buttons and hotkey field  
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(ToolbarCommands.File.SAVE);
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isKeyFieldActive(true);
      assertEquals("Ctrl+S", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());

      //step4 set new hotkey value for selected (ctrl+K) row and check elements state
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "k");
      IDE.CUSTOMIZE_HOTKEYS.isBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkDisabled();

      assertEquals("Ctrl+K", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());

      //step5 click on bind button and checked changes elements state 
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(ToolbarCommands.File.SAVE);
      assertEquals("Ctrl+K", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();

   }

   @Test
   public void testBindingAndUnbindingNewHotkey() throws Exception
   {
      //step 1 bind for CSS command new value, check state elements, save changes
      openCustomizeHotkeyForm();

      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "m");
      IDE.CUSTOMIZE_HOTKEYS.isAlredyNotView();
      IDE.CUSTOMIZE_HOTKEYS.isBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();

      //step 2 check 
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.css *");
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      openCustomizeHotkeyForm();

      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      assertEquals("Ctrl+M", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());
      IDE.CUSTOMIZE_HOTKEYS.unbindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");
      IDE.EDITOR.waitTabNotPresent("Untitled file.css *");
   }

   @Test
   public void testTryToBindForbiddenHotkeys() throws Exception
   {

      openCustomizeHotkeyForm();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_TEXT_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys("y");
      IDE.CUSTOMIZE_HOTKEYS.isFirstKeyMessageView();
      IDE.CUSTOMIZE_HOTKEYS.typeKeys("8");
      IDE.CUSTOMIZE_HOTKEYS.isFirstKeyMessageView();
      IDE.CUSTOMIZE_HOTKEYS.typeKeys("n");
      IDE.CUSTOMIZE_HOTKEYS.isFirstKeyMessageView();

      //step 2 Presses Ctrl and Alt and check states buttons and messages on the form
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString());
      IDE.CUSTOMIZE_HOTKEYS.isHoltMessageView();
      assertEquals("Ctrl+", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.ALT.toString());
      assertEquals("Alt+", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkDisabled();
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.SHIFT.toString() + "n");

      //step 3 checking forbidden values, that are reserved by editors
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "c");
      IDE.CUSTOMIZE_HOTKEYS.isHotKeyUsedMessageView();
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "p");
      IDE.CUSTOMIZE_HOTKEYS.isAlredyNotView();
      IDE.CUSTOMIZE_HOTKEYS.isBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkDisabled();
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();

      //after fix issue IDE 1420 string 213 should be remove
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_HTML_FILE);
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "p");
      IDE.CUSTOMIZE_HOTKEYS.isAlreadyToThisCommandMessView();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
   }

   @Test
   public void testUnbindingDefaultHotkey() throws Exception
   {
      openCustomizeHotkeyForm();

      //step 2: preconditioning: Select XML file, set new key bind and
      // check this work hotkey in IDE
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "m");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();

      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");

      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.isEditorTabSelected("Untitled file.css *");
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //step 3: unbind new hotkey and check this in IDE
      openCustomizeHotkeyForm();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.waitUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.unbindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");
      IDE.EDITOR.waitTabNotPresent("Untitled file.css *");

   }

   private void openCustomizeHotkeyForm() throws Exception, InterruptedException
   {
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.PREFERNCESS);
      IDE.PREFERENCES.waitPreferencesOpen();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
   }

}