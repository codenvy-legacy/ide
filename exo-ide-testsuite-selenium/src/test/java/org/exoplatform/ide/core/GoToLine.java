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
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Musienko Maksim</a>
 * @version $
 */

public class GoToLine extends AbstractTestModule
{

   interface Locators
   {
      String VIEW_LOCATOR = "//div[@view-id='ideGoToLineForm']";

      String LINE_NUBER_FIELD_ID = "ideGoToLineFormLineNumberField";

      String LINE_RANGE_LABEL_ID = "ideGoToLineFormLineRangeLabel";

      String GO_TO_LINE_BUTTON_ID = "ideGoToLineFormGoButton";

      String CANCEL_BUTTON_ID = "ideGoToLineFormCancelButton";
   }

   public static final String RANGE_LABEL = "Enter line number (%d..%d):";

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(name = Locators.LINE_NUBER_FIELD_ID)
   private WebElement lineNumberField;

   @FindBy(id = Locators.LINE_RANGE_LABEL_ID)
   private WebElement lineRangeLabel;

   @FindBy(id = Locators.GO_TO_LINE_BUTTON_ID)
   private WebElement goButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   /**
    * Wait Go to line view opened.
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
    * Wait Go to line view closed.
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
    * Check Go To LineForm present
    */
   public boolean isGoToLineViewPresent()
   {
      return (view != null && view.isDisplayed()) && (goButton != null && goButton.isDisplayed())
         && (cancelButton != null && cancelButton.isDisplayed())
         && (lineNumberField != null && lineNumberField.isDisplayed());
   }

   /**
    * Get the value of the line number range.
    * 
    * @return {@link String}
    */
   public String getLineNumberRangeLabel()
   {
      return lineRangeLabel.getText();
   }

   /**
    * Type value of the line number field.
    * 
    * @param value line number value
    * @throws InterruptedException
    */
   public void typeIntoLineNumberField(String value) throws InterruptedException
   {
      IDE().INPUT.typeToElement(lineNumberField, value, true);
   }

   /**
    * Click cancel button.
    * 
    * @throws InterruptedException
    */
   public void clickCancelButton() throws InterruptedException
   {
      cancelButton.click();
   }

   /**
    * Click go to line button.
    * 
    * @throws InterruptedException
    */
   public void clickGoButton() throws InterruptedException
   {
      goButton.click();
   }

   /**
    * Move to pointed line number.
    * 
    * @param line line number to move to
    * @throws Exception 
    */
   public void goToLine(int line) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);
      waitOpened();
      typeIntoLineNumberField(String.valueOf(line));
      clickGoButton();
      waitClosed();
   }
}
