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
import static org.junit.Assert.assertTrue;

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
   //------Create from template form elements-------------------
   private static final String FILE_FROM_TEMPLATE_FORM_ID = "ideCreateFileFromTemplateForm";

   private static final String TEMPLATES_LIST_GRID_ID = "ideCreateFileFromTemplateFormTemplateListGrid";
   
   private static final String FILE_NAME_INPUT_ID = "ideCreateFileFromTemplateFormFileNameField";

   private static final String DELETE_BUTTON_ID = "ideCreateFileFromTemplateFormDeleteButton";

   private static final String CREATE_BUTTON_ID = "ideCreateFileFromTemplateFormCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateFileFromTemplateFormCancelButton";

   private static final String FILE_NAME_INPUT_LOCATOR = "//input[@name='ideCreateFileFromTemplateFormFileNameField']";

   private static final String FILE_FROM_TEMPLATE_DIALOG_LOCATOR = "//div[@id='" + FILE_FROM_TEMPLATE_FORM_ID + "']";
   
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
    * Select template in list grid.
    * 
    * @param templateName - the name of template
    * @throws InterruptedException
    */
   public void selectTemplate(String templateName) throws InterruptedException
   {
      selenium().click(FILE_FROM_TEMPLATE_DIALOG_LOCATOR + "//span[@title='" + templateName + "']");
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
   
   /**
    * Check, that form "Create file from template" appeared and displayed correctly.
    */
   public void checkCreateFileFromTemplateWindow()
   {
      assertTrue(selenium().isElementPresent(FILE_FROM_TEMPLATE_FORM_ID));
      assertEquals("Create file", selenium().getText("//div[@id='" + FILE_FROM_TEMPLATE_FORM_ID + "']//div[@class='Caption']/span"));
      assertTrue(selenium().isElementPresent(FILE_NAME_INPUT_ID));
      assertTrue(selenium().isElementPresent(TEMPLATES_LIST_GRID_ID));
      assertTrue(selenium().isElementPresent(DELETE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CREATE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
      //check that Delete and Create buttons are disabled and Cancel is enabled
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
   
}
