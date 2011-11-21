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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: AskDialog Apr 27, 2011 2:28:35 PM evgen $
 *
 */
public class AskDialog extends AbstractTestModule
{

   interface Locators
   {
      String VIEW_ID = "exoAskDialog";

      String ASK_TITLE_SELECTOR = "div#" + VIEW_ID + " div.Caption>span";

      String QUESTION_SELECTOR = "div#" + VIEW_ID + " div.gwt-Label";

      String YES_BUTTON_ID = "YesButton";

      String NO_BUTTON_ID = "NoButton";
   }

   @FindBy(id = Locators.VIEW_ID)
   private WebElement view;

   @FindBy(css = Locators.ASK_TITLE_SELECTOR)
   private WebElement askTitle;

   @FindBy(css = Locators.QUESTION_SELECTOR)
   private WebElement question;

   @FindBy(id = Locators.YES_BUTTON_ID)
   private WebElement yesButton;

   @FindBy(id = Locators.NO_BUTTON_ID)
   private WebElement noButton;

   /**
    * Wait dialog view opened.
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
            return view != null && view.isDisplayed();
         }
      });
   }

   /**
    * Wait dialog view closed.
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
               input.findElement(By.id(Locators.VIEW_ID));
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
    * @return {@link Boolean} 
    */
   public boolean isOpened()
   {
      return (view != null && view.isDisplayed());
   }

   /**
    * Click No button.
    * 
    * @throws Exception
    */
   public void clickNo() throws Exception
   {
      noButton.click();
   }

   /**
    * Click Yes button.
    * 
    * @throws Exception
    */
   public void clickYes() throws Exception
   {
      yesButton.click();
   }

   /**
    * Get question' text.
    * 
    * @return {@link String} question
    */
   public String getQuestion()
   {
      return question.getText();
   }

   /**
    * Returns the title(caption) of the ask dialog.
    * 
    * @return {@link String} title of the ask dialog
    */
   public String getTitle()
   {
      return askTitle.getText();
   }
}
