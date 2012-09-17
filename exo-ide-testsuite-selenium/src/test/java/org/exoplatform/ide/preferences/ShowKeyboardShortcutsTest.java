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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * IDE-1597: Add "Show Keyboard Shortcuts" feature in Help menu.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowKeyboardShortcutsTest.java May 10, 2012 4:23:15 PM azatsarynnyy $
 *
 */
public class ShowKeyboardShortcutsTest extends BaseTest
{
   private final static String PRODUCTION_SERVICE_PREFIX = "production/ide-home/users/" + USER_NAME
      + "/settings/userSettings";

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

   @Test
   public void testView() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();

      IDE.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.SHOW_KEYBOARD_SHORTCUTS);
      IDE.SHOW_KEYBOARD_SHORTCUTS.waitOpened();
      IDE.SHOW_KEYBOARD_SHORTCUTS.isCloseButtonEnabled();

      // check Ctrl+S is present
      assertTrue(IDE.SHOW_KEYBOARD_SHORTCUTS.isShortcutPresent("Ctrl+S"));
      // check Ctrl+M is absent
      assertFalse(IDE.SHOW_KEYBOARD_SHORTCUTS.isShortcutPresent("Ctrl+M"));

      IDE.SHOW_KEYBOARD_SHORTCUTS.closeButtonClick();
      IDE.SHOW_KEYBOARD_SHORTCUTS.waitClosed();

      // change shortcut for Save command
//      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(ToolbarCommands.File.SAVE);
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "m");
      IDE.CUSTOMIZE_HOTKEYS.bindButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();

      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();

      IDE.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.SHOW_KEYBOARD_SHORTCUTS);
      IDE.SHOW_KEYBOARD_SHORTCUTS.waitOpened();

      // check new shortcut Ctrl+M is present
      assertTrue(IDE.SHOW_KEYBOARD_SHORTCUTS.isShortcutPresent("Ctrl+M"));

      IDE.SHOW_KEYBOARD_SHORTCUTS.closeButtonClick();
      IDE.SHOW_KEYBOARD_SHORTCUTS.waitClosed();
   }

}