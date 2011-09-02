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

import org.everrest.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class ViewVersionListTest extends BaseTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String TEST_FOLDER = ViewVersionListTest.class.getSimpleName();

   private final static String FILE_1 = "Test file1";

   private final static String FILE_2 = "Test file2";

   private String version1Text = "one-";

   private String version2Text = "two-";

   private String version3Text = "three-";

   private String version4Text = "four";

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
   
   @AfterClass
   public static void tearDown()
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

   /**
    * Tests the list of versions.
    * 
    * @throws Exception
    */
   @Test
   public void testViewVersionList() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);

      IDE.EDITOR.deleteFileContent();
      IDE.NAVIGATION.saveFileAs(FILE_1);
//      saveAsUsingToolbarButton(FILE_1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_1);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);
      //Edit and save file
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, true);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);

      //Open version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);

      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text);

      IDE.VERSIONS.checkViewVersionListButtonState(true);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.VERSIONS.waitForViewVersionsListViewOpen();

      IDE.VERSIONS.checkViewVersionsListPanel(true);
      IDE.VERSIONS.checkOpenVersionButtonState(false);
      IDE.VERSIONS.checkVersionListSize(2);

      //Close version list panel:
      IDE.VERSIONS.clickCloseVersionListPanelButton();
      IDE.VERSIONS.waitForViewVersionsListViewClose();
      IDE.VERSIONS.checkViewVersionsListPanel(false);
      IDE.VERSIONS.checkVersionPanelState(true);

      //Edit file and save
      IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkViewVersionListButtonState(true);

      //View version list
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.VERSIONS.waitForViewVersionsListViewOpen();
      IDE.VERSIONS.checkViewVersionsListPanel(true);
      IDE.VERSIONS.checkOpenVersionButtonState(false);
      IDE.VERSIONS.checkVersionListSize(3);

      IDE.VERSIONS.selectVersionInVersionList(1);
      IDE.VERSIONS.waitOpenVersionButtonEnabled();
      IDE.VERSIONS.clickOpenVersionButton();
      IDE.VERSIONS.waitForViewVersionsListViewClose();

      IDE.VERSIONS.checkViewVersionsListPanel(false);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkViewVersionListButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      //Edit file and save
      IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_NEWER_VERSION, true);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkViewVersionListButtonState(true);

      //View version list
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.VERSIONS.waitForViewVersionsListViewOpen();
      IDE.VERSIONS.checkViewVersionsListPanel(true);
      IDE.VERSIONS.checkOpenVersionButtonState(false);
      IDE.VERSIONS.checkVersionListSize(4);

      IDE.VERSIONS.selectVersionInVersionList(4);
      IDE.VERSIONS.waitOpenVersionButtonEnabled();
      IDE.VERSIONS.clickOpenVersionButton();
      IDE.VERSIONS.waitForViewVersionsListViewClose();

      IDE.VERSIONS.checkViewVersionsListPanel(false);
      IDE.VERSIONS.checkOlderVersionButtonState(false);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkViewVersionListButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel("");

      IDE.EDITOR.closeFile(0);
   }

   /**
    * Test navigate version buttons with selecting item from version list.
    * 
    * @throws Exception
    */
   @Test
   public void testViewVersionListWithNavigateVersions() throws Exception
   {
      refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);

      IDE.EDITOR.deleteFileContent();
      IDE.NAVIGATION.saveFileAs(FILE_2);
//      saveAsUsingToolbarButton(FILE_2);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_2);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);
      //Edit and save file
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      //Open version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);

      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      IDE.VERSIONS.checkViewVersionListButtonState(true);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.VERSIONS.waitForViewVersionsListViewOpen();

      IDE.VERSIONS.checkViewVersionsListPanel(true);
      IDE.VERSIONS.checkOpenVersionButtonState(false);
      IDE.VERSIONS.checkVersionListSize(5);
      IDE.VERSIONS.clickCloseVersionListPanelButton();
      //Open version:
      IDE.VERSIONS.checkOpenVersion(2, version1Text + version2Text + version3Text);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Open version:
      IDE.VERSIONS.checkOpenVersion(4, version1Text);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      //Open version:
      IDE.VERSIONS.checkOpenVersion(1, version1Text + version2Text + version3Text + version4Text);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      //Open version:
      IDE.VERSIONS.checkOpenVersion(3, version1Text + version2Text);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(false);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //Open version:
      IDE.VERSIONS.checkOpenVersion(5, "");
      IDE.VERSIONS.checkOlderVersionButtonState(false);
      IDE.VERSIONS.checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(true);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkOlderVersionButtonState(false);
      IDE.VERSIONS.checkNewerVersionButtonState(true);
      IDE.VERSIONS.checkTextOnVersionPanel("");

      IDE.EDITOR.closeFile(0);
   }

}
