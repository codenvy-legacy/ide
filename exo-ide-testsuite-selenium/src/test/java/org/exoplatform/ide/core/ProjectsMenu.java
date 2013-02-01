package org.exoplatform.ide.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProjectsMenu extends AbstractTestModule
{
   private interface Locators
   {

      String VIEW_LOCATOR = "CreateNewProjectView-window";

      String CANCEL_BUTTON = "ideCreateNewProjectCancelButton";

      String NEXT_BUTTON = "ideCreateNewProjectNextButton";

      String NAME_PROJECT_SELECT =
         "//table[@id='ideCreateFileFromTemplateFormTemplateListGrid']//td/div/span[text()='%s']";

   }

   @FindBy(id = Locators.VIEW_LOCATOR)
   WebElement view;

   @FindBy(id = Locators.CANCEL_BUTTON)
   WebElement cancel;

   @FindBy(id = Locators.NEXT_BUTTON)
   WebElement next;

   /**
    * Wait appearance Projects form
   * 
   */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed() && cancel != null && cancel.isDisplayed();
         }
      });
   }

   /**
    * Wait appearance Projects form
   * 
   */
   public void waitForProjectAppear(final String namePrj) throws InterruptedException
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement prj = driver().findElement(By.xpath(String.format(Locators.NAME_PROJECT_SELECT, namePrj)));
               return prj.isDisplayed() && prj != null;
            }
            catch (Exception e)
            {
               return false;
            }

         }
      });
   }

   /**
    * select project with specified name
    * @throws InterruptedException
    */
   public void selectPoject(String name) throws InterruptedException
   {
      WebElement elem = driver().findElement(By.xpath(String.format(Locators.NAME_PROJECT_SELECT, name)));
      elem.click();
   }

   /**
    * click on cancel button
    */
   public void cancelBtnClick()
   {
      cancel.click();
   }

   /**
    * click on next button
    */
   public void nextBtnClick()
   {
      next.click();
   }

   /**
    * type of the name project
    * @throws InterruptedException 
    * 
    */
   public void typeNameOfProject(String txt) throws InterruptedException
   {
      WebElement elem = view.findElement(By.tagName("input"));
      IDE().INPUT.typeToElement(elem, txt, true);
   }

}
