package org.exoplatform.ide.core;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:mmusienko@exoplatform.com">Musienko Maksim</a>
 * @version $
 */

public class Preferences extends AbstractTestModule
{
   private interface Locators
   {
      String PREFERNCESS_FORM_ID = "eXoPreferencesView-window";

      String PREFERNCES_STHREE_FORM_ID = "eXoPreferencesViewPreferencesTree";

      String PREFERNCES_VIEW_PANEL_FORM_ID = "eXoViewPanel";

      String PREFERNCES_TREE_SELECTOR = "//div[@id='eXoPreferencesViewPreferencesTree']//div/span[text()='%s']";

      String PREFERNCES_CLOSE_BUTTON_ID = "eXoPreferencesViewCloseButton";

   }

   @FindBy(id = Locators.PREFERNCESS_FORM_ID)
   private WebElement preferenceForm;

   @FindBy(id = Locators.PREFERNCES_STHREE_FORM_ID)
   private WebElement menuesThree;

   @FindBy(id = Locators.PREFERNCES_VIEW_PANEL_FORM_ID)
   private WebElement viewForm;

   @FindBy(id = Locators.PREFERNCES_CLOSE_BUTTON_ID)
   private WebElement closeBtn;

   /**
    * wait opening preferences form
    */
   public void waitPreferencesOpen()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return preferenceForm != null && preferenceForm.isDisplayed() && menuesThree != null
                  && menuesThree.isDisplayed() && viewForm != null && viewForm.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait while for closed
    */
   public void waitPreferencesClose()
   {
      (new WebDriverWait(driver(), 5)).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.PREFERNCESS_FORM_ID)));
   }

   /**
    * wait tree menu for calling customize form 
    * @param nameMenu
    */
   public void waitCustomizeMenuRedraw(final String nameMenu)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement menu =
                  driver().findElement(By.xpath(String.format(Locators.PREFERNCES_TREE_SELECTOR, nameMenu)));
               return menu != null && menu.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * select menu for customization
    * @param nameMenu
    */
   public void selectCustomizeMenu(String nameMenu)
   {

      driver().findElement(By.xpath(String.format(Locators.PREFERNCES_TREE_SELECTOR, nameMenu))).click();
   }

   /**
    * click on close btn of the preferences form
    */
   public void clickOnCloseFormBtn()
   {
      closeBtn.click();

   }

}
