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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class CustomizeToolbarTest extends BaseTest
{
   @AfterClass
   public static void restoreDefault()
   {
      driver.manage().deleteAllCookies();
      try
      {
         VirtualFileSystemUtils.delete(ENTRY_POINT_URL_IDE + PRODUCTION_SERVICE_PREFIX);
         VirtualFileSystemUtils.delete(ENTRY_POINT_URL_IDE + PRODUCTION_SERVICE_PREFIX);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void CustomizeToolbartest() throws Exception
   {

      //step 1 (run CUSTOMIZE TOOLBAR form, delete New * [Popup], press CANCEL button. Run form again and  check "New * [Popup]" element presents)
      IDE.PROJECT.EXPLORER.waitOpened();
      openCustomizeHotkeyForm();
      IDE.CUSTOMIZE_TOOLBAR.selectNumElementOnCommandListbar(7);
      IDE.CUSTOMIZE_TOOLBAR.selectElementOnCommandlistbarByName("New *");
      IDE.CUSTOMIZE_TOOLBAR.selectElementOnToolbarByName("New *");
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.deleteClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
      IDE.PROJECT.EXPLORER.waitOpened();
      openCustomizeHotkeyForm();

      IDE.CUSTOMIZE_TOOLBAR.waitToolbarElementIsPresent("New * [Popup]");

      IDE.CUSTOMIZE_TOOLBAR.selectElementOnToolbarByName("New *");
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.deleteClick();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.okClick();
      IDE.LOADER.waitClosed();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
      openCustomizeHotkeyForm();

      IDE.CUSTOMIZE_TOOLBAR.waitToolbarElementIsNotPresent("New * [Popup]");

      //step 3  (restore default settings and check them)
      IDE.CUSTOMIZE_TOOLBAR.defaultClick();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.isDefaultCommandlbarList();
      IDE.CUSTOMIZE_TOOLBAR.isDefaultToolbarList();
      IDE.CUSTOMIZE_TOOLBAR.okClick();

      //step 4  (move first element down, check element in new position. Reopen Customize Toolbar form, and check element in new position )
      IDE.CUSTOMIZE_TOOLBAR.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.selectElementOnToolbarByName("New *");
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.moveDownClick();
      IDE.LOADER.waitClosed();

      IDE.CUSTOMIZE_TOOLBAR.waitToolbarElementPresentInPosition(3, "New * [Popup]");

      IDE.CUSTOMIZE_TOOLBAR.okClick();
      IDE.LOADER.waitClosed();

      IDE.CUSTOMIZE_TOOLBAR.waitToolbarElementPresentInPosition(3, "New * [Popup]");

      //step 4  (Remove element which was moved Check next placement element after reopen form. (should be "Save" instead of "New * [Popup]"))
      IDE.CUSTOMIZE_TOOLBAR.selectElementOnToolbarByName("New *");
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.deleteClick();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.okClick();
      IDE.CUSTOMIZE_TOOLBAR.waitClosed();

      IDE.CUSTOMIZE_TOOLBAR.waitOpened();

      IDE.CUSTOMIZE_TOOLBAR.waitToolbarElementPresentInPosition(3, "Save");
      IDE.LOADER.waitClosed();

      //step 5 (Click on "Save" element, add delimiter, check new delimiter on toolbar. Move second delimiter on one position down. And check his position)
      IDE.CUSTOMIZE_TOOLBAR.selectElementOnToolbarByName("Save");
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.delimiterClick();
      IDE.LOADER.waitClosed();

      IDE.CUSTOMIZE_TOOLBAR.waitToolbarElementPresentInPosition(4, "Delimiter");

      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();

      //step 6 (reopen customize toolbar and set default values. Check default elements on commandlist and toolbar)
      openCustomizeHotkeyForm();
      IDE.CUSTOMIZE_TOOLBAR.defaultClick();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_TOOLBAR.isDefaultCommandlbarList();
      IDE.CUSTOMIZE_TOOLBAR.isDefaultToolbarList();

   }

   private void openCustomizeHotkeyForm() throws Exception, InterruptedException
   {
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.PREFERNCESS);
      IDE.PREFERENCES.waitPreferencesOpen();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.CUSTOMIZE_TOOLBAR);
      IDE.CUSTOMIZE_TOOLBAR.waitOpened();
   }

}