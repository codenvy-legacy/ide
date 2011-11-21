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
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AskForValueDialog extends AbstractTestModule
{

   public interface Locator
   {
      String VIEW_LOCATOR = "//div[@view-id='ideAskForValueView']";

      String OK_BUTTON_ID = "ideAskForValueViewYesButton";

      String NO_BUTTON_ID = "ideAskForValueViewNoButton";

      String CANCEL_BUTTON_ID = "ideAskForValueViewCancelButton";

      String VALUE_FIELD_ID = "ideAskForValueViewValueField";
   }

   @FindBy(xpath = Locator.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locator.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(id = Locator.NO_BUTTON_ID)
   private WebElement noButton;

   @FindBy(id = Locator.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locator.VALUE_FIELD_ID)
   private WebElement valueField;

   /**
    * @return {@link Boolean}
    */
   public boolean isOpened()
   {
      try
      {
         return view != null && view.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Waits until AskForValue dialog will be opened.
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
            return view != null && view.isDisplayed();
         }
      });
   }

   /**
    * Waits until AskForValue dialog closes.
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
               input.findElement(By.xpath(Locator.VIEW_LOCATOR));
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
    * Clicks on "Ok" button.
    * 
    * @throws Exception
    */
   public void clickOkButton() throws Exception
   {
      okButton.click();
   }

   /**
    * Clicks on "No" button.
    * 
    * @throws Exception
    */
   public void clickNoButton() throws Exception
   {
      noButton.click();
   }

   /**
    * Determines whether the "No" button is visible.
    * 
    * @return
    */
   public boolean isNoButtonPresent()
   {
      return noButton != null && noButton.isDisplayed();
   }

   /**
    * Clicks on "Cancel" button.
    * 
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      cancelButton.click();
   }

   /**
    * Sets a new value of text field.
    * 
    * @param value
    * @throws Exception
    */
   public void setValue(String value) throws Exception
   {
      IDE().INPUT.typeToElement(valueField, value, true);
   }

}
