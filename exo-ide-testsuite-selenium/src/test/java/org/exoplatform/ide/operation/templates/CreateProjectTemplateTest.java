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
package org.exoplatform.ide.operation.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.After;
import org.junit.Test;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplateTest extends BaseTest
{
   
   private static final String NAME_FIELD = "scLocator=//DynamicForm[ID=\"ideCreateProjectTemplateFormNameFieldsForm\"]/item[name=ideCreateProjectTemplateFormNameField]/element";
   
   private static final String DESCRIPTION_FIELD = "scLocator=//DynamicForm[ID=\"ideCreateProjectTemplateFormNameFieldsForm\"]/item[name=ideCreateProjectTemplateFormDescriptionField]/element";
   
   private static final String TREE_GRID = "scLocator=//TreeGrid[ID=\"ideProjectTemplateTreeGrid\"]/";
   
   private static final String ADD_FOLDER_BUTTON = "scLocator=//IButton[ID=\"ideCreateProjectTemplateFormAddFolderButton\"]/";
   
   private static final String ADD_FILE_BUTTON = "scLocator=//IButton[ID=\"ideCreateProjectTemplateFormAddFileButton\"]/";
   
   private static final String DELETE_BUTTON = "scLocator=//IButton[ID=\"ideCreateProjectTemplateFormDeleteButton\"]/";
   
   private static final String CREATE_BUTTON = "scLocator=//IButton[ID=\"ideCreateProjectTemplateFormCreateButton\"]/";
   
   private static final String CANCEL_BUTTON = "scLocator=//IButton[ID=\"ideCreateProjectTemplateFormCancelButton\"]/";
   
   private static final String ADD_FOLDER_BUTTON_TITLE = "Add Folder";
   
   private static final String ADD_FILE_BUTTON_TITLE = "Add File";
   
   private static final String DELETE_BUTTON_TITLE = "Delete";
   
   private static final String CREATE_BUTTON_TITLE = "Create";
   
   private static final String CANCEL_BUTTON_TITLE = "Cancel";
   
   private static final String ROOT_NODE_NAME = "/";
   
   private String myFolder = "myFolder";
   
   private String newProjectName = "SampleProject";
   
   private String gadgetFileName = "SampleGadget.xml";
   
   @After
   public void tearDown()
   {
      cleanRegistry();
   }
   
   @Test
   public void testCreateProjectTemplate() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //=================== Check adding folder ====================
      //----- 1 ----------------
      //open Create Project Template Form
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_TEMPLATE);
      checkCreateProjectTemplateForm();
      checkTreeNodeSelected(ROOT_NODE_NAME);
      
      //----- 2 ----------------
      //add folder
      addFolder(myFolder);
      //check folder added
      checkTreeNodeSelected(myFolder);
      checkButtonEnabled(ADD_FILE_BUTTON_TITLE);
      checkButtonEnabled(ADD_FOLDER_BUTTON_TITLE);
      checkButtonEnabled(DELETE_BUTTON_TITLE);
      
      //----- 3 ----------------
      //select root of tree
      selectRootNode();
      
      //try to add folder with the same name
      addFolder(myFolder);
      
      //error dialog appears
      assertEquals("Error", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
      //close
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      
      //=================== Check adding file ====================
      //----- 4 ----------------
      //add file
      addFile(3, gadgetFileName);
      //check new file selected
      checkTreeNodeSelected(gadgetFileName + "(from Google Gadget)");
      //check buttons
      checkButtonDisabled(ADD_FOLDER_BUTTON_TITLE);
      checkButtonDisabled(ADD_FILE_BUTTON_TITLE);
      checkButtonEnabled(DELETE_BUTTON_TITLE);
      
      //----- 5 ----------------
      //select root node
      selectRootNode();
      
      //----- 6 ----------------
      //try to add file with existing name
      addFile(3, gadgetFileName);
      
      //error dialog appears
      assertEquals("Error", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
      //close
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      
      //=================== Change project name ====================
      //----- 7 ----------------
      //type new project name to name field
      selenium.type(NAME_FIELD, newProjectName);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      
      //----- 8 ----------------
      //type description to description field
      final String description = "Sample project for test";
      selenium.type(DESCRIPTION_FIELD, description);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      
      //=================== Create project template ====================
      //----- 9 ----------------
      //click Create button
      selenium.click(CREATE_BUTTON);
      Thread.sleep(TestConstants.SLEEP);
      
      //check info dialog appears
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertEquals("Info", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header"));
      assertEquals("Template created successfully!", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/blurb/"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      
      //check template form disappears
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateProjectTemplateForm\"]/"));
      
      //----- 10 ----------------
      //check cancel button
      Thread.sleep(TestConstants.SLEEP);
      //call create project template form
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_TEMPLATE);
      checkCreateProjectTemplateForm();
      
      //click cancel button
      selenium.click(CANCEL_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check template form disappears
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateProjectTemplateForm\"]/"));
      
      //=================== Create project template with existing name ====================
      Thread.sleep(TestConstants.SLEEP);
      //----- 11 ----------------
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_TEMPLATE);
      checkCreateProjectTemplateForm();
      
      //type new project name to name field
      selenium.type(NAME_FIELD, newProjectName);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      
      //click create button
      selenium.click(CREATE_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check warn dialog appears
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertEquals("Error", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
      //click ok button and close
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check template form
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateProjectTemplateForm\"]/"));
      
      //----- 12 ----------------
      //close
      //click cancel button
      selenium.click(CANCEL_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check template form disappears
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateProjectTemplateForm\"]/"));
      
   }
   
   @Test
   public void testEnablingDisablingButtons() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 1 ----------------
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_TEMPLATE);
      checkButtonDisabled(CREATE_BUTTON_TITLE);
      
      //----- 2 ----------------
      //type text to name field
      selenium.type(NAME_FIELD, "a");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkButtonEnabled(CREATE_BUTTON_TITLE);
      
      //----- 3 ----------------
      //remove text
      selenium.type(NAME_FIELD, "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkButtonDisabled(CREATE_BUTTON_TITLE);
      
      //----- 3 ----------------
      //close
      //click cancel button
      selenium.click(CANCEL_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check template form disappears
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateProjectTemplateForm\"]/"));
   }
   
   private void addFile(int fileListGridLineNumber, String fileName) throws Exception
   {
    //click add file button
      selenium.click(ADD_FILE_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //add file form appeared
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      //select file template
      selenium.click("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[" 
         + String.valueOf(fileListGridLineNumber) + "]/col[fieldName=name||1]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //type file name to name field
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element", 
         fileName);
      //click ok button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

   }
   
   private void addFolder(String folderName) throws Exception
   {
      selenium.click(ADD_FOLDER_BUTTON);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkAddFolderForm();
      //set folder's name
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element", 
         folderName);
      //click ok button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   private void selectRootNode() throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideProjectTemplateTreeGrid\"]/body/row[0]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   private void checkAddFolderForm()
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]/"));
      assertEquals("Add folder", selenium.getText("scLocator=//Window[ID=\"ideCreateFolderForm\"]/header"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]/"));
   }
   
   private void checkButtonEnabled(String buttonText)
   {
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideCreateProjectTemplateForm']//td[@class='buttonTitle' and text()='"
         + buttonText + "']"));
   }
   
   private void checkButtonDisabled(String buttonText)
   {
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideCreateProjectTemplateForm']//td[@class='buttonTitleDisabled' and text()='"
         + buttonText + "']"));
   }
   
   private void checkTreeNodeSelected(String name)
   {
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideCreateProjectTemplateForm']//td[@class='treeCellSelected']/nobr[text()='" 
         + name + "']"));
   }
   
   private void checkCreateProjectTemplateForm()
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateProjectTemplateForm\"]/"));
      assertEquals("Create project template", selenium.getText("scLocator=//Window[ID=\"ideCreateProjectTemplateForm\"]/header"));
      assertTrue(selenium.isElementPresent(NAME_FIELD));
      assertTrue(selenium.isElementPresent(DESCRIPTION_FIELD));
      assertTrue(selenium.isElementPresent(TREE_GRID));
      assertTrue(selenium.isElementPresent(ADD_FOLDER_BUTTON));
      checkButtonEnabled(ADD_FOLDER_BUTTON_TITLE);
      assertTrue(selenium.isElementPresent(ADD_FILE_BUTTON));
      checkButtonEnabled(ADD_FILE_BUTTON_TITLE);
      assertTrue(selenium.isElementPresent(DELETE_BUTTON));
      checkButtonDisabled(DELETE_BUTTON_TITLE);
      assertTrue(selenium.isElementPresent(CREATE_BUTTON));
      checkButtonDisabled(CREATE_BUTTON_TITLE);
      assertTrue(selenium.isElementPresent(CANCEL_BUTTON));
      checkButtonEnabled(CANCEL_BUTTON_TITLE);
      
      //check first element selected
      checkTreeNodeSelected(ROOT_NODE_NAME);
   }

}
