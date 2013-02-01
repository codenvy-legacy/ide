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
 * 
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
      }
   }

   @Test
   public void testUndoRedoEditingInCodeEditor() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.saveAs(1, UNDO_REDO_TXT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + UNDO_REDO_TXT);
      IDE.EDITOR.typeTextIntoEditor("1");
      IDE.EDITOR.waitFileContentModificationMark(UNDO_REDO_TXT);

      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);

      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.typeTextIntoEditor("2");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.EDITOR.typeTextIntoEditor("3");

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor());

      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "z");
      assertEquals("1", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      assertEquals("", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      assertEquals("1", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "y");
      assertEquals("123", IDE.EDITOR.getTextFromCodeEditor());

      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.typeTextIntoEditor("a");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "y");
      assertEquals("a12", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.UNDO);
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(UNDO_REDO_TXT);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.REDO);
      assertEquals("a12", IDE.EDITOR.getTextFromCodeEditor());

      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "z");
      assertEquals("12", IDE.EDITOR.getTextFromCodeEditor());
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.typeTextIntoEditor("text");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitActiveFile();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.waitActiveFile();
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.Editor.REDO);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Editor.UNDO);
      IDE.MENU.waitCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      IDE.MENU.waitCommandDisabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      IDE.EDITOR.forcedClosureFile(1);
      IDE.EDITOR.forcedClosureFile(1);
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
      }
   }

}
