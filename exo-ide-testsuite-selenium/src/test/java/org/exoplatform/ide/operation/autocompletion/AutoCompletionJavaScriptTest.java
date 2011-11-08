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
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionJavaScriptTest extends CodeAssistantBaseTest
{

   @BeforeClass
   public static void createProject()
   {
      createProject(AutoCompletionJavaScriptTest.class.getSimpleName());
   }

   @Test
   public void testPlainJS() throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/Untitled file.js");
      javaScriptTest();
   }

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/Untitled file.xml");
      IDE.EDITOR.moveCursorDown(0, 4);
      IDE.EDITOR.moveCursorRight(0, 10);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.RETURN.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>\n\n</script>\n");
      IDE.EDITOR.moveCursorUp(0, 2);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.RETURN.toString());

      javaScriptTest();
   }

   @Test
   public void testHTML() throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/Untitled file.html");
      IDE.EDITOR.moveCursorDown(0, 2);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>\n</script>");
      IDE.EDITOR.moveCursorDown(0, 2);

      IDE.EDITOR.typeTextIntoEditor(0, "\n<script>\n</script>\n");

      IDE.EDITOR.typeTextIntoEditor(0, "<style>\n</style>\n<script>\n</script>");

      IDE.EDITOR.moveCursorUp(0, 9);

      IDE.EDITOR.typeTextIntoEditor(0, "\n");

      javaScriptTest();
   }

   @Test
   public void testGroovyTemplate() throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/Untitled file.gtmpl");
      IDE.EDITOR.deleteFileContent(0);

      IDE.EDITOR.typeTextIntoEditor(0, " <script>\n</script>\n");
      IDE.EDITOR.typeTextIntoEditor(0, "<%\n  import org.exoplatform.web.application.Parameter;\n");
      IDE.EDITOR.typeTextIntoEditor(0, "  List appCategories = uicomponent.getApplicationCategories();\n%>");

      IDE.EDITOR.typeTextIntoEditor(0, "\n<script>\n\n</script>\n");

      IDE.EDITOR.typeTextIntoEditor(0, "<style>\n</style>\n");

      IDE.EDITOR.typeTextIntoEditor(0, "<script>\n</script>");

      IDE.EDITOR.moveCursorUp(0, 5);

      javaScriptTest();

   }

   /**
    * @throws Exception 
    */
   private void javaScriptTest() throws Exception
   {

      IDE.EDITOR.typeTextIntoEditor(0, "function a () {\nreturn 1;\n}\n");
      IDE.EDITOR.typeTextIntoEditor(0, "var b = function() {\nreturn 2;\n}\n");
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("a()");
      IDE.CODEASSISTANT.checkElementPresent("b()");

      IDE.CODEASSISTANT.moveCursorDown(1);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textAfter.split("b").length >= 2);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "\n");

      IDE.EDITOR.typeTextIntoEditor(0, "function topFunc(x) {");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "  // Local variables get a different colour than global ones.");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "  var localVarOfTopFunc = 44;");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, " function privateFunc1OfTopFunc() {");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "  var localVarOfPrivateFuncOfTopFunc = 1;");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "   };");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "   var privateFunc2OfTopFunc = function() {");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "   };");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "    };");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.moveCursorUp(0, 1);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("topFunc()");
      IDE.CODEASSISTANT.checkElementNotPresent("localVarOfTopFunc");
      IDE.CODEASSISTANT.checkElementNotPresent("privateFunc1OfTopFunc()");
      IDE.CODEASSISTANT.checkElementNotPresent("localVarOfPrivateFuncOfTopFunc");
      IDE.CODEASSISTANT.checkElementNotPresent("privateFunc2OfTopFunc()");

      IDE.CODEASSISTANT.closeForm();
   }

}