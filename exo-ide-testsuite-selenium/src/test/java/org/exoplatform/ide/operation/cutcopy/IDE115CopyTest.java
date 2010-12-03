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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class IDE115CopyTest extends BaseTest
{

   @AfterClass
   public static void tearDown()
   {
      cleanDefaultWorkspace();
   }

   /*
    * Create folder "Test 1" in root. After this folder "Test 1" must be selected.
    * Create Groovy Script file
    * Type "hello"
    * Save as "test.groovy"
    * Close editor
    * Select "/Test1/test.groovy" file
    * 
    * Check Cut and Copy commands must be enabled
    * Check Paste command must be disabled
    * Click Copy command on toolbar
    * 
    * Check Paste must be enabled 
    * 
    * Select Root in workspace panel
    * Click Paste command
    * 
    * Check Paste command must be disabled
    * 
    * Open "Test 1" folder
    * Open file "/Test 1/test.groovy"
    * Type "file content"
    * Call "Ctrl+S"
    * Close file 
    * 
    * Open file "/Test 1/test.groovy"
    * 
    * Open "/test.groovy"
    * Check content of the file must be "hello"
    * 
    * Close both files
    * 
    * Delete files
    * 
    */

   @Test
   public void copyOperationTestIde115() throws Exception
   {
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      /*
      * Create folder "Test 1" in root. After this folder "Test 1" must be selected.
       */
      createFolder("Test 1");

      /*
       * Create Groovy Script file
       */
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      /*
       * Type "hello"
       */
      selectIFrameWithEditor(0);
      selenium.typeKeys("//body[@class='editbox']", "hello");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selectMainFrame();

      /*
       * Save as "test.groovy"
       */
      saveAsUsingToolbarButton("test.groovy");

      /*
       * Close editor
       */
      IDE.editor().closeTab(0);

      /*
       * Select "/Test1/test.groovy" file
       */
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * Check Cut and Copy commands must be enabled
       */

      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);
      IDE.toolbar().checkButtonExistAtLeft(MenuCommands.Edit.COPY_TOOLBAR, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, true);

      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.toolbar().checkButtonExistAtLeft(MenuCommands.Edit.CUT_TOOLBAR, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, true);

      /*
       * Check Paste command must be disabled
       */
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.toolbar().checkButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);

      /*
       * Click Copy command on toolbar
       */
      IDE.toolbar().runCommand(MenuCommands.Edit.COPY_TOOLBAR);

      /*
       * Check Paste must be enabled 
       */
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * Select Root in workspace panel
       */
      selectRootOfWorkspaceTree();

      /*
       * Click Paste command
       */
      IDE.toolbar().runCommand(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Check Paste command must be disabled
       */
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.toolbar().checkButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);

      /*
       * Open "Test 1" folder
       */
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[0]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * Open file "/Test 1/test.groovy"
       */
      openFileFromNavigationTreeWithCodeEditor("test.groovy", false);

      /*
       * Type "file content"
       */
      selectIFrameWithEditor(0);

      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_END);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.typeKeys("//body[@class='editbox']", " world");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selectMainFrame();

      /*
       * Save file
       */
      saveCurrentFile();
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      /*
      * Close file
      */
      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      /*
      * Open file "/Test 1/test.groovy"
      */
      openFileFromNavigationTreeWithCodeEditor("test.groovy", false);

      /*
       * Open "/test.groovy"
       */
      selectRootOfWorkspaceTree();
      IDE.toolbar().runCommand(MenuCommands.File.REFRESH_TOOLBAR);
      openFileFromNavigationTreeWithCodeEditor("test.groovy", false);

      /*
       * Check content of the file must be "hello"
       */

      String file1Content = getTextFromCodeEditor(0);
      String file2Content = getTextFromCodeEditor(1);

      assertEquals("hello world", file1Content);
      assertEquals("hello", file2Content);

      // Close both files
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);

      /*
       * Delete files
       */
      selectItemInWorkspaceTree("Test 1");
      deleteSelectedItems();

      selectItemInWorkspaceTree("test.groovy");
      deleteSelectedItems();
   }

}
