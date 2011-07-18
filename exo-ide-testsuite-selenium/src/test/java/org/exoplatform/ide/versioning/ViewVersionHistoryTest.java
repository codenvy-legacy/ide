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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class ViewVersionHistoryTest extends BaseTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String TEST_FOLDER = ViewVersionHistoryTest.class.getSimpleName();

   private final static String FILE_0 = "File 0";

   private final static String FILE_4 = "File 4";

   private final static String FILE_1 = "File 1";

   private final static String FILE_2 = "File 2";

   private final static String FILE_3 = "File 3";

   private String version1Text = "This is version 1";

   private String version2Text = " One more";

   private String version3Text = " And more";

   private String version4Text = " The last";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test "View Version History" button state (enable/disable, show/hide)
    * with new files, just saved files, edited files.
    * 
    * @throws Exception
    */
   @Test
   public void testViewVersionHistoryButton() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      // open folder
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      //Open new file:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.CSS_FILE);
      IDE.EDITOR.waitTabPresent(0);

      //Check there is no "View Version History" button
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);
      //Save file:
      saveAsUsingToolbarButton(FILE_0);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_0);

      //View version history button is present but not active:
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      //File content is changed, but not saved yet:
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);
      saveCurrentFile();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //File content is saved 
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);

      //Close file:
      IDE.EDITOR.closeFile(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);
      //Open versioned file again:
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_0, false);
      IDE.EDITOR.waitTabPresent(0);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);

      //Open new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(1);
      //Check there is no "View Version History" button
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);
      //Save file:
      saveAsUsingToolbarButton(FILE_4);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_4);
      //View version history button is present but not active:
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      //Close second file, versioned file becomes active:
      IDE.EDITOR.closeFile(1);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      //Open second file (is not versioned):
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_4, false);
      IDE.EDITOR.waitTabPresent(1);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      //Select versioned file in editor:
      IDE.EDITOR.selectTab(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);

      //Select unversioned file in editor:
      IDE.EDITOR.selectTab(1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.closeFile(0);
   }

   /**
    * Test Version Panel when work with one file.
    * 
    * @throws Exception
    */
   @Test
   public void testViewVersionHistoryOneFile() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);
      //Save file
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      saveAsUsingToolbarButton(FILE_1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_1);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);

      //Open version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);

      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text);

      //Close version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewClosed();

      IDE.VERSIONS.checkVersionPanelState(false);

      //Open version panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      //Close file:
      IDE.EDITOR.closeFile(0);
      IDE.VERSIONS.waitVersionContentViewClosed();
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
   }

   /**
    * Test version panel for opening/closing using "View/Hide Version History"
    * and close button.
    * 
    * @throws Exception
    */
   @Test
   public void testOpenCloseVersionPanel() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      // open folder
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_1);

      //Open file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_1, true);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      //Go to end of document
      IDE.EDITOR.clickOnEditor();

      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      //Edit file and save:
      IDE.EDITOR.typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Edit file and save:
      IDE.EDITOR.typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);

      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Close version panel
      IDE.VERSIONS.closeVersionPanel();
      IDE.VERSIONS.waitVersionContentViewClosed();
      IDE.VERSIONS.checkVersionPanelState(false);
      //Open again with button
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);

      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Close again with button

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewClosed();
      IDE.VERSIONS.checkVersionPanelState(false);

      //Edit file and save
      IDE.EDITOR.clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      //Open again with button
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);
      //Close file
      IDE.EDITOR.closeFile(0);
      IDE.VERSIONS.waitVersionContentViewClosed();
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
   }

   /**
    * Test Version Panel when work with few files, switch between them, close/open 
    * files.
    * 
    * @throws Exception
    */
   @Test
   public void testVersionPanelForFewFiles() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      // open folder
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_1);

      //Open file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_1, true);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);

      //Open new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION_HISTORY, false);

      //Select tab with saved file
      IDE.EDITOR.selectTab(0);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);

      //Select tab with unsaved file
      IDE.EDITOR.selectTab(1);
      IDE.MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION_HISTORY, false);
      saveAsUsingToolbarButton(FILE_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      //Edit second file and save
      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(1, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      IDE.EDITOR.typeTextIntoEditor(1, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Open version history for second file
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(1));

      //Select first file:
      IDE.EDITOR.selectTab(0);
      IDE.VERSIONS.waitVersionContentViewClosed();
      IDE.VERSIONS.checkVersionPanelState(false);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(0));

      //Select second file: 
      IDE.EDITOR.selectTab(1);
      IDE.VERSIONS.waitVersionContentViewClosed();
      IDE.VERSIONS.checkVersionPanelState(false);
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(IDE.EDITOR.getTextFromCodeEditor(1));

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(false);
      // View version button
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION, false);
      //Restore button
      IDE.TOOLBAR.checkButtonExistAtRight(MenuCommands.File.RESTORE_VERSION, false);
      //Newer version button
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
      //Older version button
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_OLDER_VERSION, false);

      saveAsByTopMenu(FILE_3);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(false);

      selenium().keyPressNative("" + KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.EDITOR.typeTextIntoEditor(2, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.VERSIONS.checkViewVersionHistoryButtonPresent(true);
      IDE.VERSIONS.checkViewVersionHistoryButtonState(true);
      IDE.EDITOR.typeTextIntoEditor(2, version2Text);
      saveCurrentFile();
      IDE.EDITOR.typeTextIntoEditor(2, version3Text);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.VERSIONS.waitVersionContentViewOpen();
      IDE.TOOLBAR.waitForButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
      IDE.VERSIONS.checkVersionPanelState(true);
      IDE.VERSIONS.checkTextOnVersionPanel(version1Text + version2Text);

      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.closeFile(0);
      //TODO this block should be remove after fix problem in issue IDE-804. File does not should be modified  
      if (IDE.EDITOR.isFileContentChanged(0))
      {

         IDE.EDITOR.closeTabIgnoringChanges(0);
      }
      else
         IDE.EDITOR.closeFile(0);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
