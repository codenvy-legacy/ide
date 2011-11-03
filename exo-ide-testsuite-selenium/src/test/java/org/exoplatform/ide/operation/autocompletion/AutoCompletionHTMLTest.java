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
package org.exoplatform.ide.operation.autocompletion;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionHTMLTest extends BaseTest
{
   @Test
   public void testHTML() throws InterruptedException, Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      IDE.WORKSPACE.waitForRootItem();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitEditorFileOpened();

      IDE.EDITOR.moveCursorDown(0, 4);

      htmlTest();
   }

   /**
    * @throws InterruptedException
    */

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      IDE.WORKSPACE.waitForRootItem();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);

      IDE.EDITOR.moveCursorDown(0, 4);

      GoogleGadgetTest();
   }

   @Test
   public void testGroovyTemplate() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad(TestConstants.IDE_LOAD_PERIOD + "");
      IDE.WORKSPACE.waitForRootItem();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      Thread.sleep(TestConstants.SLEEP);
      IDE.EDITOR.deleteFileContent(0);

      IDE.EDITOR.typeTextIntoEditor(0, "<div class=\"ItemDetail\" style=\"display:block\">\n");

      IDE.EDITOR.typeTextIntoEditor(0, "<div class=\"NoneAppsMessage\" style=\"display:block\">\n");

      IDE.EDITOR.typeTextIntoEditor(0, "<%=_ctx.appRes(\"UIAddNewApplication.label.NoneApp\")%>\n");

      IDE.EDITOR.typeTextIntoEditor(0, "</div>\n</div>");

      IDE.EDITOR.moveCursorUp(0, 2);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.EDITOR.moveCursorLeft(0, 2);
      IDE.EDITOR.moveCursorDown(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString());

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("!DOCTYPE");
      IDE.CODEASSISTANT.checkElementPresent("acronym");
      IDE.CODEASSISTANT.checkElementPresent("a");
      IDE.CODEASSISTANT.closeForm();
   }

   private void htmlTest() throws Exception
   {
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "\n<t");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(3);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textAfter.contains("<textarea></textarea>"));

      IDE.EDITOR.typeTextIntoEditor(0, "<p ");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(2);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textA = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textA.contains("<p class=\"\""));

      IDE.EDITOR.moveCursorRight(0, 1);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.insertSelectedItem();

      String text = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(text.contains("<p class=\"\"></p>"));
   }

   private void GoogleGadgetTest() throws Exception
   {
      IDE.EDITOR.typeTextIntoEditor(0, Keys.HOME.toString() + Keys.RETURN.toString());

      IDE.EDITOR.moveCursorRight(0, 16);

      IDE.EDITOR.typeTextIntoEditor(0, "<t");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(3);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textAfter.contains("<textarea></textarea>"));

      IDE.EDITOR.typeTextIntoEditor(0, "<p ");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(2);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textA = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textA.contains("<p class=\"\""));

      IDE.EDITOR.moveCursorRight(0, 1);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.insertSelectedItem();

      String text = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(text.contains("<p class=\"\"></p>"));
   }
}
