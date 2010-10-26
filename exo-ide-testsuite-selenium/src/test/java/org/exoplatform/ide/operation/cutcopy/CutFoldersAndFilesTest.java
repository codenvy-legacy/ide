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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CutFoldersAndFilesTest extends BaseTest
{

   /**
    * 
    */
   private static final String FOLDER_2 = "test 2";

   /**
    * 
    */
   private static final String FOLDER_1 = "test 1";

   //IDE-117
   @Test
   public void testCutOperation() throws Exception
   {
      //      1.Open Gadget window and create next folders' structure in the workspace root:
      //      "test 1/gadget.xml" file with sample content
      //      "test 1/test.groovy" file with sample content
      //      "test 1/test 1.1" folder
      //      "test 2/gadget.xml" file with sample content
      //      "test 2/test 1.1" folder
      createFolder(FOLDER_1);

      runCommandFromMenuNewOnToolbar(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      saveAsUsingToolbarButton("gadgetxml");

      String oldText = getTextFromCodeEditor(0);
      closeTab("0");

      runCommandFromMenuNewOnToolbar(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      saveAsUsingToolbarButton("testgroovy");

      String omg = selenium.getText("//body[@class='editbox']");

      closeTab("0");

      createFolder("test 1-1");

      selectRootOfWorkspaceTree();

      createFolder(FOLDER_2);

      openOrCloseFolder(FOLDER_1);

      selectItemInWorkspaceTree(FOLDER_2);

      createFolder("test 1-1");

      runCommandFromMenuNewOnToolbar(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      selectItemInWorkspaceTree(FOLDER_2);

      saveAsUsingToolbarButton("gadget1xml");

      closeTab("0");

      //    Open Gadget window, open all created files.
      openFileFromNavigationTreeWithCodeEditor("gadgetxml", false);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      //    Open Gadget window, open all created files.
      openFileFromNavigationTreeWithCodeEditor("testgroovy", false);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      openFileFromNavigationTreeWithCodeEditor("gadget1xml", false);
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

      checkButtons();

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

      checkButtons();

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      //      Select folders "test 1/test 1.1", and root folder.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkButtons();

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

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      checkPaste(true);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[5]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      assertTrue(selenium.isTextPresent("412 Precondition Failed"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertTrue(selenium.isTextPresent("Precondition Failed"));
      assertTrue(selenium.isTextPresent("OK"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkPaste(true);

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

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      selectEditorTab(0);

      assertEquals(oldText, getTextFromCodeEditor(0));

      selectEditorTab(1);

      assertEquals(omg, getTextFromCodeEditor(1));

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);

      String url =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetItemURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");
      selenium.click("scLocator=//IButton[ID=\"ideGetItemURLFormOkButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      System.out.println(url);

      selenium.open(url);
      selenium.waitForPageToLoad("" + TestConstants.PAGE_LOAD_PERIOD);

      assertTrue(selenium.isElementPresent("link=testgroovy"));
      assertTrue(selenium.isElementPresent("link=gadgetxml"));
      assertTrue(selenium.isElementPresent("link=test 1-1"));

      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[0]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);

      String url1 =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetItemURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");
      System.out.println(url1);

      selenium.click("scLocator=//IButton[ID=\"ideGetItemURLFormOkButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.open(url1);
      selenium.waitForPageToLoad("" + TestConstants.PAGE_LOAD_PERIOD);

      assertFalse(selenium.isElementPresent("link=testgroovy"));
      assertFalse(selenium.isElementPresent("link=gadgetxml"));
      assertFalse(selenium.isElementPresent("link=test 1-1"));

      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkPaste(false);

      selenium.controlKeyDown();

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[5]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //      Remove all created folders and files
      deleteSelectedItems();
   }

   /**
    * @throws Exception
    */
   private void checkPaste(boolean enabled) throws Exception
   {
      checkToolbarButtonState(MenuCommands.Edit.PASTE_TOOLBAR, enabled);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, enabled);

   }

   /**
    * @throws Exception
    */
   private void checkButtons() throws Exception
   {
      checkToolbarButtonState(MenuCommands.Edit.CUT_TOOLBAR, false);
      checkToolbarButtonState(MenuCommands.Edit.COPY_TOOLBAR, false);
      checkToolbarButtonState(MenuCommands.Edit.PASTE_TOOLBAR, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
