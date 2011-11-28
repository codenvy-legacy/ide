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

import org.exoplatform.ide.MenuCommands;
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
 * @version $Id:  Jun 27, 2011 2:50:36 PM anya $
 *
 */
public class Branches extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideBranchView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String CREATE_BUTTON_ID = "ideBranchViewCreateButton";

      String CHECKOUT_BUTTON_ID = "ideBranchViewCheckoutButton";

      String DELETE_BUTTON_ID = "ideBranchViewDeleteButton";

      String CLOSE_BUTTON_ID = "ideBranchViewCloseButton";

      String BRANCHES_GRID_ID = "ideBranchGrid";

      String BRANCH_LOCATOR = "//table[@id=\"" + BRANCHES_GRID_ID + "\"]//div[contains(., \"%s\")]";

      String BRANCH_CHECKED_LOCATOR = "//table[@id=\"" + BRANCHES_GRID_ID + "\"]//div[contains(., \"%s\")]/img";

      String BRANCH_ROW_SELECTOR = "table#" + BRANCHES_GRID_ID + ">tbody:nth(0) tr";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.CREATE_BUTTON_ID)
   private WebElement createButton;

   @FindBy(id = Locators.CHECKOUT_BUTTON_ID)
   private WebElement checkoutButton;

   @FindBy(id = Locators.DELETE_BUTTON_ID)
   private WebElement deleteButton;

   @FindBy(id = Locators.CLOSE_BUTTON_ID)
   private WebElement closeButton;

   @FindBy(id = Locators.BRANCHES_GRID_ID)
   private WebElement branchesGrid;

   /**
   * Waits for Branches view to be opened.
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
    * Waits for Branches view to be closed.
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
      return (view != null && view.isDisplayed() && checkoutButton != null && checkoutButton.isDisplayed()
         && createButton != null && createButton.isDisplayed() && closeButton != null && closeButton.isDisplayed()
         && deleteButton != null && deleteButton.isDisplayed() && branchesGrid != null && branchesGrid.isDisplayed());
   }

   /**
    * Click Create button.
    */
   public void clickCreateButton()
   {
      createButton.click();
   }

   /**
    * Click Checkout button.
    */
   public void clickCheckoutButton()
   {
      checkoutButton.click();
   }

   /**
    * Click Delete button.
    */
   public void clickDeleteButton()
   {
      deleteButton.click();
   }

   /**
    * Click Close button.
    */
   public void clickCloseButton()
   {
      closeButton.click();
   }

   /**
    * Check Create button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCreateButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(createButton);
   }

   /**
    * Check Checkout button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCheckoutButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(checkoutButton);
   }

   /**
    * Check Delete button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isDeleteButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(deleteButton);
   }

   /**
    * Check Create button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCloseButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(closeButton);
   }

   /**
    * Get the number of branches in grid.
    * 
    * @return count of branches
    */
   public int getBranchesCount()
   {
      return driver().findElements(By.cssSelector(Locators.BRANCH_ROW_SELECTOR)).size();
   }

   /**
    * Select branch in grid by name.
    * 
    * @param branchName name of the branch
    */
   public void selectBranchByName(String branchName)
   {
      WebElement branch = driver().findElement(By.xpath(String.format(Locators.BRANCH_LOCATOR, branchName)));
      branch.click();
   }

   /**
    * Checks if pointed branch is checked.
    * 
    * @param branchName name of the branch
    * @return {@link Boolean} checked state of the branch
    */
   public boolean isBranchChecked(String branchName)
   {
      try
      {
         WebElement checkedBranch =
            driver().findElement(By.xpath(String.format(Locators.BRANCH_CHECKED_LOCATOR, branchName)));
         return checkedBranch != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Waits pointed branch is checked.
    * 
    * @param branchName name of the branch
    * @return {@link Boolean} checked state of the branch
    * @throws Exception 
    */
   public void waitBranchChecked(final String branchName) throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return isBranchChecked(branchName);
         }
      });
   }

   /** Waits for New Branch view to be opened.
   * 
   * @throws Exception
   */
   public void waitNewBranchViewOpened() throws Exception
   {
      IDE().ASK_FOR_VALUE_DIALOG.waitOpened();
   }

   /**
    * Waits for New Branch view to be closed.
    * 
    * @throws Exception
    */
   public void waitNewBranchViewClosed() throws Exception
   {
      IDE().ASK_FOR_VALUE_DIALOG.waitClosed();
   }

   /**
    * Click Ok button.
    * @throws Exception 
    */
   public void clickNewBranchOkButton() throws Exception
   {
      IDE().ASK_FOR_VALUE_DIALOG.clickOkButton();
   }

   /**
    * Click Cancel button.
    * @throws Exception 
    */
   public void clickNewBranchCancelButton() throws Exception
   {
      IDE().ASK_FOR_VALUE_DIALOG.clickCancelButton();
   }

   /**
    * Type the name of new branch.
    * 
    * @param newBranch new branch name
    * @throws Exception 
    */
   public void setNewBranchName(String newBranch) throws Exception
   {
      IDE().ASK_FOR_VALUE_DIALOG.setValue(newBranch);
   }

   /**
    * Wait for the pointed number of the branches.
    * 
    * @param count number of branches
    * @throws Exception
    */
   public void waitForBranchesCount(final int count) throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return count == getBranchesCount();
         }
      });
   }

   /**
    * Switch to pointed branch.
    * 
    * @param branchName name of the branch
    * @throws Exception
    */
   public void switchBranch(String branchName) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.BRANCHES);
      waitOpened();
      selectBranchByName(branchName);
      clickCheckoutButton();
      waitBranchChecked(branchName);
      clickCloseButton();
      waitClosed();
   }
}
