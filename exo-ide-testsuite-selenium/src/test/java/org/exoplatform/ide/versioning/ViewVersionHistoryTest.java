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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class ViewVersionHistoryTest extends VersioningTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private final static String TEST_FOLDER = "testFolder";

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

   @Test
   public void testViewVersionHistoryOneFile() throws Exception
   {
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      //Open new file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      //Save file
      selectItemInWorkspaceTree(TEST_FOLDER);
      saveAsUsingToolbarButton(FILE_1);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent();

      typeTextIntoEditor(0, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      checkViewVersionHistoryButtonPresent();

      //Open version panel
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text);

      //Close version panel
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkVersionPanelState(false);

      //Open version panel
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkVersionPanelState(true);
      //Close file:
      closeTab("0");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
   }

   @Test
   public void testOpenCloseVersionPanel() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.PAGE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      openOrCloseFolder(TEST_FOLDER);
      //Open file
      openFileFromNavigationTreeWithCodeEditor(FILE_1, true);
      checkViewVersionHistoryButtonPresent();
      //Go to end of document
      selenium.keyPressNative("" + KeyEvent.VK_END);
      //Edit file and save:
      typeTextIntoEditor(0, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Edit file and save:
      typeTextIntoEditor(0, version3Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Close version panel
      closeVersionPanel();
      checkVersionPanelState(false);
      //Open again with button
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text);
      //Close again with button
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      checkVersionPanelState(false);

      //Edit file and save
      selectIFrameWithEditor(0);
      selenium.clickAt("//body[@class='editbox']", "5,5");
      selenium.keyPressNative("" + KeyEvent.VK_END);
      selectMainFrame();
      typeTextIntoEditor(0, version4Text);
      saveCurrentFile();
      //Open again with button
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD * 2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text + version2Text + version3Text + version4Text);
      //Close file
      closeTab("0");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
   }

   @Test
   public void testVersionPanelForFewFiles() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad(TestConstants.PAGE_LOAD_PERIOD + "");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      openOrCloseFolder(TEST_FOLDER);
      //Open file
      openFileFromNavigationTreeWithCodeEditor(FILE_1, true);
      checkViewVersionHistoryButtonPresent();
      
      //Open new file:
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_VERSION_HISTORY, false);
      
      //Select tab with saved file
      selectEditorTab(0);
      checkViewVersionHistoryButtonPresent();
      
      //Select tab with unsaved file
      selectEditorTab(1);
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_VERSION_HISTORY, false);
      saveAsUsingToolbarButton(FILE_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkViewVersionHistoryButtonPresent();
      //Edit second file and save
      typeTextIntoEditor(1, version1Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      typeTextIntoEditor(1, version2Text);
      saveCurrentFile();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Open version history for second file
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(getTextFromCodeEditor(1));
      
      //Select first file:
      selectEditorTab(0);
      checkVersionPanelState(false);
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(getTextFromCodeEditor(0));
      
      //Select second file: 
      selectEditorTab(1);
      checkVersionPanelState(false);
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(getTextFromCodeEditor(1));
      
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, false);
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_VERSION_HISTORY, false);
      assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
      // View version button
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_VERSION, false);
      //Restore button
      checkToolbarButtonPresentOnRightSide(MenuCommands.File.RESTORE_VERSION, false);
      //Newer version button
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
      //Older version button
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_OLDER_VERSION, false);
      
      saveAsByTopMenu(FILE_3);
      checkViewVersionHistoryButtonPresent();
      
      typeTextIntoEditor(2, version1Text);
      saveCurrentFile();
      typeTextIntoEditor(2, version2Text);
      saveCurrentFile();
      typeTextIntoEditor(2, version3Text);
      
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD*2);
      checkVersionPanelState(true);
      checkTextOnVersionPanel(version1Text+version2Text);

      closeTab("0");
      closeTab("0");
      closeTab("0");
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
