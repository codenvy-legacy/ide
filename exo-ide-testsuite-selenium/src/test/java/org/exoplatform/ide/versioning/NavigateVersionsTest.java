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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class NavigateVersionsTest extends VersioningTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private final static String TEST_FOLDER = "testFolder";

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

   @Test
   public void testNavigateOlderVersion() throws Exception
   {
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      selectItemInWorkspaceTree(TEST_FOLDER);
      //Open new file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.HTML_FILE);
      checkViewVersionHistoryButtonPresent(false);
      
      deleteFileContent();
      saveAsUsingToolbarButton(FILE_1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);

      typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));
      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(false);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel("");

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(false);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel("");

      closeTab("0");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //Open file:
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      openFileFromNavigationTreeWithCodeEditor(FILE_1, true);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      //View versions
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));
      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      closeTab("0");
   }

   @Test
   public void testNavigateNewerVersion() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      checkViewVersionHistoryButtonPresent(false);
      selectItemInWorkspaceTree(TEST_FOLDER);
      //Create new file, add text and save file:
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      typeTextIntoEditor(0, version1Text);
      saveAsUsingToolbarButton(FILE_2);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      
      typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version5Text);
      saveCurrentFile();

      //View versions
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));
      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(false);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text);

      closeTab("0");
   }

   @Test
   public void testNavigateNewerVersionWithSave() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      checkViewVersionHistoryButtonPresent(false);
      selectItemInWorkspaceTree(TEST_FOLDER);
      //Create new file, add text and save file:
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      typeTextIntoEditor(0, version1Text);
      saveAsUsingToolbarButton(FILE_3);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      
      typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version5Text);
      saveCurrentFile();

      //View versions
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));
      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //Edit file and save
      selectIFrameWithEditor(0);
      selenium.clickAt("//body[@class='editbox']", "5,5");
      selenium.keyPressNative("" + KeyEvent.VK_END);
      selectMainFrame();
      typeTextIntoEditor(0, version6Text);
      saveCurrentFile();
      //Check viewed version on version panel is not changed
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(true);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text + version6Text);

      //Edit file and save
      selectIFrameWithEditor(0);
      selenium.clickAt("//body[@class='editbox']", "5,5");
      selenium.keyPressNative("" + KeyEvent.VK_END);
      selectMainFrame();
      typeTextIntoEditor(0, version7Text);
      saveCurrentFile();

      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text + version6Text);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text + version6Text
         + version7Text);

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text + version6Text);

      //View newer version:
      runToolbarButton(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text + version5Text + version6Text
         + version7Text);

      closeTab("0");
   }

   @Test
   public void testNavigateVersionWithClosePanel() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      checkViewVersionHistoryButtonPresent(false);
      selectItemInWorkspaceTree(TEST_FOLDER);
      //Create new file, add text and save file:
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      typeTextIntoEditor(0, version1Text);
      saveAsUsingToolbarButton(FILE_4);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      
      typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      typeTextIntoEditor(0, version5Text);
      saveCurrentFile();

      //View versions
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));
      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //Close version panel
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkVersionPanelState(false);

      //Open version panel
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));

      //View older version:
      runToolbarButton(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD*2);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);
      //Close version panel
      closeVersionPanel();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkVersionPanelState(false);

      //Edit file and save
      selectIFrameWithEditor(0);
      selenium.clickAt("//body[@class='editbox']", "5,5");
      selenium.keyPressNative("" + KeyEvent.VK_END);
      selectMainFrame();
      typeTextIntoEditor(0, version6Text);
      saveCurrentFile();

      //Open version panel
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));

      closeTab("0");
   }
   
   @After
   public void cleanResults() throws Exception
   {
      closeTab("0");
   }
}
