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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.GoToLine;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 12, 2010
 *
 */
public class GoToLineTest extends BaseTest
{
   private static String PROJECT = GoToLineTest.class.getSimpleName();

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

   //IDE-152
   @Test
   public void goToLine() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Open new Groovy file in editor.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");

      //Open new HTML file in editor.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.html");

      //Select Groovy file.
      IDE.EDITOR.selectTab(1);

      //Go to menu and click "View->Go To Line".
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.waitOpened();
      assertTrue(IDE.GOTOLINE.isGoToLineViewPresent());
      assertEquals(String.format(GoToLine.RANGE_LABEL, 1, 13), IDE.GOTOLINE.getLineNumberRangeLabel());

      // Type empty value an check form (form should remain unchanged)
      IDE.GOTOLINE.typeIntoLineNumberField("");
      IDE.GOTOLINE.clickGoButton();
      assertTrue(IDE.GOTOLINE.isGoToLineViewPresent());

      // Print "abc" in input field.
      IDE.GOTOLINE.typeIntoLineNumberField("abc");
      IDE.GOTOLINE.clickGoButton();
      IDE.WARNING_DIALOG.waitOpened();
      assertEquals("Can't parse line number.", IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      // Type "100" (above range maximum) and click "Go" button.
      IDE.GOTOLINE.typeIntoLineNumberField("100");
      IDE.GOTOLINE.clickGoButton();
      IDE.WARNING_DIALOG.waitOpened();
      assertEquals("Line number out of range", IDE.WARNING_DIALOG.getWarningMessage());
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      // Type "2" and click "Go" button.
      IDE.GOTOLINE.typeIntoLineNumberField("2");
      IDE.GOTOLINE.clickGoButton();
      IDE.GOTOLINE.waitClosed();
      IDE.STATUSBAR.waitCursorPositionControl();
      assertEquals("2 : 1", IDE.STATUSBAR.getCursorPosition());

      // Select HTML file's tab.
      IDE.EDITOR.selectTab(2);
      IDE.STATUSBAR.waitCursorPositionControl();
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      // Go to menu and click "View->Go To Line".
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.waitOpened();
      IDE.GOTOLINE.typeIntoLineNumberField("1");
      IDE.GOTOLINE.typeIntoLineNumberField(Keys.ENTER.toString());
      IDE.GOTOLINE.waitClosed();
      IDE.STATUSBAR.waitCursorPositionControl();
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      // Go to status bar - right down corner , where row and column numbers are displayed, hover on them with the mouse and click on it.
      IDE.STATUSBAR.clickOnCursorPositionControl();
      IDE.GOTOLINE.waitOpened();
      assertEquals(String.format(GoToLine.RANGE_LABEL, 1, 8), IDE.GOTOLINE.getLineNumberRangeLabel());

      // Print "2" and click "Go".
      IDE.GOTOLINE.typeIntoLineNumberField("2");
      IDE.GOTOLINE.clickGoButton();
      IDE.GOTOLINE.waitClosed();

      IDE.STATUSBAR.waitCursorPositionControl();
      assertEquals("2 : 1", IDE.STATUSBAR.getCursorPosition());
   }
}
