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

public class PopupDialogsBrowser extends AbstractTestModule
{

   /**
    * check state pop up browser window
    * @return
    */
   public boolean isAlertPresent()
   {
      try
      {
         driver().switchTo().alert();
         return true;
      }
      catch (Exception e)
      {
         return false;
      }

   }

   /**
    * click on accept button on pop up browser window
    */
   public void acceptAlert()
   {
      driver().switchTo().alert().accept();
   }

   /**
    * click on dismiss button on pop up browser window
    */
   public void dismissAlert()
   {
      driver().switchTo().alert().dismiss();
   }
   
   /**
    * get text from Alert
    */
   public String getTextFromAlert()
   {
      return driver().switchTo().alert().getText();
   }
   

   /**
    * Wait pop up browser window 
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return isAlertPresent();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

}
