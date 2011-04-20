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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
import org.junit.Test;

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
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.SLEEP);

      saveAsByTopMenu(UNDO_REDO_TXT);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "1");
		
		Thread.sleep(3000);
		IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
		IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
		
		IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
		IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
		
		AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "2");
		Thread.sleep(3000);
		AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "3");
		Thread.sleep(3000);
		
		IDE.toolbar().runCommand(ToolbarCommands.Editor.UNDO);
		Thread.sleep(3000);
		
		String currentText = IDE.editor().getTextFromCodeEditor(0);
		
		assertEquals("12", currentText);
		
		IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
		
      
      IDE.editor().runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Z);//Press Ctrl+Z
      Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("1", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
//      Thread.sleep(TestConstants.SLEEP);
      
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, false);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      IDE.toolbar().runCommand(ToolbarCommands.Editor.REDO);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("1", currentText);
      
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
//      Thread.sleep(TestConstants.SLEEP);
      
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("12", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      IDE.editor().runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Y);//Press Ctrl+Y
      Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("123", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      IDE.toolbar().runCommand(ToolbarCommands.Editor.UNDO);
      Thread.sleep(TestConstants.SLEEP);
      
      currentText = IDE.editor().getTextFromCodeEditor(0);
      
      assertEquals("12", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "a");
      
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      IDE.editor().runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Y);//Press Ctrl+Y
      Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("a12", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      IDE.toolbar().runCommand(ToolbarCommands.Editor.UNDO);
      Thread.sleep(TestConstants.SLEEP);
      
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("12", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      
      
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.toolbar().runCommand(ToolbarCommands.Editor.REDO);
      Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("a12", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      
      IDE.editor().runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Z); //Press Ctrl+Z
      Thread.sleep(TestConstants.SLEEP_SHORT);
      Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.editor().getTextFromCodeEditor(0);
      assertEquals("12", currentText);
      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, false);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      IDE.editor().typeTextIntoEditor(1, "text");
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      IDE.editor().selectTab(0);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);
      
      IDE.editor().selectTab(1);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      
      IDE.editor().closeUnsavedFileAndDoNotSave(1);
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
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