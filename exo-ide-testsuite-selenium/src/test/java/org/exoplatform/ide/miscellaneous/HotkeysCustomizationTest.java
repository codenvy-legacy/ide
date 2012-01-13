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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.MenuCommands.File;
import org.exoplatform.ide.SaveFileUtils;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
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
public class HotkeysCustomizationTest extends AbstractHotkeysTest
{
   private final static String PROJECT = CursorPositionStatusBarTest.class.getSimpleName();

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

   @After
   public void tearDown()
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

   /**
    * IDE-156:HotKeys customization
    * ----- 1-2 ------------
    * @throws Exception
    */

   @Test
   public void testDefaultHotkeys() throws Exception
   {
      //step 1 create new project, open default xml file and check hotkey ctrl+N.
      //change xml file, press Ctrl+S and check ask for value dialog
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.openItem(PROJECT);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      //driver.switchTo().activeElement().sendKeys(Keys.CONTROL.toString() + "h");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.deleteFileContent(0);
      IDE.EDITOR.typeTextIntoEditor(0, "change file");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "s");
      IDE.ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE.ASK_FOR_VALUE_DIALOG.clickNoButton();
      IDE.ASK_DIALOG.waitClosed();
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @Test
   public void testHotkeysInSeveralTabs() throws Exception
   {

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.openItem(PROJECT);

      //step 1 create new hotkey for create html file (Ctrl+H) and file from template (Alt+N)
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("New HTML");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "h");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("Create File From Template...");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.ALT.toString() + "n");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();

      // step 2 tabs and check in first tab new hotkeys. Selecting second tab, and checking new hotkey here
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitTabPresent(2);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.typeTextIntoEditor(1, Keys.ALT.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.typeTextIntoEditor(1, Keys.CONTROL.toString() + "h");
      IDE.EDITOR.waitTabPresent(3);
      IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.html *");
      IDE.EDITOR.closeTabIgnoringChanges(3);
      IDE.EDITOR.waitTabNotPresent(3);

      //repeat all actions in gadget tab
      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.typeTextIntoEditor(1, Keys.ALT.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.typeTextIntoEditor(1, Keys.CONTROL.toString() + "h");
      IDE.EDITOR.waitTabPresent(3);
      IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.html *");
      IDE.EDITOR.closeTabIgnoringChanges(3);
      IDE.EDITOR.waitTabNotPresent(3);

      IDE.EDITOR.closeTabIgnoringChanges(2);
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   //TODO after fix issue IDE-1392 should be uncomment
   @Test
   public void testHotkeysAfterRefresh() throws Exception
   {

      //step 1 restore default values for HTML file and Create File From Template... commands 
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(MenuCommands.New.HTML_FILE);
      IDE.CUSTOMIZE_HOTKEYS.waitUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.unbindlButtonClick();

      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("Create File From Template...");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "n");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
//      //step 2 opening 2 files and checking restore commands in 2 tabs after refresh
//      //driver.navigate().refresh();
//
//
//      //      IDE.PROJECT.EXPLORER.waitOpened();
//      //      IDE.PROJECT.OPEN.openProject(PROJECT);
//      //      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
//      //      IDE.PROJECT.EXPLORER.openItem(PROJECT);
//
//      
//      
//      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
//      IDE.EDITOR.waitTabPresent(1);
//      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
//      IDE.EDITOR.waitTabPresent(2);
//
//      //  driver.navigate().refresh();
//      IDE.EDITOR.waitTabPresent(2);
//      IDE.EDITOR.selectTab(1);
//
//      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "n");
//      IDE.TEMPLATES.waitOpened();
//      IDE.TEMPLATES.clickCancelButton();
//      IDE.TEMPLATES.waitClosed();
//
//      // driver.navigate().refresh();
//      IDE.EDITOR.waitTabPresent(1);
//      IDE.EDITOR.selectTab(2);
//      IDE.EDITOR.typeTextIntoEditor(1, Keys.CONTROL.toString() + "n");
//      IDE.TEMPLATES.waitOpened();
//      IDE.TEMPLATES.clickCancelButton();
//      IDE.TEMPLATES.waitClosed();

   }

}