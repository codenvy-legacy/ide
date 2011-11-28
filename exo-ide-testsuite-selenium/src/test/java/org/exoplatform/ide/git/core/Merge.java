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

import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 22, 2011 12:31:53 PM anya $
 *
 */
public class Merge extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "MergeView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String MERGE_BUTTON_ID = "MergeViewMergeButton";

      String CANCEL_BUTTON_ID = "MergeViewCancelButton";

      String REF_TREE_ID = "MergeViewRefTree";

      String REFERENCE_LOCATOR = "//div[@id='" + REF_TREE_ID + "']//div[contains(. , '%s')]";
   }

   public interface Messages
   {
      String FAST_FORWARD = "Fast-forward";

      String CONFLICTING = "Conflicting";

      String MERGED_COMMITS = "Merged commits:";

      String NEW_HEAD_COMMIT = "New HEAD commit:";

      String CONFLICTS = "Conflicts:\n%s";

      String UP_TO_DATE = "Already up-to-date";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.MERGE_BUTTON_ID)
   private WebElement mergeButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = Locators.REF_TREE_ID)
   private WebElement tree;

   /**
    * Waits for Merge view to be opened.
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
    * Waits for Merge view to be closed.
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
      return (view != null && view.isDisplayed() && mergeButton != null && mergeButton.isDisplayed()
         && cancelButton != null && cancelButton.isDisplayed() && tree != null && tree.isDisplayed());
   }

   /**
    * Click Merge button.
    */
   public void clickMergeButton()
   {
      mergeButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Check Merge button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isMergeButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(mergeButton);
   }

   /**
    * @param name reference's name
    * @return {@link Boolean}
    */
   public boolean isRererencePresent(String name)
   {
      try
      {
         return driver().findElement(By.xpath(String.format(Locators.REFERENCE_LOCATOR, name))) != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Wait reference is visible.
    * 
    * @param name reference's name
    * @throws Exception
    */
   public void waitRererenceVisible(final String name) throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return isRererencePresent(name);
         }
      });
   }

   /**
    * Select reference by name.
    *  
    * @param name reference's name
    */
   public void selectReference(String name)
   {
      WebElement reference = driver().findElement(By.xpath(String.format(Locators.REFERENCE_LOCATOR, name)));
      new Actions(driver()).moveToElement(reference, 1, 1).click().build().perform();
   }

   /**
    * Make double click on reference.
    * 
    * @param name reference's name
    */
   public void doubleClickReference(String name)
   {
      WebElement reference = driver().findElement(By.xpath(String.format(Locators.REFERENCE_LOCATOR, name)));
      new Actions(driver()).doubleClick(reference).build().perform();
   }

}
