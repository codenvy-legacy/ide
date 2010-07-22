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
package org.exoplatform.ideall.operation.folder;

import org.exoplatform.ideall.AbstractTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
@Test
public class FolderOperationsTest extends AbstractTest
{
   /*private void selectWorkspace() throws Exception
   {
      selenium.deleteAllVisibleCookies();
      selenium.open("/org.exoplatform.ideall.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
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
      selenium.open("/org.exoplatform.ideall.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
      selenium.waitForPageToLoad("10000");
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      Assert.assertTrue(selenium.isTextPresent("Name of new folder:"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));
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
      Assert.assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      Assert.assertTrue(selenium.isTextPresent("Test Folder"));
      Assert
         .assertTrue(selenium
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
      Assert.assertTrue(selenium.isTextPresent("Name of new folder:"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]");
      Thread.sleep(1000);
      Assert.assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      Assert.assertTrue(selenium.isTextPresent("New Folder"));
      Assert
         .assertTrue(selenium
            .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=Test Folder]/col[fieldName=nodeTitle||0]"));
      Assert
         .assertTrue(selenium
            .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=New Folder]/col[fieldName=nodeTitle||0]"));
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
      selenium.waitForPageToLoad("20000");
      Thread.sleep(10000);
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
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
      Assert.assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      Assert.assertTrue(selenium.isTextPresent("FolderToDelete"));
      Assert
         .assertTrue(selenium
            .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=FolderToDelete]/col[fieldName=nodeTitle||0]"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(1000);
      Assert.assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      Assert.assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      Assert.assertTrue(selenium.isTextPresent("exact:Do you want to delete FolderToDelete ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(1000);
      Assert.assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      Assert
         .assertFalse(selenium
            .isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=FolderToDelete]/col[fieldName=nodeTitle||0]"));
   }
}
