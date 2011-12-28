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
 * @version $Id:  Dec 27, 2011 11:07:36 AM anya $
 *
 */
public class PreviewNodeType extends AbstractTestModule
{
   private final class Locators
   {
      static final String VIEW_ID = "ideGenerateNodeTypeView";

      static final String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      static final String GENERATE_BUTTON = "ideGenerateNodeTypeViewGenerateButton";

      static final String CANCEL_BUTTON = "ideGenerateNodeTypeViewCancelButton";

      static final String NODE_TYPE_FIELD = "ideGenerateNodeTypeViewFormatField";

      static final String GENERATED_TYPE_VIEW_ID = "ideGeneratedTypePreviewView";

      static final String GENERATED_TYPE_VIEW_LOCATOR = "//div[@view-id='" + GENERATED_TYPE_VIEW_ID + "']";

      static final String GENERATED_TYPE_FRAME = GENERATED_TYPE_VIEW_LOCATOR + "//iframe";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.GENERATE_BUTTON)
   private WebElement generateButton;

   @FindBy(id = Locators.CANCEL_BUTTON)
   private WebElement cancelButton;

   @FindBy(name = Locators.NODE_TYPE_FIELD)
   private WebElement nodeTypeField;

   @FindBy(xpath = Locators.GENERATED_TYPE_VIEW_LOCATOR)
   private WebElement generatedTypeView;

   @FindBy(xpath = Locators.GENERATED_TYPE_FRAME)
   private WebElement generatedTypeFrame;

   /**
    * Wait Preview node type view opened.
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
    * Wait Preview node type view closed.
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
      return (view != null && view.isDisplayed() && generateButton != null && generateButton.isDisplayed()
         && cancelButton != null && cancelButton.isDisplayed() && nodeTypeField != null);
   }

   /**
    * Wait Generated type preview opened.
    * 
    * @throws Exception
    */
   public void waitGeneratedTypeViewOpened() throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return isGeneratedTypeViewOpened();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait Generated type preview closed.
    * 
    * @throws Exception
    */
   public void waitGeneratedTypeViewClosed() throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.GENERATED_TYPE_VIEW_LOCATOR));
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
   public boolean isGeneratedTypeViewOpened()
   {
      return (generatedTypeView != null && generatedTypeView.isDisplayed());
   }

   /**
    * Click Generate button.
    */
   public void clickGenerateButton()
   {
      generateButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Select format of the node type.
    */
   public void selectFormat(String format)
   {
      new Select(nodeTypeField).selectByValue(format);
   }

   public String getGeneratedNodeType()
   {
      //FIXME Switch frames doesn't work with Google Chrome WebDriver.
      //Issue - http://code.google.com/p/selenium/issues/detail?id=1969
      driver().switchTo().frame(generatedTypeFrame);
      String text = driver().findElement(By.tagName("body")).getText();
      IDE().selectMainFrame();
      return text;
   }
}
