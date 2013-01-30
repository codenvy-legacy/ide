/*
 * Copyright (C) 2012 eXo Platform SAS.
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ImportFromGitHub extends AbstractTestModule
{
   private interface Locators
   {
      final static String IMPORT_FROM_GITHUB_FORM_ID = "ideImportFromGithubView-window";

      final static String PROJECT_BY_NAME_SELECTOR = "//table[@id='ideGithubProjectsGrid']//div[text()='%s']";

      final static String NAME_FIELD = "ideImportFromGithubViewNameField";

      final static String READ_ONLY_CHECK_BOX = "ideImportFromGithubViewReadOnlyModeField";

      final static String FINISH_BTN_ID = "ideImportFromGithubViewFinishButton";

      final static String CANCEL_BTN_ID = "ideImportFromGithubViewCancelButton";

      final static String LOGIN_FORM = "ideOAuthLoginView-window";

      final static String OK_BUTTON_ON_LOGIN_FORM = "ideOAuthLoginViewAuthButton";

      final static String CLONING_PROGRESS_FORM = "ide.modalJob.view-window";

   }

   @FindBy(id = Locators.IMPORT_FROM_GITHUB_FORM_ID)
   WebElement importFromGithubView;

   @FindBy(name = Locators.NAME_FIELD)
   WebElement nameSelectField;

   @FindBy(name = Locators.READ_ONLY_CHECK_BOX)
   WebElement readOnlyCheckBox;

   @FindBy(id = Locators.FINISH_BTN_ID)
   WebElement finishBtn;

   @FindBy(id = Locators.CANCEL_BTN_ID)
   WebElement cancelBtn;

   @FindBy(id = Locators.LOGIN_FORM)
   WebElement loginForm;

   @FindBy(id = Locators.OK_BUTTON_ON_LOGIN_FORM)
   WebElement okButtonOnLoginForm;

   @FindBy(id = Locators.CLONING_PROGRESS_FORM)
   WebElement cloningProgressForm;

   /**
    * wait opening login form to github
    */
   public void waitLoginFormToGithub()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return loginForm.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait opening button 'Import from github'
    * form opened in IDE
    */
   public void waitLoadFromGithubFormOpened()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return importFromGithubView.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait while for  load form will closed
    */
   public void waitLoadFromGithubFormClosed()
   {
      (new WebDriverWait(driver(), 30)).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.IMPORT_FROM_GITHUB_FORM_ID)));
   }

   /**
    * wait while for cloning form will closed
    * @throws Exception 
    */

   public void waitCloningProgressFormClosed() throws Exception
   {
      new WebDriverWait(driver(), 120).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.CLONING_PROGRESS_FORM)));
      IDE().LOADER.waitClosed();
   }

   /**
    * wait checkBox readOnly is selected
    * form opened in IDE
    */
   public void waitReadOnlyCheckBoxIsChecked()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return readOnlyCheckBox.isSelected();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait checkBox readOnly is unselected
    * form opened in IDE
    */
   public void waitReadOnlyCheckBoxIUnChecked()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return !readOnlyCheckBox.isSelected();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * select project by name in 'Import from github' form
    * @param name
    */
   public void selectProjectByName(String name)
   {
      driver().findElement(By.xpath(String.format(Locators.PROJECT_BY_NAME_SELECTOR, name))).click();
   }

   /**
    * type project in Name Feld
    * @param name
    */
   public void typeNameOfTheProject(String name)
   {
      nameSelectField.sendKeys(name);
   }

   /**
    * click on 'Read only' checkBox
    */
   public void readOnlyCheckBoxClick()
   {
      readOnlyCheckBox.click();
   }

   /**
    * click on 'Finish' button
    */
   public void finishBtnClick()
   {
      finishBtn.click();
   }

   /**
    * click on 'Cancel' button
    */
   public void cancelBtnClick()
   {
      cancelBtn.click();
   }

   /**
    * click ok on login to github form
    */
   public void clickOkOnLoginForm()
   {
      okButtonOnLoginForm.click();
   }

}
