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
 * @version $Id:  Jun 23, 2011 10:41:06 AM anya $
 *
 */
public class InitRepository extends AbstractTestModule
{
   private static interface Locators
   {
      String VIEW_ID = "ideInitRepositoryView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String INIT_BUTTON_ID = "ideInitRepositoryViewInitButton";

      String CANCEL_BUTTON_ID = "ideInitRepositoryViewCancelButton";

      String WORKDIR_FIELD_ID = "ideInitRepositoryViewWorkDirField";

      String BARE_FIELD_ID = "ideInitRepositoryViewBareField";
   }

   public static interface Titles
   {
      String VIEW_TITLE = "Initialize local repository";

      String INIT_BUTTON = "Ok";

      String CANCEL_BUTTON = "Cancel";

      String WORKDIR_FIELD = "Work directory";

      String BARE_FIELD = "Bare repository";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.INIT_BUTTON_ID)
   private WebElement initButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.WORKDIR_FIELD_ID)
   private WebElement workdirField;

   @FindBy(name = Locators.BARE_FIELD_ID)
   private WebElement bareField;

   /**
    * Waits for Init Git Repository view to be opened.
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
    * Waits for Init Git Repository view to be closed.
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
      return (view != null && view.isDisplayed() && cancelButton != null && cancelButton.isDisplayed()
         && bareField != null && bareField.isDisplayed() && workdirField != null && workdirField.isDisplayed()
         && initButton != null && initButton.isDisplayed());
   }

   /**
    * Click Init button.
    */
   public void clickInitButton()
   {
      initButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Check the field bare repository.
    */
   public void checkBareRepositoryField()
   {
      bareField.click();
   }

   /**
    * Check Init button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isInitButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(initButton);
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
    * Get Init button's title.
    * 
    * @return {@link String} button's title
    */
   public String getInitButtonTitle()
   {
      return initButton.getText();
   }

   /**
    * Get Cancel button's title.
    * 
    * @return {@link String} button's title
    */
   public String getCancelButtonTitle()
   {
      return cancelButton.getText();
   }

   /**
    * Perform init Git repository.
    * 
    * @throws Exception
    */
   public void initRepository() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      waitOpened();
      clickInitButton();
      waitClosed();
   }
}
