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
