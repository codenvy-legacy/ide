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

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Class for operations with file templates.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Templates.java May 6, 2011 12:14:41 PM vereshchaka $
 *
 */
public class Templates extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideCreateFileFromTemplateForm";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String TEMPLATES_GRID_ID = "ideCreateFileFromTemplateFormTemplateListGrid";

      String NAME_FIELD_ID = "ideCreateFileFromTemplateFormNameField";

      String DELETE_BUTTON_ID = "ideCreateFileFromTemplateFormDeleteButton";

      String CREATE_BUTTON_ID = "ideCreateFileFromTemplateFormCreateButton";

      String CANCEL_BUTTON_ID = "ideCreateFileFromTemplateFormCancelButton";

      String TEMPLATE_SELECTOR = "table#" + TEMPLATES_GRID_ID + " span[title='%s']";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.TEMPLATES_GRID_ID)
   private WebElement templateGrid;

   @FindBy(id = Locators.CREATE_BUTTON_ID)
   private WebElement createButton;

   @FindBy(id = Locators.DELETE_BUTTON_ID)
   private WebElement deleteButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.NAME_FIELD_ID)
   private WebElement nameField;

   /**
    * Wait Create file from template view opened.
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
               return (view != null && view.isDisplayed() && templateGrid != null && templateGrid.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait Create file from template closed.
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
    * @return {@link Boolean} <code>true</code> if opened
    */
   public boolean isOpened()
   {
      try
      {
         return view != null && view.isDisplayed() && createButton != null && createButton.isDisplayed()
            && cancelButton != null && cancelButton.isDisplayed() && deleteButton != null && deleteButton.isDisplayed()
            && templateGrid != null && templateGrid.isDisplayed() && nameField != null && nameField.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Select template in grid by name.
    * 
    * @param templateName name of the template
    * @throws InterruptedException
    */
   public void selectTemplate(String templateName) throws InterruptedException
   {
      WebElement template =
         driver().findElement(By.cssSelector(String.format(Locators.TEMPLATE_SELECTOR, templateName)));
      template.click();
   }

   /**
    * Set file's name.
    * 
    * @param name file's name
    * @throws InterruptedException
    */
   public void setFileName(String name) throws InterruptedException
   {
      IDE().INPUT.typeToElement(nameField, name, true);
   }

   /**
    * Click Create button.
    * 
    * @throws Exception
    */
   public void clickCreateButton() throws Exception
   {
      createButton.click();
   }

   /**
    * Click Delete button.
    * 
    * @throws Exception
    */
   public void clickDeleteButton() throws Exception
   {
      deleteButton.click();
   }

   /**
    * Click Cancel button.
    * 
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      cancelButton.click();
   }

   /**
    * Returns the enabled state of the create button.
    * 
    * @return boolean enabled state
    */
   public boolean isCreateButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(createButton);
   }

   /**
    * Returns the enabled state of the delete button.
    * 
    * @return boolean enabled state
    */
   public boolean isDeleteButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(deleteButton);
   }

   /**
    * Returns the enabled state of the cancel button.
    * 
    * @return boolean enabled state
    */
   public boolean isCancelButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }

   /**
    * Creates new file from template.
    * 
    * @param templateName name of template
    * @param fileName name of created file
    * @throws Exception
    */
   public void createFileFromTemplate(String templateName, String fileName) throws Exception
   {
      IDE().TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      waitOpened();
      selectTemplate(templateName);
      setFileName(fileName);
      clickCreateButton();
      waitClosed();
   }

   /**
    * Returns whether template is present in grid.
    * 
    * @param templateName
    * @return boolean <code>true</code> if template is present in grid
    */
   public boolean isTemplatePresent(String templateName)
   {
      try
      {
         WebElement template =
            driver().findElement(By.cssSelector(String.format(Locators.TEMPLATE_SELECTOR, templateName)));
         return template != null && template.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Wait, while template name dissapears from list grid.
    * 
    * @param templateName - the name of template
    * @throws Exception
    */
   public void waitForTemplateDeleted(final String templateName) throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            return !isTemplatePresent(templateName);
         }
      });
   }
}
