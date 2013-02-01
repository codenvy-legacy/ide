package org.exoplatform.ide.core;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Formatter extends AbstractTestModule
{
   private interface Locators
   {
      String FORMATTER_VIEW = "div[view-id='eXoIdeJavaCodeFormatterProfileView']";

      String OK_BTN = "div[view-id='eXoIdeJavaCodeFormatterProfileView'] div.imageButtonPanel";

      String TEXT_CONTAINER = "//div[@view-id='eXoIdeJavaCodeFormatterProfileView']//div[@tabindex='-1']/div";

      String LAST_TEXT_ELEMENT_IN_DEFAULT_TEXT =
         "//div[@view-id='eXoIdeJavaCodeFormatterProfileView']//div[@tabindex='-1']/div/div[20]";

      String COMBOBOX = "//div[@view-id='eXoIdeJavaCodeFormatterProfileView']//select";

      String ECLIPSE_FORMATTER_OPTION = "//option[@value='Eclipse [built-in]']";

      String EXO_FORMATTER_OPTION = "//option[@value='eXo [built-in]']";

   }

   @FindBy(css = Locators.FORMATTER_VIEW)
   WebElement view;

   @FindBy(xpath = Locators.TEXT_CONTAINER)
   WebElement textArea;

   @FindBy(xpath = Locators.LAST_TEXT_ELEMENT_IN_DEFAULT_TEXT)
   WebElement textReparse;

   @FindBy(xpath = Locators.COMBOBOX)
   WebElement comboBox;

   @FindBy(xpath = Locators.ECLIPSE_FORMATTER_OPTION)
   WebElement eclipseFormat;

   @FindBy(xpath = Locators.EXO_FORMATTER_OPTION)
   WebElement eXoFormat;

   @FindBy(css = Locators.OK_BTN)
   WebElement okBtn;

   /**
    * Wait Formatter opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return view != null && view.isDisplayed();

         }
      });
   }

   /**
    * Wait Eclipse Formatter is selected.
    * 
    * @throws Exception
    */
   public void waitEclipseFormatterIsSelect() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return eclipseFormat.isSelected();
         }
      });
   }

   /**
    * Wait Exo Formatter is selected.
    * 
    * @throws Exception
    */
   public void waitExoFormatterIsSelect() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return eXoFormat.isSelected();
         }
      });
   }

   /**
    * Wait while all sample text in 
    * formatter appearance
    * 
    * @throws Exception
    */
   public void waitRedrawSampleText() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return textReparse != null && textReparse.isDisplayed();

         }
      });
   }

   /**
    * get text from formatter
    */
   public String getFormatterText()
   {
      return textArea.getText();
   }

   /**
    * Wait Formatter opened.
    * 
    * @throws Exception
    */
   public void waitLastTextElement() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return view != null && view.isDisplayed();

         }
      });
   }

   /** expand combobox of the formatter
    * @throws Exception
    */
   public void clickOnCombobox() throws Exception
   {
      comboBox.click();
   }

   /**click on ok button on formmater form 
    * @throws Exception
    */
   public void clickOkBtn() throws Exception
   {
      okBtn.click();
   }

   /**
    * select Exo Format
    * @throws Exception
    */
   public void selectExoFormatter() throws Exception
   {
      //In this method used options select with key events, because select with mouse actions, works incorrect in Chrome browser 
      new Actions(driver()).moveToElement(comboBox).click().build().perform();
      new Actions(driver()).sendKeys(comboBox, Keys.ARROW_DOWN.toString()).build().perform();
      waitExoFormatterIsSelect();
      clickOkBtn();
      IDE().LOADER.waitClosed();
   }

   /**
    * select Eclipse Format
    * @throws Exception
    */
   public void selectEclipseFormatter() throws Exception
   {
      //In this method used options select with key events, because select with mouse actions, works incorrect in Chrome browser
      new Actions(driver()).moveToElement(comboBox).click().build().perform();
      new Actions(driver()).sendKeys(comboBox, Keys.ARROW_UP.toString()).build().perform();
      waitEclipseFormatterIsSelect();
      clickOkBtn();
      IDE().LOADER.waitClosed();
   }

}