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

   private final static String STORAGE_URL =
      BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   @Test
   public void testUndoRedoEditingInCodeEditor() throws Exception
   {

      waitForRootElement();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      waitForRootElement();

      saveAsByTopMenu(UNDO_REDO_TXT);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "1");

      //Thread.sleep(3000);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "2");
      //Thread.sleep(3000);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "3");
      //Thread.sleep(3000);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      //Thread.sleep(3000);

      String currentText = IDE.EDITOR.getTextFromCodeEditor(0);

      assertEquals("12", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Z);//Press Ctrl+Z
      //Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("1", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      //      Thread.sleep(TestConstants.SLEEP);

      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, false);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      //Thread.sleep(TestConstants.SLEEP);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("1", currentText);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      //      Thread.sleep(TestConstants.SLEEP);

      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("12", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Y);//Press Ctrl+Y
      //Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("123", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      //Thread.sleep(TestConstants.SLEEP);

      currentText = IDE.EDITOR.getTextFromCodeEditor(0);

      assertEquals("12", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "a");

      //Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Y);//Press Ctrl+Y
      //Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("a12", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      //Thread.sleep(TestConstants.SLEEP);

      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("12", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      saveCurrentFile();
      // Thread.sleep(TestConstants.SLEEP);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      //Thread.sleep(TestConstants.SLEEP);
      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("a12", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_Z); //Press Ctrl+Z
      currentText = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals("12", currentText);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, false);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.EDITOR.typeTextIntoEditor(1, "text");

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.EDITOR.selectTab(0);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      IDE.EDITOR.selectTab(1);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.EDITOR.closeUnsavedFileAndDoNotSave(1);
      IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
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