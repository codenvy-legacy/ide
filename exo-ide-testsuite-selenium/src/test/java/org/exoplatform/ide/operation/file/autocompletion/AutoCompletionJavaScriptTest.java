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
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar("JavaScript File");
      Thread.sleep(TestConstants.SLEEP);

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestJSON();
      javaScriptTestFunctions();

      closeUnsavedFileAndDoNotSave("0");
   }

   @Test
   public void testGoogleGadget() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar("Google Gadget");
      Thread.sleep(TestConstants.SLEEP);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      selenium.typeKeys("//body[@class='editbox']", "<script>");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);      
      selenium.typeKeys("//body[@class='editbox']", "</script>");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      
      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestJSON();
      javaScriptTestFunctions();

      closeUnsavedFileAndDoNotSave("0");
   }

   @Test
   public void testHTML() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar("HTML File");
      Thread.sleep(TestConstants.SLEEP);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyDown("//body[@class='editbox']", "\\" + java.awt.event.KeyEvent.VK_END);
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<script>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<script>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", "le>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "</st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", "le>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<script>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "</script>");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      for (int i = 0; i < 9; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      }

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestJSON();
      javaScriptTestFunctions();

      closeUnsavedFileAndDoNotSave("0");
   }

   @Test
   public void testGroovyTemplate() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      selenium.typeKeys("//body[@class='editbox']", " <script>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", " </script>");

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<%");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "  import org.exoplatform.web.application.Parameter;");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "  List appCategories = uicomponent.getApplicationCategories();");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "%>");

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<script>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "</script>");

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", "le>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "</st");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", "le>");

      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "<script>");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "</script>");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      javaScriptTestVar();
      javaScriptTestFunction();
      javaScriptTestJSON();
      javaScriptTestFunctions();

      closeUnsavedFileAndDoNotSave("0");
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestFunctions() throws Exception
   {
      selenium.typeKeys("//body[@class='editbox']", "function topFunc(x) {");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "  // Local variables get a different colour than global ones.");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "  var localVarOfTopFunc = 44;");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", " function privateFunc1OfTopFunc() {");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "  var localVarOfPrivateFuncOfTopFunc = 1;");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "   };");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "   var privateFunc2OfTopFunc = function() {");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "   };");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "    };");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      openAutoCompleteForm();

      assertTrue(selenium.isElementPresent("//div[contains(text(), 'topFunc')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'localVarOfTopFunc')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'privateFunc1OfTopFunc')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'localVarOfPrivateFuncOfTopFunc')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'privateFunc2OfTopFunc')]"));

      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\" + java.awt.event.KeyEvent.VK_ESCAPE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestJSON() throws Exception
   {
      selenium.typeKeys("//body[@class='editbox']", "var topVar = {");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "propert");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.typeKeys("//body[@class='editbox']", "1: \"value1\",");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "method1: function() {}");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "};");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "topVar");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
      openAutoCompleteForm();

      assertTrue(selenium.isElementPresent("//div[contains(text(), 'method1')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'property1')]"));

      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\" + java.awt.event.KeyEvent.VK_ESCAPE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      // remove created text
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);    
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3)); 
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);      
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestFunction() throws Exception
   {
      selenium.typeKeys("//body[@class='editbox']", "function a () {");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));

      selenium.typeKeys("//body[@class='editbox']", "return 1;");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "};");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "var b = function() {");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "return 2;");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "};");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));

      openAutoCompleteForm();

      assertTrue(selenium.isElementPresent("//div[contains(text(), 'a')]"));
      assertTrue(selenium.isElementPresent("//div[contains(text(), 'b')]"));

      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\40");
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));

      String textAfter = selenium.getText("//body[@class='editbox']");
      assertTrue(textAfter.split("b").length >= 2);
      
      // remove created text
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3)); 
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
   }

   /**
    * @throws Exception 
    */
   private void javaScriptTestVar() throws Exception
   {
      selenium.typeKeys("//body[@class='editbox']", "var a = 1;");
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));
      selenium.typeKeys("//body[@class='editbox']", "a");

      openAutoCompleteForm();

      openAutoCompleteForm();

      selenium.focus("//input[@class='exo-autocomplete-edit']");

      assertTrue(selenium.isElementPresent("//div[contains(text(), 'a')]"));
      selenium.keyDown("//input[@class='exo-autocomplete-edit']", "\\" + java.awt.event.KeyEvent.VK_ESCAPE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      // remove created text
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);      
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);
      
      selenium.keyDown("//body[@class='editbox']", "\\" + (java.awt.event.KeyEvent.VK_ENTER + 3));      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);      
   }

   /**
    * @throws Exception 
    */
   private void openAutoCompleteForm() throws Exception
   {
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_SPACE);
      Thread.sleep(TestConstants.SLEEP);
   }
}