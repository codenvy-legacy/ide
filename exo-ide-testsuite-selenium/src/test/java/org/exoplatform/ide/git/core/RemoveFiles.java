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
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 29, 2011 11:51:46 AM anya $
 *
 */
public class RemoveFiles extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideRemoveFilesView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String REMOVE_BUTTON_ID = "ideRemoveFilesViewRemoveButton";

      String CANCEL_BUTTON_ID = "ideRemoveFilesViewCancelButton";

      String INDEX_FILES_GRID_ID = "ideIndexFilesGrid";

      String ITEM_CHECKBOX_LOCATOR = "//table[@id=\"" + INDEX_FILES_GRID_ID
         + "\"]//tr[contains(., \"%s\")]//input[@type=\"checkbox\"]";

      String FILE_ROW_SELECTOR = "table#" + INDEX_FILES_GRID_ID + ">tbody:first-of-type tr";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.REMOVE_BUTTON_ID)
   private WebElement removeButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = Locators.INDEX_FILES_GRID_ID)
   private WebElement filesGrid;

   /**
    * Waits for Remove files view to be opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
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
    * Waits for Remove files view to be closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
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
      return (view != null && view.isDisplayed() && filesGrid != null && filesGrid.isDisplayed()
         && removeButton != null && removeButton.isDisplayed() && cancelButton != null && cancelButton.isDisplayed());
   }

   /**
    * Click Remove button.
    */
   public void clickRemoveButton()
   {
      removeButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Make item checked.
    * 
    * @param name item's name
    */
   public void checkFileByName(String name)
   {
      WebElement item = driver().findElement(By.xpath(String.format(Locators.ITEM_CHECKBOX_LOCATOR, name)));
      item.click();
   }

   /**
    * Make item unchecked.
    * 
    * @param name item's name
    */
   public void unCheckFileByName(String name)
   {
      WebElement item = driver().findElement(By.xpath(String.format(Locators.ITEM_CHECKBOX_LOCATOR, name)));
      item.click();
   }

   /**
    * Get the number of files in index grid.
    * 
    * @return count of files
    */
   public int getFilesCount()
   {
      return driver().findElements(By.cssSelector(Locators.FILE_ROW_SELECTOR)).size();
   }
}
