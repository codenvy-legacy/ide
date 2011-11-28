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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Class for operations with templates: file and projects from templates.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Templates.java May 6, 2011 12:14:41 PM vereshchaka $
 *
 */
public class SaveAsTemplate extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideSaveAsTemplateForm";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String CANCEL_BUTTON_ID = "ideSaveAsTemplateFormCancelButton";

      String SAVE_BUTTON_ID = "ideSaveAsTemplateFormSaveButton";

      String TYPE_FIELD_ID = "ideSaveAsTemplateFormTypeField";

      String NAME_FIELD_ID = "ideSaveAsTemplateFormNameField";

      String DESCRIPTION_FIELD_ID = "ideSaveAsTemplateFormDescriptionField";

   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.SAVE_BUTTON_ID)
   private WebElement saveButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.NAME_FIELD_ID)
   private WebElement nameField;

   @FindBy(name = Locators.DESCRIPTION_FIELD_ID)
   private WebElement descriptionField;

   @FindBy(name = Locators.TYPE_FIELD_ID)
   private WebElement typeField;

   /**
    * Wait Save as template view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement view = input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return (view != null && view.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait Save as template view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Returns opened state of the dialog.
    * 
    * @return {@link Boolean} opened  
    */
   public boolean isOpened()
   {
      try
      {
         return view != null && view.isDisplayed() && saveButton != null && saveButton.isDisplayed()
            && cancelButton != null && cancelButton.isDisplayed() && nameField != null && nameField.isDisplayed()
            && typeField != null && typeField.isDisplayed() && descriptionField != null
            && descriptionField.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Returns the enabled state of the save button.
    * 
    * @return {@link Boolean} <code>true<code> if enabled
    */
   public boolean isSaveButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(saveButton);
   }

   /**
    * Set name of the template.
    * 
    * @param name template's name
    * @throws InterruptedException
    */
   public void setName(String name) throws InterruptedException
   {
      IDE().INPUT.typeToElement(nameField, name, true);
   }

   /**
    * Set type of the template.
    * 
    * @param type - the type
    * @throws InterruptedException
    */
   public void setType(String type) throws InterruptedException
   {
      IDE().INPUT.typeToElement(typeField, type, true);
   }

   /**
    * Set description of the template.
    * 
    * @param description - the description
    * @throws InterruptedException
    */
   public void setDescription(String description) throws InterruptedException
   {
      IDE().INPUT.typeToElement(descriptionField, description, true);
   }

   /**
    * Click save button.
    * 
    * @throws Exception
    */
   public void clickSaveButton() throws Exception
   {
      saveButton.click();
   }

   /**
    * Click cancel button.
    * 
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      cancelButton.click();
   }

}
