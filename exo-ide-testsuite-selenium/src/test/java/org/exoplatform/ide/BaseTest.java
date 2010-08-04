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
package org.exoplatform.ide;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class BaseTest
{
   protected static Selenium selenium;

   @BeforeClass
   public static void startSelenium()
   {
      selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://127.0.0.1:8888/");
      selenium.start();
      selenium.open("/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
      selenium.waitForPageToLoad("30000");
      selenium.windowMaximize();
   }

   /**
    * Performs click on toolbar button and makes pause after it.
    * @param buttonTitle toolbar button title
    */
   public void clickOnToolbarButton(String buttonTitle) throws Exception {
      selenium.mouseDownAt("//div[@title='" + buttonTitle + "']//img", "");
      selenium.mouseUpAt("//div[@title='" + buttonTitle + "']//img", "");
      Thread.sleep(1000);
   }
   
   /**
    * Closes the editor tab by index.
    * 
    * @param index tab index
    */
   protected void closeTab(String index)
   {
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + index + "]/icon");
   }
   
   @AfterClass
   public static void stopSelenium()
   {
      selenium.stop();
   }
   
   /**
    * Types text to selected frame to body tag, which has attribute
    * class='editbox'.
    * 
    * Use keyPressNative for typing such symbols: 'y', '.'
    * 
    * Replace '\n' (Enter) symbol with keyDown and keyUp functions
    * 
    * @param text text to type
    */
   protected void typeText(String text)
   {
      for (int i = 0; i < text.length(); i++)
      {
         char symbol = text.charAt(i);
         if (symbol == 'y')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
         }
         else if (symbol =='\n')
         {
            selenium.keyDown("//body[@class='editbox']/", "\\13");
            selenium.keyUp("//body[@class='editbox']/", "\\13");
         }
         else if (symbol == '.')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
         }
         else
         {
            selenium.typeKeys("//body[@class='editbox']/", String.valueOf(symbol));
         }
      }
   }
   
   /**
    * Select main frame of IDE.
    * 
    * This method is used, after typing text in editor.
    * To type text you must select editor iframe. After typing,
    * to return to them main frame, use selectMainFrame()
    * 
    */
   protected void selectMainFrame()
   {
      selenium.selectFrame("relative=top");
   }
   
   /**
    * @param tabIndex begins from 0
    */
   protected void selectEditor(int tabIndex)
   {
      String divIndex = String.valueOf(tabIndex + 2);
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[" + divIndex + "]//iframe");
   }
   
   /**
    * 
    * @param tabIndex begins from 0
    * @param text (can be used '\n' as line break)
    */
   protected void typeTextIntoEditor(int tabIndex, String text)
   {
      selectEditor(tabIndex);
      typeText(text);
      selectMainFrame();
   }
   
   /**
    * Get text from tab number "tabIndex" from editor
    * @param tabIndex begins from 0
    */
   protected String getTextFromCodeEditor(int tabIndex)
   {
      selectEditor(tabIndex);
      String text = selenium.getText("//body[@class='editbox']");
      selectMainFrame();
      return text;
   }   
   
   /**
    * Select the item in the workspace navigation tree. 
    * 
    * @param name item name
    * @throws Exception
    */
   protected void selectItemInWorkspaceTree(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + name + "]/col[1]");
   }
   
   protected void assertElementPresentInWorkspaceTree(String name) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" 
         + name + "]/col[0]"));
   }
   
   /**
    * Delete selected item in navigation tree.
    * 
    * @throws Exception
    */
   protected void deleteSelectedFileOrFolder() throws Exception
   {
      clickOnToolbarButton("Delete Item(s)...");
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
      //TODO check deletion form
   }
   
   /**
    * Creates folder with name folderName.
    * 
    * Folder, that will be parent for folderName must be selected before.
    * 
    * Clicks on New button on toolbar, then click on Folder menu from list.
    * 
    * @param folderName folder name
    */
   protected void createFolder(String folderName) throws Exception
   {
      clickOnToolbarButton("New");
      //TODO check creation form
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            folderName);
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]/");
   }
   
   /**
    * Calls Save As command by clicking Save As... icon on toolbar.
    * 
    * Checks is dialog appears, and do all elements are present in window.
    * 
    * Enters name to text field and click Ok button
    * 
    * @param name file name
    * @throws Exception
    */
   protected void saveAsUsingToolbarButton(String name) throws Exception
   {
      clickOnToolbarButton("Save As...");
      checkSaveAsDialogAndSave(name);
   }
   
   /**
    * Call Save As command using top menu File.
    * 
    * @param name file name to save
    * @throws Exception
    */
   protected void saveAsByTopMenu(String name) throws Exception
   {
      //open menu File
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      
      //check is Save As enabled
      assertTrue(selenium.isElementPresent(
         "//td[@class='exo-popupMenuTitleField']/nobr[text()='Save As...']"));
      
      //click Save As
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save As...']", "");
      Thread.sleep(1000);
      
      checkSaveAsDialogAndSave(name);
   }
   
   protected void openFileWithCodeEditor(String fileName) throws Exception
   {
     //TODO add check form
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(500);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
   }
   
   protected void saveCurrentFile() throws Exception
   {
      clickOnToolbarButton("Save");
   }
   
   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * @param menuName
    */
   protected void openNewFileFromToolbar(String menuName) throws Exception
   {
      clickOnToolbarButton("New");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"" 
         + menuName + "\")]", "");
   }
   
   /**
    * Opens folder in Workspace tree (if folder is closed)
    * or closes folder (if it is opened)
    * 
    * Clicks on open sign of folder.
    * 
    * If folderName doesn't present in Workspace tree, test fails.
    * 
    * @param folderName
    */
   protected void openOrCloseFolder(String folderName)
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" 
         + folderName + "]/col[0]/open");
   }
   
   /**
    * Check is dialog window Save as file appeared
    * and do all elements present.
    * 
    * Enter name to text field and click Ok button
    * 
    * @param name file name
    */
   private void checkSaveAsDialogAndSave(String name)
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]"));
      assertTrue(selenium.isTextPresent("Save file as"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
      selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element", "");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element", name);
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
   }
}
