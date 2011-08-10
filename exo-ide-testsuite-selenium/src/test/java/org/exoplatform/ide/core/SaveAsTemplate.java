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
 * Class for operations with templates: file and projects from templates.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Templates.java May 6, 2011 12:14:41 PM vereshchaka $
 *
 */
public class SaveAsTemplate extends AbstractTestModule
{
   //------Save as template form elements----------------------
   public static final String SAVE_AS_TEMPLATE_FORM_LOCATOR = "//div[@view-id='ideSaveAsTemplateForm']";
   
   public static final String CANCEL_BUTTON_ID = "ideSaveAsTemplateFormCancelButton";
   
   public static final String SAVE_BUTTON_ID = "ideSaveAsTemplateFormSaveButton";
   
   public static final String TYPE_FIELD_ID = "ideSaveAsTemplateFormTypeField";
   
   public static final String NAME_FIELD_ID = "ideSaveAsTemplateFormNameField";
   
   public static final String DESCRIPTION_FIELD_ID = "ideSaveAsTemplateFormDescriptionField";
   
   public void checkSaveAsTemplateWindow()
   {
      assertTrue(selenium().isElementPresent(SAVE_AS_TEMPLATE_FORM_LOCATOR));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
      assertTrue(selenium().isElementPresent(SAVE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(TYPE_FIELD_ID));
      assertTrue(selenium().isElementPresent(NAME_FIELD_ID));
      assertTrue(selenium().isElementPresent(DESCRIPTION_FIELD_ID));
      assertTrue(selenium().isTextPresent("Type:"));
      assertTrue(selenium().isTextPresent("Name:"));
      assertTrue(selenium().isTextPresent("Description:"));
   }
   
   public void waitForDialog() throws Exception
   {
      waitForElementPresent(SAVE_AS_TEMPLATE_FORM_LOCATOR);
   }
   
   /**
    * Check is button enabled or disabled.
    * 
    * @param buttonId - the id of button
    * @param isEnabled - is button enabled
    */
   public void checkButtonState(String buttonId, boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent(
         "//div[@id='" + buttonId + "' and @button-enabled='" + String.valueOf(isEnabled) + "']"));
   }
   
   /**
    * Type new name to input name field.
    * 
    * @param name - the new name of item.
    * @throws InterruptedException
    */
   public void typeNameToInputField(String name) throws InterruptedException
   {
      selenium().type(NAME_FIELD_ID, name);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * Enter the type of template
    * @param type - the type
    * @throws InterruptedException
    */
   public void typeFileTypeToInputField(String type) throws InterruptedException
   {
      selenium().type(TYPE_FIELD_ID, type);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * Enter the description for new template.
    * @param description - the description
    * @throws InterruptedException
    */
   public void typeDescriptionToInputField(String description) throws InterruptedException
   {
      selenium().type(DESCRIPTION_FIELD_ID, description);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   public void clickSaveButton() throws Exception
   {
      selenium().click(SAVE_BUTTON_ID);

      waitForElementNotPresent(SAVE_AS_TEMPLATE_FORM_LOCATOR);
   }

}
