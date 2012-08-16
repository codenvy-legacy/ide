package org.exoplatform.ide.core;

import com.sun.swing.internal.plaf.basic.resources.basic;

import com.gargoylesoftware.htmlunit.WebWindow;

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.selenesedriver.FindElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xmlpull.v1.sax2.Driver;

public class PaasAuthorization extends AbstractTestModule
{

   private interface Locators
   {
      String LOGIN_VIEW = "ideLoginView-window";

      String LOGIN_BTN = "ideLoginViewLoginButton";

      String CANCEL_BTN = "ideLoginViewCancelButton";

      String CLOSE_WINDOW = "div#ideLoginView-window img[title=Close]";

      String MAXIMIZE_WINDOW = "div#ideLoginView-window img[title=Maximize]";

      String EMAIL_FIELD = "ideLoginViewEmailField";

      String PASS_FIELD = "ideLoginViewPasswordField";

      String ENABLED_BTN_ACTIVE_STATE = "//div[@id=\"ideLoginViewLoginButton\" and @button-enabled=\"true\"]";
   }

   @FindBy(id = Locators.LOGIN_VIEW)
   WebElement view;

   @FindBy(id = Locators.LOGIN_BTN)
   WebElement loginBtn;

   @FindBy(id = Locators.CANCEL_BTN)
   WebElement cancelBtn;

   @FindBy(css = Locators.CLOSE_WINDOW)
   WebElement closeTitleWin;

   @FindBy(css = Locators.MAXIMIZE_WINDOW)
   WebElement maxTitleWin;

   @FindBy(name = Locators.EMAIL_FIELD)
   WebElement emailField;

   @FindBy(name = Locators.PASS_FIELD)
   WebElement passwordField;

   /**
    * Wait 
   * 
   */
   public boolean checkAppearAutorizationForm(int mlsec) throws InterruptedException
   {
      for (int i = 0; i < mlsec*5; i++)
      {
         System.out.println("<<<<<<<<<<<<<<<<<<<I'm into loop");
         Thread.sleep(500);
         System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + isAutorizationFormPresent());
         if (isAutorizationFormPresent())
            return true;
         break;

      }
      return false;
   }

   /**
     * Wait appearance Deploy form
    * 
    */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed() && cancelBtn != null && cancelBtn.isDisplayed()
               && closeTitleWin.isDisplayed() && closeTitleWin != null && maxTitleWin.isDisplayed()
               && maxTitleWin != null;
         }
      });

   }

   /**
    * Wait appearance active login button
   * 
   */
   public void waitLoginBtnIsActive() throws InterruptedException
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement elem = driver().findElement(By.xpath(Locators.ENABLED_BTN_ACTIVE_STATE));
               return elem.isDisplayed() && elem != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

   }

   /**
    * return true if all basic elements of the form is present
    * @return
    */
   public boolean isAutorizationFormPresent()
   {
      JavascriptExecutor js = (JavascriptExecutor)driver();
      Object gg =
         js.executeScript("var element = document.getElementById('ideLoginView-window');if (element != null){return true;} else {return false;}");
      String val = gg.toString();
      Boolean b = Boolean.parseBoolean(val);
      return b.booleanValue();
   }

   /**
    * click on the login button
    */
   public void clickLoginBtn()
   {
      loginBtn.click();
   }

   /**
    * click on the cancel button
    */
   public void clickCancelBtn()
   {
      cancelBtn.click();
   }

   /**
    * type your email into email field
    */
   public void typeEmailField(String email)
   {
      emailField.sendKeys(email);
   }

   /**
    * type your pass into password field
    */
   public void typePasswordField(String pass)
   {
      passwordField.sendKeys(pass);
   }

}