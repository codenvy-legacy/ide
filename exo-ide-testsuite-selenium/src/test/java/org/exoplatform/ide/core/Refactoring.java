/*
 * Copyright (C) 2013 eXo Platform SAS.
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 12:16:31 PM  Jan 29, 2013 $
 *
 */
public class Refactoring extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_LOCATOR = "//div[@view-id='ideRefactoringRenameView']";

      String RENAME_BUTTON_ID = "ideRefactoringRenameViewRenameButton";

      String CANCEL_BUTTON_ID = "ideRefactoringRenameViewCancelButton";

      String NEW_NAME = "//input[@name='ideRefactoringRenameViewNewNameField']";

      String WAIT_FOR_INIT_WINDOW =
         "//div[@view-id='ideInformationModalView']//div[text()='Wait for initialize Java tooling.']";

      String WAIT_FOR_NEED_TO_SAVE_WINDOW =
         "//div[@view-id='ideInformationModalView']//div[text()='You should save all unsaved files to continue.']";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(id = Locators.RENAME_BUTTON_ID)
   WebElement renameBtn;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelBtn;

   @FindBy(xpath = Locators.NEW_NAME)
   WebElement newNameInput;

   /**
    * wait rename form
    * @throws Exception
    */
   public void waitRenameForm() throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.VIEW_LOCATOR)));
   }

   
   /**
    * wait rename form is closed
    * @throws Exception
    */
   public void waitRenameFormIsClosed() throws Exception
   {
      new WebDriverWait(driver(), 10).until(ExpectedConditions.invisibilityOfElementLocated(By
         .xpath(Locators.VIEW_LOCATOR)));
   }
   
   
   /**
    * type text in new name field
    */
   public void typeNewName(String newName)
   {
      newNameInput.clear();
      newNameInput.sendKeys(newName);
   }

   /**
    * click rename button
    */
   public void clickRenameButton()
   {
      renameBtn.click();
   }

   /**
    * wait pop up with message that need to wait initialize Java tooling before refactoring.
    * @throws Exception
    */
   public void waitPopupWithWaitInitializeJavaToolingMessage() throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.WAIT_FOR_INIT_WINDOW)));
   }

   /**
    * wait pop up with message that need save file before refactoring.
    * @throws Exception
    */
   public void waitPopupWithMessageThatNeedSaveFileBeforeRefactor() throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.WAIT_FOR_NEED_TO_SAVE_WINDOW)));
   }
}
