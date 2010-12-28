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
package org.exoplatform.ide.operation.cutcopy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class CutFoldersAndFilesTest extends BaseTest
{

   private static final String FOLDER_1 = "test 1";
   
   private static final String FOLDER_2 = "test 2";
   
   private static final String FOLDER_3 = "test 1-1";

   
   private static final String FILE1 = "gadgetxml";
   
   private static final String FILE2 = "testgroovy";
   
   private static final String FILE3 = "gadget1xml";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
  
   /**
    *  Test from TestLink IDE-117
    * @throws Exception
    */
   @Test
   public void testCutOperation() throws Exception
   {
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      //      1.Open Gadget window and create next folders' structure in the workspace root:
      //      "test 1/gadget.xml" file with sample content
      //      "test 1/test.groovy" file with sample content
      //      "test 1/test 1.1" folder
      //      "test 2/gadget.xml" file with sample content
      //      "test 2/test 1.1" folder
      createFolder(FOLDER_1);

      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      saveAsUsingToolbarButton(FILE1);

      String oldText = getTextFromCodeEditor(0);
      IDE.editor().closeTab(0);
      

      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      saveAsUsingToolbarButton(FILE2);

      String omg = selenium.getText("//body[@class='editbox']");

      IDE.editor().closeTab(0);

      createFolder(FOLDER_3);

      selectRootOfWorkspaceTree();

      createFolder(FOLDER_2);

      openOrCloseFolder(FOLDER_1);

      selectItemInWorkspaceTree(FOLDER_2);

      createFolder(FOLDER_3);

      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      selectItemInWorkspaceTree(FOLDER_2);

      saveAsUsingToolbarButton(FILE3);
      
      IDE.editor().closeTab(0);

      //    Open Gadget window, open all created files.
      openFileFromNavigationTreeWithCodeEditor(FILE1, false);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      //    Open Gadget window, open all created files.
      openFileFromNavigationTreeWithCodeEditor(FILE2, false);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      openFileFromNavigationTreeWithCodeEditor(FILE3, false);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      //      Select file "test 1/gadgetxml", and folder "test 2".
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[7]/col[1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[5]/col[1]");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      checkButtonsDisabled();

      selectRootOfWorkspaceTree();

      //      Select files "test 1/gadgetxml", and "test 2/gadgetxml".
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[7]/col[1]");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      checkButtonsDisabled();

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      //      Select folders "test 1/test 1.1", and root folder.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkButtonsDisabled();

      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");

      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //      Select "test 1/gadgetxml", "test 1/test 1.1" items in the Workspace Panel and press the "Cut" toolbar button.
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      checkPasteButton(true);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[5]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      assertTrue(selenium.isTextPresent("412 Precondition Failed"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertTrue(selenium.isTextPresent("Precondition Failed"));
      assertTrue(selenium.isTextPresent("OK"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkPasteButton(true);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.controlKeyDown();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      IDE.editor().selectTab(0);

      assertEquals(oldText, getTextFromCodeEditor(0));

      IDE.editor().selectTab(1);

      assertEquals(omg, getTextFromCodeEditor(1));

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);

      String url =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetItemURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");
      selenium.click("scLocator=//IButton[ID=\"ideGetItemURLFormOkButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      System.out.println(url);

      selenium.open(url);
      selenium.waitForPageToLoad("" + TestConstants.PAGE_LOAD_PERIOD);

      assertTrue(selenium.isElementPresent("link="+FILE2));
      assertTrue(selenium.isElementPresent("link="+FILE1));
      assertTrue(selenium.isElementPresent("link="+FOLDER_3));

      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(10000);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[0]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);

      String url1 =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetItemURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");
      System.out.println(url1);

      selenium.click("scLocator=//IButton[ID=\"ideGetItemURLFormOkButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.open(url1);
      selenium.waitForPageToLoad("" + TestConstants.PAGE_LOAD_PERIOD);

      assertFalse(selenium.isElementPresent("link="+FILE2));
      assertFalse(selenium.isElementPresent("link="+FILE1));
      assertFalse(selenium.isElementPresent("link="+FOLDER_3));

      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(10000);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkPasteButton(false);

     //Close Tabs
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
   }

   /**
    * Check "Paste" buttons state (enabled/disabled).
    * 
    * @throws Exception
    */
   private void checkPasteButton(boolean enabled) throws Exception
   {
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, enabled);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, enabled);

   }

   /**
    * Check copy/cut/paste buttons are disabled in top menu and on toolbar.
    * 
    * @throws Exception
    */
   private void checkButtonsDisabled() throws Exception
   {
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, false);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, false);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);
   }

   /**
    * Clear test results.
    * 
    * @throws Exception
    */
   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
      try
      {
         VirtualFileSystemUtils.delete(URL +FOLDER_1);
         VirtualFileSystemUtils.delete(URL +FOLDER_2);
         VirtualFileSystemUtils.delete(URL +FOLDER_3);
         VirtualFileSystemUtils.delete(URL +FILE1);
         VirtualFileSystemUtils.delete(URL +FILE2);
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
