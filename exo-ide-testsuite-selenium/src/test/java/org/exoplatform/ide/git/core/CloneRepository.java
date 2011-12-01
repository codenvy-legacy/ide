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
package org.exoplatform.ide.git.core;

import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 23, 2011 2:32:10 PM anya $
 *
 */
public class CloneRepository extends AbstractTestModule
{
   private static interface Locators
   {
      String VIEW_ID = "ideCloneRepositoryView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String CLONE_BUTTON_ID = "ideCloneRepositoryViewCloneButton";

      String CANCEL_BUTTON_ID = "ideCloneRepositoryViewCancelButton";

      String WORKDIR_FIELD_ID = "ideCloneRepositoryViewWorkDirField";

      String REMOTE_URI_FIELD_ID = "ideCloneRepositoryViewRemoteUriField";

      String REMOTE_NAME_FIELD_ID = "ideCloneRepositoryViewRemoteNameField";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.CLONE_BUTTON_ID)
   private WebElement cloneButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.WORKDIR_FIELD_ID)
   private WebElement workdirField;

   @FindBy(name = Locators.REMOTE_URI_FIELD_ID)
   private WebElement remoteUriField;

   @FindBy(name = Locators.REMOTE_NAME_FIELD_ID)
   private WebElement remoteNameField;

   /**
    * Waits for Clone Git Repository view to be opened.
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
               return isOpened();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Waits for Clone Git Repository view to be closed.
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
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isOpened()
   {
      return (view != null && view.isDisplayed() && workdirField != null && workdirField.isDisplayed()
         && remoteNameField != null && remoteNameField.isDisplayed() && remoteUriField != null
         && remoteUriField.isDisplayed() && cloneButton != null && cloneButton.isDisplayed() && cancelButton != null && cancelButton
         .isDisplayed());
   }

   /**
    * Click Clone button.
    */
   public void clickCloneButton()
   {
      cloneButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Check Clone button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCloneButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cloneButton);
   }

   /**
    * Check Cancel button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCancelButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }

   /**
    * Get work directory field's value.
    * 
    * @return {@link String} fields value
    */
   public String getWorkDirectoryValue()
   {
      return IDE().INPUT.getValue(workdirField);
   }

   /**
    * Get remote uri field's value.
    * 
    * @return {@link String} fields value
    */
   public String getRemoteUriFieldValue()
   {
      return IDE().INPUT.getValue(remoteUriField);
   }

   /**
    * Get remote name field's value.
    * 
    * @return {@link String} fields value
    */
   public String getRemoteNameFieldValue()
   {
      return IDE().INPUT.getValue(remoteNameField);
   }

   /**
    * Type text to remote URI field.
    * 
    * @param text text to type
    * @throws InterruptedException 
    */
   public void setRemoteUri(String text) throws InterruptedException
   {
      IDE().INPUT.typeToElement(remoteUriField, text, true);
   }

   /**
    * Type text to remote name field.
    * 
    * @param text text to type
    * @throws InterruptedException 
    */
   public void setRemoteName(String text) throws InterruptedException
   {
      IDE().INPUT.typeToElement(remoteNameField, text, true);
   }
}
