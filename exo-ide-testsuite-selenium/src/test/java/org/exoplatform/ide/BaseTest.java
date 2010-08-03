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
    * Select main frame of IDE.
    * 
    * This method used, after typing text in editor.
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
    * @param text
    */
   protected void typeTextIntoEditor(int tabIndex, String text)
   {
      selectEditor(tabIndex);
      selenium.typeKeys("//body", text);
      selectMainFrame();
   }
   
   protected void selectItemInWorkspaceTree(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + name + "]/col[1]");
   }
   
   protected void assertElementPresentInWorkspaceTree(String name) throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" 
         + name + "]/col[0]"));
   }
   
   protected void deleteSelectedFileOrFolder() throws Exception
   {
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
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
   protected void createFolder(String folderName)
   {
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
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
   protected void saveAsFile(String name) throws Exception
   {
      selenium.mouseDownAt("//div[@title='Save As...']//img", "");
      selenium.mouseUpAt("//div[@title='Save As...']//img", "");
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
   
   protected void openFileWithCodeEditor(String fileName) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(500);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[0]/col[0]");
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
   }
   
   protected void saveCurrentFile()
   {
      selenium.mouseDownAt("//div[@title='Save']//img", "");
      selenium.mouseUpAt("//div[@title='Save']//img", "");
   }
   
   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * @param menuName
    */
   protected void openNewFileFromToolbar(String menuName) throws Exception
   {
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      Thread.sleep(500);
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
}
