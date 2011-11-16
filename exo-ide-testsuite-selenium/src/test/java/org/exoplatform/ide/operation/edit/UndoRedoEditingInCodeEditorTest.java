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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class UndoRedoEditingInCodeEditorTest extends BaseTest
{
   private static String UNDO_REDO_TXT = "undo-redo.txt";

   private static String PROJECT = UndoRedoEditingInCodeEditorTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testUndoRedoEditingInCodeEditor() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, UNDO_REDO_TXT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + UNDO_REDO_TXT);
      IDE.EDITOR.typeTextIntoEditor(0, "1");
      IDE.EDITOR.waitFileContentModificationMark(UNDO_REDO_TXT);

      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.typeTextIntoEditor(0, "2");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.EDITOR.typeTextIntoEditor(0, "3");

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor(0));

      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "z");
      assertEquals("1", IDE.EDITOR.getTextFromCodeEditor(0));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      assertEquals("", IDE.EDITOR.getTextFromCodeEditor(0));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      assertEquals("1", IDE.EDITOR.getTextFromCodeEditor(0));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor(0));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "y");
      assertEquals("123", IDE.EDITOR.getTextFromCodeEditor(0));

      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor(0));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.typeTextIntoEditor(0, "a");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "y");
      assertEquals("a12", IDE.EDITOR.getTextFromCodeEditor(0));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor(0));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(UNDO_REDO_TXT);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      assertEquals("a12", IDE.EDITOR.getTextFromCodeEditor(0));

      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "z");
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor(0));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.typeTextIntoEditor(1, "text");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + UNDO_REDO_TXT);
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.REDO));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Editor.UNDO));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING));

      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
