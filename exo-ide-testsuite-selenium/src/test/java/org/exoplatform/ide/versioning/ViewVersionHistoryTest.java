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

import static org.junit.Assert.assertFalse;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
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
public class ViewVersionHistoryTest extends VersioningTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String TEST_FOLDER = ViewVersionHistoryTest.class.getSimpleName();

   private final static String FILE_0 = "File 0";

   private final static String FILE_4 = "File 4";

   private final static String FILE_1 = "File 1";

   private final static String FILE_2 = "File 2";

   private final static String FILE_3 = "File 3";

   private String version1Text = "This is version 1.";

   private String version2Text = " One more.";

   private String version3Text = " And more.";

   private String version4Text = " The last";

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
    * Test "View Version History" button state (enable/disable, show/hide)
    * with new files, just saved files, edited files.
    * 
    * @throws Exception
    */
   @Test
   public void testViewVersionHistoryButton() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      // open folder
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);     
      Thread.sleep(TestConstants.SLEEP);
      
      //Open new file:
      selectItemInWorkspaceTree(TEST_FOLDER);      
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.CSS_FILE);
      //Check there is no "View Version History" button
      checkViewVersionHistoryButtonPresent(false);
      //Save file:
      saveAsUsingToolbarButton(FILE_0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //View version history button is present but not active:
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);

      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      typeTextIntoEditor(0, version1Text);
      //File content is changed, but not saved yet:
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      saveCurrentFile();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //File content is saved 
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);

      //Close file:
      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(false);
      //Open versioned file again:
      openFileFromNavigationTreeWithCodeEditor(FILE_0, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);

      //Open new file:
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      //Check there is no "View Version History" button
      checkViewVersionHistoryButtonPresent(false);
      //Save file:
      saveAsUsingToolbarButton(FILE_4);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //View version history button is present but not active:
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);

      //Close second file, versioned file becomes active:
      IDE.editor().closeTab(1);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      //Open second file (is not versioned):
      openFileFromNavigationTreeWithCodeEditor(FILE_4, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);

      //Select versioned file in editor:
      IDE.editor().selectTab(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);

      //Select unversioned file in editor:
      IDE.editor().selectTab(1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);

      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
   }

   /**
    * Test Version Panel when work with one file.
    * 
    * @throws Exception
    */
   @Test
   public void testViewVersionHistoryOneFile() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(TEST_FOLDER);
      
      //Open new file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkViewVersionHistoryButtonPresent(false);
      //Save file
      selectItemInWorkspaceTree(TEST_FOLDER);
      saveAsUsingToolbarButton(FILE_1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      
      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);

      //Open version panel
      
//      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text);

      //Close version panel
//      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
//      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      checkVersionPanelState(false);

      //Open version panel
//      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
//      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      checkVersionPanelState(true);
      //Close file:
      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
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
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      // open folder
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);     
      Thread.sleep(TestConstants.SLEEP);
      
      //Open file
      openFileFromNavigationTreeWithCodeEditor(FILE_1, true);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      //Go to end of document
      IDE.editor().clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      //Edit file and save:
      typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Edit file and save:
      typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

//      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Close version panel
      closeVersionPanel();
      checkVersionPanelState(false);
      //Open again with button
  
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Close again with button
      
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      checkVersionPanelState(false);

      //Edit file and save
      IDE.editor().clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      //Open again with button
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);
      //Close file
      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
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
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      // open folder
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);     
      Thread.sleep(TestConstants.SLEEP);
      
      //Open file
      openFileFromNavigationTreeWithCodeEditor(FILE_1, true);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);

      //Open new file:
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION_HISTORY, false);

      //Select tab with saved file
      IDE.editor().selectTab(0);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);

      //Select tab with unsaved file
      IDE.editor().selectTab(1);
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION_HISTORY, false);
      saveAsUsingToolbarButton(FILE_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);

      //Edit second file and save
      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      typeTextIntoEditor(1, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      typeTextIntoEditor(1, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Open version history for second file
      
      //runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(getTextFromCodeEditor(1));

      //Select first file:
      IDE.editor().selectTab(0);
      checkVersionPanelState(false);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));

      //Select second file: 
      IDE.editor().selectTab(1);
      checkVersionPanelState(false);
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(getTextFromCodeEditor(1));

      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkViewVersionHistoryButtonPresent(false);
      assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
      // View version button
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION, false);
      //Restore button
      IDE.toolbar().checkButtonExistAtRight(MenuCommands.File.RESTORE_VERSION, false);
      //Newer version button
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
      //Older version button
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_OLDER_VERSION, false);

      saveAsByTopMenu(FILE_3);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(false);
      
      selenium.keyPressNative(""+KeyEvent.VK_END);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      typeTextIntoEditor(2, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent(true);
      checkViewVersionHistoryButtonState(true);
      typeTextIntoEditor(2, version2Text);
      saveCurrentFile();
      typeTextIntoEditor(2, version3Text);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text + version2Text);

      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
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
}
