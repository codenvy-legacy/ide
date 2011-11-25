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

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Operations with form for selection and changing current workspace.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: $
*/
public class SelectWorkspace extends AbstractTestModule
{

   private interface Locators
   {
      String VIEW_ID = "ideSelectWorkspaceView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String LIST_GRID_ID = "ideEntryPointListGrid";

      String OK_BUTTON_ID = "ideEntryPointOkButton";

      String CANCEL_BUTTON_ID = "ideEntryPointCancelButton";

      String WORKSPACE_LOCATOR = "//table[@id='" + LIST_GRID_ID + "']//div[text()='%s']";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.LIST_GRID_ID)
   private WebElement grid;

   @FindBy(id = Locators.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   /**
    * Wait Select workspace view opened.
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
               return (view != null && view.isDisplayed() && grid != null && grid.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait Select workspace view closed.
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
    * Returns opened state of the dialog.
    * 
    * @return {@link Boolean} <code>true</code> if opened
    */
   public boolean isOpened()
   {
      try
      {
         return view != null && view.isDisplayed() && okButton != null && okButton.isDisplayed()
            && cancelButton != null && cancelButton.isDisplayed() && grid != null && grid.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Call "Select workspace" dialog and select workspace by workspaceId.
    *  
    * @param workspaceId workspace's id
    * @throws Exception
    * @throws InterruptedException
    */
   public void changeWorkspace(String workspaceId) throws Exception, InterruptedException
   {
      IDE().MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
      waitOpened();
      selectWorkspace(workspaceId);
      clickOkButton();
      waitClosed();
   }

   /**
    * Select workspace by id in grid.
    * 
    * @param workspaceId workspace's id
    * @throws InterruptedException
    */
   public void selectWorkspace(String workspaceId) throws InterruptedException
   {
      WebElement workspace = driver().findElement(By.xpath(String.format(Locators.WORKSPACE_LOCATOR, workspaceId)));
      workspace.click();
   }

   /**
    * Return Ok button's enabled state.
    * 
    * @return boolean
    * @throws Exception
    */
   public boolean isOkButtonEnabled() throws Exception
   {
      return IDE().BUTTON.isButtonEnabled(okButton);
   }

   /**
    * Return is Cancel button's enabled state.
    * 
    * @return boolean
    * @throws Exception
    */
   public boolean isCancelButtonEnabled() throws Exception
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }

   /**
    * Click Ok button.
    * 
    * @throws Exception
    */
   public void clickOkButton() throws Exception
   {
      okButton.click();
   }

   /**
    * Click Cancel button.
    * 
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      cancelButton.click();
   }

   /**
    * Double click on workspace's id.
    * 
    * @param workspaceId workspace's id
    * @throws InterruptedException
    */
   public void doubleClickWorkspace(String workspaceId) throws InterruptedException
   {
      WebElement workspace = driver().findElement(By.xpath(String.format(Locators.WORKSPACE_LOCATOR, workspaceId)));
      new Actions(driver()).doubleClick(workspace).build().perform();
   }
}
