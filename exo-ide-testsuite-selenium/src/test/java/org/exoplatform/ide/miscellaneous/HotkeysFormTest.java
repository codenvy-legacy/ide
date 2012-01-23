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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.miscellaneous.AbstractHotkeysTest.Commands;
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

   private final static String PROJECT = HotkeysFormTest.class.getSimpleName();

   @AfterClass
   public static void tearDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
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
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isCancelEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isKeyFieldActive(true);

      //step2 deselect row and check elements state
      //function (Ctrl+click) At the moment, does not work. See issue IDE-1412
      // Workaround: Restart HotkeyCustomization form, after restart all elements not selected
      IDE.CUSTOMIZE_HOTKEYS.closeClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isCancelEnabled();

      //step3 select 'Save' raw and check state buttons and hotkey field  
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(ToolbarCommands.File.SAVE);
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isCancelEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isKeyFieldActive(true);
      assertEquals("Ctrl+S", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());

      //step4 set new hotkey value for selected (ctrl+K) row and check elements state
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "k");
      IDE.CUSTOMIZE_HOTKEYS.isBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isCancelEnabled();
      assertEquals("Ctrl+K", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());

      //step5 click on bind button and checked changes elements state 
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.isBindDisabled();
      IDE.CUSTOMIZE_HOTKEYS.isUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isCancelEnabled();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(ToolbarCommands.File.SAVE);
      assertEquals("", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());
      IDE.CUSTOMIZE_HOTKEYS.cancelButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
   }

   @Test
   public void testBindingAndUnbindingNewHotkey() throws Exception
   {
      //step 1 bind for CSS command new value, check state elements, save changes
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "m");
      IDE.CUSTOMIZE_HOTKEYS.isAlredyNotView();
      IDE.CUSTOMIZE_HOTKEYS.isBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();

      //step 2 check 
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.css *");
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      assertEquals("Ctrl+M", IDE.CUSTOMIZE_HOTKEYS.getTextTypeKeys());
      IDE.CUSTOMIZE_HOTKEYS.unbindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");
      IDE.EDITOR.waitTabNotPresent("Untitled file.css *");
   }

   @Test
   public void testTryToBindForbiddenHotkeys() throws Exception
   {
      //Select "New Text file" command and try to bind Shift+N. 
      //Then try to bind ordinal keys Y, 8, PrintScreen and simmilar ordinal keys
      driver.navigate().refresh();

      //step 1trying bind incorrect values for text files
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_TEXT_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.SHIFT.toString() + "Y");
      IDE.CUSTOMIZE_HOTKEYS.isFirstKeyMessageView();
      IDE.CUSTOMIZE_HOTKEYS.typeKeys("8");
      IDE.CUSTOMIZE_HOTKEYS.isFirstKeyMessageView();
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.SHIFT.toString() + "n");
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
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();

      //after fix issue IDE 1420 string 213 should be remove
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_HTML_FILE);
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "p");
      IDE.CUSTOMIZE_HOTKEYS.isAlreadyToThisCommandMessView();
      IDE.CUSTOMIZE_HOTKEYS.cancelButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
   }

   @Test
   //TODO If will be fix problem with impossible selecting elements after 
   //scroll of form "Customize Hotkeys..." in FF 4.0 and higher, test should be reworked.
   public void testUnbindingDefaultHotkey() throws Exception
   {
      driver.navigate().refresh();
      //step 1: checking default hotkey
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //step 2: preconditioning: Select XML file, set new key bind and
      // check this work hotkey in IDE
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "m");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.isEditorTabSelected("Untitled file.css *");
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //step 3: unbind new hotkey and check this in IDE
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(Commands.NEW_CSS_FILE);
      IDE.CUSTOMIZE_HOTKEYS.waitUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.unbindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "m");
      IDE.EDITOR.waitTabNotPresent("Untitled file.css *");

   }
}