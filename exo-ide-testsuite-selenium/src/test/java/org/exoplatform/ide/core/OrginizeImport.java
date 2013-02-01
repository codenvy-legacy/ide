package org.exoplatform.ide.core;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OrginizeImport extends AbstractTestModule
{

   private interface Locators
   {
      String VIEW_CSS = "div#ideOrganizeImportsView-window.gwt-DialogBox";

      String IMPORT_LIST_CSS_SELECTOR =
         "div#ideOrganizeImportsView-window.gwt-DialogBox div table tbody tr.dialogMiddle td.dialogMiddleCenter div.dialogMiddleCenterInner div div div div div div div div div div div";

      String BACK_BTN_ID = "ideOrganizeImportBack";

      String NEXT_BTN_ID = "ideOrganizeImportNext";

      String ORGINIZE_BTN_FINISH_ID = "ideOrganizeImportFinish";

      String ORGINIZE_BTN_CANCEL_ID = " ideOrganizeImportCancel";

      String NAME_OF_IMPORT_LIST = "//div[@id='ideOrganizeImportsView-window']//td[text()='%s']";

   }

   @FindBy(css = Locators.VIEW_CSS)
   private WebElement view;

   @FindBy(css = Locators.IMPORT_LIST_CSS_SELECTOR)
   private WebElement importList;

   @FindBy(id = Locators.BACK_BTN_ID)
   private WebElement backBtn;

   @FindBy(id = Locators.NEXT_BTN_ID)
   private WebElement nextBtn;

   @FindBy(id = Locators.ORGINIZE_BTN_FINISH_ID)
   private WebElement finishBtn;

   @FindBy(id = Locators.ORGINIZE_BTN_CANCEL_ID)
   private WebElement cancelBtn;

   /**
    * wait OrginizeImport Window is open
    */
   public void waitForWindowOpened()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed();
         }
      });
   }

   /**
    * wait OrginizeImport Window closed
    */
   public void waitForWindowClosed()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement elem = driver().findElement(By.cssSelector(Locators.VIEW_CSS));
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
    * wait OrginizeImport with the with the specified name
    * 
    */
   public void waitForValueInImportList(final String name)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement elem = driver().findElement(By.xpath(String.format(Locators.NAME_OF_IMPORT_LIST, name)));
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
    * get all text from import list
    */
   public String getTextFromImportList()
   {
      return importList.getText();
   }

   /**
    * select value with the specified name in Organize import window 
    * @param name
    */
   public void selectValueInImportList(String name)
   {
      driver().findElement(By.xpath(String.format(Locators.NAME_OF_IMPORT_LIST, name))).click();
   }

   /**
    * click on back button on Organize import form
    */
   public void backBtnclick()
   {
      backBtn.click();
   }

   /**
    * click on next button on Organize import form
    */
   public void nextBtnclick()
   {
      nextBtn.click();
   }

   /**
    * click on finish button on Organize import form
    */
   public void finishBtnclick()
   {
      finishBtn.click();
   }

   /**
    * click on cancel button on Organize import form
    */
   public void cancelBtnclick()
   {
      cancelBtn.click();
   }

}
