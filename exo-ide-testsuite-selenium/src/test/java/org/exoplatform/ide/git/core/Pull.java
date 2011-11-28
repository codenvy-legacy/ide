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
 * @version $Id:  Jul 1, 2011 3:23:05 PM anya $
 *
 */
public class Pull extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "idePullView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String PULL_BUTTON_ID = "idePullViewPullButton";

      String CANCEL_BUTTON_ID = "idePullViewCancelButton";

      String REMOTE_FIELD_ID = "idePullViewRemoteField";

      String REMOTE_BRANCHES_FIELD_ID = "idePullViewRemoteBranchesField";

      String LOCAL_BRANCHES_FIELD_ID = "idePullViewLocalBranchesField";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.PULL_BUTTON_ID)
   private WebElement pullButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.REMOTE_FIELD_ID)
   private WebElement remoteField;

   @FindBy(name = Locators.REMOTE_BRANCHES_FIELD_ID)
   private WebElement remoteBranchesField;

   @FindBy(name = Locators.LOCAL_BRANCHES_FIELD_ID)
   private WebElement localBranchesField;

   /**
    * Waits for Pull view to be opened.
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
    * Waits for Pull view to be closed.
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
      return (view != null && view.isDisplayed() && remoteBranchesField != null && remoteBranchesField.isDisplayed()
         && localBranchesField != null && localBranchesField.isDisplayed() && remoteField != null
         && remoteField.isDisplayed() && pullButton != null && pullButton.isDisplayed() && cancelButton != null && cancelButton
         .isDisplayed());
   }

   /**
    * Click Pull button.
    */
   public void clickPullButton()
   {
      pullButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Check Pull button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isPullButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(pullButton);
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
   public void typeToRemoteBranch(String text) throws InterruptedException
   {
      IDE().INPUT.setComboboxValue(remoteBranchesField, text);
   }

   /**
    * Type pointed text to local branch field.
    * 
    * @return {@link String} text to type
    * @throws InterruptedException 
    */
   public void typeToLocalBranch(String text) throws InterruptedException
   {
      IDE().INPUT.setComboboxValue(localBranchesField, text);
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
