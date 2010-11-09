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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeOutlineJavaScriptTest extends BaseTest
{
   //IDE-161:JavaScript Code Outline
   @Test
   public void testCodeOutlineJavascript() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //---- 2 -----------------
      //Create new JavaScript file and click on Show Outline button
      runCommandFromMenuNewOnToolbar(MenuCommands.New.JAVASCRIPT_FILE);
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 3 -----------------
      //Click Enter in editor and type such text:
      //var a
      typeTextIntoEditor(0, "var a");
      Thread.sleep(TestConstants.SLEEP);

      //In 2 seconds, after stopping typing text, new node a appeared in Outline tree. 
      //Near item appeard red circul with V, which means variable
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      checkOutlineTreeNodeSelected(0, "a", true);
      OulineTreeHelper.checkIconNearToken(0, "var-item.png", true);
      
      //---- 4 -----------------
      //continue typing text:
      //var a = {
      //   "a1": "1",
      //    a2: a3.a4,
      //    a5: function(),
      //    a6 function
      // };

      String textJson = " = {\n" + "\"a1\": \"1\",\n" + "a2: a3.a4,\n" + "a5: function(),\n" + "a6 function\n" + "};\n";

      typeTextIntoEditor(0, textJson);
      Thread.sleep(TestConstants.SLEEP);

      //In 2 seconds, after stopping typing text, next node structure should be displayed 
      //in the Outline Panel:
      //
      //variable a
      //  property "a1"
      //  property a2
      //  method a5

      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("\"a1\"", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("a2", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("a5", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      OulineTreeHelper.checkIconNearToken(1, "property-item.png", false);
      OulineTreeHelper.checkIconNearToken(2, "property-item.png", false);
      OulineTreeHelper.checkIconNearToken(3, "method-item.png", true);

      //---- 5 -----------------
      //Click Enter and enter text in editor:
      String jsText =
         "\n" + "var b = b1.b2;\n" + "\n" + "var c = function(){};\n" + "\n" + "function d() {\n" + "var d1 = d2.d3;\n"
            + "var d4 = function() {};\n" + "function d5(){};\n" + "}\n" + "\n" + "var g = function() {\n"
            + "var g1 = g2.g3;\n" + "var g4 = function() {};\n" + "function g5(){};\n" + "}\n" + "\n" + "var e;\n"
            + "e;\n" + "\n" + "function f(){}\n" + "f();";

      //click on editor
      selenium.clickAt("//body[@class='editbox']", "5,5");

      typeTextIntoEditor(0, jsText);
      Thread.sleep(TestConstants.SLEEP);

      //In 2 seconds, after stopping typing text, 
      //next node structure should be displayed in the Outline Panel:
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("b", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("c", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("d", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      assertEquals("g", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));
      
      Thread.sleep(TestConstants.SLEEP);
      
      
      assertEquals("e", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("f", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));
      checkOutlineTreeNodeSelected(6, "f", true);
      OulineTreeHelper.checkIconNearToken(0, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(1, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(2, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(3, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(4, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(5, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(6, "function-item.png", true);

      //open node a
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      firstCheckJavaScriptOutlineTree();

      //open node d
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //subnodes of d
      assertEquals("d1", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("d4", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("d5", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
      OulineTreeHelper.checkIconNearToken(7, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(8, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(9, "function-item.png", false);
      //other nodes
      assertEquals("g", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      assertEquals("e", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("f", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));

      //open node g
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //subnodes of g
      assertEquals("g1", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("g4", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));
      assertEquals("g5", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));
      OulineTreeHelper.checkIconNearToken(11, "var-item.png", false);
      OulineTreeHelper.checkIconNearToken(12, "function-item.png", false);
      OulineTreeHelper.checkIconNearToken(13, "function-item.png", false);
      //other nodes
      assertEquals("e", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));
      assertEquals("f", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[15]/col[0]"));

      //---- 6 -----------------
      //Click a node in Outline tree.
      //cursor jump to line, where a variable is defined
      OulineTreeHelper.clickNode(0);
      assertEquals("1 : 1", getCursorPositionUsingStatusBar());

      //---- 7 -----------------
      //Click g4 node in Outline tree
      //cursor jump to line, where g4 is defined
      OulineTreeHelper.clickNode(12);
      assertEquals("20 : 1", getCursorPositionUsingStatusBar());

      //---- 8 -----------------
      //Create new text file.
      //new text file is active, Outline panel is hidden, Show Outline buttons on toolbar disappears
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      checkOutlineVisibility(false);
      checkToolbarButtonPresentOnLeftSide(ToolbarCommands.View.SHOW_OUTLINE, false);

      //---- 9 -----------------
      //Click on tab with JavaScript file
      selectEditorTab(0);
      //JavaScript file is active, Show Outline buttons on toolbar appears. 
      //Outline panel is shown and Outline tree has nodes with defined variables, 
      //functions, method and property
      checkOutlineVisibility(true);
      checkToolbarButtonPresentOnLeftSide(ToolbarCommands.View.HIDE_OUTLINE, true);

      //check outline tree
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("b", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("c", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("d", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      assertEquals("g", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));
      assertEquals("g1", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("g4", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));
      assertEquals("g5", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("e", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("f", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
      checkOutlineTreeNodeSelected(6, "g4", true);

      //---- 10 -----------------
      //Save JavaScript file and close it
      final String jsFile = "JavaScriptFile.js";
      saveAsUsingToolbarButton(jsFile);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      //text file is active, Outline panel is hidden
      assertEquals("Untitled file.txt *", getTabTitle(0));
      checkOutlineVisibility(false);
      checkToolbarButtonPresentOnLeftSide(ToolbarCommands.View.SHOW_OUTLINE, false);

      //---- 11 -----------------
      //Open JavaScript file
      openFileFromNavigationTreeWithCodeEditor(jsFile, false);
      //existed JavaScript file is active, Outline panel is shown and Outline tree has 
      //nodes with defined variables, functions, method and property
      assertEquals(jsFile, getTabTitle(1));
      checkOutlineVisibility(true);
      checkToolbarButtonPresentOnLeftSide(ToolbarCommands.View.HIDE_OUTLINE, true);

      //check outline tree
      Thread.sleep(TestConstants.SLEEP);
      firstCheckJavaScriptOutlineTree();

      checkOutlineTreeNodeSelected(0, "a", true);

      //---- 12 -----------------
      //Create new Google Gadget file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.GOOGLE_GADGET_FILE);
      //Gadget file is active. Outline panel is shown
      assertEquals("Untitled file.xml *", getTabTitle(2));
      checkOutlineVisibility(true);
      checkToolbarButtonPresentOnLeftSide(ToolbarCommands.View.HIDE_OUTLINE, true);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("Module", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("ModulePrefs", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("Content", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      //open Content node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("CDATA", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));

      checkOutlineTreeNodeSelected(0, "Module", true);

      //---- 13 -----------------
      //If Gadget file has text, clear it and enter such text:

      // delete default content 
      goToLine(1);
      deleteFileContent();

      final String gadgetText =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
            + "<Content type=\"html\">\n" + "<![CDATA[\n" + "<script type=\"text/javascript\">\n"
            + "var prefs = new gadgets.Prefs();\n" + "\n" + "function displayGreeting () {\n" + "// Get current time\n"
            + "var today = new Date();\n" + "var time = today.getTime();\n" + "var html = \"\";\n" + "}\n"
            + "</script>\n" + "]]></Content></Module>\n";
      
      runToolbarButton(ToolbarCommands.View.HIDE_OUTLINE);
      
      selectIFrameWithEditor(2);
      selenium.clickAt("//body", "5,5");
      selectMainFrame();
      typeTextIntoEditor(2, gadgetText);
      Thread.sleep(TestConstants.SLEEP);
      
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      
      //New nodes with variables, functions (may be with methods and properties) appear in Outline tree. 
      //If you click on some node, cursor jumps to the line in file, where this variable 
      //(function, method or property) is defined
      assertEquals("Module", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("ModulePrefs", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("Content", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      //open Content node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("CDATA", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      //open CDATA
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));
      //open script
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("prefs", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("displayGreeting", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));

      //open displayGreeting
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("today", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("time", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("html", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));

      //click on prefs node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[1]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("7 : 1", getCursorPositionUsingStatusBar());
      //click on today node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[1]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("11 : 1", getCursorPositionUsingStatusBar());

      //---- 14 -----------------
      //Go to JavaScript file
      selectEditorTab(1);
      Thread.sleep(TestConstants.SLEEP);
      //Outline Panel is visible, Outline tree must refresh and show varialbes, 
      //functions, method and properties from current file
      assertEquals(jsFile, getTabTitle(1));
      checkOutlineVisibility(true);
      checkToolbarButtonPresentOnLeftSide(ToolbarCommands.View.HIDE_OUTLINE, true);

      //check outline tree
      Thread.sleep(TestConstants.SLEEP);
      
      firstCheckJavaScriptOutlineTree();

      checkOutlineTreeNodeSelected(0, "a", true);
      
      // walk through content to navigate in editor
      for (int i = 1; i <= 8; i++)
      {
         goToLine(i);
      }

      Thread.sleep(TestConstants.SLEEP);
      assertEquals("8 : 1", getCursorPositionUsingStatusBar());
      checkOutlineTreeNodeSelected(4, "b", true);

      for (int i = 9; i <= 17; i++)
      {
         goToLine(i);
      }

      Thread.sleep(TestConstants.SLEEP);
      assertEquals("17 : 1", getCursorPositionUsingStatusBar());
      checkOutlineTreeNodeSelected(4, "b", false);
      checkOutlineTreeNodeSelected(9, "d5", true);

      //---- 15 -----------------
      //Close Outline tab and click Show Outline button on toolbar

      //After closing Outline tab, Outline Panel hides. 
      //After clicking on Show Outline buttons, Outline Panel appears

//      selenium.click("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[index=0]/icon");  // see bug http://jira.exoplatform.org/browse/IDE-417
      runToolbarButton(ToolbarCommands.View.HIDE_OUTLINE);
      
      Thread.sleep(TestConstants.SLEEP);
      checkOutlineVisibility(false);

      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      checkOutlineVisibility(true);

      // check outline tree
      secondCheckJavaScriptOutlineTree();

      // close google gadget file
      closeUnsavedFileAndDoNotSave("2");
      Thread.sleep(TestConstants.SLEEP);

      // close js file
      closeTab("1");
      openFileFromNavigationTreeWithCodeEditor(jsFile, false);
      Thread.sleep(TestConstants.SLEEP);
      runToolbarButton(ToolbarCommands.View.HIDE_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      checkOutlineVisibility(false);

      // remove jsFile from workspace panel
      selectItemInWorkspaceTree(jsFile);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);

      // close text file
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(TestConstants.SLEEP);
   }

   private void firstCheckJavaScriptOutlineTree()
   {
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("\"a1\"", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("a2", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("a5", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      assertEquals("b", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));
      assertEquals("c", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("d", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));
      assertEquals("g", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("e", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("f", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
   } 

   private void secondCheckJavaScriptOutlineTree()
   {
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("b", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("c", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("d", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      assertEquals("d1", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));
      assertEquals("d4", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("d5", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));
      assertEquals("g", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("e", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("f", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
   }
}