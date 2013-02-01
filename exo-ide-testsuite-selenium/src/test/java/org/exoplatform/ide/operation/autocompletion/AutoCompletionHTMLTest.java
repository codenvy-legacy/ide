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

import org.exoplatform.ide.MenuCommands;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class AutoCompletionHTMLTest extends CodeAssistantBaseTest
{

   @BeforeClass
   public static void createProject() throws Exception
   {
      createProject("HtmlTestProject");
   }

   @After
   public void forceClosedTabs() throws Exception
   {
      IDE.EDITOR.forcedClosureFile(1);
   }

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      openProject();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.moveCursorDown(4);
      googleGadgetTest();
   }

   @Test
   public void testGroovyTemplate() throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.deleteFileContent();

      IDE.EDITOR.typeTextIntoEditor("<div class=\"ItemDetail\" style=\"display:block\">\n");

      IDE.EDITOR.typeTextIntoEditor("<div class=\"NoneAppsMessage\" style=\"display:block\">\n");

      IDE.EDITOR.typeTextIntoEditor("<%=_ctx.appRes(\"UIAddNewApplication.label.NoneApp\")%>\n");

      IDE.EDITOR.typeTextIntoEditor("</div>\n</div>");

      IDE.EDITOR.moveCursorUp(2);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.EDITOR.moveCursorLeft(2);
      IDE.EDITOR.moveCursorDown(1);
      IDE.EDITOR.typeTextIntoEditor(Keys.HOME.toString());

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("!DOCTYPE");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("acronym");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("a");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testHTMLFile() throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.getNumberTabOfActiveEditor();
      IDE.EDITOR.moveCursorDown(4);
      htmlTest();
   }

   private void htmlTest() throws Exception
   {
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.EDITOR.typeTextIntoEditor("\n<t");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(3);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(textAfter.contains("<textarea></textarea>"));

      IDE.EDITOR.typeTextIntoEditor("<p ");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(2);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textA = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(textA.contains("<p class=\"\""));

      IDE.EDITOR.moveCursorRight(1);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.insertSelectedItem();

      String text = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(text.contains("<p class=\"\"></p>"));

   }

   private void googleGadgetTest() throws Exception
   {
      IDE.EDITOR.typeTextIntoEditor(Keys.HOME.toString() + Keys.RETURN.toString());

      IDE.EDITOR.moveCursorRight(16);

      IDE.EDITOR.typeTextIntoEditor("<t");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(3);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(textAfter.contains("<textarea></textarea>"));

      IDE.EDITOR.typeTextIntoEditor("<p ");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.moveCursorDown(2);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textA = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(textA.contains("<p class=\"\""));

      IDE.EDITOR.moveCursorRight(1);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.insertSelectedItem();

      String text = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(text.contains("<p class=\"\"></p>"));
   }
}
