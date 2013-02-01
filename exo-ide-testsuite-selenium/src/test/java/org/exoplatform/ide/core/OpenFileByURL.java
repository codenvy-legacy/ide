/*
 * Copyright (C) 2012 eXo Platform SAS.
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
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jan 26, 2012 12:34:22 PM anya $
 * 
 */
public class OpenFileByURL extends AbstractTestModule
{
   private final class Locators
   {
      public static final String VIEW_ID = "ide.openFileByURL.view";

      public static final String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      public static final String OPEN_BUTTON_ID = "ide.openFileByURL.openButton";

      public static final String CANCEL_BUTTON_ID = "ide.openFileByURL.cancelButton";

      public static final String URL_FIELD_ID = "ide.openFileByURL.view.URL";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(name = Locators.URL_FIELD_ID)
   WebElement urlField;

   @FindBy(id = Locators.OPEN_BUTTON_ID)
   WebElement openButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelButton;

   /**
    * Waits for Get URL view to be opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
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
    * Waits for Get URL view to be closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
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
      return (view != null && view.isDisplayed() && openButton != null && openButton.isDisplayed() && urlField != null
         && urlField.isDisplayed() && cancelButton != null && cancelButton.isDisplayed());
   }

   /**
    * Click Open button.
    */
   public void clickOpenButton()
   {
      openButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Set the URL value.
    * 
    * @param value
    * @throws InterruptedException
    */
   public void setURL(String value) throws InterruptedException
   {
      IDE().INPUT.typeToElement(urlField, value, true);
   }

   /**
    * Get enabled state of the open button.
    * 
    * @return {@link Boolean} enabled state of the Open button
    */
   public boolean isOpenButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(openButton);
   }

   /**
    * Get enabled state of the Cancel button.
    * 
    * @return {@link Boolean} enabled state of the Cancel button
    */
   public boolean isCancelButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }
}
