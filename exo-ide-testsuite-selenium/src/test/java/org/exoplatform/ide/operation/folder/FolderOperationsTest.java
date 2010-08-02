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
package org.exoplatform.ide.operation.folder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FolderOperationsTest extends BaseTest
{
   /*private void selectWorkspace() throws Exception
   {
      selenium.deleteAllVisibleCookies();
      selenium.open("/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
      selenium.waitForPageToLoad("10000");
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      Assert.assertTrue(selenium
         .isTextPresent("Workspace is not set. Goto Window->Select workspace in main menu for set working workspace?"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(1000);
      selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]");
      selenium.click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[0]/col[fieldName=entryPoint||0]");
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]/");
      Assert.assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      Thread.sleep(1000);
   }*/

   /**
    * Test to create folder using ToolBar button. (TestCase IDE-3)
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolderFromToolbar() throws Exception
   {
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
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
            "Test Folder");
      selenium
         .keyPress(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "\\13");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isTextPresent("Test Folder"));
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Test Folder]/col[fieldName=nodeTitle||0]"));
   }

   /**
    * Test to create folder using main menu (TestCase IDE-3).
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolderMenu() throws Exception
   {
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'New')]", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Folder...']", "");
      assertTrue(selenium.isTextPresent("Name of new folder:"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isTextPresent("New Folder"));
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Test Folder]/col[fieldName=nodeTitle||0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=New Folder]/col[fieldName=nodeTitle||0]"));
      clearCreationTestResult("Test Folder");
   }

   /**
    * Test to delete folder using ToolBar button. (TestCase IDE-18)
    * 
    * @throws Exception
    */
   @Test
   public void testDeleteFolder() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "FolderToDelete");
      selenium
         .keyPress(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "\\13");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isTextPresent("FolderToDelete"));
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=FolderToDelete]/col[fieldName=nodeTitle||0]"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete FolderToDelete ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertFalse(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=FolderToDelete]/col[fieldName=nodeTitle||0]"));
   }

   /**
    * Test the rename folder operation (TestCase IDE-51).
    * 
    * @throws Exception
    */
   @Test
   public void testRenameFolder() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "renamed");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      Thread.sleep(1000);
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=renamed]/col[fieldName=nodeTitle||0]"));
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Rename')]", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideRenameItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideRenameItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideRenameItemFormRenameButton\"]"));
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideRenameItemFormDynamicForm\"]/item[name=ideRenameItemFormRenameField||title=ideRenameItemFormRenameField]/element");
      assertEquals(
         "renamed",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideRenameItemFormDynamicForm\"]/item[name=ideRenameItemFormRenameField||title=ideRenameItemFormRenameField]/element"));
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideRenameItemFormDynamicForm\"]/item[name=ideRenameItemFormRenameField||title=ideRenameItemFormRenameField]/element",
            "renamedNew");
      selenium
         .keyPress(
            "scLocator=//DynamicForm[ID=\"ideRenameItemFormDynamicForm\"]/item[name=ideRenameItemFormRenameField||title=ideRenameItemFormRenameField]/element",
            "\\13");
      Thread.sleep(1000);
      assertFalse(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=renamed]/col[fieldName=nodeTitle||0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=renamedNew]/col[fieldName=nodeTitle||0]"));
      clearCreationTestResult("renamedNew");
   }

   @Test
   public void testCreateFolderWithNonLatinSymbols() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "");
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "�������� �����");
      assertEquals(
         "�������� �����",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element"));
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]/");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=�������� �����]/col[fieldName=nodeTitle||0]"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete �������� ����� ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertFalse(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=��������� �����]/col[fieldName=nodeTitle||0]"));
   }

   @Test
   public void testRootFolder() throws Exception
   {
      Thread.sleep(1000);
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=dev-monit||0]/col[fieldName=name||1]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Text File\")]", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]"));
      selenium.mouseDownAt("//div[@title='Save As...']//img", "");
      selenium.mouseUpAt("//div[@title='Save As...']//img", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]"));
      String textFileName =
         selenium
            .getValue("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField]/element");
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]");
      System.out.println(textFileName);
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" + textFileName
         + "]/col[1]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[0]/col[1]");
      selenium.mouseDownAt("//div[contains(@title,'Refresh')]//img", "");
      selenium.mouseUpAt("//div[contains(@title,'Refresh')]//img", "");
      Thread.sleep(2000);
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" + textFileName
         + "]/col[1]"));
      clearCreationTestResult(textFileName);
   }
   
   @Test
   public void testDeleteSeveralFoldersSimultaniously() throws Exception{
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      selenium.click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "");
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "test 1");
      selenium.keyPress("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "\\13");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 1]/col[fieldName=nodeTitle||0]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 1]/col[1]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      selenium.click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "");
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "test 2");
      selenium.keyPress("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "\\13");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 2]/col[fieldName=nodeTitle||0]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[0]/col[1]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      selenium.click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "");
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "test 3");
      selenium.keyPress("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "\\13");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 3]/col[fieldName=nodeTitle||0]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 3]/col[fieldName=nodeTitle||0]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"REST Service\")]", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]"));
      selenium.mouseDownAt("//div[@title='Save As...']//img", "");
      selenium.mouseUpAt("//div[@title='Save As...']//img", "");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField]/element", "test.groovy");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test.groovy]/col[0]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 1]/col[0]/open");
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 2]/col[1]");
      selenium.controlKeyUp();
      assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));
      assertTrue(selenium.isElementPresent("//div[@title='Delete Item(s)...']/div[@elementenabled='false']"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 1]/col[1]");
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 3]/col[1]");
      selenium.controlKeyUp();
      assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));
      assertTrue(selenium.isElementPresent("//div[@title='Delete Item(s)...']/div[@elementenabled='true']"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete 2 items?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 1]/col[fieldName=nodeTitle||0]"));
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 2]/col[fieldName=nodeTitle||0]"));
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test 3]/col[fieldName=nodeTitle||0]"));
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=test.groovy]/col[fieldName=nodeTitle||0]"));
   }
   

   private void clearCreationTestResult(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" + name + "]/col[1]");
      selenium.mouseDownAt("//div[contains(@title,'Delete')]//img", "");
      selenium.mouseUpAt("//div[contains(@title,'Delete')]//img", "");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" + name
         + "]/col[fieldName=nodeTitle||0]"));
   }

}
