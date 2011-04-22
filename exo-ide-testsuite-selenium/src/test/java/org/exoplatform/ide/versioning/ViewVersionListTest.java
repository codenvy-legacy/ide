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
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class ViewVersionListTest extends VersioningTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private final static String TEST_FOLDER =  ViewVersionListTest.class.getSimpleName();

   private final static String FILE_1 = "Test file1";

   private final static String FILE_2 = "Test file2";

   private String version1Text = "one-";

   private String version2Text = "two-";

   private String version3Text = "three-";

   private String version4Text = "four.";

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

   /**
    * Tests the list of versions.
    * 
    * @throws Exception
    */
   @Test
   public void testViewVersionList() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkViewVersionHistoryButtonPresent(false);

     IDE.EDITOR.deleteFileContent();
      saveAsUsingToolbarButton(FILE_1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      //Edit and save file
     IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);

      //Open version panel
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text);

      checkViewVersionListButtonState(true);
      
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      
      Thread.sleep(TestConstants.SLEEP);
      checkViewVersionsListPanel(true);
      checkOpenVersionButtonState(false);
      checkVersionListSize(2);

      //Close version list panel:
      clickCloseVersionListPanelButton();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionsListPanel(false);
      checkVersionPanelState(true);

      //Edit file and save
     IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
     IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkViewVersionListButtonState(true);

      //View version list
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      
      Thread.sleep(TestConstants.SLEEP);
      checkViewVersionsListPanel(true);
      checkOpenVersionButtonState(false);
      checkVersionListSize(3);

      selectVersionInVersionList(0);
      clickOpenVersionButton();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
     
      checkViewVersionsListPanel(false);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);
      checkViewVersionListButtonState(true);
      checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      //Edit file and save
     IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
     IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkViewVersionListButtonState(true);

      //View version list
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      
      Thread.sleep(TestConstants.SLEEP);
      checkViewVersionsListPanel(true);
      checkOpenVersionButtonState(false);
      checkVersionListSize(4);

      selectVersionInVersionList(3);
      clickOpenVersionButton();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkViewVersionsListPanel(false);
      checkOlderVersionButtonState(false);
      checkNewerVersionButtonState(true);
      checkViewVersionListButtonState(true);
      checkTextOnVersionPanel("");

     IDE.EDITOR.closeTab(0);
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
      IDE.NAVIGATION.selectItem(WS_URL + TEST_FOLDER + "/");
      //Open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkViewVersionHistoryButtonPresent(false);
      
     IDE.EDITOR.deleteFileContent();
      saveAsUsingToolbarButton(FILE_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      //Edit and save file
     IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
     IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
     IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
     IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();

      //Open version panel
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      checkViewVersionListButtonState(true);
      
//      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
//      Thread.sleep(TestConstants.SLEEP);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      
      checkViewVersionsListPanel(true);
      checkOpenVersionButtonState(false);
      checkVersionListSize(5);
      clickCloseVersionListPanelButton();
      //Open version:
      checkOpenVersion(1, version1Text + version2Text + version3Text);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Open version:
      checkOpenVersion(3, version1Text);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //Open version:
      checkOpenVersion(0, version1Text + version2Text + version3Text + version4Text);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      //Open version:
      checkOpenVersion(2, version1Text + version2Text);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(false);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);

      //Open version:
      checkOpenVersion(4, "");
      checkOlderVersionButtonState(false);
      checkNewerVersionButtonState(true);

      //View newer version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_NEWER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(true);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel(version1Text);

      //View older version:
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_OLDER_VERSION);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkOlderVersionButtonState(false);
      checkNewerVersionButtonState(true);
      checkTextOnVersionPanel("");

     IDE.EDITOR.closeTab(0);
   }

}
