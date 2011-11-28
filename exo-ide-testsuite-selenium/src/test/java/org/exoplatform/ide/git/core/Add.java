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
 * @version $Id:  Jun 23, 2011 6:00:40 PM anya $
 *
 */
public class Add extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideAddToIndexView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String ADD_BUTTON_ID = "ideAddToIndexViewAddButton";

      String CANCEL_BUTTON_ID = "ideAddToIndexViewCancelButton";

      String UPDATE_FIELD_ID = "ideAddToIndexViewUpdaterField";

      String MESSAGE_FIELD_ID = "ideAddToIndexViewMessageField";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.ADD_BUTTON_ID)
   private WebElement addButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.UPDATE_FIELD_ID)
   private WebElement updateField;

   @FindBy(id = Locators.MESSAGE_FIELD_ID)
   private WebElement messageField;

   public interface Messages
   {
      String ADD_FILE = "Add file %s to index.";

      String ADD_FOLDER = "Add content of folder %s to index.";

      String ADD_ALL_CHANGES = "Add all changes in repository to index.";
   }

   /**
    * Waits for Add to index view to be opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
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
    * Waits for Add to index view to be closed.
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
      return view != null && view.isDisplayed() && addButton != null && addButton.isDisplayed() && updateField != null
         && updateField.isDisplayed() && messageField != null && messageField.isDisplayed();
   }

   /**
    * Click Add button.
    */
   public void clickAddButton()
   {
      addButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Check Add button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isAddButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(addButton);
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
    * Get message about what is gonna to be added to index.
    * 
    * @return {@link String} message
    */
   public String getAddMessage()
   {
      return messageField.getText();
   }

   /**
    * Make the update field to be checked.
    */
   public void checkUpdateField()
   {
      updateField.click();
   }

   /**
    * Make the update field to be unchecked.
    */
   public void unCheckUpdateField()
   {
      updateField.click();
   }

   /**
    * Get the checked value of update field.
    * 
    * @return {@link Boolean}
    */
   public boolean isUpdateFieldChecked()
   {
      return updateField.isSelected();
   }

   /**
    * Perform actions for adding to index.
    * 
    * @throws Exception
    */
   public void addToIndex() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      waitOpened();
      clickAddButton();
      waitClosed();
   }
}
