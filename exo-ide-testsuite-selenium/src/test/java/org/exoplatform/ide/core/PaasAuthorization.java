package org.exoplatform.ide.core;

import com.gargoylesoftware.htmlunit.WebWindow;

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xmlpull.v1.sax2.Driver;

public class PaasAuthorization extends AbstractTestModule
{

   private interface Locators
   {
      String VIEW_LOCATOR = "ideLoginView-window";

      String EMAIL_INPUT = "ideLoginViewEmailField";

      String LOGIN_INPUT = "ideLoginViewPasswordField";
   
      String LOGIN_BTN = "ideLoginViewLoginButton";
      
      String CANCEL_BTN = "ideLoginViewCancelButton";
      
      String LOGIN_BTN_IS_DISABLED ="//div[@id=\"ideLoginViewLoginButton\" and @button-enabled=\"true\"]";
   }

   @FindBy(id = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(name = Locators.EMAIL_INPUT)
   WebElement email;
  
   @FindBy(name = Locators.EMAIL_INPUT)
   WebElement login;
  


///**
// * Wait appearance Authorization form
//* 
//*/
//public void waitOpened() throws InterruptedException
//{
//   new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
//   {
//
//      @Override
//      public Boolean apply(WebDriver input)
//      {
//       
//      }
//   });
//}
}