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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.Test;

/**
 * Test Code Outline panel for javascript file.
 * 
 * That tree is correctly displayed for javascript file.
 * 
 * That if working throught file, than correct node is 
 * highlited in outline tree.
 * 
 * That if click on node in outline tree, than cursor
 * goes to this token in editor.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeOutlineJavaScriptTest extends BaseTest
{
   /**
    * IDE-161:JavaScript Code Outline
    * @throws Exception
    */
   @Test
   public void testCodeOutlineJavascript() throws Exception
   {
      waitForRootElement();
      //---- 2 -----------------
      //Create new JavaScript file and click on Show Outline button
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 3 -----------------
      //Click Enter in editor and type such text:
      //var a
     IDE.EDITOR.typeTextIntoEditor(0, "var a");
      Thread.sleep(TestConstants.SLEEP);

      //In 2 seconds, after stopping typing text, new node a appeared in Outline tree. 
      //Near item appeard red circul with V, which means variable
      assertEquals("a", IDE.OUTLINE.getTitle(0, 0));
      IDE.OUTLINE.checkOutlineTreeNodeSelected(0, "a", true);
      OulineTreeHelper.checkIconNearToken(0, "var-item.png", true);
      
      //---- 4 -----------------
      //continue typing text:
      //var a = {
      //   "a1": "1",
      //    a2: a3.a4,
      //    a5: function(),
      //    a6 function
      // };

      final String textJson = " = {\n" + "\"a1\": \"1\",\n" + "a2: a3.a4,\n" + "a5: function(),\n" + "a6 function\n" + "};\n";

     IDE.EDITOR.typeTextIntoEditor(0, textJson);
      Thread.sleep(TestConstants.SLEEP);

      //In 2 seconds, after stopping typing text, next node structure should be displayed 
      //in the Outline Panel:
      //
      //variable a
      //  property "a1"
      //  property a2
      //  method a5

      assertEquals("a : Object", IDE.OUTLINE.getTitle(0, 0));
//      assertEquals("\"a1\"", IDE.OUTLINE.getTitle(1, 0));
//      assertEquals("a2", IDE.OUTLINE.getTitle(2, 0));
//      assertEquals("a5", IDE.OUTLINE.getTitle(3, 0));
//      OulineTreeHelper.checkIconNearToken(1, "property-item.png", false);
//      OulineTreeHelper.checkIconNearToken(2, "property-item.png", false);
//      OulineTreeHelper.checkIconNearToken(3, "method-item.png", true);

      //---- 5 -----------------
      //Click Enter and enter text in editor:
      final String jsText = "\n" + "var b = b1.b2;\n"
            + "\n"
            + "var c = function(){};\n"
            + "\n"
            + "function d() {\n"
            + "var d1 = d2.d3;\n"
            + "var d4 = function() {};\n" 
            + "function d5(){};\n"
            + "}\n"
            + "\n"
            + "var g = function() {\n"
            + "var g1 = g2.g3;\n"
            + "var g4 = function() {};\n"
            + "function g5(){}; var i = {a: 1};\n"
            + "}\n"
            + "\n"
            + "var e;\n"
            + "e;\n"
            + "\n"
            + "function f(){}\n\n"
            + "f();\n"
            + "var a = UWA.Data({\"type\": 1}\n"
            + "   var h = window.document.getElementById(\"a\")\n"
            + "var l = Array()\n"
            + "  var d = ...UWA.Data(  // error\n"
            + "  var b = 11;  // atomic type Number\n"
            + " var c = true    // atomic type Boolean\n"
            + "var f = null; var g = \"string\"\n"
            + "var e = widget;\n"
            + "var k = window.document\n"
            + "             var i = {a: 1}\n"
            + "var j = [1, \"two\", false, null, undefined]";      

      //click on editor
      selenium.clickAt(Locators.EDITOR_LOCATOR, "5,5");

     IDE.EDITOR.typeTextIntoEditor(0, jsText);
      Thread.sleep(TestConstants.SLEEP);

      //In 2 seconds, after stopping typing text, 
      //next node structure should be displayed in the Outline Panel:
      assertEquals("a : Object", IDE.OUTLINE.getTitle(0, 0));
      assertEquals("b : Object", IDE.OUTLINE.getTitle(1, 0));
      assertEquals("c()", IDE.OUTLINE.getTitle(2, 0));
      assertEquals("d()", IDE.OUTLINE.getTitle(3, 0));
      assertEquals("g()", IDE.OUTLINE.getTitle(4, 0));
      
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("e", IDE.OUTLINE.getTitle(5, 0));
      assertEquals("f()", IDE.OUTLINE.getTitle(6, 0));     
      IDE.OUTLINE.checkOutlineTreeNodeSelected(6, "f()", true);
      OulineTreeHelper.checkIconNearToken(0, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(1, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(2, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(3, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(4, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(5, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(6, "function-item.png", true);

      // test issue GWTX-76 "Return initialization statement of JavaScript variable in the InitializationStatement property of token from JavaScriptParser"
      assertEquals("a : Object", IDE.OUTLINE.getTitle(7, 0));      
      OulineTreeHelper.checkIconNearToken(7, "var-item.png", false);
      
      assertEquals("h : Object", IDE.OUTLINE.getTitle(8, 0));      
      OulineTreeHelper.checkIconNearToken(8, "var-item.png", false);
      
      assertEquals("l : Object", IDE.OUTLINE.getTitle(9, 0));      
      OulineTreeHelper.checkIconNearToken(9, "var-item.png", false);

      assertEquals("b : number", IDE.OUTLINE.getTitle(10, 0));      
      OulineTreeHelper.checkIconNearToken(10, "var-item.png", false);
      
      assertEquals("c : boolean", IDE.OUTLINE.getTitle(11, 0));      
      OulineTreeHelper.checkIconNearToken(11, "var-item.png", false);
      
      assertEquals("f : Object", IDE.OUTLINE.getTitle(12, 0));      
      OulineTreeHelper.checkIconNearToken(12, "var-item.png", false);
      
      assertEquals("g : string", IDE.OUTLINE.getTitle(13, 0));      
      OulineTreeHelper.checkIconNearToken(13, "var-item.png", false);
      
      assertEquals("e : Object", IDE.OUTLINE.getTitle(14, 0));      
      OulineTreeHelper.checkIconNearToken(14, "var-item.png", false);

      assertEquals("k : Object", IDE.OUTLINE.getTitle(15, 0));      
      OulineTreeHelper.checkIconNearToken(15, "var-item.png", false);
      
      assertEquals("i : Object", IDE.OUTLINE.getTitle(16, 0));      
      OulineTreeHelper.checkIconNearToken(16, "var-item.png", false);

      assertEquals("j : Array", IDE.OUTLINE.getTitle(17, 0));      
      OulineTreeHelper.checkIconNearToken(17, "var-item.png", false);
      
      //open node a
//      IDE.OUTLINE.clickOpenImg(0, 0);
//      firstCheckJavaScriptOutlineTree();

      //open node d
      IDE.OUTLINE.clickOpenImg(3, 0);
      //subnodes of d
      assertEquals("d1 : Object", IDE.OUTLINE.getTitle(4, 0));
      assertEquals("d4()", IDE.OUTLINE.getTitle(5, 0));
      assertEquals("d5()", IDE.OUTLINE.getTitle(6, 0));
      OulineTreeHelper.checkIconNearToken(4, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(5, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(6, "function-item.png", false);
      //other nodes
      assertEquals("g()", IDE.OUTLINE.getTitle(7, 0));
      assertEquals("e", IDE.OUTLINE.getTitle(8, 0));
      assertEquals("f()", IDE.OUTLINE.getTitle(9, 0));

      //open node g
      IDE.OUTLINE.clickOpenImg(7, 0);
      //subnodes of g
      assertEquals("g1 : Object", IDE.OUTLINE.getTitle(8, 0));
      assertEquals("g4()", IDE.OUTLINE.getTitle(9, 0));
      assertEquals("g5()", IDE.OUTLINE.getTitle(10, 0));
      assertEquals("i : Object", IDE.OUTLINE.getTitle(11, 0));      
      
      OulineTreeHelper.checkIconNearToken(8, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(9, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(10, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(11, "var-item.png", false);
      
      //other nodes
      assertEquals("e", IDE.OUTLINE.getTitle(12, 0));
      assertEquals("f()", IDE.OUTLINE.getTitle(13, 0));     
      
      //---- 6 -----------------
      //Click a node in Outline tree.
      //cursor jump to line, where a variable is defined
      OulineTreeHelper.clickNode(0);
      assertEquals("1 : 1", getCursorPositionUsingStatusBar());

      //---- 7 -----------------
      //Click g4 node in Outline tree
      //cursor jump to line, where g4 is defined
//      OulineTreeHelper.clickNode(10);
      IDE.OUTLINE.select(9);
//      assertEquals("21 : 1", getCursorPositionUsingStatusBar());
      assertEquals("20 : 1", getCursorPositionUsingStatusBar());

      //---- 8 -----------------
      //Create new text file.
      //new text file is active, Outline panel is hidden, Show Outline buttons on toolbar disappears
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.OUTLINE.checkOutlinePanelVisibility(false);
      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, false);

      //---- 9 -----------------
      //Click on tab with JavaScript file
     IDE.EDITOR.selectTab(0);
      //JavaScript file is active, Show Outline buttons on toolbar appears. 
      //Outline panel is shown and Outline tree has nodes with defined variables, 
      //functions, method and property
      IDE.OUTLINE.checkOutlinePanelVisibility(true);
      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.View.HIDE_OUTLINE, true);

      //check outline tree
      assertEquals("a : Object", IDE.OUTLINE.getTitle(0, 0));
      assertEquals("b : Object", IDE.OUTLINE.getTitle(1, 0));
      assertEquals("c()", IDE.OUTLINE.getTitle(2, 0));
      assertEquals("d()", IDE.OUTLINE.getTitle(3, 0));
      assertEquals("g()", IDE.OUTLINE.getTitle(4, 0));
      assertEquals("g1 : Object", IDE.OUTLINE.getTitle(5, 0));
      assertEquals("g4()", IDE.OUTLINE.getTitle(6, 0));
      assertEquals("g5()", IDE.OUTLINE.getTitle(7, 0));
      assertEquals("e", IDE.OUTLINE.getTitle(8, 0));
      assertEquals("f()", IDE.OUTLINE.getTitle(9, 0));
      IDE.OUTLINE.checkOutlineTreeNodeSelected(6, "g4()", true);

      //---- 10 -----------------
      //Save JavaScript file and close it
      final String jsFile = "JavaScriptFile.js";
      saveAsUsingToolbarButton(jsFile);
      Thread.sleep(TestConstants.SLEEP);
      
     IDE.EDITOR.closeTab(0);
      
      //text file is active, Outline panel is hidden
      assertEquals(TestConstants.UNTITLED_FILE_NAME + ".txt *",IDE.EDITOR.getTabTitle(0));
      IDE.OUTLINE.checkOutlinePanelVisibility(false);
      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.View.SHOW_OUTLINE, false);

      //---- 11 -----------------
      //Open JavaScript file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(jsFile, false);
      //existed JavaScript file is active, Outline panel is shown and Outline tree has 
      //nodes with defined variables, functions, method and property
      assertEquals(jsFile,IDE.EDITOR.getTabTitle(1));
      IDE.OUTLINE.checkOutlinePanelVisibility(true);
      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.View.HIDE_OUTLINE, true);

      //check outline tree
      Thread.sleep(TestConstants.SLEEP);
      firstCheckJavaScriptOutlineTree();
//      Thread.sleep(TestConstants.SLEEP);
      //TODO

//      IDE.OUTLINE.checkOutlineTreeNodeSelected(0, "a : Object", true);

      //---- 12 -----------------
      //Create new Google Gadget file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      //Gadget file is active. Outline panel is shown
      assertEquals("Untitled file.xml *",IDE.EDITOR.getTabTitle(2));
      IDE.OUTLINE.checkOutlinePanelVisibility(true);
      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.View.HIDE_OUTLINE, true);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("Module", IDE.OUTLINE.getTitle(0, 0));
      assertEquals("ModulePrefs", IDE.OUTLINE.getTitle(1, 0));
      assertEquals("Content", IDE.OUTLINE.getTitle(2, 0));
      //open Content node
      IDE.OUTLINE.clickOpenImg(2, 0);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("CDATA", IDE.OUTLINE.getTitle(3, 0));

      IDE.OUTLINE.checkOutlineTreeNodeSelected(0, "Module", true);

      //---- 13 -----------------
      //If Gadget file has text, clear it and enter such text:

      // delete default content 
     IDE.EDITOR.clickOnEditor();
     IDE.EDITOR.deleteFileContent();

      final String gadgetText =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
            + "<Content type=\"html\">\n" + "<![CDATA[\n" + "<script type=\"text/javascript\">\n"
            + "var prefs = new gadgets.Prefs();\n" + "\n" + "function displayGreeting () {\n" + "// Get current time\n"
            + "var today = new Date();\n" + "var time = today.getTime();\n" + "var html = \"\";\n" + "}\n"
            + "</script>\n" + "]]></Content></Module>\n";
           
     IDE.EDITOR.clickOnEditor();
     IDE.EDITOR.typeTextIntoEditor(2, gadgetText);
      Thread.sleep(TestConstants.SLEEP);
      
      //New nodes with variables, functions (may be with methods and properties) appear in Outline tree. 
      //If you click on some node, cursor jumps to the line in file, where this variable 
      //(function, method or property) is defined
      assertEquals("Module", IDE.OUTLINE.getTitle(0, 0));
      assertEquals("ModulePrefs", IDE.OUTLINE.getTitle(1, 0));
      assertEquals("Content", IDE.OUTLINE.getTitle(2, 0));
      //open Content node
      IDE.OUTLINE.clickOpenImg(2, 0);
      assertEquals("CDATA", IDE.OUTLINE.getTitle(3, 0));
      //open CDATA
      IDE.OUTLINE.clickOpenImg(3, 0);
      assertEquals("script", IDE.OUTLINE.getTitle(4, 0));
      //open script
      IDE.OUTLINE.clickOpenImg(4, 0);
      assertEquals("prefs : gadgets.Prefs", IDE.OUTLINE.getTitle(5, 0));
      assertEquals("displayGreeting()", IDE.OUTLINE.getTitle(6, 0));

      //open displayGreeting
      IDE.OUTLINE.clickOpenImg(6, 0);
      assertEquals("today : Date", IDE.OUTLINE.getTitle(7, 0));
      assertEquals("time : Object", IDE.OUTLINE.getTitle(8, 0));
      assertEquals("html : Object", IDE.OUTLINE.getTitle(9, 0));  
      
      //click on prefs node
      IDE.OUTLINE.select(5);
      assertEquals("7 : 1", getCursorPositionUsingStatusBar());
      //click on today node
      IDE.OUTLINE.select(7);
      assertEquals("11 : 1", getCursorPositionUsingStatusBar());

      //---- 14 -----------------
      //Go to JavaScript file
     IDE.EDITOR.selectTab(1);
      Thread.sleep(TestConstants.SLEEP);
      //Outline Panel is visible, Outline tree must refresh and show varialbes, 
      //functions, method and properties from current file
      assertEquals(jsFile,IDE.EDITOR.getTabTitle(1));
      IDE.OUTLINE.checkOutlinePanelVisibility(true);
      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.View.HIDE_OUTLINE, true);

      //check outline tree
      Thread.sleep(TestConstants.SLEEP);
      
      firstCheckJavaScriptOutlineTree();

      //TODO:
//      IDE.OUTLINE.checkOutlineTreeNodeSelected(0, "a : Object", true);
      
      // walk through content to navigate in editor
     IDE.EDITOR.clickOnEditor();
      for (int i = 1; i < 8; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }

      Thread.sleep(TestConstants.SLEEP);
      assertEquals("8 : 1", getCursorPositionUsingStatusBar());
      
      //TODO:
//      IDE.OUTLINE.checkOutlineTreeNodeSelected(1, "b : Object", true);

      for (int i = 8; i < 17; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }

      Thread.sleep(TestConstants.SLEEP);
      assertEquals("17 : 1", getCursorPositionUsingStatusBar());
      //TODO:
//      IDE.OUTLINE.checkOutlineTreeNodeSelected(1, "b : Object", false);
//      IDE.OUTLINE.checkOutlineTreeNodeSelected(6, "d5()", true);

      //---- 15 -----------------
      //Close Outline tab and click Show Outline button on toolbar

      //After closing Outline tab, Outline Panel hides. 
      //After clicking on Show Outline buttons, Outline Panel appears

//      selenium.click("scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]/tab[index=0]/icon");  // see bug http://jira.exoplatform.org/browse/IDE-417
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.HIDE_OUTLINE);
      
      Thread.sleep(TestConstants.SLEEP);
      IDE.OUTLINE.checkOutlinePanelVisibility(false);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      IDE.OUTLINE.checkOutlinePanelVisibility(true);

      // check outline tree
      secondCheckJavaScriptOutlineTree();

      // close google gadget file
     IDE.EDITOR.closeUnsavedFileAndDoNotSave(2);
      Thread.sleep(TestConstants.SLEEP);

      // close js file
     
     IDE.EDITOR.closeTab(1);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(jsFile, false);
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.HIDE_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      IDE.OUTLINE.checkOutlinePanelVisibility(false);

      // remove jsFile from workspace panel
      IDE.NAVIGATION.selectItem(WS_URL + jsFile); 
      IDE.NAVIGATION.deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);

      // close text file
     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      Thread.sleep(TestConstants.SLEEP);
   }

   private void firstCheckJavaScriptOutlineTree() throws Exception
   {
      assertEquals("a : Object", IDE.OUTLINE.getTitle(0, 0));
//      assertEquals("\"a1\"", IDE.OUTLINE.getTitle(1, 0));
//      assertEquals("a2", IDE.OUTLINE.getTitle(2, 0));
//      assertEquals("a5", IDE.OUTLINE.getTitle(3, 0));
      assertEquals("b : Object", IDE.OUTLINE.getTitle(1, 0));
      assertEquals("c()", IDE.OUTLINE.getTitle(2, 0));
      assertEquals("d()", IDE.OUTLINE.getTitle(3, 0));
      assertEquals("g()", IDE.OUTLINE.getTitle(4, 0));
      assertEquals("e", IDE.OUTLINE.getTitle(5, 0));
      assertEquals("f()", IDE.OUTLINE.getTitle(6, 0));
   } 

   private void secondCheckJavaScriptOutlineTree() throws Exception
   {
      assertEquals("a : Object", IDE.OUTLINE.getTitle(0, 0));
      assertEquals("b : Object", IDE.OUTLINE.getTitle(1, 0));
      assertEquals("c()", IDE.OUTLINE.getTitle(2, 0));
      assertEquals("d()", IDE.OUTLINE.getTitle(3, 0));
      assertEquals("d1 : Object", IDE.OUTLINE.getTitle(4, 0));
      assertEquals("d4()", IDE.OUTLINE.getTitle(5, 0));
      assertEquals("d5()", IDE.OUTLINE.getTitle(6, 0));
      assertEquals("g()", IDE.OUTLINE.getTitle(7, 0));
      assertEquals("e", IDE.OUTLINE.getTitle(8, 0));
      assertEquals("f()", IDE.OUTLINE.getTitle(9, 0));
   }
}