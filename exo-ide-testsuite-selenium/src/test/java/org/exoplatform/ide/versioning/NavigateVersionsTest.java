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
package org.exoplatform.ide.versioning;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class NavigateVersionsTest extends BaseTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String TEST_FOLDER = NavigateVersionsTest.class.getSimpleName();

   private final static String FILE_1 = "Test File 1";

   private final static String FILE_2 = "Test File 2";

   private final static String FILE_3 = "Test File 3";

   private final static String FILE_4 = "Test File 4";

   private String version1Text = "first,";

   private String version2Text = " second,";

   private String version3Text = " third,";

   private String version4Text = " fourth,";

   private String version5Text = " fifth,";

   private String version6Text = " sixth,";

   private String version7Text = " seventh.";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         cleanRegistry();
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void cleanResults() throws Exception
   {
      IDE.EDITOR.closeTab(0);
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testNavigateOlderVersion() throws Exception
   {
      waitForRootElement();
      IDE.MENU.waitForMenuItemPresent(MenuCommands.View.VIEW);

      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      /*
       * 1. Open new file
       */
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      /*
       * Version History button is not present
       */
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);

      /*
       * 2. Clear text in text editor and save file.
       */
      IDE.EDITOR.deleteFileContent();
      saveAsUsingToolbarButton(FILE_1);

      /*
       * Version History button is present, but disabled
       */
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, false, 5000);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      /*
       * 3. Type text in editor and save file.
       */
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();

      /*
       * Version History button is enabled
       */
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, true, 5000);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);

      /*
       * 4. Create 2, 3, 4 versions.
       */
      selenium.keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      /*
       * 5. Click Version History button
       */
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();

      /*
       * Version Panel appeared.
       */
      IDE.VERSIONS.checkVersionPanelState(true);
      /*
       * View older version button is enabled. 
       */
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      /*
       * View newer version button is disabled because current version is opened:
       */
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));
      /*
       * 6. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      /*
       * 7. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      /*
       * 8. View newer version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      /*
       * 9. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      /*
       * 10. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text);

      /*
       * 11. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(false);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel("");

      /*
       * 12. View newer version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text);

      /*
       * 13. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(false);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel("");

      /*
       * 14. Close file.
       */
      IDE.EDITOR.closeTab(0);

      /*
       * Version history button dissapeared.
       */
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);

      /*
       * 15. Open file.
       */
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_1, true);

      /*
       * Version History button is enabled
       */
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      /*
       * 16. View versions
       */
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);

      /*
       * View older version button is enabled. 
       */
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      /*
       * View newer version button is disabled because current version is opened.
       */
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));
      /*
       * 17. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      /*
       * 18. View older version.
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

   }

   @Test
   public void testNavigateNewerVersion() throws Exception
   {
      refresh();
      waitForRootElement();

      // Version Histrory button is not present.
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);

      // 1. Create new file in TEST_FOLDER
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveAsUsingToolbarButton(FILE_2);

      // Version History button is disabled.
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      // 2. Create new version.
      selenium.keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();

      // Version History button is enabled.
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, true, 2000);

      //3. Create versions.
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version5Text);
      saveCurrentFile();

      // 4. Click Version History button.
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);

      // Version Panel appeared.
      IDE.VERSIONS.waitVersionContentViewOpen();

      // View Older Version button is enabled. 
      IDE.VERSIONS.checkOlderVersionButtonState(true);

      // View Newer Version button is disabled because current version is opened.
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      // 5. Click View Older Version button.
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);

      // Check buttons state and text in version panel.
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_OLDER_VERSION, true, 2000);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_NEWER_VERSION, true, 2000);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      // 6. View newer version.
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text);

      // 7. View older version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      // 8. View older version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      // 9. View older version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      // 10. View older version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(false);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text);

      // 11. View newer version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      // 12. View newer version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      // 13. View newer version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      // 14. View newer version.

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text);
   }

   @Test
   public void testNavigateNewerVersionWithSave() throws Exception
   {
      refresh();
      waitForRootElement();

      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Create new file, add text and save file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveAsUsingToolbarButton(FILE_3);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      selenium.keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version5Text);
      saveCurrentFile();

      //View versions
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();

      IDE.VERSIONS.checkVersionPanelState(true);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));
      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //Edit file and save
      IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version6Text);
      saveCurrentFile();
      //Check viewed version on version panel is not changed
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text
         + version6Text);

      //Edit file and save
      IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version7Text);
      saveCurrentFile();

      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text
         + version6Text);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text
         + version6Text + version7Text);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text
         + version6Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text
         + version6Text + version7Text);
   }

   @Test
   public void testNavigateVersionWithClosePanel() throws Exception
   {
      refresh();
      waitForRootElement();

      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Create new file, add text and save file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveAsUsingToolbarButton(FILE_4);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      selenium.keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version5Text);
      saveCurrentFile();

      //View versions
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      IDE.VERSIONS.checkVersionPanelState(true);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));
      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //Close version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkVersionPanelState(false);

      //Open version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      IDE.VERSIONS.checkVersionPanelState(true);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);
      //Close version panel
      IDE.VERSIONS.closeVersionPanel();
      IDE.VERSIONS.waitVersionContentViewClosed();
      IDE.VERSIONS.checkVersionPanelState(false);

      //Edit file and save
      IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version6Text);
      saveCurrentFile();

      //Open version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false, 5000);
      
      IDE.VERSIONS.checkVersionPanelState(true);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));
   }

}
