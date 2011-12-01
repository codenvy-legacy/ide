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

import static org.junit.Assert.fail;

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class provides methods for working with Warning dialog.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WarningDialog extends AbstractTestModule
{
   interface Locators
   {
      String WARNING_DIALOG_ID = "ideWarningModalView";

      String WARNING_DIALOG_LOCATOR = "//div[@view-id='" + WARNING_DIALOG_ID + "']";

      String OK_BUTTON_ID = "OkButton";

      String MESSAGE_SELECTOR = "div[view-id=" + WARNING_DIALOG_ID + "] div.gwt-Label";
   }

   @FindBy(id = Locators.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(how = How.CSS, using = Locators.MESSAGE_SELECTOR)
   private WebElement warningMessage;

   /**
    * Wait Warning dialog opened.
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
               WebElement view = input.findElement(By.xpath(Locators.WARNING_DIALOG_LOCATOR));
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
    * Wait Warning dialog closed.
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
               input.findElement(By.id(Locators.WARNING_DIALOG_ID));
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
    * Click ok button.
    * 
    * @throws Exception
    */
   public void clickOk() throws Exception
   {
      okButton.click();
   }

   public void clickYes() throws Exception
   {
      fail();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void clickNo() throws Exception
   {
      fail();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void clickCancel() throws Exception
   {
      fail();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Get warning message.
    * 
    * @return {@link String} warning message
    */
   public String getWarningMessage()
   {
      String text = warningMessage.getText().trim();
      text = (text.endsWith("\n")) ? text.substring(0, text.length() - 2) : text;
      return text;
   }
}
