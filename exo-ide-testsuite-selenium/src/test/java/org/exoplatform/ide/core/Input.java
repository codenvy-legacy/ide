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

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Nov 2, 2011 12:18:42 PM evgen $
 *
 */
public class Input extends AbstractTestModule
{
   interface Locators
   {
      String SUGGEST_BOX_ID = "exoSuggestPanel";
   }

   /**
    * Type text to element, optional clear it. 
    * @param element WebElement MUST point or input or textarea Html element
    * @param text to type
    * @param isClear is clear element before typing
    * @throws InterruptedException 
    */
   public void typeToElement(WebElement element, String text, boolean isClear) throws InterruptedException
   {
      if (isClear)
      {
         element.clear();
      }
      element.sendKeys(text);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
   }

   /**
    *  Type text to element
    * @param element WebElement, MUST point or input or textarea Html element
    * @param text Text to type
    * @throws InterruptedException 
    */
   public void typeToElement(WebElement element, String text) throws InterruptedException
   {
      typeToElement(element, text, false);
   }

   /**
    * Set value of the combobox item by typing it into it and pressing enter.
    * 
    * @param element combobox element
    * @param value value to set
    * @throws InterruptedException
    */
   public void setComboboxValue(WebElement element, String value) throws InterruptedException
   {
      typeToElement(element, value, true);
      typeToElement(element, Keys.ENTER.toString());
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               driver.findElement(By.id(Locators.SUGGEST_BOX_ID));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }
}
