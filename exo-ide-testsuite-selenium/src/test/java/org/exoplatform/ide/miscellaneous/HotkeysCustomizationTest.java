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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
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
public class HotkeysCustomizationTest extends BaseTest
{
   private final static String PROJECT = HotkeysCustomizationTest.class.getSimpleName();

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
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.New.NEW);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      //step 1 create new hotkey for create html file (Ctrl+H) and file from template (Alt+N)
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      //This action (maximize)need for select rows of customize hotkey form in FF v.4.0 and higher.
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("New HTML");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "h");
      IDE.CUSTOMIZE_HOTKEYS.waitBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.bindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("Save As Template...");
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
      IDE.SAVE_AS_TEMPLATE.waitOpened();
      IDE.SAVE_AS_TEMPLATE.clickCancelButton();
      IDE.SAVE_AS_TEMPLATE.waitClosed();

      //A temporary solution for problem switching between tabs in FF 4.0 and higher
      if ("CHROME".equals(IDE_SETTINGS.getString("selenium.browser.commad"))
         || "FIREFOX_CHROME".equals(IDE_SETTINGS.getString("selenium.browser.commad")))
      {
         IDE.EDITOR.closeTabIgnoringChanges(2);
         IDE.EDITOR.closeTabIgnoringChanges(1);
         IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      }
      else
      {
         IDE.EDITOR.typeTextIntoEditor(1, Keys.CONTROL.toString() + "h");
         // TODO After opening third tab, all next steps test cases  don't work in FF v.4.0 and higher.
         //Maybe this problem switch between iframes with WebDriver. Maybe this problem will resolved after 
         //refresh browser and fix issue IDE-1392
         IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.html *");
         IDE.selectMainFrame();
         IDE.EDITOR.selectTab(2);
         IDE.EDITOR.closeTabIgnoringChanges(3);
         IDE.EDITOR.waitTabNotPresent(3);

         //repeat all actions in gadget tab
         IDE.EDITOR.selectTab(2);
         IDE.EDITOR.typeTextIntoEditor(2, Keys.ALT.toString() + "n");
         IDE.SAVE_AS_TEMPLATE.waitOpened();
         IDE.SAVE_AS_TEMPLATE.clickCancelButton();
         IDE.SAVE_AS_TEMPLATE.waitClosed();
         IDE.EDITOR.typeTextIntoEditor(2, Keys.CONTROL.toString() + "h");
         IDE.EDITOR.waitTabPresent(3);
         IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.html *");
         IDE.EDITOR.closeTabIgnoringChanges(3);
         IDE.EDITOR.waitTabNotPresent(3);

         IDE.EDITOR.closeTabIgnoringChanges(2);
         IDE.EDITOR.closeTabIgnoringChanges(1);
         IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      }
   }

   @Test
   public void testHotkeysAfterRefresh() throws Exception
   {
      //step 1 opening 2 files and checking restore commands in 2 tabs after refresh. 
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.TOOLBAR.waitButtonPresentAtLeft(MenuCommands.New.NEW);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitTabPresent(2);

      //  driver.navigate().refresh();
      IDE.EDITOR.waitTabPresent(2);
      IDE.EDITOR.selectTab(1);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();

      // driver.navigate().refresh();
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.typeTextIntoEditor(1, Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();

      //last step restore default values for HTML file and Create File From Template... commands 
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
      IDE.CUSTOMIZE_HOTKEYS.maximizeClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName(MenuCommands.New.HTML_FILE);
      IDE.CUSTOMIZE_HOTKEYS.waitUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.unbindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("Save As Template...");
      IDE.CUSTOMIZE_HOTKEYS.waitUnBindEnabled();
      IDE.CUSTOMIZE_HOTKEYS.unbindlButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.CUSTOMIZE_HOTKEYS.waitClosed();
   }

}