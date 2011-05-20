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
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 12, 2010
 *
 */
public class GoToLineTest extends BaseTest
{
   //IDE-152
   //TODO doesn't work on Windows
   @Test
   public void goToLine() throws Exception
   {
      //      Open new Groovy file in editor.

      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);

      String text = IDE.STATUSBAR.getCursorPosition();
      System.out.println("cursor position > [" + text + "]");

      //     Open new HTML file in editor.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //      Select Groovy file.
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.waitTabPresent(0);
      selenium.selectFrame("relative=top");

      //  Go to menu and click "View->Go To Line".
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.checkAppearGoToLineForm();
      IDE.GOTOLINE.checkLineNumberLabel("Enter line number (1..13):");

      // Type empty value an check form (form should remain unchanged)
      IDE.GOTOLINE.typeIntoGoToLineFormField("");
      IDE.GOTOLINE.pressGoButton();
      IDE.GOTOLINE.checkAppearGoToLineForm();

      // Print "abc" in input field.
      IDE.GOTOLINE.typeIntoGoToLineFormField("abc");
      IDE.GOTOLINE.pressGoButton();
      IDE.GOTOLINE.checkAppearExoWarningDialogGoToLineForm("Can't parse line number.");
      IDE.GOTOLINE.closeExoWarningDialogGoToLineForm();

      // Type "100" (above range maximum) and click "Go" button.
      IDE.GOTOLINE.typeIntoGoToLineFormField("100");
      IDE.GOTOLINE.pressGoButton();
      IDE.GOTOLINE.checkAppearExoWarningDialogGoToLineForm("Line number out of range");
      IDE.GOTOLINE.closeExoWarningDialogGoToLineForm();

      // Type "2" and click "Go" button.
      IDE.GOTOLINE.typeIntoGoToLineFormField("2");
      IDE.GOTOLINE.pressGoButton();
      waitForElementPresent(IDE.STATUSBAR.STATUSBAR_LOCATOR);
      assertEquals("2 : 1", IDE.STATUSBAR.getCursorPosition());

      // Select HTML file's tab.
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitTabPresent(1);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      selenium.selectFrame("relative=top");

      // Go to menu and click "View->Go To Line".
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.typeIntoGoToLineFormField("1");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      waitForElementNotPresent("ideGoToLineForm");
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      // Go to status bar - right down corner , where row and column numbers are displayed, hover on them with the mouse and click on it.
      IDE.STATUSBAR.clickOnStatusBar();

      IDE.GOTOLINE.checkLineNumberLabel("Enter line number (1..7):");
      // Print "2" and click "Go".
      IDE.GOTOLINE.typeIntoGoToLineFormField("2");
      IDE.GOTOLINE.pressGoButtonWithCorrectValue();
   }

}
