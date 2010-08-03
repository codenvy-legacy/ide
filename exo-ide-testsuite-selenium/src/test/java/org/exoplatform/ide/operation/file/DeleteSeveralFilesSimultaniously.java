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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.*;
import org.exoplatform.ide.BaseTest;
import org.junit.Test;

public class DeleteSeveralFilesSimultaniously extends BaseTest
{
   	/**
	    * 
	    * 
	    * @throws Exception
	    */
	   @Test  	        
	   public void testDeleteSeveralFilesSimultaniously() throws Exception {
		   selenium.open("http://127.0.0.1:8888/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
			Thread.sleep(15000);
		   // -------1-------
			// create file groovy
			selenium.mouseDownAt("//div[@title='New']//img", "");
			selenium.mouseUpAt("//div[@title='New']//img", "");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Groovy Script\")]", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_8||index=0]/"));
			assertTrue(selenium.isTextPresent("Untitled file.groovy *"));
			assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_9||index=1]/"));
			// ----control string in eclipse-----
			selenium.mouseDownAt("//div[@title='Save As...']//img", "");
			selenium.mouseUpAt("//div[@title='Save As...']//img", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Enter new file name:"));
			assertTrue(selenium.isTextPresent("Save file as"));
			selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||index=2||Class=TextItem]/element", "Test.groovy");
			selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
			Thread.sleep(1000);
			Thread.sleep(1000);
			assertTrue(selenium.isTextPresent("Test.groovy"));
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			Thread.sleep(1000);
			assertFalse(selenium.isElementPresent("//body[@class='editbox']"));
			// -------------------
			// create file test 1 xml
			selenium.mouseDownAt("//div[@title='New']//img", "");
			selenium.mouseUpAt("//div[@title='New']//img", "");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"XML File\")]", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_8||index=0]/"));
			assertTrue(selenium.isTextPresent("Untitled file.xml *"));
			// ----control string in eclipse-----
			selenium.mouseDownAt("//div[@title='Save As...']//img", "");
			selenium.mouseUpAt("//div[@title='Save As...']//img", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Enter new file name:"));
			assertTrue(selenium.isTextPresent("Save file as"));
			selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||index=2||Class=TextItem]/element", "test 1.xml");
			selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
			Thread.sleep(1000);
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			Thread.sleep(2000);
			assertFalse(selenium.isElementPresent("//body[@class='editbox']"));
			// create file test 2 html
			selenium.mouseDownAt("//div[@title='New']//img", "");
			selenium.mouseUpAt("//div[@title='New']//img", "");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"HTML File\")]", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_8||index=0]/"));
			assertTrue(selenium.isTextPresent("Untitled file.html *"));
			// ----control string in eclipse-----
			selenium.mouseDownAt("//div[@title='Save As...']//img", "");
			selenium.mouseUpAt("//div[@title='Save As...']//img", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Enter new file name:"));
			assertTrue(selenium.isTextPresent("Save file as"));
			selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||index=2||Class=TextItem]/element", "test 2.html");
			selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
			Thread.sleep(1000);
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			Thread.sleep(1000);
			// --------2--------
			// create_folder
			selenium.mouseDownAt("//div[@title='New']//img", "");
			selenium.mouseUpAt("//div[@title='New']//img", "");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Folder\")]", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]/header/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Create folder"));
			assertTrue(selenium.isTextPresent("Name of new folder:"));
			selenium.contextMenu("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField||title=ideCreateFolderFormNameField||value=New%20Folder||index=2||Class=TextItem]/element");
			selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", "test 1");
			selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]/start");
			Thread.sleep(1000);
			// create groovy file in folder
			selenium.mouseDownAt("//div[@title='New']//img", "");
			selenium.mouseUpAt("//div[@title='New']//img", "");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Groovy Script\")]", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_8||index=0]/"));
			assertTrue(selenium.isTextPresent("Untitled file.groovy *"));
			// ----control string in eclipse-----
			selenium.mouseDownAt("//div[@title='Save As...']//img", "");
			selenium.mouseUpAt("//div[@title='Save As...']//img", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Enter new file name:"));
			assertTrue(selenium.isTextPresent("Save file as"));
			selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||index=2||Class=TextItem]/element", "test.groovy");
			selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
			Thread.sleep(1000);
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			Thread.sleep(1000);
			assertFalse(selenium.isElementPresent("//body[@class='editbox']"));
			// create XML file in folder
			selenium.mouseDownAt("//div[@title='New']//img", "");
			selenium.mouseUpAt("//div[@title='New']//img", "");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"XML File\")]", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_8||index=0]/"));
			assertTrue(selenium.isTextPresent("Untitled file.xml *"));
			// ----control string in eclipse-----
			selenium.mouseDownAt("//div[@title='Save As...']//img", "");
			selenium.mouseUpAt("//div[@title='Save As...']//img", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Enter new file name:"));
			assertTrue(selenium.isTextPresent("Save file as"));
			selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||index=2||Class=TextItem]/element", "test 1.xml");
			selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
			Thread.sleep(1000);
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			Thread.sleep(1000);
			// create file test 2 html
			selenium.mouseDownAt("//div[@title='New']//img", "");
			selenium.mouseUpAt("//div[@title='New']//img", "");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"HTML File\")]", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_8||index=0]/"));
			assertTrue(selenium.isTextPresent("Untitled file.html *"));
			// ----control string in eclipse-----
			selenium.mouseDownAt("//div[@title='Save As...']//img", "");
			selenium.mouseUpAt("//div[@title='Save As...']//img", "");
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Enter new file name:"));
			assertTrue(selenium.isTextPresent("Save file as"));
			selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||index=2||Class=TextItem]/element", "test 2.html");
			selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
			Thread.sleep(1000);
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			Thread.sleep(1000);
			assertFalse(selenium.isElementPresent("//body[@class='editbox']"));
			// -----3-------
			selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[7]/col[0]");
			Thread.sleep(1000);
			selenium.controlKeyDown();
			selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[0]");
			selenium.shiftKeyDown();
			selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[0]");
			selenium.shiftKeyUp();
			selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
			selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
			selenium.click("scLocator=//Canvas[ID=\"isc_WidgetCanvas_1\"]/");
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Delete Item(s)"));
			assertTrue(selenium.isTextPresent("Do you want to delete 3 items?"));
			// ----5----
			selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
			Thread.sleep(1000);
			selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Refresh\")]", "");
			Thread.sleep(1000);
			selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[0]");
			selenium.shiftKeyDown();
			selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[0]");
			selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
			selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Delete Item(s)"));
			assertTrue(selenium.isTextPresent("Do you want to delete 4 items?"));
			Thread.sleep(2000);
			selenium.click("scLocator=//Window[ID=\"ideDeleteItemForm\"]/closeButton/");
			Thread.sleep(1000);
			assertTrue(selenium.isTextPresent("test 1"));
			assertTrue(selenium.isTextPresent("test 1.xml"));
			assertTrue(selenium.isTextPresent("test 2.html"));
			assertTrue(selenium.isTextPresent("Test.groovy"));
			selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[0]");
			selenium.shiftKeyDown();
			selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[0]");
			selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
			selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
			assertTrue(selenium.isTextPresent("Delete Item(s)"));
			assertTrue(selenium.isTextPresent("Do you want to delete 4 items?"));
			Thread.sleep(1000);
			selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
			Thread.sleep(1000);
			assertFalse(selenium.isTextPresent("test 1"));
			assertFalse(selenium.isTextPresent("test 1.xml"));
			assertFalse(selenium.isTextPresent("test 2.html"));
			assertFalse(selenium.isTextPresent("Test.groovy"));
			selenium.open("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/");
			Thread.sleep(10000);
			assertFalse(selenium.isTextPresent("test 1"));
			assertFalse(selenium.isTextPresent("test 1.xml"));
			assertFalse(selenium.isTextPresent("test 2.html"));
			assertFalse(selenium.isTextPresent("Test.groovy"));   
	}
}