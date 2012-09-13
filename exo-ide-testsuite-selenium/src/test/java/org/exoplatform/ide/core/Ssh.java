package org.exoplatform.ide.core;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Ssh extends AbstractTestModule
{
   private interface Locators
   {

      String SSH_MANAGER_GENERATE_KEY_BUTTON_ID = "ideSshKeyManagerGenerateButton";

      String SSH_MANAGER_UPLOAD_KEYB_UTTON_ID = "ideSshKeyManagerUploadButton";

      String SSH_MANAGER_FORM_ID = "div[view-id='ideSshKeyManagerView']";

      String HOST_COLUMN = "//table[@id='ideSshKeysGrid']//th[text()='Host']";

      String PUBLIC_KEY_COLUMN = "//table[@id='ideSshKeysGrid']//th[text()='Public Key']";

      String PUBLIC_DELETE_COLUMN = "//table[@id='ideSshKeysGrid']//th[text()='Delete']";

   }

   @FindBy(css = Locators.SSH_MANAGER_FORM_ID)
   private WebElement ssForm;

   @FindBy(xpath = Locators.HOST_COLUMN)
   private WebElement hostCol;

   @FindBy(xpath = Locators.PUBLIC_KEY_COLUMN)
   private WebElement publicCol;

   @FindBy(xpath = Locators.PUBLIC_DELETE_COLUMN)
   private WebElement deleteCol;

   @FindBy(id = Locators.SSH_MANAGER_GENERATE_KEY_BUTTON_ID)
   private WebElement generateBtn;

   @FindBy(id = Locators.SSH_MANAGER_UPLOAD_KEYB_UTTON_ID)
   private WebElement uploadBtn;

   public void waitSSHView()
   {
      new WebDriverWait(driver(), 7).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return ssForm != null && ssForm.isDisplayed() && hostCol != null && hostCol.isDisplayed()
                  && publicCol != null && publicCol.isDisplayed() && deleteCol != null && deleteCol.isDisplayed()
                  && generateBtn != null && generateBtn.isDisplayed() && uploadBtn != null && uploadBtn.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   public void clickGenerateBtn(){
      generateBtn.click();
   }   
   
}
