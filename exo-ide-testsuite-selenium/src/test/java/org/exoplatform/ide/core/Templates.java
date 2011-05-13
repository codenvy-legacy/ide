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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Class for operations with templates: file and projects from templates.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Templates.java May 6, 2011 12:14:41 PM vereshchaka $
 *
 */
public class Templates extends AbstractTestModule
{
   //------Create file from template form elements-------------------
   private static final String FILE_FROM_TEMPLATE_FORM_ID = "ideCreateFileFromTemplateForm";

   private static final String TEMPLATES_LIST_GRID_ID = "ideCreateFileFromTemplateFormTemplateListGrid";
   
   private static final String INPUT_FIELD_NAME = "ideCreateFileFromTemplateFormFileNameField";

   public static final String DELETE_BUTTON_ID = "ideCreateFileFromTemplateFormDeleteButton";

   public static final String CREATE_BUTTON_ID = "ideCreateFileFromTemplateFormCreateButton";

   public static final String CANCEL_BUTTON_ID = "ideCreateFileFromTemplateFormCancelButton";

   public static final String FILE_NAME_INPUT_LOCATOR = "//input[@name='ideCreateFileFromTemplateFormFileNameField']";

   private static final String FILE_FROM_TEMPLATE_DIALOG_LOCATOR = "//div[@id='" + FILE_FROM_TEMPLATE_FORM_ID + "']";
   
   //------Create project from template form elements------------------
   public static final String DEFAULT_PROJECT_TEMPLATE_NAME = "ide-project";
   
   public static final String EMPTY_PROJECT_TEMPLATE_NAME = "new-project";
   
   private static final String PROJECT_CREATE_FORM_LOCATOR = "//div[@class='gwt-DialogBox']//div[@view-id='ideCreateProjectFromTemplateView']";
   
   public void waitForFileFromTemplateForm() throws Exception
   {
      waitForElementPresent(FILE_FROM_TEMPLATE_FORM_ID);
      waitForElementPresent(TEMPLATES_LIST_GRID_ID);
      waitForElementPresent(DELETE_BUTTON_ID);
      waitForElementPresent(CREATE_BUTTON_ID);
      waitForElementPresent(CANCEL_BUTTON_ID);
      waitForElementPresent(FILE_NAME_INPUT_LOCATOR);
   }
   
   /**
    * Wait, while Project From Template form appears.
    * @throws Exception
    */
   public void waitForProjectCreateForm() throws Exception
   {
      waitForElementPresent(PROJECT_CREATE_FORM_LOCATOR);
   }
   
   /**
    * Select template in list grid.
    * 
    * @param templateName - the name of template
    * @throws InterruptedException
    */
   public void selectFileTemplate(String templateName) throws InterruptedException
   {
      selenium().click(FILE_FROM_TEMPLATE_DIALOG_LOCATOR + "//span[@title='" + templateName + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   /**
    * Select project template in list grid.
    * @param templateName
    * @throws InterruptedException
    */
   public void selectProjectTemplate(String templateName) throws InterruptedException
   {
      selenium().click(PROJECT_CREATE_FORM_LOCATOR + "//span[@title='" + templateName + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   /**
    * Type new name to input name field.
    * 
    * @param name - the new name of item.
    * @throws InterruptedException
    */
   public void typeNameToInputField(String name) throws InterruptedException
   {
      selenium().type(FILE_NAME_INPUT_LOCATOR, name);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * Click create button and wait until templates form disappeared.
    * 
    * @throws Exception
    */
   public void clickCreateButton() throws Exception
   {
      selenium().click(CREATE_BUTTON_ID);

      waitForElementNotPresent(FILE_FROM_TEMPLATE_FORM_ID);
   }
   
   public void clickDeleteButton() throws Exception
   {
      selenium().click(DELETE_BUTTON_ID);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
   }
   
   public void clickCancelButton() throws Exception
   {
      selenium().click(CANCEL_BUTTON_ID);

      waitForElementNotPresent(FILE_FROM_TEMPLATE_FORM_ID);
   }
   
   /**
    * Check, that form "Create file from template" appeared and displayed correctly.
    */
   public void checkCreateFileFromTemplateWindow()
   {
      assertTrue(selenium().isElementPresent(FILE_FROM_TEMPLATE_FORM_ID));
      assertEquals("Create file", selenium().getText("//div[@id='" + FILE_FROM_TEMPLATE_FORM_ID + "']//div[@class='Caption']/span"));
      assertTrue(selenium().isElementPresent(INPUT_FIELD_NAME));
      assertTrue(selenium().isElementPresent(TEMPLATES_LIST_GRID_ID));
      assertTrue(selenium().isElementPresent(DELETE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CREATE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
      //check that Delete and Create buttons are disabled and Cancel is enabled
      checkButtonState(DELETE_BUTTON_ID, false);
      checkButtonState(CREATE_BUTTON_ID, false);
      checkButtonState(CANCEL_BUTTON_ID, true);
      checkInputFieldState(false);
   }
   
   public void checkProjectCreateForm()
   {
      assertTrue(selenium().isElementPresent(PROJECT_CREATE_FORM_LOCATOR));
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
    * Check, is input field enabled or disabled.
    * 
    * @param isEnabled - is enabled
    */
   public void checkInputFieldState(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium().isElementPresent("//input[@name='" + INPUT_FIELD_NAME + "']"));
         assertFalse(selenium().isElementPresent("//input[@name='" + INPUT_FIELD_NAME + "' and @disabled='']"));
      }
      else
      {
         assertTrue(selenium().isElementPresent("//input[@name='" + INPUT_FIELD_NAME + "' and @disabled='']"));
      }
   }
   
   /**
    * Create project from template using "Create Project" (from template) form.
    * 1. Call "Project From Template" form
    * 2. Select <code>templateName</code> template in list grid
    * 3. Type project name <code>projectName</code> (if null - leave the default project name)
    * 4. Click Create button
    * 
    * @param templateName - the name of template.
    * @param projectName - the name of future project.
    * @throws Exception
    */
   public void createProjectFromTemplate(String templateName, String projectName) throws Exception
   {
      IDE().TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      waitForProjectCreateForm();
      checkProjectCreateForm();
      
      selectProjectTemplate(templateName);
      if (projectName != null)
      {
         typeNameToInputField(projectName);
      }
      clickCreateButton();
   }
   
   /**
    * Check, is template name present in list grid.
    * @param templateName
    * @param isPresent
    */
   public void checkTemplatePresent(String templateName, boolean isPresent)
   {
      final String locator = getTemplateRowLocatorByName(templateName);
      if (isPresent)
      {
         assertTrue(selenium().isElementPresent(locator));
      }
      else
      {
         assertFalse(selenium().isElementPresent(locator));
      }
   }
   
   /**
    * Wait, while template name dissapears from list grid.
    * 
    * @param templateName - the name of template
    * @throws Exception
    */
   public void waitForTemplateDeleted(String templateName) throws Exception
   {
      final String locator = getTemplateRowLocatorByName(templateName);
      waitForElementNotPresent(locator);
   }
   
   /**
    * Get the locator of row in list grid by template name.
    * 
    * @param templateName - the name of template.
    * @return
    */
   private String getTemplateRowLocatorByName(String templateName)
   {
      final String locator = "//table[@id='" + TEMPLATES_LIST_GRID_ID + "']//span[text()='" + templateName + "']";
      return locator;
   }
   
}
