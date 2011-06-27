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

import org.exoplatform.ide.IDE;
import org.exoplatform.ide.core.AbstractTestModule;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 27, 2011 2:50:36 PM anya $
 *
 */
public class Branches extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "ideBranchView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String CREATE_BUTTON_ID = "ideBranchViewCreateButton";

      String CHECKOUT_BUTTON_ID = "ideBranchViewCheckoutButton";

      String DELETE_BUTTON_ID = "ideBranchViewDeleteButton";

      String CLOSE_BUTTON_ID = "ideBranchViewCloseButton";

      String BRANCHES_GRID_ID = "ideBranchGrid";

      String branchRowLocator = "//table[@id=\"" + BRANCHES_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";

      String NEW_BRANCH_VIEW_ID = "exoAskForValueDialog";

      String NEW_BRANCH_VALUE_FIELD = "//div[@id=\"" + NEW_BRANCH_VIEW_ID + "\"]//input[@name=\"valueField\"]";

      String NEW_BRANCH_OK_BUTTON_ID = "exoAskForValueDialogOkButton";

      String NEW_BRANCH_CANCEL_BUTTON_ID = "exoAskForValueDialogCancelButton";
   }

   /**
   * Waits for Branches view to be opened.
   * 
   * @throws Exception
   */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Branches view to be closed.
    * 
    * @throws Exception
    */
   public void waitForViewClosed() throws Exception
   {
      waitForElementNotPresent(Locators.VIEW_LOCATOR);
   }

   /**
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isViewComponentsPresent()
   {
      return selenium().isElementPresent(Locators.CREATE_BUTTON_ID)
         && selenium().isElementPresent(Locators.CHECKOUT_BUTTON_ID)
         && selenium().isElementPresent(Locators.CLOSE_BUTTON_ID)
         && selenium().isElementPresent(Locators.DELETE_BUTTON_ID)
         && selenium().isElementPresent(Locators.BRANCHES_GRID_ID);
   }

   /**
    * Click Create button.
    */
   public void clickCreateButton()
   {
      selenium().click(Locators.CREATE_BUTTON_ID);
   }

   /**
    * Click Checkout button.
    */
   public void clickCheckoutButton()
   {
      selenium().click(Locators.CHECKOUT_BUTTON_ID);
   }

   /**
    * Click Delete button.
    */
   public void clickDeleteButton()
   {
      selenium().click(Locators.DELETE_BUTTON_ID);
   }

   /**
    * Click Close button.
    */
   public void clickCloseButton()
   {
      selenium().click(Locators.CLOSE_BUTTON_ID);
   }

   /**
    * Check Create button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCreateButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.CREATE_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Check Checkout button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCheckoutButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.CHECKOUT_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Check Delete button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isDeleteButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.DELETE_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Check Create button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCloseButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.CLOSE_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Get the number of branches in grid.
    * 
    * @return count of branches
    */
   public int getBranchesCount()
   {
      return selenium().getXpathCount("//table[@id=\"" + Locators.BRANCHES_GRID_ID + "\"]/tbody[1]//tr").intValue();
   }

   /**
    * Select branch in grid by name.
    * 
    * @param branchName name of the branch
    */
   public void selectBranchByName(String branchName)
   {
      selenium().click(String.format(Locators.branchRowLocator, branchName));
   }

   /**
    * Checks if pointed branch is checked.
    * 
    * @param branchName name of the branch
    * @return {@link Boolean} checked state of the branch
    */
   public boolean isBranchChecked(String branchName)
   {
      return selenium().isElementPresent(
         "//table[@id=\"" + Locators.BRANCHES_GRID_ID + "\"]//tr[contains(., \"" + branchName + "\")]//img");
   }

   /**
    * Waits pointed branch is checked.
    * 
    * @param branchName name of the branch
    * @return {@link Boolean} checked state of the branch
    * @throws Exception 
    */
   public void waitBranchChecked(String branchName) throws Exception
   {
      waitForElementPresent("//table[@id=\"" + Locators.BRANCHES_GRID_ID + "\"]//tr[contains(., \"" + branchName
         + "\")]//img");
   }

   /** Waits for New Branch view to be opened.
   * 
   * @throws Exception
   */
   public void waitForNewBranchViewOpened() throws Exception
   {
      waitForElementPresent(Locators.NEW_BRANCH_VIEW_ID);
      waitForElementVisible(Locators.NEW_BRANCH_VIEW_ID);
   }

   /**
    * Waits for New Branch view to be closed.
    * 
    * @throws Exception
    */
   public void waitForNewBranchViewClosed() throws Exception
   {
      waitForElementNotPresent(Locators.NEW_BRANCH_VIEW_ID);
   }

   /**
    * Click Ok button.
    */
   public void clickNewBranchOkButton()
   {
      selenium().click(Locators.NEW_BRANCH_OK_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickNewBranchCancelButton()
   {
      selenium().click(Locators.NEW_BRANCH_CANCEL_BUTTON_ID);
   }

   /**
    * Type the name of new branch.
    * 
    * @param newBranch new branch name
    */
   public void typeNewBranchName(String newBranch)
   {
      selenium().type(Locators.NEW_BRANCH_VALUE_FIELD, newBranch);
   }
}
