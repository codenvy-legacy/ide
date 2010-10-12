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
import org.exoplatform.ide.ToolbarCommands;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class IDE116CopyTest extends BaseTest
{

   @AfterClass
   public static void tearDown()
   {
      cleanDefaultWorkspace();
   }

   /*
    * Create folder "/Test 1"
    * Create folder "/Test 1/Test 1.1" 
    * Create new groovy script
    * 
    * Type "groovy file content"
    * Save file as "/Test 1/Test 1.1/test.groovy"
    * 
    * Select folder "/Test 1/Test 1.1"
    * 
    * Check Paste must be disabled
    * Check Copy must be enabled
    * 
    * Call "Edit/Copy" in menu
    * 
    * Check Paste must be enabled
    * 
    * Select root in workspace tree and call "Edit/Paste"
    * 
    * Edit currently opened file
    * Call "Ctrl+S"
    * Close opened file
    * 
    * Open "/Test 1.1/test.groovy"
    * Check it content
    * 
    */

   @Test
   public void copyOperationTestIde116() throws Exception
   {
      /*
       * Create folder "/Test 1"
       */
      createFolder("Test 1");

      /*
       * Create folder "/Test 1/Test 1.1"
       */
      createFolder("Test 1.1");

      /*
       * Create new groovy script
       */
      runCommandFromMenuNewOnToolbar(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      /*
       *  Type "groovy file content"
       */
      selectIFrameWithEditor(0);
      selenium.typeKeys("//body[@class='editbox']", "file content");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selectMainFrame();

      /*  
       * Save file as "/Test 1/Test 1.1/test.groovy"
       */
      saveAsUsingToolbarButton("test.groovy");

      /* 
      * Select folder "/Test 1/Test 1.1"
      */
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * Check Copy must be enabled
       */
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);
      checkToolbarButtonPresentOnLeftSide(MenuCommands.Edit.COPY_TOOLBAR, true);
      checkToolbarButtonState(MenuCommands.Edit.COPY_TOOLBAR, true);

      /* 
       * Check Paste must be disabled
       */
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      checkToolbarButtonPresentOnLeftSide(MenuCommands.Edit.PASTE_TOOLBAR, true);
      checkToolbarButtonState(MenuCommands.Edit.PASTE_TOOLBAR, false);

      /* 
      * Call "Edit/Copy" in menu
      */
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU);

      /* 
      * Check Paste must be enabled
      */
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      checkToolbarButtonState(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /* 
      * Select root in workspace tree and call "Edit/Paste"
      */
      selectRootOfWorkspaceTree();
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      /* 
      * Edit currently opened file
      */
      selectIFrameWithEditor(0);

      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_HOME);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selenium.typeKeys("//body[@class='editbox']", "updated ");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      selectMainFrame();

      /* 
      * Call "Ctrl+S"
      */
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_S);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      /* 
      * Close opened file
      */
      closeTab("0");

      /* 
      * Open "/Test 1.1/test.groovy"
      */
      selectRootOfWorkspaceTree();
      runToolbarButton(ToolbarCommands.File.REFRESH);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[0]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      openFileFromNavigationTreeWithCodeEditor("test.groovy", false);

      /* 
      * Check it content
      * 
      */

      String fileContent = getTextFromCodeEditor(0);
      assertEquals("file content", fileContent);

      /*
       * close file
       */
      closeTab("0");

      /*
       * delete files
       */
      selectRootOfWorkspaceTree();
      runToolbarButton(ToolbarCommands.File.REFRESH);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      deleteSelectedItems();

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      deleteSelectedItems();
   }

}
