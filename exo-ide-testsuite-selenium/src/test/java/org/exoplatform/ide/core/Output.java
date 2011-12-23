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
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Output May 11, 2011 5:05:08 PM evgen $
 *
 */
public class Output extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideOutputView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String CLEAR_BUTTON_SELECTOR = "div[view-id='" + VIEW_ID + "'] div[title='Clear output']>img";

      String OUTPUT_CONTENT_ID = "ideOutputContent";

      String OUTPUT_ROW_BY_INDEX = "//div[@id='" + OUTPUT_CONTENT_ID + "']/div[%d]";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(css = Locators.CLEAR_BUTTON_SELECTOR)
   private WebElement clearButton;

   @FindBy(id = Locators.OUTPUT_CONTENT_ID)
   private WebElement outputContent;

   /**
    * Wait Output view opened.
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
    * Wait Output view closed.
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
               return view == null || !view.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Get Output message by its index.
    * 
    * @param index message's index. <b>Message count starts with 1.</b>
    * @return {@link String} text of the message
    */
   public String getOutputMessage(int index)
   {
      WebElement message = getMessageByIndex(index);
      return message.getText();
   }

   /**
    * Check is Output form opened.
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
    * @param messageIndex index of the message to wait
    * @param timeOut seconds
    * @throws Exception
    */
   public void waitForMessageShow(final int messageIndex, int timeout) throws Exception
   {
      new WebDriverWait(driver(), timeout).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement message = getMessageByIndex(messageIndex);
               return message != null && message.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   public void waitForMessageShow(final int messageIndex) throws Exception
   {
      waitForMessageShow(messageIndex, 3);
   }

   /**
    * Click on error message, pointed by its position in output panel.
    * The index starts from 1.
    * 
    * @param messageNumber number of the message
    */
   public void clickOnErrorMessage(int messageNumber)
   {
      WebElement message = getMessageByIndex(messageNumber);
      message.click();
   }

   /**
    * @param index
    * @return
    */
   private WebElement getMessageByIndex(int index)
   {
      return outputContent.findElement(By.xpath(String.format(Locators.OUTPUT_ROW_BY_INDEX, index)));
   }

   /**
    * Click clear output button.
    */
   public void clickClearButton() throws Exception
   {
      clearButton.click();
   }

   /**
    * Wait output panel is cleaned.
    */
   public void waitOutputCleaned()
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            return outputContent.getText() == null || outputContent.getText().isEmpty();
         }
      });
   }
}
