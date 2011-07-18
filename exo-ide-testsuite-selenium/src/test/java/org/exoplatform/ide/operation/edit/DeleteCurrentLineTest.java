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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class DeleteCurrentLineTest extends BaseTest
{
   private static final String TEST_FOLDER = DeleteCurrentLineTest.class.getSimpleName();

   private static final String FILE_NAME_1 = "file-" + DeleteCurrentLineTest.class.getSimpleName();

   private static final String FILE_NAME_2 = "file-" + DeleteCurrentLineTest.class.getSimpleName() + "111";

   private static final String WAIT_APPEAR_STATUSBAR =
      "//div[@id='exoIDEStatusbar']//div[@control-id='__editor_cursor_position']//table[@class='exo-statusText-table']//td[@class='exo-statusText-table-middle']/nobr";

   private interface Lines
   {
      public static final String LINE_1 = "<html>\n";

      public static final String LINE_2 = "<head>\n";

      public static final String LINE_3 = "<title> </title>\n";

      public static final String LINE_4 = "</head>\n";

      public static final String LINE_5 = "<body>\n";

      public static final String LINE_6 = "</body>\n";

      public static final String LINE_7 = "</html>";

      public static final String DEFAULT_TEXT = LINE_1 + LINE_2 + LINE_3 + LINE_4 + LINE_5 + LINE_6 + LINE_7;

      public static final String TEXT_LINE_1 = "line 1";
   }

   private String currentTextInEditor;

   @BeforeClass
   public static void setUp()
   {
      final String filePath1 = "src/test/resources/org/exoplatform/ide/operation/edit/delete-current-line.html";
      final String filePath2 = "src/test/resources/org/exoplatform/ide/operation/edit/delete-current-line.txt";

      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath1, MimeType.TEXT_HTML, WS_URL + TEST_FOLDER + "/" + FILE_NAME_1);
         VirtualFileSystemUtils.put(filePath2, MimeType.TEXT_PLAIN, WS_URL + TEST_FOLDER + "/" + FILE_NAME_2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //IDE-151
   @Test
   public void deleteLine() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME_1, false);

      currentTextInEditor = Lines.DEFAULT_TEXT;
      assertEquals(currentTextInEditor, IDE.EDITOR.getTextFromCodeEditor(0));
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      //----- 1 -----------
      // Click on "Edit->Delete Current Line" top menu command.
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      currentTextInEditor = Lines.LINE_2 + Lines.LINE_3 + Lines.LINE_4 + Lines.LINE_5 + Lines.LINE_6 + Lines.LINE_7;
      assertEquals(currentTextInEditor, IDE.EDITOR.getTextFromCodeEditor(0));

      //----- 2 -----------
      //Press "Ctrl+D" keys.
      IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      currentTextInEditor = Lines.LINE_3 + Lines.LINE_4 + Lines.LINE_5 + Lines.LINE_6 + Lines.LINE_7;
      assertEquals(currentTextInEditor, IDE.EDITOR.getTextFromCodeEditor(0));

      //----- 3 -----------
      //Move cursor down on 2 lines
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      //      Thread.sleep(TestConstants.SLEEP_SHORT);

      //----- 4 -----------
      //Press "Ctrl+D"
      IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);
      currentTextInEditor = Lines.LINE_3 + Lines.LINE_4 + Lines.LINE_6 + Lines.LINE_7;
      assertEquals(currentTextInEditor, IDE.EDITOR.getTextFromCodeEditor(0));
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("3 : 1", IDE.STATUSBAR.getCursorPosition());

      //----- 5 -----------
      //Click on "Edit->Delete Current Line" top menu command
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      currentTextInEditor = Lines.LINE_3 + Lines.LINE_4 + Lines.LINE_7;
      assertEquals(currentTextInEditor, IDE.EDITOR.getTextFromCodeEditor(0));
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("3 : 1", IDE.STATUSBAR.getCursorPosition());

      //----- 6 -----------
      //Click on "Edit->Delete Current Line" top menu command
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      currentTextInEditor = Lines.LINE_3 + Lines.LINE_4.trim();
      assertEquals(currentTextInEditor, IDE.EDITOR.getTextFromCodeEditor(0));
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("3 : 1", IDE.STATUSBAR.getCursorPosition());

      //----- 7 -----------
      //Open empty text file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME_2, false);
      waitForElementPresent(WAIT_APPEAR_STATUSBAR + "[text()='1 : 1']");

      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      IDE.EDITOR.typeTextIntoEditor(1, Lines.TEXT_LINE_1);

      //----- 8 -----------
      //Go to line 2 and click on "Edit->Delete Current Line" top menu command
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      assertEquals("", IDE.EDITOR.getTextFromCodeEditor(1));

      //----- 9 -----------
      //Press "Ctrl+D" keys
      IDE.EDITOR.runHotkeyWithinEditor(1, true, false, java.awt.event.KeyEvent.VK_D);

      IDE.EDITOR.selectTab(0);

      IDE.EDITOR.selectTab(1);
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      assertEquals("", IDE.EDITOR.getTextFromCodeEditor(1));

      IDE.EDITOR.runHotkeyWithinEditor(1, true, false, java.awt.event.KeyEvent.VK_D);
      waitForElementPresent(WAIT_APPEAR_STATUSBAR);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      assertEquals("", IDE.EDITOR.getTextFromCodeEditor(1));
   }

}
