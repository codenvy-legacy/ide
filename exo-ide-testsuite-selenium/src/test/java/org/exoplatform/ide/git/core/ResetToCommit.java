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
 * @version $Id:  Jun 27, 2011 5:34:45 PM anya $
 *
 */
public class ResetToCommit extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideResetToCommitView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String REVERT_BUTTON_ID = "ideRevertToCommitViewRevertButton";

      String CANCEL_BUTTON_ID = "ideRevertToCommitViewCancelButton";

      String MODE_ID = "ideRevertToCommitViewMode";

      String REVISION_GRID_ID = "ideRevisionGrid";

      String REVISION_LOCATOR = "//table[@id=\"" + REVISION_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";

      String SOFT_MODE_LOCATOR = VIEW_LOCATOR + "//label[contains(text(), \"soft\")]";

      String MIXED_MODE_LOCATOR = VIEW_LOCATOR + "//label[contains(text(), \"mixed\")]";

      String HARD_MODE_LOCATOR = VIEW_LOCATOR + "//label[contains(text(), \"hard\")]";

      String REVISION_ROW_SELECTOR = "table#" + REVISION_GRID_ID + ">tbody:first-of-type tr";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.REVERT_BUTTON_ID)
   private WebElement revertButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = Locators.MODE_ID)
   private WebElement modeField;

   @FindBy(id = Locators.REVISION_GRID_ID)
   private WebElement revisionsGrid;

   @FindBy(xpath = Locators.SOFT_MODE_LOCATOR)
   private WebElement softModeField;

   @FindBy(xpath = Locators.MIXED_MODE_LOCATOR)
   private WebElement mixedModeField;

   @FindBy(xpath = Locators.HARD_MODE_LOCATOR)
   private WebElement hardModeField;

   /**
    * Waits for Reset commit view to be opened.
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
    * Waits for Reset commit view to be closed.
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
      return (view != null && view.isDisplayed() && revisionsGrid != null && revisionsGrid.isDisplayed()
         && revertButton != null && revertButton.isDisplayed() && cancelButton != null && cancelButton.isDisplayed()
         && modeField != null && modeField.isDisplayed());
   }

   /**
    * Click Revert button.
    */
   public void clickRevertButton()
   {
      revertButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Select "soft" mode of the revert operation.
    */
   public void selectSoftMode()
   {
      softModeField.click();
   }

   /**
    * Select "mixed" mode of the revert operation.
    */
   public void selectMixedMode()
   {
      mixedModeField.click();
   }

   /**
    * Select "hard" mode of the revert operation.
    */
   public void selectHardMode()
   {
      hardModeField.click();
   }

   /**
    * Select the row with revision by the pointed comment.
    * 
    * @param comment
    */
   public void selectRevisionByComment(String comment)
   {
      WebElement revision = driver().findElement(By.xpath(String.format(Locators.REVISION_LOCATOR, comment)));
      revision.click();
   }

   /**
    * Get the number of revisions in grid.
    * 
    * @return count of revisions
    */
   public int getRevisionsCount()
   {
      return driver().findElements(By.cssSelector(Locators.REVISION_ROW_SELECTOR)).size();
   }

}
