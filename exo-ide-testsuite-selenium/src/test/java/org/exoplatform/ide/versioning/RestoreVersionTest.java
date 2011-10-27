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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

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
public class RestoreVersionTest extends BaseTest
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
   }

   @Test
   public void testRestoreVersion() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);

      IDE.EDITOR.deleteFileContent();
      IDE.NAVIGATION.saveFileAs(FILE_1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_1);

      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.VERSIONS.checkVersionPanelState(true);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      //Restore version button is disabled because current version is opened:
      IDE.VERSIONS.checkRestoreVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //Restore version and check opened file has restored content
      IDE.TOOLBAR.runCommand(MenuCommands.File.RESTORE_VERSION);
      IDE.ASK_DIALOG.waitForAskDialogOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitForAskDialogClosed();
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(version1Text + version2Text + version3Text, IDE.EDITOR.getTextFromCodeEditor(0));
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //Reopen file to check content:
      IDE.EDITOR.closeFile(0);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_1, false);
      IDE.EDITOR.waitTabPresent(0);
      assertEquals(version1Text + version2Text + version3Text, IDE.EDITOR.getTextFromCodeEditor(0));

      //Open version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      //Restore version button is disabled because current version is opened:
      IDE.VERSIONS.checkRestoreVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      //Restore version and check opened file has restored content
      IDE.TOOLBAR.runCommand(MenuCommands.File.RESTORE_VERSION);

      IDE.ASK_DIALOG.waitForAskDialogOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitForAskDialogClosed();

      Thread.sleep(TestConstants.SLEEP);
      assertEquals(version1Text + version2Text, IDE.EDITOR.getTextFromCodeEditor(0));
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium().captureScreenshot("1.png");
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(false);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

   }

   @Test
   public void testRestoreVersionAndEditFile() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);

      IDE.EDITOR.deleteFileContent();
      IDE.NAVIGATION.saveFileAs(FILE_2);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_2);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      //View older version button is enabled: 
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      //View newer version button is disabled because current version is opened:
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      //Restore version button is disabled because current version is opened:
      IDE.VERSIONS.checkRestoreVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.VERSIONS.checkVersionListSize(5);
      IDE.VERSIONS.clickCloseVersionListPanelButton();
      IDE.VERSIONS.waitForViewVersionsListViewClose();

      IDE.VERSIONS.checkOpenVersion(3, version1Text + version2Text);

      //Restore version and check opened file has restored content
      IDE.TOOLBAR.runCommand(MenuCommands.File.RESTORE_VERSION);

      IDE.ASK_DIALOG.waitForAskDialogOpened();
      
      IDE.ASK_DIALOG.clickYes();
      
      IDE.ASK_DIALOG.waitForAskDialogClosed();
      IDE.EDITOR.waitTabPresent(0);
      assertEquals(version1Text + version2Text, IDE.EDITOR.getTextFromCodeEditor(0));
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkRestoreVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.VERSIONS.checkVersionListSize(6);
      IDE.VERSIONS.clickCloseVersionListPanelButton();
      IDE.VERSIONS.waitForViewVersionsListViewClose();

      /*   IDE.EDITOR.typeTextIntoEditor(0, version5Text);
      saveCurrentFile();

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.VERSIONS.waitForViewVersionsListViewOpen();
      IDE.VERSIONS.checkVersionListSize(7);
      IDE.VERSIONS.clickCloseVersionListPanelButton();
      IDE.VERSIONS.waitForViewVersionsListViewClose();*/
   }

   @After
   public void cleanResults() throws Exception
   {
      IDE.EDITOR.closeFile(0);
   }

}
