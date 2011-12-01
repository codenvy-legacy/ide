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
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Rename extends AbstractTestModule
{
   private interface Locators
   {

      String VIEW_LOCATOR = "//div[@view-id='ideRenameItemForm']";

      String RENAME_BUTTON_ID = "ideRenameItemFormRenameButton";

      String CANCEL_BUTTON_ID = "ideRenameItemFormCancelButton";

      String NEW_NAME_ID = "ideRenameItemFormRenameField";

      String MIME_TYPE_ID = "ideRenameItemFormMimeTypeField";

      String WARNING_MESSAGE_CLASS_NAME = "exo-rename-warning-msg";

   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(name = Locators.NEW_NAME_ID)
   WebElement newNameField;

   @FindBy(name = Locators.MIME_TYPE_ID)
   WebElement mimeTypeField;

   @FindBy(id = Locators.RENAME_BUTTON_ID)
   WebElement renameButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelButton;

   @FindBy(className = Locators.WARNING_MESSAGE_CLASS_NAME)
   WebElement warningMessage;

   /**
    * Wait Rename item view opened.
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
    * Wait Rename item view closed.
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
    * Returns value of new name field.
    * 
    * @return {@link String} value of new name field
    */
   public String getNewName()
   {
      return IDE().INPUT.getValue(newNameField);
   }

   /**
    * Sets new item's name.
    * 
    * @param newName name to rename to
    * @throws InterruptedException 
    */
   public void setNewName(String newName) throws InterruptedException
   {
      IDE().INPUT.typeToElement(newNameField, newName, true);
   }

   /**
    * Returns value of mime type field.
    * 
    * @return {@link String} mime type field value
    */
   public String getMimeType()
   {
      return IDE().INPUT.getValue(mimeTypeField);
   }

   /**
    * Sets the new file's mime type.
    * 
    * @param mimeType mime type to change to
    * @throws InterruptedException 
    */
   public void setMimeType(String mimeType) throws InterruptedException
   {
      IDE().INPUT.setComboboxValue(mimeTypeField, mimeType);
   }

   /**
    * Clicks on Rename button.
    * 
    * @throws Exception
    */
   public void clickRenameButton() throws Exception
   {
      renameButton.click();
   }

   /**
    * Returns the enabled state of the rename button.
    * 
    * @return enabled state of the rename button
    */
   public boolean isRenameButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(renameButton);
   }

   /**
    * Clicks on Cancel button.
    * 
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      cancelButton.click();
   }

   /**
    * Performs rename of the item (only name changes).
    * 
    * @param newName new name
    * @throws Exception
    */
   public void rename(String newName) throws Exception
   {
      rename(newName, null);
   }

   /**
    * Perform renaming and/or changing the file's mime type.
    * 
    * @param newName new name of the item (may be <code>null</code> if only mime type is changed)
    * @param mimeType new file's mime type (may be <code>null</code> if only name is changed)
    * @throws Exception
    */
   public void rename(String newName, String mimeType) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      waitOpened();

      if (newName != null)
      {
         setNewName(newName);
      }

      if (mimeType != null)
      {
         setMimeType(mimeType);
      }
      clickRenameButton();
      waitClosed();
   }

   /**
    * @return {@link String} text of the warning message
    */
   public String getWarningMessage()
   {
      return warningMessage.getText();
   }

   /**
    * Returns enabled state of the mime type field.
    * 
    * @return enabled state of the mime type field
    */
   public boolean isMimeTypeFieldEnabled()
   {
      return mimeTypeField.isEnabled();
   }
}
