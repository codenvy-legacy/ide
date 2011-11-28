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
 * @version $Id:  Jun 30, 2011 12:25:27 PM anya $
 *
 */
public class Push extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "idePushToRemoteView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String PUSH_BUTTON_ID = "idePushToRemoteViewPushButton";

      String CANCEL_BUTTON_ID = "idePushToRemoteViewCancelButton";

      String REMOTE_FIELD_ID = "idePushToRemoteViewRemoteField";

      String LOCAL_BRANCHES_FIELD_ID = "idePushToRemoteViewLocalBranchesField";

      String REMOTE_BRANCHES_FIELD_ID = "idePushToRemoteViewRemoteBranchesField";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.PUSH_BUTTON_ID)
   private WebElement pushButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.REMOTE_FIELD_ID)
   private WebElement remoteField;

   @FindBy(name = Locators.LOCAL_BRANCHES_FIELD_ID)
   private WebElement localBranchesField;

   @FindBy(name = Locators.REMOTE_BRANCHES_FIELD_ID)
   private WebElement remoteBranchesField;

   /**
    * Waits for Push view to be opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
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
    * Waits for Push view to be closed.
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
      return (view != null && view.isDisplayed() && pushButton != null && pushButton.isDisplayed()
         && cancelButton != null && cancelButton.isDisplayed() && remoteBranchesField != null
         && remoteBranchesField.isDisplayed() && localBranchesField != null && localBranchesField.isDisplayed()
         && remoteField != null && remoteField.isDisplayed());
   }

   /**
    * Click Push button.
    */
   public void clickPushButton()
   {
      pushButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Check Push button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isPushButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(pushButton);
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
    * Get displayed value of remote repository field.
    * 
    * @return {@link String} displayed value
    */
   public String getRemoteRepositoryValue()
   {
      return remoteField.getText();
   }

   /**
    * Get displayed value of remote branch field.
    * 
    * @return {@link String} displayed value
    */
   public String getRemoteBranchValue()
   {
      return remoteBranchesField.getText();
   }

   /**
    * Type pointed text to remote branch field.
    * 
    * @return {@link String} text to type
    * @throws InterruptedException 
    */
   public void setRemoteBranch(String text) throws InterruptedException
   {
      IDE().INPUT.setComboboxValue(remoteBranchesField, text);
   }

   /**
    * Get displayed value of local branch field.
    * 
    * @return {@link String} displayed value
    */
   public String getLocalBranchValue()
   {
      return localBranchesField.getText();
   }
}
