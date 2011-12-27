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
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 26, 2011 12:39:21 PM anya $
 *
 */
public class AvailableDependencies extends AbstractTestModule
{
   private final class Locators
   {
      static final String VIEW_ID = "ideJARPackagesView";

      static final String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      static final String DEPENDENCIES_GRID_ID = "ideAvailableDependenciesJARList";

      static final String ATTRIBUTES_GRID_ID = "ideAvailableDependenciesJARAttributesTable";

      static final String OK_BUTTON_ID = "ideAvailableDependenciesOkButton";

      static final String DEPENDENCY_LOCATOR = "//table[@id='" + DEPENDENCIES_GRID_ID
         + "']//div/span[contains(text(), '%s')]";

      static final String DEPENDENCY_SELECTOR = "table#" + DEPENDENCIES_GRID_ID + " tbody:first-of-type>tr";

      static final String ATTRIBUTE_ROW_LOCATOR = "//table[@id='" + ATTRIBUTES_GRID_ID + "']//tr[contains(., '%s')]";;

      static final String ATTRIBUTE_SELECTOR = "table#" + ATTRIBUTES_GRID_ID + " tbody:first-of-type>tr";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(id = Locators.DEPENDENCIES_GRID_ID)
   private WebElement dependenciesGrid;

   @FindBy(id = Locators.ATTRIBUTES_GRID_ID)
   private WebElement attributesGrid;

   /**
    * Wait Available dependencies view opened.
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
    * Wait Available dependencies view closed.
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
      return (view != null && view.isDisplayed() && okButton != null && okButton.isDisplayed()
         && dependenciesGrid != null && dependenciesGrid.isDisplayed() && attributesGrid != null && attributesGrid
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
    * Returns number of the attributes.
    * 
    * @return int number of the attributes
    */
   public int getAttributeCount()
   {
      return driver().findElements(By.cssSelector(Locators.ATTRIBUTE_SELECTOR)).size();
   }

   /**
    * Returns number of the dependencies.
    * 
    * @return int number of the dependencies
    */
   public int getDependencyCount()
   {
      return driver().findElements(By.cssSelector(Locators.DEPENDENCY_SELECTOR)).size();
   }

   /**
    * Wait for dependencies to load.
    */
   public void waitForDependencies()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return getDependencyCount() > 0;
         }
      });
   }

   /**
    * Select dependency by it's name.
    * 
    * @param dependency dependency
    */
   public void selectDependency(String dependency)
   {
      try
      {
         WebElement dependencyElement =
            driver().findElement(By.xpath(String.format(Locators.DEPENDENCY_LOCATOR, dependency)));
         dependencyElement.click();
      }
      catch (NoSuchElementException e)
      {
      }
   }

   /**
    * Get value of the attribute, by it's name.
    * 
    * @param attribute name of the attribute
    * @return {@link String} attribute's name
    */
   public String getAttributeValue(String attribute)
   {
      try
      {
         WebElement row = driver().findElement(By.xpath(String.format(Locators.ATTRIBUTE_ROW_LOCATOR, attribute)));
         return row.findElement(By.xpath("td[2]")).getText();
      }
      catch (NoSuchElementException e)
      {
         return null;
      }
   }
}
