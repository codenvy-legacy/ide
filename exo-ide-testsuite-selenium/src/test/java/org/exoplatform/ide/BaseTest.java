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

import static org.junit.Assert.*;

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
   public void clickOnToolbarButton(String buttonTitle) throws Exception
   {
      selenium.mouseDownAt("//div[@title='" + buttonTitle + "']//img", "");
      selenium.mouseUpAt("//div[@title='" + buttonTitle + "']//img", "");
      Thread.sleep(1000);
   }
   
   /**
    * Close tab by it's index.
    * 
    * @param index numeration starts with 0 index
    */
   protected void closeTab(String index) throws Exception
   {
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + index + "]/icon");
      Thread.sleep(1000);
   }
   
   /**
    * Returns the title of the tab with the pointed index.
    * 
    * @param index tab index
    * @return {@link String} tab's title
    * @throws Exception
    */
   protected String getTabTitle(int index) throws Exception
   {
      return selenium.getText("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + index + "]/title");
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
         else if (symbol == '\n')
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
   
   /**
    * Select the item in the search results tree. 
    * 
    * @param name item's name
    * @throws Exception
    */
   protected void selectItemInSearchResultsTree(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[name=" + name + "]/col[1]");
   }
   
   /**
    * Select the root workspace item in workspace tree.
    * 
    * @param name
    * @throws Exception
    */
   protected void selectRootOfWorkspaceTree() throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
   }


   /**
    * Check navigation workspace tree contains pointed item.
    * 
    * @param name name of item in the navigation tree
    * @throws Exception
    */
   protected void assertElementPresentInWorkspaceTree(String name) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name="+name+"]/col[0]"));
   }
   
   /**
    * Check item is shown in search results tree.
    * 
    * @param name
    * @throws Exception
    */
   protected void assertElementPresentSearchResultsTree(String name) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[name="+name+"]/col[0]"));
   }
   
   
   
   /**
    * Check navigation workspace tree doesn't contain pointed item.
    * 
    * @param name name of item in the navigation tree
    * @throws Exception
    */
   protected void assertElementNotPresentInWorkspaceTree(String name) throws Exception
   {
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name="+name+"]/col[0]"));
   }

   /**
    * Delete selected item in navigation tree.
    * 
    * @throws Exception
    */
   protected void deleteSelectedItem() throws Exception
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
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");

      //Check creation form elements
      assertTrue(selenium.isTextPresent("Name of new folder:"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));

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

      Thread.sleep(1000);
     //Check creation form is not shown
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertElementPresentInWorkspaceTree(folderName);
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
      assertTrue(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save As...']"));

      //click Save As
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save As...']", "");
      Thread.sleep(1000);

      checkSaveAsDialogAndSave(name);
   }

   protected void openFileFromNavigationTreeWithCodeEditor(String fileName) throws Exception
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
   
   protected void openFileFromSearchResultsWithCodeEditor(String fileName) throws Exception
   {
      //TODO add check form
      selenium.click("scLocator=//TreeGrid[ID=\"ideSearchResultItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(500);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
   }

	protected void openFileWithCkEditorAndSetAsDefault(String fileName) throws Exception
   {
      //TODO add check form
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(500);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[1]/col[0]");
      //click on checkbox Use as default editor
      selenium.click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
      Thread.sleep(2000);
      //TODO add check that editor opened
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
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[text()='"
         + menuName + "']", "");
   }

   /**
    * Check is button present on toolbar and is it enabled or disabled.
    * 
    * @param name button name
    * @param enabled boolean value
    */
   protected void checkToolbarButtonState(String name, boolean enabled)
   {
      if (enabled)
      {
         assertTrue(selenium.isElementPresent("//div[@title='" + name + "']/div[@elementenabled='true']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@title='" + name + "']/div[@elementenabled='false']"));
      }
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
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + folderName
         + "]/col[0]/open");
   }
   
   /**
    * 
    */
   protected void openCloseRootWorkspace()
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[0]/open");
   }
   
   /**
    * Close unsaved file withous saving it.
    * 
    * Close tab with tabIndex. Check is warning dialog appears.
    * Click No button]
    * 
    * @param tabIndex index of tab to close
    * @throws Exception
    */
   protected void closeUnsavedFileAndDoNotSave(String tabIndex) throws Exception
   {
      closeTab(tabIndex);
      
      //check is warning dialog appears
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      
      //click No button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      Thread.sleep(1000);
   }
   
   /**
    * Check is command in top menu enabled or disabled.
    * 
    * @param topMenuName mane of menu
    * @param commandName command name
    * @param enabled boolean value
    */
   protected void checkMenuCommandState(String topMenuName, String commandName, boolean enabled)
   {
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='" 
         + topMenuName + "']", "");
      if (enabled)
      {
      assertTrue(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='" 
         + commandName + "']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='" 
            + commandName + "']"));
      }
      selenium.mouseDown("//div[@class='exo-lockLayer']/");
   }
   
   /**
    * Open command from top menu.
    * 
    * @param topMenuName name of menu
    * @param commandName command name
    */
   protected void selectTopMenuCommand(String topMenuName, String commandName)
   {
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='" 
         + topMenuName + "']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='" 
         + commandName + "']", "");
   }
   
   /**
    * Check is file in tabIndex tab opened with CK editor.
    * 
    * @param tabIndex index of tab
    * @throws Exception
    */
   protected void checkCkEditorOpened(int tabIndex) throws Exception
   {
      String divIndex = String.valueOf(tabIndex + 2);
      assertTrue(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" 
         + divIndex + "]//table[@class='cke_editor']//td[@class='cke_contents']/iframe"));
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
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
      selenium
         .click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element");
      selenium
         .type(
            "scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element",
            "");
      selenium
         .type(
            "scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element",
            name);
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
   }
}
