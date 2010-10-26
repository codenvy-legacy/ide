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
package org.exoplatform.ide.operation;

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TemplateOperationsTest extends BaseTest
{
   
	/**
	    * 
	    * 
	    * @throws Exception
	    */
	   @Test  	        
	   public void testRemoveNonDefaultFileTemplates() throws Exception {
				
		// --------1----------
			selenium.open("http://127.0.0.1:8888/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
			Thread.sleep(15000);
			selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"New\")]", "");
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"HTML File\")]", "");
			Thread.sleep(1000);
			selenium.focus("//body[@class='editbox']");
			// Using type command me be (type...)
			// delete all Symbols
			// ----------2-------------
		
			for (int i=0; i<100;i++){
				
				selenium.keyPress("//body[@class='editbox']", "46");
			}
				
			selenium.typeKeys("//body[@class='editbox']", "<html>");
			selenium.keyDown("//body[@class='editbox']", "13");
			selenium.keyUp("//body[@class='editbox']", "13");
			selenium.typeKeys("//body[@class='editbox']", "<head>");
			selenium.keyDown("//body[@class='editbox']", "13");
			selenium.keyUp("//body[@class='editbox']", "13");
			selenium.typeKeys("//body[@class='editbox']", "<title></title>");
			selenium.keyDown("//body[@class='editbox']", "13");
			selenium.keyUp("//body[@class='editbox']", "13");
			selenium.typeKeys("//body[@class='editbox']", "</head>");
			selenium.keyDown("//body[@class='editbox']", "13");
			selenium.keyUp("//body[@class='editbox']", "13");
			selenium.typeKeys("//body[@class='editbox']", "<bod");
			selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
			selenium.typeKeys("//body[@class='editbox']", ">");
			selenium.keyDown("//body[@class='editbox']", "13");
			selenium.keyUp("//body[@class='editbox']", "13");
			selenium.typeKeys("//body[@class='editbox']", "<h1>Hello World!</h1>");
			selenium.keyDown("//body[@class='editbox']", "13");
			selenium.keyUp("//body[@class='editbox']", "13");
			selenium.typeKeys("//body[@class='editbox']", "</bod");
			selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
			selenium.typeKeys("//body[@class='editbox']", ">");
			selenium.keyDown("//body[@class='editbox']", "13");
			selenium.keyUp("//body[@class='editbox']", "13");
			selenium.typeKeys("//body[@class='editbox']", "</html>");
			Thread.sleep(5000);
			// -------2--------
			selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Save As Template...\")]", "");
		// control window created
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSaveAsTemplateForm\"]"));
			assertTrue(selenium.isTextPresent("Save file as template"));
			assertTrue(selenium.isTextPresent("Description:"));
			assertTrue(selenium.isTextPresent("Name:"));
			assertTrue(selenium.isTextPresent("Type:"));
			// ----------------------------
			selenium.type("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||index=3||Class=TextItem]/element", "test template");
			// --------3--------------
			selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]");
			// --------4-------------
			// contol info dialog created
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
			assertTrue(selenium.isTextPresent("Info"));
			assertTrue(selenium.isTextPresent("Template created successfully!"));
			// ----------------------------
			// --------5-------------
			selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
			Thread.sleep(1000);
			// --------6--------
			selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"New\")]", "");
			selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template\")]", "");
			Thread.sleep(5000);
			// control "Create file" dialog window
			assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item"));
			assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]"));
			assertTrue(selenium.isTextPresent("Name"));
			assertTrue(selenium.isTextPresent("Description"));
			// --------------------------------
			// mozno procontrolirovat imya v eclipse
			selenium.click("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[6]/col[fieldName=name||1]");
			// -------7----------
			selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/");
			// control Ideall delete template dialog
			assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
			assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
			assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
			// ----------------------------
			// ------8-------
			selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
			// control Template test template deleted dialog
			Thread.sleep(1000);
			assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
			// -------9---------
			assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
			assertTrue(selenium.isTextPresent("Template test template deleted."));
			// ----------------------------
			selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
			// control content in create file window
			assertFalse(selenium.isTextPresent("test template"));
			String Emty_XML = selenium.getText("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[0]/col[fieldName=name||1]");
			String Emty_HTML = selenium.getText("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[1]/col[fieldName=name||1]");
			String Emty_Text = selenium.getText("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[2]/col[fieldName=name||1]");
			String Google_gadget = selenium.getText("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[3]/col[fieldName=name||1]");
			String Groovy_Rest_Service = selenium.getText("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[4]/col[fieldName=name||1]");
			String Netvibes_Widget = selenium.getText("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[5]/col[fieldName=name||1]");
			System.out.println(Emty_XML);
			System.out.println(Emty_HTML);
			System.out.println(Emty_Text);
			System.out.println(Google_gadget);
			System.out.println(Groovy_Rest_Service);
			System.out.println(Netvibes_Widget);
			// -------10-------
			selenium.click("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/");
			selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/");
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
			Thread.sleep(5000);
			selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
			Thread.sleep(5000);
	   }
	   //--------------------------------------------------------------------------------------------------------------------------------------
	   /**
    * 
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFileFromTemplateTest() throws Exception {
		// --------1----------
		selenium.open("http://127.0.0.1:8888/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
		Thread.sleep(15000);
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		// ---------2--------
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"New\")]", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template...\")]", "");
		Thread.sleep(1000);
		// -------3-------
		selenium.mouseDownAt("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[5]/col[fieldName=name||1]", "");
		selenium.click("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/");
		selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
		selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_0]/");
		// control string in eclipse
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		// --------4------------
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Save As..\")]", "");
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
		assertTrue(selenium.isTextPresent("Enter new file name:"));
		selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element");
		selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element", "Test Groovy File.groovy");
		selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
		Thread.sleep(2000);
		assertTrue(selenium.isTextPresent("Test Groovy File.groovy"));
		// --------repeat_for_Emty_XML---------
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"New\")]", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template...\")]", "");
		Thread.sleep(1000);
		selenium.mouseDownAt("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[1]/col[fieldName=name||0]", "");
		selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
		selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[ID=isc_EditorTab_1]/");
		// control string in eclipse
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Save As..\")]", "");
		Thread.sleep(1000);
		assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
		assertTrue(selenium.isTextPresent("Enter new file name:"));
		selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element");
		selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element", "Test XML File.xml");
		selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
		Thread.sleep(10000);
		// --------repeat_for_Emty_HTML---------
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"New\")]", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template...\")]", "");
		Thread.sleep(1000);
		selenium.mouseDownAt("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[2]/col[fieldName=name||0]", "");
		selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
		// control string in eclipse
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Save As..\")]", "");
		Thread.sleep(1000);
		assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
		assertTrue(selenium.isTextPresent("Enter new file name:"));
		selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element");
		selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element", "Test HTML File.html");
		selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
		Thread.sleep(2000);
		assertTrue(selenium.isTextPresent("Test XML File.xml"));
		// --------repeat_for_Emty_Text---------
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"New\")]", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template...\")]", "");
		Thread.sleep(1000);
		selenium.mouseDownAt("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[3]/col[fieldName=name||0]", "");
		selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
		// control string in eclipse
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Save As..\")]", "");
		Thread.sleep(1000);
		assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
		assertTrue(selenium.isTextPresent("Enter new file name:"));
		selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element");
		selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||Class=TextItem]/element", "Test TXT File.txt");
		selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
		Thread.sleep(2000);
		selenium.open("http://127.0.0.1:8888/rest/private/"+WEBDAV_CONTEXT+"/repository/dev-monit/");
		Thread.sleep(10000);
		assertTrue(selenium.isTextPresent("Test XML File.xml"));
		assertTrue(selenium.isTextPresent("Test Groovy File.groovy"));
		assertTrue(selenium.isTextPresent("Test TXT File.txt"));
		assertTrue(selenium.isTextPresent("Test HTML File.html"));
		assertTrue(selenium.isTextPresent("Test XML File.xml"));
		selenium.open("http://127.0.0.1:8888/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
		Thread.sleep(10000);
		// ----delete_files---------
		// step1
		selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]");
		selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
		selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
		selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
		Thread.sleep(2000);
		// step2
		selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]");
		selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
		selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
		selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
		Thread.sleep(2000);
		// step3
		selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]");
		selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
		selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
		selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
		Thread.sleep(2000);
		// step4
		selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]");
		selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
		selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}

		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
		selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
		Thread.sleep(2000);
		// --------7-------
		selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"New\")]", "");
		selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template...\")]", "");
		Thread.sleep(1000);
		assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/"));
		assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/"));
		assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]"));
		assertTrue(selenium.isTextPresent("Name"));
		assertTrue(selenium.isTextPresent("Description"));
		assertTrue(selenium.isTextPresent("File Name"));
		selenium.click("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/closeButton/");
		// close tabs
		selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
		selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
		selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
		selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");   
   }
   
   //-----------------------------------------------------------------------------------------------
      /**
    * 
    * 
    * @throws Exception
    */
   @Test  	        
   public void testSaveFileAsFromTemplate() throws Exception {
	   {
	   	// --------1----------
		   Thread.sleep(1000);
		   selenium.open("http://127.0.0.1:8888/org.exoplatform.ide.IDEApplication/IDEApplication.html?gwt.codesvr=127.0.0.1:9997");
	   	Thread.sleep(15000);
	   	// --------2-------
	   	selenium.mouseDownAt("//div[@title='New']//img", "");
	   	selenium.mouseUpAt("//div[@title='New']//img", "");
	   	Thread.sleep(1000);
	   	selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"REST Service\")]", "");
	   	// ---------3--------
	   	Thread.sleep(1000);
	   	selenium.typeKeys("//body[@class='editbox']", "// test groovY file template");
	   	Thread.sleep(1000);
	   	selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
	   	selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Save As Template...\")]", "");
	   	// control dialog form Save file as template window
	   	assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSaveAsTemplateForm\"]/header/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormCancelButton\"]/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]/"));
	   	assertTrue(selenium.isTextPresent("Type:"));
	   	assertTrue(selenium.isTextPresent("Name:"));
	   	assertTrue(selenium.isTextPresent("Description:"));
	   	selenium.click("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||Class=TextItem]/element");
	   	selenium.type("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||Class=TextItem]/element", "test groovy template");
	   	selenium.click("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormDescriptionField||title=ideSaveAsTemplateFormDescriptionField||Class=TextAreaItem]/element");
	   	selenium.type("scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormDescriptionField||title=ideSaveAsTemplateFormDescriptionField||Class=TextAreaItem]/element", "test groovy template description");
	   	selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]/");
	   	// control dialog create dialog info
	   	Thread.sleep(1000);
	   	assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
	   	assertTrue(selenium.isTextPresent("Template created successfully!"));
	   	selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
	   	// ------------5----------
	   	selenium.mouseDownAt("//div[@title='New']//img", "");
	   	selenium.mouseUpAt("//div[@title='New']//img", "");
	   	Thread.sleep(1000);
	   	selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"From Template...\")]", "");
	   	// control create "Create file dialog window"
	   	Thread.sleep(1000);
	   	assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]"));
	   	assertTrue(selenium.isTextPresent("Name"));
	   	assertTrue(selenium.isTextPresent("Description"));
	   	assertTrue(selenium.isTextPresent("test groovy template"));
	   	assertTrue(selenium.isTextPresent("test groovy template description"));
	   	assertTrue(selenium.isTextPresent("File Name"));
	   	// --------6--------
	   	// control first line in eclipse
	   	selenium.click("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[6]/col[fieldName=description||2]");
	   	selenium.click("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField||Class=TextItem]/element");
	   	selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField||Class=TextItem]/element", "Test Groovy File.groovy");
	   	selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/end");
	   	Thread.sleep(1000);
	   	// --------7-------
	   	selenium.mouseDownAt("//div[@title='Save As...']//img", "");
	   	selenium.mouseUpAt("//div[@title='Save As...']//img", "");
	   	assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
	   	assertTrue(selenium.isTextPresent("Enter new file name:"));
	   	selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
	   	Thread.sleep(1000);
	   	assertTrue(selenium.isTextPresent("Test Groovy File.groovy"));
	   	// ----------8---------
	   	selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]");
	   	selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
	   	selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
	   	for (int second = 0;; second++) {
	   		if (second >= 60) fail("timeout");
	   		try { if (selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]/")) break; } catch (Exception e) {}
	   		Thread.sleep(1000);
	   	}

	   	assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/"));
	   	assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]/"));
	   	Thread.sleep(1000);
	   	selenium.click("scLocator=//Window[ID=\"ideDeleteItemForm\"]/header/");
	   	selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]/");
	   	Thread.sleep(2000);
	   	selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
	   	// closeall
	   	selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
	   	Thread.sleep(3000);
	   	selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
	   	selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon");
	   	Thread.sleep(3000);
	   	selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
	   	selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon"); 
	   		
	      }
	   }
	      }