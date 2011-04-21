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

import static org.junit.Assert.assertEquals;

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
 * Test doesn't pass, becouse during restoring version,
 * new window (from CodeMirror) appears (this window appears
 * only in selenium-test).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public abstract class RestoreVersionTest extends VersioningTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String TEST_FOLDER = RestoreVersionTest.class.getSimpleName();

   private final static String FILE_1 = "Test File 1";

   private final static String FILE_2 = "Test File 2";

   private String version1Text = "1+1+";

   private String version2Text = "2+2+";

   private String version3Text = "3+3+";

   private String version4Text = "4+4+";

   private String version5Text = "5+5+";

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
   public void testRestoreVersion() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);

      IDE.editor().deleteFileContent();
      saveAsUsingToolbarButton(FILE_1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      
      IDE.editor().typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      checkViewVersionHistoryButtonState(true);
      IDE.editor().typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      IDE.editor().typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.editor().typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      //Restore version button is disabled because current version is opened:
      checkRestoreVersionButtonState(false);
      checkTextOnVersionPanel(IDE.editor().getTextFromCodeEditor(0));

      //View older version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //Restore version and check opened file has restored content
      IDE.toolbar().runCommand(MenuCommands.File.RESTORE_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkAskDialogPresent(true);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD*2);
      checkAskDialogPresent(false);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(version1Text + version2Text + version3Text, IDE.editor().getTextFromCodeEditor(0));
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //Reopen file to check content:
      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_1, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals(version1Text + version2Text + version3Text, IDE.editor().getTextFromCodeEditor(0));

      //Open version panel
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      //Restore version button is disabled because current version is opened:
      checkRestoreVersionButtonState(false);
      checkTextOnVersionPanel(IDE.editor().getTextFromCodeEditor(0));

      //View older version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //View older version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //Restore version and check opened file has restored content
      IDE.toolbar().runCommand(MenuCommands.File.RESTORE_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkAskDialogPresent(true);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkAskDialogPresent(false);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(version1Text + version2Text, IDE.editor().getTextFromCodeEditor(0));
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //View newer version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View newer version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //View newer version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View newer version:
      IDE.toolbar().runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkRestoreVersionButtonState(false);
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(version1Text + version2Text);

      IDE.editor().closeTab(0);
   }

   @Test
   public void testRestoreVersionAndEditFile() throws Exception
   {
      Thread.sleep(10000);
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);

      IDE.editor().deleteFileContent();
      saveAsUsingToolbarButton(FILE_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      
      IDE.editor().typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      checkViewVersionHistoryButtonState(true);
      IDE.editor().typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      IDE.editor().typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.editor().typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 3);
      checkVersionPanelState(true);
      //View older version button is enabled: 
      checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      checkNewerVersionButtonState(false);
      //Restore version button is disabled because current version is opened:
      checkRestoreVersionButtonState(false);
      checkTextOnVersionPanel(IDE.editor().getTextFromCodeEditor(0));

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      checkVersionListSize(5);
      clickCloseVersionListPanelButton();
      checkOpenVersion(2, version1Text + version2Text);

      //Restore version and check opened file has restored content
      IDE.toolbar().runCommand(MenuCommands.File.RESTORE_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkAskDialogPresent(true);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD*2);
      checkAskDialogPresent(false);
      Thread.sleep(30000);
      assertEquals(version1Text + version2Text, IDE.editor().getTextFromCodeEditor(0));
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkRestoreVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      checkVersionListSize(6);
      clickCloseVersionListPanelButton();

      IDE.editor().selectIFrameWithEditor(0);
      selenium.clickAt("//body[@class='editbox']", "5,5");
      selenium.keyPressNative("" + KeyEvent.VK_END);
      IDE.selectMainFrame();
      Thread.sleep(5000);
      IDE.editor().typeTextIntoEditor(0, version5Text);
      Thread.sleep(5000);
      saveCurrentFile();
      Thread.sleep(5000);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      checkVersionListSize(7);
      clickCloseVersionListPanelButton();

      IDE.editor().closeTab(0);
   }

   private void checkAskDialogPresent(boolean isPresent)
   {
      assertEquals(isPresent, selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertEquals(isPresent, selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
   }

   @After
   public void cleanResults() throws Exception
   {
      IDE.editor().closeTab(0);
   }
   
}
