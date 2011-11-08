/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.TestConstants;

/**
 * Operations with "Create project template" form.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateProjectTemplate.java May 13, 2011 4:11:48 PM vereshchaka $
 *
 */
public class CreateProjectTemplate extends AbstractTestModule
{
   private static final String PROJECT_TEMPLATE_FORM_ID = "ideCreateProjectTemplateForm";
   
   private static final String NAME_FIELD_NAME = "ideCreateProjectTemplateFormNameField";
   
   private static final String DESCRIPTION_FIELD_NAME = "ideCreateProjectTemplateFormDescriptionField";
   
   public static final String ADD_FOLDER_BUTTON_ID = "ideCreateProjectTemplateFormAddFolderButton";
   
   public static final String ADD_FILE_BUTTON_ID = "ideCreateProjectTemplateFormAddFileButton";
   
   public static final String DELETE_BUTTON_ID = "ideCreateProjectTemplateFormDeleteButton";
   
   public static final String CREATE_BUTTON_ID = "ideCreateProjectTemplateFormCreateButton";
   
   public static final String CANCEL_BUTTON_ID = "ideCreateProjectTemplateFormCancelButton";
   
   /**
    * Wait while Create project template form appears.
    * @throws Exception
    */
   public void waitForDialog() throws Exception
   {
      waitForElementPresent(PROJECT_TEMPLATE_FORM_ID);
   }
   
   /**
    * Check, that form Create project template contains all needed buttons
    * and has right caption.
    */
   public void checkCreateProjectTemplateDialog()
   {
      assertTrue(selenium().isElementPresent(PROJECT_TEMPLATE_FORM_ID));
      assertTrue(selenium().isElementPresent(
         "//div[@id='" + PROJECT_TEMPLATE_FORM_ID + "']//span[text()='Create project template']"));
      assertTrue(selenium().isElementPresent(NAME_FIELD_NAME));
      assertTrue(selenium().isElementPresent(DESCRIPTION_FIELD_NAME));
      assertTrue(selenium().isElementPresent(ADD_FOLDER_BUTTON_ID));
      assertTrue(selenium().isElementPresent(ADD_FILE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(DELETE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CREATE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
      
      checkButtonState(ADD_FILE_BUTTON_ID, true);
      checkButtonState(ADD_FOLDER_BUTTON_ID, true);
      checkButtonState(DELETE_BUTTON_ID, false);
      checkButtonState(CREATE_BUTTON_ID, false);
      checkButtonState(CANCEL_BUTTON_ID, true);
   }
   
   /**
    * Check the state of button (enabled, disabled) by button id.
    * 
    * @param buttonId - the id of button
    * @param isEnabled - is enabled
    */
   public void checkButtonState(String buttonId, boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent("//div[@id='" + buttonId + "' and @button-enabled='" + String.valueOf(isEnabled) + "']"));
   }
   
   /**
    * Click Add folder button.
    * @throws Exception
    */
   public void clickAddFolderButton() throws Exception
   {
      selenium().click(ADD_FOLDER_BUTTON_ID);
      IDE().FOLDER.waitOpened();
   }
   
   /**
    * Click Add file button.
    * @throws Exception
    */
   public void clickAddFileButton() throws Exception
   {
      selenium().click(ADD_FILE_BUTTON_ID);
      IDE().TEMPLATES.waitForFileAddFromTemplateForm();
   }
   
   /**
    * Click create button.
    * @throws Exception
    */
   public void clickCreateButton() throws Exception
   {
      selenium().click(CREATE_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * Click the cancel button and wait while Create project template form dissapears.
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      selenium().click(CANCEL_BUTTON_ID);
      
      waitForElementNotPresent(PROJECT_TEMPLATE_FORM_ID);
   }
   
   /**
    * Select item in project structure tree.
    * 
    * @param name - the name of item
    * @throws InterruptedException
    */
   public void selectItemInTree(String name) throws InterruptedException
   {
      selenium().clickAt("//div[@class='ide-Tree-label' and text()='" + name + "']", "1,1");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   /**
    * Select the root node of project structure tree.
    * @throws InterruptedException
    */
   public void selectRootNode() throws InterruptedException
   {
      selectItemInTree("/");
   }
   
   /**
    * Type new name to input name field.
    * 
    * @param name - the new name of item.
    * @throws InterruptedException
    */
   public void typeNameToInputField(String name) throws InterruptedException
   {
      selenium().type(NAME_FIELD_NAME, name);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * Type the description of project template to input field.
    * 
    * @param description - the description
    * @throws InterruptedException
    */
   public void typeDescriptionToInputField(String description) throws InterruptedException
   {
      selenium().type(DESCRIPTION_FIELD_NAME, description);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * Wait while dialog dissapeared.
    * @throws Exception
    */
   public void waitForDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(PROJECT_TEMPLATE_FORM_ID);
   }
   
   /**
    * Is dialog opened.
    */
   public void checkDialogOpened()
   {
      assertTrue(selenium().isElementPresent(PROJECT_TEMPLATE_FORM_ID));
   }

}
