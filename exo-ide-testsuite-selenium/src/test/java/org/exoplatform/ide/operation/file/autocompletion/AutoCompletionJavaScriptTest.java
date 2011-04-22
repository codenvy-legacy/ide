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
package org.exoplatform.ide.operation.file.autocompletion;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.CodeAssistant;
import org.junit.Test;

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
      waitForRootElement();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestFunctions();

     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
   }

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      waitForRootElement();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.SLEEP);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
     IDE.EDITOR.typeTextIntoEditor(0, "<script>");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
     IDE.EDITOR.typeTextIntoEditor(0, "</script>");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

     IDE.EDITOR.pressEnter();

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestFunctions();

     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
   }

   @Test
   public void testHTML() throws InterruptedException, Exception
   {
      waitForRootElement();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.SLEEP);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyDown("//body[@class='editbox']", "\\" + java.awt.event.KeyEvent.VK_END);
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<script>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<script>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
     IDE.EDITOR.typeTextIntoEditor(0, "le>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
     IDE.EDITOR.typeTextIntoEditor(0, "le>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<script>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      for (int i = 0; i < 9; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      }

     IDE.EDITOR.pressEnter();

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestFunctions();

     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
   }

   @Test
   public void testGroovyTemplate() throws InterruptedException, Exception
   {
      waitForRootElement();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      Thread.sleep(TestConstants.SLEEP);
     IDE.EDITOR.deleteFileContent();
      
     IDE.EDITOR.typeTextIntoEditor(0, " <script>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, " </script>");

     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<%");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "  import org.exoplatform.web.application.Parameter;");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "  List appCategories = uicomponent.getApplicationCategories();");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "%>");

     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<script>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</script>");

     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
     IDE.EDITOR.typeTextIntoEditor(0, "le>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
     IDE.EDITOR.typeTextIntoEditor(0, "le>");

     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "<script>");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "</script>");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestFunctions();

     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestFunctions() throws Exception
   {
     IDE.EDITOR.typeTextIntoEditor(0, "function topFunc(x) {");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "  // Local variables get a different colour than global ones.");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "  var localVarOfTopFunc = 44;");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, " function privateFunc1OfTopFunc() {");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "  var localVarOfPrivateFuncOfTopFunc = 1;");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "   };");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "   var privateFunc2OfTopFunc = function() {");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "   };");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "    };");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.pressEnter();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

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
     IDE.EDITOR.typeTextIntoEditor(0, "function a () {");
     IDE.EDITOR.pressEnter();

     IDE.EDITOR.typeTextIntoEditor(0, "return 1;");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "};");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "var b = function() {");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "return 2;");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "};");
     IDE.EDITOR.pressEnter();

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("a()");
      IDE.CODEASSISTANT.checkElementPresent("b()");

      selenium.keyDown(CodeAssistant.Locators.INPUT, "\\40");
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter =IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textAfter.split("b").length >= 2);

      // remove created text
     IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);
     IDE.EDITOR.pressEnter();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestVar() throws Exception
   {
     IDE.EDITOR.typeTextIntoEditor(0, "var a = 1;");
     IDE.EDITOR.pressEnter();
     IDE.EDITOR.typeTextIntoEditor(0, "a");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("a");
      IDE.CODEASSISTANT.closeForm();

      // remove created text
     IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
     IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);

     IDE.EDITOR.pressEnter();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
   }

}