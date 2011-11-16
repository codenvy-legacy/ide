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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Musienko Maksim</a>
 * @version $
 */

public class FindReplace extends AbstractTestModule
{
   interface Locators
   {
      String VIEW_LOCATOR = "//div[@view-id='ideFindReplaceTextView']";

      String FIND_BUTTON_ID = "ideFindReplaceTextFormFindButton";

      String REPLACE_BUTTON_ID = "ideFindReplaceTextFormReplaceButton";

      String REPLACE_FIND_BUTTON_ID = "ideFindReplaceTextFormReplaceFindButton";

      String REPLACE_ALL_BUTTON_ID = "ideFindReplaceTextFormReplaceAllButton";

      String CANCEL_BUTTON_ID = "ideFindReplaceTextFormCancelButton";

      String FIND_FIELD_ID = "ideFindReplaceTextFormFindField";

      String REPLACE_FIELD_ID = "ideFindReplaceTextFormReplaceField";

      String CASE_SENSITIVE_FIELD_ID = "ideFindReplaceTextFormCaseSensitiveField";

      String FIND_RESULT_LABEL_ID = "ideFindReplaceTextFormFindResult";
   }

   public static final String NOT_FOUND_RESULT = "String not found.";

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.FIND_BUTTON_ID)
   private WebElement findButton;

   @FindBy(id = Locators.REPLACE_BUTTON_ID)
   private WebElement replaceButton;

   @FindBy(id = Locators.REPLACE_FIND_BUTTON_ID)
   private WebElement replaceFindButton;

   @FindBy(id = Locators.REPLACE_ALL_BUTTON_ID)
   private WebElement replaceAllButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(name = Locators.FIND_FIELD_ID)
   private WebElement findField;

   @FindBy(name = Locators.REPLACE_FIELD_ID)
   private WebElement replaceField;

   @FindBy(name = Locators.CASE_SENSITIVE_FIELD_ID)
   private WebElement caseSensitiveField;

   @FindBy(id = Locators.FIND_RESULT_LABEL_ID)
   private WebElement resultLabel;

   /**
    * Wait Find/Replace text view opened.
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
               WebElement view = input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return (view != null && view.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait Find/Replace text view closed.
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
    * Check the state of the find replace form: all elements to be present and the state of buttons.
    */
   public void checkFindReplaceFormAppeared()
   {
      assertTrue(view.isDisplayed());
      assertTrue(findButton.isDisplayed());
      assertTrue(replaceButton.isDisplayed());
      assertTrue(replaceFindButton.isDisplayed());
      assertTrue(replaceAllButton.isDisplayed());
      assertTrue(cancelButton.isDisplayed());
      assertTrue(findField.isDisplayed());
      assertTrue(replaceField.isDisplayed());
      assertTrue(caseSensitiveField.isDisplayed());

      // Check buttons state
      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertFalse(isFindButtonEnabled());
      assertFalse(isReplaceAllButtonEnabled());
      assertTrue(isCancelButtonEnabled());
   }

   public void checkFindReplaceFormNotAppeared()
   {
      assertNull(view);
   }

   /**
    * Get enabled state of cancel button.
    * 
    * @return boolean enabled state of cancel button
    */
   public boolean isCancelButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }

   /**
    * Get enabled state of find button.
    * 
    * @return boolean enabled state of find button
    */
   public boolean isFindButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(findButton);
   }

   /**
    * Get enabled state of replace button.
    * 
    * @return boolean enabled state of replace button
    */
   public boolean isReplaceButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(replaceButton);
   }

   /**
    * Get enabled state of replace/find button.
    * 
    * @return boolean enabled state of replace/find button
    */
   public boolean isReplaceFindButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(replaceFindButton);
   }

   /**
    * Get enabled state of replace all button.
    * 
    * @return boolean enabled state of replace all button
    */
   public boolean isReplaceAllButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(replaceAllButton);
   }

   public void typeInFindField(String text) throws InterruptedException
   {
      IDE().INPUT.typeToElement(findField, text, true);
   }

   public void typeInReplaceField(String text) throws InterruptedException
   {
      IDE().INPUT.typeToElement(replaceField, text, true);
   }

   public void checkFindFieldNotEmptyState()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
   }

   public void checkFindFieldEmptyState()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
   }

   public void clickFindButton() throws InterruptedException
   {
      findButton.click();
   }

   public void clickReplaceButton() throws InterruptedException
   {
      replaceButton.click();
   }

   public void clickReplaceFindButton() throws InterruptedException
   {
      replaceFindButton.click();
   }

   public void clickReplaceAllButton() throws InterruptedException
   {
      replaceAllButton.click();
   }

   public void clickCancelButton() throws InterruptedException
   {
      cancelButton.click();
   }

   /**
    * Check buttons when text is found.
    */
   public void checkStateWhenTextFound()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertTrue(isReplaceButtonEnabled());
      assertTrue(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
      assertEquals("", getFindResultText());
   }

   /**
    * Check buttons when text is not found.
    */
   public void checkStateWhenTextNotFound()
   {
      assertTrue(isFindButtonEnabled());
      assertTrue(isReplaceAllButtonEnabled());
      assertFalse(isReplaceButtonEnabled());
      assertFalse(isReplaceFindButtonEnabled());
      assertTrue(isCancelButtonEnabled());
      assertEquals(NOT_FOUND_RESULT, getFindResultText());
   }

   public String getFindResultText()
   {
      return resultLabel.getText();
   }

   public void clickCaseSensitiveField()
   {
      caseSensitiveField.click();
   }
}
