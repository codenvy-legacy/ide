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
 * @version $Id:  Jun 30, 2011 12:38:07 PM anya $
 *
 */
public class Remotes extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideRemoteView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String ADD_BUTTON_ID = "ideRemoteViewAddButton";

      String DELETE_BUTTON_ID = "ideRemoteViewDeleteButton";

      String CLOSE_BUTTON_ID = "ideRemoteViewCloseButton";

      String REMOTE_GRID_ID = "ideRemoteGrid";

      String ADD_REMOTE_VIEW_ID = "ideAddRemoteRepositoryView";

      String ADD_REMOTE_VIEW_LOCATOR = "//div[@view-id='" + ADD_REMOTE_VIEW_ID + "']";

      String OK_BUTTON_ID = "ideAddRemoteRepositoryViewOkButton";

      String CANCEL_BUTTON_ID = "ideAddRemoteRepositoryViewCancelButton";

      String NAME_FIELD_ID = "ideAddRemoteRepositoryViewNameField";

      String URL_FIELD_ID = "ideAddRemoteRepositoryViewUrlField";

      String REMOTE_REPOSITORY_LOCATOR = "//table[@id=\"" + REMOTE_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";

      String REMOTE_ROW_SELECTOR = "table#" + REMOTE_GRID_ID + ">tbody:first-of-type tr";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.ADD_BUTTON_ID)
   private WebElement addButton;

   @FindBy(id = Locators.DELETE_BUTTON_ID)
   private WebElement deleteButton;

   @FindBy(id = Locators.CLOSE_BUTTON_ID)
   private WebElement closeButton;

   @FindBy(id = Locators.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = Locators.REMOTE_GRID_ID)
   private WebElement remotesGrid;

   @FindBy(xpath = Locators.ADD_REMOTE_VIEW_LOCATOR)
   private WebElement addRemoteView;

   @FindBy(name = Locators.NAME_FIELD_ID)
   private WebElement nameField;

   @FindBy(name = Locators.URL_FIELD_ID)
   private WebElement urlField;

   /**
    * Waits for Remotes view to be opened.
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
    * Waits for Remotes view to be closed.
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
               input.findElement(By.xpath(Locators.ADD_REMOTE_VIEW_LOCATOR));
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
    * Waits for Add remote repository view to be opened.
    * 
    * @throws Exception
    */
   public void waitAddRemoteViewOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return isAddRepositoryOpened();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Waits for Add remote repository view to be closed.
    * 
    * @throws Exception
    */
   public void waitAddRemoteViewClosed() throws Exception
   {
      waitForElementNotPresent(Locators.ADD_REMOTE_VIEW_LOCATOR);
   }

   /**
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isOpened()
   {
      return (view != null && view.isDisplayed() && closeButton != null && closeButton.isDisplayed()
         && addButton != null && addButton.isDisplayed() && deleteButton != null && deleteButton.isDisplayed()
         && remotesGrid != null && remotesGrid.isDisplayed());
   }

   /**
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isAddRepositoryOpened()
   {
      return (addRemoteView != null && addRemoteView.isDisplayed() && cancelButton != null && cancelButton.isDisplayed()
         && okButton != null && okButton.isDisplayed() && nameField != null && nameField.isDisplayed()
         && urlField != null && urlField.isDisplayed());
   }

   /**
    * Click Add button.
    */
   public void clickAddButton()
   {
      addButton.click();
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Click Close button.
    */
   public void clickCloseButton()
   {
      closeButton.click();
   }

   /**
    * Click Delete button.
    */
   public void clickDeleteButton()
   {
      deleteButton.click();
   }

   /**
    * Click Ok button.
    */
   public void clickOkButton()
   {
      okButton.click();
   }

   /**
    * Type pointed text to repository's name field.
    * 
    * @param text
    * @throws InterruptedException 
    */
   public void typeToNameField(String text) throws InterruptedException
   {
      IDE().INPUT.typeToElement(nameField, text, true);
   }

   /**
    * Type pointed text to repository's URL field.
    * 
    * @param text
    * @throws InterruptedException 
    */
   public void typeToUrlField(String text) throws InterruptedException
   {
      IDE().INPUT.typeToElement(urlField, text, true);
   }

   /**
    * Check Ok button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isOkButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(okButton);
   }

   /**
    * Check Add button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isAddButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(addButton);
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
    * Get the number of remote repositories in grid.
    * 
    * @return count of remote repositories
    */
   public int getRemoteRepositoriesCount()
   {
      return driver().findElements(By.cssSelector(Locators.REMOTE_ROW_SELECTOR)).size();
   }

   public void waitForRemotesCount(final int count) throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return count == getRemoteRepositoriesCount();
         }
      });
   }

   /**
    * Select remote repository in grid by name.
    * 
    * @param remote name
    */
   public void selectRemoteByName(String remoteName)
   {
      WebElement remoteRepository =
         driver().findElement(By.xpath(String.format(Locators.REMOTE_REPOSITORY_LOCATOR, remoteName)));
      remoteRepository.click();
   }

   /**
    * Add new remote repository with pointed name and location.
    * 
    * @param name remote repository view
    * @param location remote repository location
    * @throws Exception 
    */
   public void addRemoteRepository(String name, String location) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      waitOpened();

      //Add remote repository:
      clickAddButton();
      waitAddRemoteViewOpened();

      typeToNameField(name);
      typeToUrlField(location);
      clickOkButton();
      waitAddRemoteViewClosed();

      //Close Remotes view:
      clickCloseButton();
      waitClosed();
   }
}
