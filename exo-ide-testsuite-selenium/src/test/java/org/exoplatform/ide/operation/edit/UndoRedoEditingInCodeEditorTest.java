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
package org.exoplatform.ide.operation.edit;

import static org.exoplatform.ide.CloseFileUtils.closeUnsavedFileAndDoNotSave;
import static org.junit.Assert.*;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

//IDE-57:Undo/Redo Editing in Code editor

public class UndoRedoEditingInCodeEditorTest extends BaseTest
{
   /**
    * TestCase IDE-57
    */
   
   private static String UNDO_REDO_TXT = "undo-redo.txt"; 
   
   private final static String STORAGE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @Test
   	        
	   public void testUndoRedoEditingInCodeEditor() throws Exception {
			
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar("Text File");
      Thread.sleep(TestConstants.SLEEP);

      saveAsByTopMenu(UNDO_REDO_TXT);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "1");
		
		Thread.sleep(3000);
		checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
		checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
		
		checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
		checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
		
		AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "2");
		Thread.sleep(3000);
		AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "3");
		Thread.sleep(3000);
		
		runToolbarButton(ToolbarCommands.Editor.UNDO);
		Thread.sleep(3000);
		
		String currentText = getTextFromCodeEditor(0);
		
		assertEquals("12", currentText);
		
		checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
		
      
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Z);//Press Ctrl+Z
      Thread.sleep(TestConstants.SLEEP);
      currentText = getTextFromCodeEditor(0);
      assertEquals("1", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);      
      currentText = getTextFromCodeEditor(0);
      assertEquals("", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, false);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      runToolbarButton(ToolbarCommands.Editor.REDO);
      Thread.sleep(TestConstants.SLEEP);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      currentText = getTextFromCodeEditor(0);
      assertEquals("1", currentText);
      
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);      
      currentText = getTextFromCodeEditor(0);
      assertEquals("12", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Y);//Press Ctrl+Y
      Thread.sleep(TestConstants.SLEEP);
      currentText = getTextFromCodeEditor(0);
      assertEquals("123", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      runToolbarButton(ToolbarCommands.Editor.UNDO);
      Thread.sleep(TestConstants.SLEEP);
      
      currentText = getTextFromCodeEditor(0);
      
      assertEquals("12", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "a");
      
      Thread.sleep(TestConstants.SLEEP);
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Y);//Press Ctrl+Y
      Thread.sleep(TestConstants.SLEEP);
      currentText = getTextFromCodeEditor(0);
      assertEquals("a12", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      runToolbarButton(ToolbarCommands.Editor.UNDO);
      Thread.sleep(TestConstants.SLEEP);
      
      currentText = getTextFromCodeEditor(0);
      assertEquals("12", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      
      
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      
      runToolbarButton(ToolbarCommands.Editor.REDO);
      Thread.sleep(TestConstants.SLEEP);
      currentText = getTextFromCodeEditor(0);
      assertEquals("a12", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Z); //Press Ctrl+Z
      Thread.sleep(TestConstants.SLEEP_SHORT);
      Thread.sleep(TestConstants.SLEEP);
      currentText = getTextFromCodeEditor(0);
      assertEquals("12", currentText);
      
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      runCommandFromMenuNewOnToolbar("XML File");
      Thread.sleep(TestConstants.SLEEP);
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, false);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      typeTextIntoEditor(1, "text");
      Thread.sleep(TestConstants.SLEEP);
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      selectEditorTab(0);
      Thread.sleep(TestConstants.SLEEP);
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, true);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      selectEditorTab(1);
      Thread.sleep(TestConstants.SLEEP);
      checkToolbarButtonState(ToolbarCommands.Editor.REDO, false);
      checkToolbarButtonState(ToolbarCommands.Editor.UNDO, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      closeUnsavedFileAndDoNotSave(1);
      closeUnsavedFileAndDoNotSave(0);
   }
   
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(STORAGE_URL + UNDO_REDO_TXT);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}