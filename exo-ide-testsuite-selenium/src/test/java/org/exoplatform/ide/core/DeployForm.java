package org.exoplatform.ide.core;

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DeployForm extends AbstractTestModule
{

   private interface Locators
   {

      String VIEW_LOCATOR = "DeployNewProjectView-window";

      String CANCEL_BUTTON_ID = "ideLoadDeploymentCancelButtonId";

      String BACK_BUTTON_ID = "ideWizardDeploymentBackButtonId";

      String FINISH_BTN_ID = "ideWizardDeploymentNextButtonId";

      String SELECT_TAG = "select";

      String LIST = "//option[text()='%s']";

   }

   @FindBy(id = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   WebElement cancelBtn;

   @FindBy(id = Locators.BACK_BUTTON_ID)
   WebElement backBtn;

   @FindBy(id = Locators.FINISH_BTN_ID)
   WebElement finishBtn;

   @FindBy(tagName = Locators.SELECT_TAG)
   WebElement select;

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
            return view != null && view.isDisplayed() && cancelBtn != null && cancelBtn.isDisplayed();
         }
      });
   }

   /**
    * Wait appearance list with specified PaaS name
   * 
   */
   public void waitPaasListOpened(final String paas) throws InterruptedException
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {

            try
            {
               WebElement listNamePaas = driver().findElement(By.xpath(String.format(Locators.LIST, paas)));
               return listNamePaas != null && listNamePaas.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }

         }
      });
   }

   /**
    * click on PaaS option form
    */
   public void collapsePaasList()
   {
      select.click();
   }

   /**
    * click on PaaS option form
    */
   public void selectAndClickOnPaasInList(String selectionName)
   {
      WebElement namePaas = driver().findElement(By.xpath(String.format(Locators.LIST, selectionName)));
      new Actions(driver()).moveToElement(namePaas).build().perform();
      namePaas.click();
   }

}
