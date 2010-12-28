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
import static org.junit.Assert.assertTrue;

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
public class IDE113CopyTest extends BaseTest
{

   @AfterClass
   public static void tearDown()
   {
      cleanDefaultWorkspace();
   }
   
   /*
    * Create folder "/Test 1"
    * Create Google Gadget
    * Select folder "/Test 1"
    * Save file as "gadget_xml"
    * Close editor
    * Create Groovy Script
    * Select folder "/Test 1"
    * Save file as "test_groovy"
    * Close editor
    * Create folder "/Test 1/Test 1-1"
    * Create folder "/Test 1/Test 1-2"
    * Open file "/Test 1/test_groovy"
    * Open file "/Test 1/gadget_xml"
    * Select all files in folder "/Test 1"
    * Run menu command "Edit/Copy"
    * 
    * Select file "/Test 1/gadget_xml"
    * Delete selected item
    * Select folder "/Test 1/Test 1-1"
    * Delete selected item
    * 
    * Select root
    * Run menu command "Edit/Paste"
    * Run menu command "View/Get URL" and receive WebDAV url of root
    * 
    * Navigate to received URL and check items "test_groovy" and "Test 1-2" for existing
    * 
    * Go Back
    * 
    * Check file is opened
    * Check content of opened file
    * 
    * Check state of "Edit/Paste" command ( it must be false ) 
    * 
    * Select all items in root
    * Delete selected items
    * 
    */

   //IDE-113
   //TODO doesn't work on Windows
   @Test
   public void copyOperationTestIde113() throws Exception
   {
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      //Open Gadget window and create next folders' structure in the workspace root:
      //      "test 1/gadget.xml" file with sample content
      //      "test 1/test.groovy" file with sample content
      //      "test 1/test 1.1" folder
      //      "test 1/test 1.2" folder
      createFolder("Test 1");

      /*
       * create gadget
       */
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);

      /*
       * select item in tree
       */
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * save as "gadget.xml"
       */
      saveAsUsingToolbarButton("gadget_xml");

      IDE.editor().closeTab(0);
     
//      /*
//       * select element in tree
//       */
//      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
//      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * create groovy file
       */
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);

      /*
       * select element in tree
       */
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * save as
       */
      saveAsUsingToolbarButton("test_groovy");

      /*
       * get file content from editor
       */
      String oldText = getTextFromCodeEditor(0);
      IDE.editor().closeTab(0);
      
      /*
       * create folder
       */
      createFolder("Test 1-1");

      /*
       * select element in tree
       */
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * crate folder
       */
      createFolder("Test 1-2");

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[5]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      openFileFromNavigationTreeWithCodeEditor("test_groovy", false);

      /*
       * select element in tree
       */
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      openFileFromNavigationTreeWithCodeEditor("gadget_xml", false);      

      //      Select "test 1/gadget.xml", "test 1/test.groovy", "test 1/test 1.1",  "test 1/test 1.2" items in the Workspace Panel.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[5]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
//      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[1]");
//      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.toolbar().checkButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      
      // Call the "Edit->Copy Items" topmenu command.
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU);
      
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[0]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      deleteSelectedItems();

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      deleteSelectedItems();

      //      Delete "test 1/test 1.1" folder and "test 1/gadget.xml" file. Select root item. Click on "Paste" button.
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);

      String url =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetItemURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");
      System.out.println(url);

      selenium.click("scLocator=//IButton[ID=\"ideGetItemURLFormOkButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.open(url);
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);

      assertTrue(selenium.isElementPresent("link=test_groovy"));
      assertTrue(selenium.isElementPresent("link=Test 1-2"));

      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);      
      
      checkCodeEditorOpened(0);

      assertEquals(oldText, getTextFromCodeEditor(0));
      
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      
      IDE.toolbar().checkButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(500);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(500);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(500);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(500);
      selenium.controlKeyUp();

      deleteSelectedItems();
   }

}
