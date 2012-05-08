/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.operation.contextmenu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 7, 2012 2:55:58 PM anya $
 * 
 */
public class EditorContextMenuTest extends BaseTest
{
   private final static String PROJECT = EditorContextMenuTest.class.getSimpleName();

   private final static String FILE_NAME = "contextmenu.txt";

   private final static String FILE_CONTENT = "Testing context menu.";

   private final static String EDIT_CONTENT = " Test Undo/Redo.";

   /**
    * Create test folder and test data object file.
    */
   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/contextmenu.txt";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.TEXT_PLAIN, filePath);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Clear tests results.
    */
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

   @Test
   public void testSelectAll() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.openContextMenu(0);
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.SELECT_ALL);
      IDE.CONTEXT_MENU.waitClosed();

      assertEquals(FILE_CONTENT, IDE.EDITOR.getSelectedText(0));
   }

   @Test
   public void testDeleteText() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.selectTab(FILE_NAME);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString());
      IDE.EDITOR.typeTextIntoEditor(0, Keys.chord(Keys.SHIFT, Keys.ARROW_RIGHT));
      IDE.EDITOR.typeTextIntoEditor(0, Keys.chord(Keys.SHIFT, Keys.ARROW_RIGHT));

      IDE.EDITOR.openContextMenu(0);
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.DELETE);
      IDE.CONTEXT_MENU.waitClosed();

      assertEquals("sting context menu.", IDE.EDITOR.getTextFromCodeEditor(0));
   }

   @Test
   public void testUndoRedoChanges() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.selectTab(FILE_NAME);
      IDE.EDITOR.openContextMenu(0);
      IDE.CONTEXT_MENU.waitOpened();
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.REDO_TYPING));
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.UNDO_TYPING));
      IDE.CONTEXT_MENU.closeContextMenu();
      IDE.CONTEXT_MENU.waitClosed();

      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.EDITOR.typeTextIntoEditor(0, EDIT_CONTENT);
      IDE.EDITOR.openContextMenu(0);
      IDE.CONTEXT_MENU.waitOpened();
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.UNDO_TYPING));
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.REDO_TYPING));
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.UNDO_TYPING);
      IDE.CONTEXT_MENU.waitClosed();

      assertEquals(FILE_CONTENT, IDE.EDITOR.getTextFromCodeEditor(0));

      IDE.EDITOR.openContextMenu(0);
      IDE.CONTEXT_MENU.waitOpened();
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.UNDO_TYPING));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.REDO_TYPING));
      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.REDO_TYPING);
      IDE.CONTEXT_MENU.waitClosed();

      assertEquals(FILE_CONTENT + EDIT_CONTENT, IDE.EDITOR.getTextFromCodeEditor(0));
   }
}
