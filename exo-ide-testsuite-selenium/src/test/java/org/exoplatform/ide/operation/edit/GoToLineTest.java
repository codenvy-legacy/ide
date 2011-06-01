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
import org.exoplatform.ide.core.GoToLine;
import org.exoplatform.ide.core.Statusbar;
import org.exoplatform.ide.core.WarningDialog;
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 12, 2010
 *
 */
public class GoToLineTest extends BaseTest
{
   //IDE-152
   @Test
   public void goToLine() throws Exception
   {
      //Open new Groovy file in editor.

      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitTabPresent(0);

      //Open new HTML file in editor.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitTabPresent(1);

      //Select Groovy file.
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.waitTabPresent(0);
      IDE.selectMainFrame();

      //Go to menu and click "View->Go To Line".
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      IDE.GOTOLINE.waitForGoToLineForm();
      assertTrue(selenium.isElementPresent(GoToLine.GO_TO_LINE_FORM_ID));
      assertTrue(selenium.isElementPresent(GoToLine.GO_TO_LINE_BUTTON_ID));
      assertTrue(selenium.isElementPresent(GoToLine.CANCEL_BUTTON_ID));
      IDE.GOTOLINE.checkLineNumberLabel("Enter line number (1..13):");

      // Type empty value an check form (form should remain unchanged)
      IDE.GOTOLINE.typeIntoGoToLineFormField("");
      IDE.GOTOLINE.pressGoButton();
      IDE.GOTOLINE.waitForGoToLineForm();
      assertTrue(selenium.isElementPresent(GoToLine.GO_TO_LINE_FORM_ID));

      // Print "abc" in input field.
      IDE.GOTOLINE.typeIntoGoToLineFormField("abc");
      IDE.GOTOLINE.pressGoButton();
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      assertTrue(selenium.isElementPresent(WarningDialog.WARNING_DIALOG_ID));
      assertTrue(selenium.isTextPresent("Can't parse line number."));
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitForWarningDialogClosed();

      // Type "100" (above range maximum) and click "Go" button.
      IDE.GOTOLINE.typeIntoGoToLineFormField("100");
      IDE.GOTOLINE.pressGoButton();
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      assertTrue(selenium.isElementPresent(WarningDialog.WARNING_DIALOG_ID));
      assertTrue(selenium.isTextPresent("Line number out of range"));
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitForWarningDialogClosed();

      // Type "2" and click "Go" button.
      IDE.GOTOLINE.typeIntoGoToLineFormField("2");
      IDE.GOTOLINE.pressGoButton();
      waitForElementPresent(Statusbar.STATUSBAR_LOCATOR);
      assertEquals("2 : 1", IDE.STATUSBAR.getCursorPosition());

      // Select HTML file's tab.
      IDE.EDITOR.selectTab(1);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());
      IDE.selectMainFrame();

      // Go to menu and click "View->Go To Line".
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      waitForElementPresent(GoToLine.GO_TO_LINE_FORM_ID);
      IDE.GOTOLINE.typeIntoGoToLineFormField("1");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      waitForElementNotPresent(GoToLine.GO_TO_LINE_FORM_ID);
      assertEquals("1 : 1", IDE.STATUSBAR.getCursorPosition());

      // Go to status bar - right down corner , where row and column numbers are displayed, hover on them with the mouse and click on it.
      IDE.STATUSBAR.clickOnStatusBar();
      IDE.GOTOLINE.waitForGoToLineForm();
      assertTrue(selenium.isElementPresent(GoToLine.GO_TO_LINE_FORM_ID));
      IDE.GOTOLINE.checkLineNumberLabel("Enter line number (1..7):");
      
      // Print "2" and click "Go".
      IDE.GOTOLINE.typeIntoGoToLineFormField("2");
      IDE.GOTOLINE.pressGoButtonWithCorrectValue();
   }

}
