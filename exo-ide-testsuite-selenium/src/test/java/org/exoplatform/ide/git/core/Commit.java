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

import org.exoplatform.ide.MenuCommands;
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
 * @version $Id:  Jun 27, 2011 11:23:17 AM anya $
 *
 */
public class Commit extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideCommitView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String COMMIT_BUTTON_ID = "ideCommitViewCommitButton";

      String CANCEL_BUTTON_ID = "ideCommitViewCancelButton";

      String MESSAGE_FIELD_ID = "ideCommitViewMessageField";

      String ALL_FIELD_ID = "ideCommitViewAllField";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.COMMIT_BUTTON_ID)
   private WebElement commitButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.MESSAGE_FIELD_ID)
   private WebElement messageField;

   @FindBy(name = Locators.ALL_FIELD_ID)
   private WebElement allField;

   /**
    * Waits for Commit view to be opened.
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
    * Waits for Commit view to be closed.
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
      return (view != null && view.isDisplayed() && commitButton != null && commitButton.isDisplayed()
         && cancelButton != null && cancelButton.isDisplayed() && messageField != null && messageField.isDisplayed()
         && allField != null && allField.isDisplayed());
   }

   /**
    * Click Commit button.
    */
   public void clickCommitButton()
   {
      commitButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Check Commit button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCommitButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(commitButton);
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
    * Make the add field to be checked.
    */
   public void checkAddField()
   {
      allField.click();
   }

   /**
    * Returns the checked state of add field. 
    * 
    * @return {@link Boolean} if <code>true</code> - checked
    */
   public boolean isAddFieldChecked()
   {
      return allField.isSelected();
   }

   /**
    * Type message to message field.
    * 
    * @param message
    * @throws InterruptedException 
    */
   public void typeToMessageField(String message) throws InterruptedException
   {
      IDE().INPUT.typeToElement(messageField, message, true);
   }

   /**
    * Perform actions to commit changes.
    * 
    * @param message commit message
    * @throws Exception
    */
   public void commit(String message) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      waitOpened();
      typeToMessageField(message);
      clickCommitButton();
      waitClosed();
   }
}
