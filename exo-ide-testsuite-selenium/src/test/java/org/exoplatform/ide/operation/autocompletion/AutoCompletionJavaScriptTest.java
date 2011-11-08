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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionJavaScriptTest extends BaseTest
{

   @Test
   public void testPlainJS() throws InterruptedException, Exception
   {
      IDE.WORKSPACE.waitForRootItem();      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);

      javaScriptTestVar();
//      javaScriptTestFunction();
//      javaScriptTestFunctions();

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
//      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      driver.navigate().refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.moveCursorDown(0, 4);
      IDE.EDITOR.moveCursorRight(0, 10);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.RETURN.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>\n\n</script>\n");
      IDE.EDITOR.moveCursorUp(0, 2);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.RETURN.toString());

      javaScriptTestVar();
//      javaScriptTestFunction();
//      javaScriptTestFunctions();
      

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
//      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @Test
   @Ignore
   public void testHTML() throws InterruptedException, Exception
   {
      IDE.WORKSPACE.waitForRootItem();      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium().keyDown("//body[@class='editbox']", "\\" + java.awt.event.KeyEvent.VK_END);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<st");
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      IDE.EDITOR.typeTextIntoEditor(0, "le>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "</st");
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      IDE.EDITOR.typeTextIntoEditor(0, "le>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      for (int i = 0; i < 9; i++)
      {
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      }

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestFunctions();

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @Test
   @Ignore
   public void testGroovyTemplate() throws InterruptedException, Exception
   {
      IDE.WORKSPACE.waitForRootItem();      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);

      IDE.EDITOR.deleteFileContent(0);

      IDE.EDITOR.typeTextIntoEditor(0, " <script>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, " </script>");

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<%");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "  import org.exoplatform.web.application.Parameter;");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "  List appCategories = uicomponent.getApplicationCategories();");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "%>");

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "</script>");

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<st");
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      IDE.EDITOR.typeTextIntoEditor(0, "le>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "</st");
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      IDE.EDITOR.typeTextIntoEditor(0, "le>");

      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<script>");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "</script>");

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestFunctions();

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestFunctions() throws Exception
   {
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

   /**
    * @throws Exception 
    */
   private void javaScriptTestFunction() throws Exception
   {
     
      // remove created text
//      IDE.EDITOR.deleteFileContent(0);
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestVar() throws Exception
   {      
//      IDE.EDITOR.moveCursorUp(0, 1);
//      IDE.EDITOR.deleteLinesInEditor(0, 2);
      
      IDE.EDITOR.typeTextIntoEditor(0, "function a () {\nreturn 1;\n}\n");
      IDE.EDITOR.typeTextIntoEditor(0, "var b = function() {\nreturn 2;\n}\n");
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("a()");
      IDE.CODEASSISTANT.checkElementPresent("b()");

      IDE.CODEASSISTANT.moveCursorDown(1);
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textAfter.split("b").length >= 2);

//      IDE.EDITOR.deleteLinesInEditor(0, 6);
      
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