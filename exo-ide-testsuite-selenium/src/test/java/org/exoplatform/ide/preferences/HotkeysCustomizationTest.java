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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;

import javax.validation.constraints.AssertFalse;

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
      //need for fix problem 
      Thread.sleep(500);
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @Test
   public void testHotkeysInSeveralTabs() throws Exception
   {
      //step 1 opening 2 files and checking restore commands in 2 tabs after refresh. 
      driver.navigate().refresh();

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

      // step 2 tabs and check in first tab new hotkeys. Selecting second tab, and checking new hotkey here
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitTabPresent(2);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ALT.toString() + "n");
      IDE.SAVE_AS_TEMPLATE.waitOpened();
      IDE.SAVE_AS_TEMPLATE.clickCancelButton();
      IDE.SAVE_AS_TEMPLATE.waitClosed();
      IDE.EDITOR.selectTab(2);
      //this workaround for problem with select iframes in coseeditor 
      new Actions(driver).sendKeys(Keys.CONTROL + "h").build().perform();
      // TODO After opening third tab, all next steps test cases  don't work in FF v.4.0 and higher.
      //Maybe this problem switch between iframes with WebDriver. Maybe this problem will resolved after 
      //refresh browser and fix issue IDE-1392
      IDE.EDITOR.isTabPresentInEditorTabset("Untitled file.html *");
      IDE.selectMainFrame();
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeTabIgnoringChanges(1);

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
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);

      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();

      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.typeTextIntoEditor(1, Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();
      IDE.TEMPLATES.clickCancelButton();
      IDE.TEMPLATES.waitClosed();
      closeAllTabs();
   }

   @Test
   public void testResettingToDefaults() throws Exception
   {
      //reopen project after refresh, because if this action need for correctly work in short keys in IDE 
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //step 1: create new hotkey for upload file (Alt+U)
      openCustomizeHotkeyForm();

      IDE.CUSTOMIZE_HOTKEYS.selectElementOnCommandlistbarByName("Open File By Path...");
      IDE.CUSTOMIZE_HOTKEYS.typeKeys(Keys.CONTROL.toString() + "g");
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
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL + "g");
      IDE.OPEN_FILE_BY_PATH.waitOpened();
      IDE.OPEN_FILE_BY_PATH.clickCancelButton();
      IDE.OPEN_FILE_BY_PATH.waitClosed();
      //step 3: resetting the hotkeys to the default values and checking
      openCustomizeHotkeyForm();
      IDE.CUSTOMIZE_HOTKEYS.defaultsButtonClick();
      IDE.LOADER.waitClosed();
      IDE.CUSTOMIZE_HOTKEYS.isOkEnabled();
      IDE.CUSTOMIZE_HOTKEYS.okButtonClick();
      IDE.PREFERENCES.clickOnCloseFormBtn();
      IDE.PREFERENCES.waitPreferencesClose();
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL + "g");
      assertFalse(IDE.UPLOAD.ddd());
   }

   //--------------
   private void closeAllTabs() throws Exception
   {
      for (int i = 0; i < 2; i++)
      {
         IDE.EDITOR.closeTabIgnoringChanges(1);
      }
   }

   private void openCustomizeHotkeyForm() throws Exception, InterruptedException
   {
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.PREFERNCESS);
      IDE.PREFERENCES.waitPreferencesOpen();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.CUSTOMIZE_HOTKEYS);
      IDE.CUSTOMIZE_HOTKEYS.waitOpened();
   }

}