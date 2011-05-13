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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.core.CreateProjectTemplate;
import org.junit.After;
import org.junit.Test;

/**
 * Test, that checks "Create project template" form:
 * create new project template and the behavior of form.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplateTest extends BaseTest
{
   
   private String myFolder = CreateProjectTemplateTest.class.getSimpleName();
   
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
      IDE.NAVIGATION.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      //=================== Check adding folder ====================
      //----- 1 ----------------
      //open Create Project Template Form
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_TEMPLATE);
      IDE.PROJECT_TEMPLATE.waitForDialog();
      IDE.PROJECT_TEMPLATE.checkCreateProjectTemplateDialog();
//      checkTreeNodeSelected(ROOT_NODE_NAME);
      
      //----- 2 ----------------
      //add folder
      addFolder(myFolder);
      //check folder added
//      checkTreeNodeSelected(myFolder);
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.ADD_FILE_BUTTON_ID, true);
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.ADD_FOLDER_BUTTON_ID, true);
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.DELETE_BUTTON_ID, true);
      
      //----- 3 ----------------
      //select root of tree
      IDE.PROJECT_TEMPLATE.selectRootNode();
      
      //try to add folder with the same name
      addFolder(myFolder);
      
      //error dialog appears
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      IDE.WARNING_DIALOG.checkIsOpened("Folder with such name already exists");
      //close
      IDE.WARNING_DIALOG.clickOk();
      
      //=================== Check adding file ====================
      //----- 4 ----------------
      //add file
      addFile("Google Gadget", gadgetFileName);
      //check new file selected
//      checkTreeNodeSelected(gadgetFileName + "(from Google Gadget)");
      //check buttons
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.ADD_FILE_BUTTON_ID, false);
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.ADD_FOLDER_BUTTON_ID, false);
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.DELETE_BUTTON_ID, true);
      
      //----- 5 ----------------
      //select root node
      IDE.PROJECT_TEMPLATE.selectRootNode();
      
      //----- 6 ----------------
      //try to add file with existing name
      addFile("Google Gadget", gadgetFileName);
      
      //error dialog appears
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      //close
      IDE.WARNING_DIALOG.clickOk();
      
      //=================== Change project name ====================
      //----- 7 ----------------
      //type new project name to name field
      IDE.PROJECT_TEMPLATE.typeNameToInputField(newProjectName);
      
      //----- 8 ----------------
      //type description to description field
      final String description = "Sample project for test";
      IDE.PROJECT_TEMPLATE.typeDescriptionToInputField(description);
      
      //=================== Create project template ====================
      //----- 9 ----------------
      //click Create button
      IDE.PROJECT_TEMPLATE.clickCreateButton();
      IDE.PROJECT_TEMPLATE.waitForDialogNotPresent();
      
      //check info dialog appears
      IDE.INFORMATION_DIALOG.waitForInfoDialog("Template created successfully!");
      IDE.INFORMATION_DIALOG.clickOk();
      
      //----- 10 ----------------
      //check cancel button
      //call create project template form
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_TEMPLATE);
      IDE.PROJECT_TEMPLATE.waitForDialog();
      
      //click cancel button
      IDE.PROJECT_TEMPLATE.clickCancelButton();
      
      //=================== Create project template with existing name ====================
      //----- 11 ----------------
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_TEMPLATE);
      IDE.PROJECT_TEMPLATE.waitForDialog();
      
      //type new project name to name field
      IDE.PROJECT_TEMPLATE.typeNameToInputField(newProjectName);
      
      //click create button
      IDE.PROJECT_TEMPLATE.clickCreateButton();
      
      //check warn dialog appears
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      //click ok button and close
      IDE.WARNING_DIALOG.clickOk();
      
      //check template form
      IDE.PROJECT_TEMPLATE.checkDialogOpened();
      
      //----- 12 ----------------
      //close
      //click cancel button
      IDE.PROJECT_TEMPLATE.clickCancelButton();
   }
   
   @Test
   public void testEnablingDisablingButtons() throws Exception
   {
      refresh();
      IDE.NAVIGATION.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      
      //----- 1 ----------------
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_TEMPLATE);
      IDE.PROJECT_TEMPLATE.waitForDialog();
      IDE.PROJECT_TEMPLATE.checkCreateProjectTemplateDialog();
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.DELETE_BUTTON_ID, false);
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.CREATE_BUTTON_ID, false);
      
      //----- 2 ----------------
      //type text to name field
      IDE.PROJECT_TEMPLATE.typeNameToInputField("a");
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.CREATE_BUTTON_ID, true);
      
      //----- 3 ----------------
      //remove text
      IDE.PROJECT_TEMPLATE.typeNameToInputField("");
      IDE.PROJECT_TEMPLATE.checkButtonState(CreateProjectTemplate.CREATE_BUTTON_ID, false);
      
      //----- 3 ----------------
      //close
      //click cancel button
      IDE.PROJECT_TEMPLATE.clickCancelButton();
   }
   
   private void addFile(String templateName, String fileName) throws Exception
   {
      //click add file button
      IDE.PROJECT_TEMPLATE.clickAddFileButton();
      //add file form appeared
      IDE.TEMPLATES.checkCreateFileFromTemplateWindowComponents();
      //select file template
      IDE.TEMPLATES.selectFileTemplate(templateName);
      //type file name to name field
      IDE.TEMPLATES.typeNameToInputField(fileName);
      //click ok button
      IDE.TEMPLATES.clickCreateButton();

   }
   
   private void addFolder(String folderName) throws Exception
   {
      IDE.PROJECT_TEMPLATE.clickAddFolderButton();
      //set folder's name
      IDE.FOLDER.typeFolderName(folderName);
      //click ok button
      IDE.FOLDER.clickCreateButton();
   }
   
}
