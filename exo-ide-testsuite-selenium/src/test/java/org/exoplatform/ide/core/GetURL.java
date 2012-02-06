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

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jan 26, 2012 11:48:17 AM anya $
 * 
 */
public class GetURL extends AbstractTestModule
{
   private final class Locators
   {
      public static final String VIEW_ID = "ideGetItemURLForm";

      public static final String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      public static final String OK_BUTTON_ID = "ideGetItemURLFormOkButton";

      public static final String URL_FIELD_ID = "ideGetItemURLFormURLField";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(name = Locators.URL_FIELD_ID)
   WebElement urlField;

   @FindBy(id = Locators.OK_BUTTON_ID)
   WebElement okButton;

   /**
    * Waits for Get URL view to be opened.
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
    * Waits for Get URL view to be closed.
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
      return (view != null && view.isDisplayed() && okButton != null && okButton.isDisplayed() && urlField != null && urlField
         .isDisplayed());
   }

   /**
    * Click Ok button.
    */
   public void clickOkButton()
   {
      okButton.click();
   }

   /**
    * Get the value of the URL field.
    * 
    * @return {@link String} URL value
    */
   public String getURLValue()
   {
      return IDE().INPUT.getValue(urlField);
   }

   public String getURL() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);
      waitOpened();
      String url = IDE().INPUT.getValue(urlField);
      clickOkButton();
      waitClosed();
      return url;
   }

}
