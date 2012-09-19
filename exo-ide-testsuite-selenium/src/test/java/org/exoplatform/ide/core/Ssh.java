package org.exoplatform.ide.core;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Ssh extends AbstractTestModule
{
   private interface Locators
   {

      String SSH_MANAGER_GENERATE_KEY_BUTTON_ID = "ideSshKeyManagerGenerateButton";

      String SSH_MANAGER_UPLOAD_KEY_BUTTON_ID = "ideSshKeyManagerUploadButton";

      String SSH_MANAGER_FORM_ID = "div[view-id='ideSshKeyManagerView']";

      String HOST_COLUMN = "//table[@id='ideSshKeysGrid']//th[text()='Host']";

      String PUBLIC_KEY_COLUMN = "//table[@id='ideSshKeysGrid']//th[text()='Public Key']";

      String PUBLIC_DELETE_COLUMN = "//table[@id='ideSshKeysGrid']//th[text()='Delete']";

      String ASK_SSH_KEY_FORM = "exoAskForValueModalView-window";

      String ASK_SSH_FIELD_FORM = "valueField";

      String ASK_SSH_OKBUTTON = "OkButton";

      String ASK_SSH_CANCELBUTTON = "CancelButton";

      String SHH_KEYS_LIST_TABLE = "table#ideSshKeysGrid>tbody";

      String SHH_KEYS_LIST_GET_KEY_NUM_ORDER = "//table[@id='ideSshKeysGrid']/tbody/tr[%s]";

      String SHH_KEYS_VIEW_KEY_NUM_GRID = "//table[@id='ideSshKeysGrid']/tbody/tr[%s]//u[text()='View']";

      String SHH_KEYS_DELETE_KEY_NUM_GRID = "//table[@id='ideSshKeysGrid']/tbody/tr[%s]//u[text()='Delete']";

      String SHH_KEY_MANAGER_TEXTAREA = "exoSshPublicKeyField";

      String SHH_KEY_MANAGER_CLOSE_BTN = "ideSshKeyManagerCloseButton";

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

   @FindBy(id = Locators.SSH_MANAGER_UPLOAD_KEY_BUTTON_ID)
   private WebElement uploadBtn;

   @FindBy(id = Locators.ASK_SSH_KEY_FORM)
   private WebElement askSshForm;

   @FindBy(name = Locators.ASK_SSH_FIELD_FORM)
   private WebElement askSshField;

   @FindBy(id = Locators.ASK_SSH_OKBUTTON)
   private WebElement askSshokBtn;

   @FindBy(id = Locators.ASK_SSH_CANCELBUTTON)
   private WebElement askSshCancelBtn;

   @FindBy(css = Locators.SHH_KEYS_LIST_TABLE)
   private WebElement listKeys;

   @FindBy(name = Locators.SHH_KEY_MANAGER_TEXTAREA)
   private WebElement sshHash;

   @FindBy(id = Locators.SHH_KEY_MANAGER_CLOSE_BTN)
   private WebElement closeSshManagerBtn;

   /**
    * wait appearance Ssh key form
    */
   public void waitSshView()
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

   /**
    * wait appearance ask form for generate a new ssh key
    */
   public void waitSshAskForm()
   {
      new WebDriverWait(driver(), 7).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return askSshForm != null && askSshForm.isDisplayed() && askSshField != null
                  && askSshField.isDisplayed() && askSshokBtn != null && askSshokBtn.isDisplayed()
                  && askSshCancelBtn != null && askSshCancelBtn.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    *  wait ask form for generate a new ssh key closed
    */
   public void waitSshAskFormClose()
   {
      (new WebDriverWait(driver(), 5)).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.ASK_SSH_KEY_FORM)));
   }

   /**
    *  wait ssh grid is not empty
    */
   public void waitAppearContentInSshListGrig()
   {
      new WebDriverWait(driver(), 7).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return !listKeys.getText().isEmpty();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    *  wait ssh grid is not empty
    */
   public void waitDisAppearContentInSshListGrig()
   {
      new WebDriverWait(driver(), 7).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return listKeys.getText().isEmpty();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    *  wait closed ssh key manadger form  
    */
   public void waitAppearSshKeyManadger()
   {
      new WebDriverWait(driver(), 7).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return sshHash != null && sshHash.isDisplayed() && closeSshManagerBtn != null
                  && closeSshManagerBtn.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    *  wait appearance ssh key manadger form  
    */
   public void waitCloseSshKeyManadger()
   {
      (new WebDriverWait(driver(), 5)).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.SHH_KEY_MANAGER_TEXTAREA)));
   }

   /**
    * write new web host into field the generate ssh key form
    * @param host
    */
   public void typeHostToSshAsk(String host)
   {
      askSshField.sendKeys(host);
   }

   /**
    * click on ok button in generate ssh key form
    */
   public void cliclOkBtnSshAsk()
   {
      askSshokBtn.click();
   }

   /**
    * click on ok button in generate ssh key form
    */
   public void cliclCancelBtnSshAsk()
   {
      askSshCancelBtn.click();
   }

   /**
    * click on generate button in ssh keys 
    */
   public void clickGenerateBtn()
   {
      generateBtn.click();
   }

   /**
    *  get all text from Ssh Keys Grid
    */
   public String getAllKeysList()
   {
      return listKeys.getText();
   }

   /**
    *  get row in ssh table with set number
    */
   public String getNumRowInKeysList(int num)
   {
      return driver().findElement(By.xpath(String.format(Locators.SHH_KEYS_LIST_GET_KEY_NUM_ORDER, num))).getText();
   }

   /**
    * Clicks on View link in Ssh Key grid with set num position
    * num start with 1
    * @param num
    */
   public void clickViewKeyInGridPosition(int num)
   {
      driver().findElement(By.xpath(String.format(Locators.SHH_KEYS_VIEW_KEY_NUM_GRID, num))).click();
   }

   /**
    * Clicks on delete link in Ssh Key grid with set num position
    * num start with 1
    * @param num
    */
   public void clickDeleteKeyInGridPosition(int num)
   {
      driver().findElement(By.xpath(String.format(Locators.SHH_KEYS_DELETE_KEY_NUM_GRID, num))).click();
   }

   /**
    * get full text from ssh key manager
    * @param num
    */
   public String getSshKeyHash()
   {
      return sshHash.getAttribute("value");
   }

   /**
    * 
    * click on close btn ssh key manager
    */
   public void clickOnCloseSsshKeyManager()
   {
      closeSshManagerBtn.click();
   }

}
