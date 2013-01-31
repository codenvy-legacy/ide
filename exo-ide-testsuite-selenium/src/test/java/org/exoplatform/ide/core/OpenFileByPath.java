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
 * @version $Id: Feb 29, 2012 10:39:14 AM anya $
 * 
 */
public class OpenFileByPath extends AbstractTestModule
{
   private final class Locators
   {
      public static final String VIEW_ID = "ideOpenFileByPathWindow";

      public static final String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      public static final String OPEN_BUTTON_ID = "ideOpenFileByPathFormOpenButton";

      public static final String CANCEL_BUTTON_ID = "ideOpenFileByPathFormCancelButton";

      public static final String FILE_PATH_FIELD_NAME = "ideOpenFileByPathFormFilePathField";

   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(name = Locators.FILE_PATH_FIELD_NAME)
   WebElement pathField;

   @FindBy(id = Locators.OPEN_BUTTON_ID)
   WebElement openButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelButton;

   /**
    * Waits for Open file by path view to be opened.
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
    * Waits for Open file by path view to be closed.
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
      return (view != null && view.isDisplayed() && openButton != null && openButton.isDisplayed() && pathField != null
         && pathField.isDisplayed() && cancelButton != null && cancelButton.isDisplayed());
   }

   public void clickOpenButton()
   {
      openButton.click();
   }

   public void clickCancelButton()
   {
      cancelButton.click();
   }

   public boolean isOpenButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(openButton);
   }

   public boolean isCancelButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }

   public String getFilePath()
   {
      return pathField.getText();
   }

   public void setFilePath(String path) throws InterruptedException
   {
      IDE().INPUT.typeToElement(pathField, path, true);
   }
}
