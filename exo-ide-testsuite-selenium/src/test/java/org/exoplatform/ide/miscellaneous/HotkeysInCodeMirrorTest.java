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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.preferences.AbstractCustomizeHotkeys;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * This test contains Thread.sleep, because we need
 * delay after typing text or pressing hotkeys
 * as it users do.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
public class HotkeysInCodeMirrorTest extends AbstractCustomizeHotkeys
{
   private static final String PROJECT = HotkeysInCodeMirrorTest.class.getSimpleName();

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

   @After
   public void after() throws Exception
   {
      IDE.EDITOR.forcedClosureFile(1);
   }

   @AfterClass
   public static void tearDownTest()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testHotkeysWithinCodeEditor() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      //----- 1 ------------
      //Create new text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(1);
      //type text to editor
      IDE.EDITOR.typeTextIntoEditor("Text File");
      IDE.EDITOR.waitFileContentModificationMark("Untitled file.txt");
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.SAVE_AS);
      //----- 2 ------------
      //Press Ctrl+F
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "f");
      //Find-replace form appeared
      IDE.FINDREPLACE.waitOpened();
      IDE.FINDREPLACE.waitFindButtonAppeared();
      IDE.FINDREPLACE.waitFindFieldAppeared();
      IDE.FINDREPLACE.typeInFindField("abcdefghi");
      IDE.FINDREPLACE.waitCloseButtonAppeared();

      //close form
      IDE.FINDREPLACE.closeView();
      IDE.FINDREPLACE.waitClosed();

      //----- 3 ------------
      //check Ctrl+D
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "d");
      assertEquals("", IDE.EDITOR.getTextFromCodeEditor());

      //----- 4 ------------
      //check Ctrl+L
      //check go to line window dialog appeared
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "l");
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.clickCancelButton();

   }

   @Test
   public void testCopyPasetHotkeys() throws Exception
   {
      IDE.selectMainFrame();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      //----- 1 ------------
      //Create new text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(1);

      //type text to editor
      final String textToEdit = "text to edit";
      IDE.EDITOR.typeTextIntoEditor(textToEdit);

      //check Ctrl+C, Ctrl+V
      IDE.EDITOR.typeTextIntoEditor(Keys.ARROW_LEFT.toString());
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "a");
      //used delay to imitate the user's typing

      //TODO Used two "Ctrl+c" because after first pressing does not copy to clipboard
      //If, maybe this problem will fixed in latest versions WebDriver, one "Ctrl+c" should remove. 
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "c");
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "c");

      IDE.EDITOR.typeTextIntoEditor(Keys.ARROW_DOWN.toString());

      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());

      //paste text by pressing Ctrl+V
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "v");

      //check text
      assertEquals(textToEdit + "\n" + textToEdit, IDE.EDITOR.getTextFromCodeEditor());

      //----- 2 ------------
      //check Ctrl+X
      //delete all text
      IDE.EDITOR.typeTextIntoEditor(Keys.ARROW_UP.toString());
      Thread.sleep(500);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "d");
      Thread.sleep(500);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "d");
      Thread.sleep(500);

      final String textToCut = "text to cut";
      IDE.EDITOR.typeTextIntoEditor(textToCut);

      //select all text
      IDE.EDITOR.typeTextIntoEditor(Keys.ARROW_LEFT.toString());
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "a");
      Thread.sleep(500);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "x");
      Thread.sleep(500);
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "v");
      Thread.sleep(500);
      assertEquals(textToCut, IDE.EDITOR.getTextFromCodeEditor());
   }

   @Test
   public void testUndoRedoHotkeys() throws Exception
   {
      IDE.selectMainFrame();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Create new text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(1);

      final String textToRevert = "a";

      IDE.EDITOR.typeTextIntoEditor(textToRevert);
      Thread.sleep(500);
      //change text
      IDE.EDITOR.typeTextIntoEditor("5");
      Thread.sleep(500);

      assertEquals(textToRevert + "5", IDE.EDITOR.getTextFromCodeEditor());
      Thread.sleep(500);
      //press Ctrl+Z
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "z");
      Thread.sleep(500);
      assertEquals(textToRevert, IDE.EDITOR.getTextFromCodeEditor());

      // press Ctrl+Y
      IDE.EDITOR.typeTextIntoEditor(Keys.CONTROL.toString() + "y");
      assertEquals(textToRevert + "5", IDE.EDITOR.getTextFromCodeEditor());
   }

}