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
      checkCreateFolderFormElements();
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
      assertElementPresentInWorkspaceTree("Test Folder");
      clearCreationTestResult("Test Folder");
   }

   /**
    * Test to create folder using main menu (TestCase IDE-3).
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolderMenu() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'New')]", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Folder...']", "");
      checkCreateFolderFormElements();
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertElementPresentInWorkspaceTree("New Folder");
      clearCreationTestResult("New Folder");
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
      createFolder("FolderToDelete");
      clickOnToolbarButton("Delete Item(s)...");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete FolderToDelete ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertElementNotPresentInWorkspaceTree("FolderToDelete");
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
      createFolder("RenameMe");
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
         "RenameMe",
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
      assertElementPresentInWorkspaceTree("renamedNew");
      clearCreationTestResult("renamedNew");
   }

   @Test
   public void testCreateFolderWithNonLatinSymbols() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      createFolder("Тестовая папка");
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete Тестовая папка ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertElementNotPresentInWorkspaceTree("Тестовая папка");
   }

   @Test
   public void testRootFolder() throws Exception
   {
      Thread.sleep(1000);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=dev-monit||0]/col[fieldName=name||1]");
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
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + textFileName
         + "]/col[1]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      clickOnToolbarButton("Refresh Selected Folder");
      Thread.sleep(2000);
      assertElementPresentInWorkspaceTree(textFileName);
      clearCreationTestResult(textFileName);
   }

   @Test
   public void testDeleteSeveralFoldersSimultaniously() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      createFolder("test 1");
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 1]/col[1]");
      createFolder("test 2");
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[0]/col[1]");
      createFolder("test 3");
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 3]/col[fieldName=nodeTitle||0]");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"REST Service\")]", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]"));
      selenium.mouseDownAt("//div[@title='Save As...']//img", "");
      selenium.mouseUpAt("//div[@title='Save As...']//img", "");
      selenium
         .type(
            "scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField]/element",
            "test.groovy");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]");
      Thread.sleep(1000);
      assertTrue(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test.groovy]/col[0]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 1]/col[0]/open");
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 2]/col[1]");
      selenium.controlKeyUp();
      assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));
      assertTrue(selenium.isElementPresent("//div[@title='Delete Item(s)...']/div[@elementenabled='false']"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 1]/col[1]");
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 3]/col[1]");
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

      assertElementNotPresentInWorkspaceTree("test 1");
      assertElementNotPresentInWorkspaceTree("test 2");
      assertElementNotPresentInWorkspaceTree("test 3");
      assertElementNotPresentInWorkspaceTree("test.groovy");

   }

   /**
    * Deletes the created in tests items.
    * 
    * @param name
    * @throws Exception
    */
   private void clearCreationTestResult(String name) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + name + "]/col[1]");
      clickOnToolbarButton("Delete Item(s)...");
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      assertElementNotPresentInWorkspaceTree(name);
   }

   /**
    * Checks the present of create folder form elements.
    */
   private void checkCreateFolderFormElements()
   {
      assertTrue(selenium.isTextPresent("Name of new folder:"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));
   }

}
