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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 28, 2011 10:33:47 AM anya $
 *
 */
public class DeployNodeType extends AbstractTestModule
{
   private final class Locators
   {
      static final String VIEW_ID = "ideDeployNodeTypeView";

      static final String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      static final String CANCEL_BUTTON_ID = "ideDeployNodeTypeViewCancelButton";

      static final String DEPLOY_BUTTON_ID = "ideDeployNodeTypeViewDeployButton";

      static final String FORMAT_FIELD_ID = "ideDeployNodeTypeViewFormatField";

      static final String ALREADY_EXIST_BEHAVIOR_FIELD_ID = "ideDeployNodeTypeViewAlreadyExistBehaviorField";

   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.DEPLOY_BUTTON_ID)
   private WebElement deployButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.FORMAT_FIELD_ID)
   private WebElement formatField;

   @FindBy(name = Locators.ALREADY_EXIST_BEHAVIOR_FIELD_ID)
   private WebElement alreadyExistsField;

   /**
    * Wait Deploy node type view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
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
    * Wait Deploy node type view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
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
    * Returns the opened state of the view and it's elements.
    * 
    * @return {@link Boolean} opened state
    */
   public boolean isOpened()
   {
      return (view != null && view.isDisplayed() && deployButton != null && deployButton.isDisplayed()
         && cancelButton != null && cancelButton.isDisplayed() && formatField != null && formatField.isDisplayed()
         && alreadyExistsField != null && alreadyExistsField.isDisplayed());
   }

   /**
    * Click deploy button.
    */
   public void clickDeployButton()
   {
      deployButton.click();
   }

   /**
    * Click cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Select the value of the Node type format field by visible text.
    * 
    * @param format
    */
   public void selectNodeTypeFormat(String format)
   {
      new Select(formatField).selectByVisibleText(format);
   }

   /**
    * Select the value of the Already exists behavior field by visible text.
    * 
    * @param behavior
    */
   public void selectAlreadyExists(String behavior)
   {
      new Select(alreadyExistsField).selectByVisibleText(behavior);
   }
}
