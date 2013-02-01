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
import org.exoplatform.ide.TestConstants;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class AutoCompletionJavaScriptNoneCollabEditorTest extends CodeAssistantBaseTest
{

   @BeforeClass
   public static void createProject() throws Exception
   {
      createProject(AutoCompletionJavaScriptNoneCollabEditorTest.class.getSimpleName());
   }

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      openProject();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.moveCursorDown(4);
      IDE.EDITOR.moveCursorRight(10);
      IDE.EDITOR.typeTextIntoEditor(Keys.RETURN.toString());
      IDE.EDITOR.typeTextIntoEditor("<script>\n\n</script>\n");
      IDE.EDITOR.moveCursorUp(2);
      IDE.EDITOR.typeTextIntoEditor(Keys.RETURN.toString());
      javaScriptTest();
   }

   @Test
   public void testHTML() throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.moveCursorDown(2);

      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString() + Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("<script>\n</script>");
      IDE.EDITOR.moveCursorDown(2);

      IDE.EDITOR.typeTextIntoEditor("\n<script>\n</script>\n");

      IDE.EDITOR.typeTextIntoEditor("<style>\n</style>\n<script>\n</script>");

      IDE.EDITOR.moveCursorUp(9);

      IDE.EDITOR.typeTextIntoEditor("\n");

      javaScriptTest();

   }

   @Test
   public void testGroovyTemplate() throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.deleteFileContent();

      IDE.EDITOR.typeTextIntoEditor(" <script>\n</script>\n");
      IDE.EDITOR.typeTextIntoEditor("<%\n  import org.exoplatform.web.application.Parameter;\n");
      IDE.EDITOR.typeTextIntoEditor("  List appCategories = uicomponent.getApplicationCategories();\n%>");

      IDE.EDITOR.typeTextIntoEditor("\n<script>\n\n</script>\n");

      IDE.EDITOR.typeTextIntoEditor("<style>\n</style>\n");

      IDE.EDITOR.typeTextIntoEditor("<script>\n</script>");

      IDE.EDITOR.moveCursorUp(5);

      javaScriptTest();
   }

   /**
    * @throws Exception
    */
   private void javaScriptTest() throws Exception
   {

      IDE.EDITOR.typeTextIntoEditor("function a () {\nreturn 1;\n}\n");
      IDE.EDITOR.typeTextIntoEditor("var b = function() {\nreturn 2;\n}\n");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.CODEASSISTANT.openForm();

      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("a()");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("b()");

      IDE.CODEASSISTANT.moveCursorDown(1);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(textAfter.split("b").length >= 2);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString() + "\n");

      IDE.EDITOR.typeTextIntoEditor("function topFunc(x) {");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("  // Local variables get a different colour than global ones.");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("  var localVarOfTopFunc = 44;");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(" function privateFunc1OfTopFunc() {");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("  var localVarOfPrivateFuncOfTopFunc = 1;");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("   };");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("   var privateFunc2OfTopFunc = function() {");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("   };");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("    };");
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(Keys.ENTER.toString());
      IDE.EDITOR.moveCursorUp(1);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("topFunc()");
      IDE.CODEASSISTANT.checkElementNotPresent("localVarOfTopFunc");
      IDE.CODEASSISTANT.checkElementNotPresent("privateFunc1OfTopFunc()");
      IDE.CODEASSISTANT.checkElementNotPresent("localVarOfPrivateFuncOfTopFunc");
      IDE.CODEASSISTANT.checkElementNotPresent("privateFunc2OfTopFunc()");

      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

}