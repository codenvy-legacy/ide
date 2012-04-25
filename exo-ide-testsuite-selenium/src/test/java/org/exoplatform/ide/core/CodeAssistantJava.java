/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 17, 2011 2:27:36 PM vereshchaka $
 * 
 */
public class CodeAssistantJava extends AbstractTestModule
{

   /**
    * XPath autocompletion panel locator.
    */
   private static final String PANEL_ID = "exo-ide-autocomplete-panel";

   private static final String PANEL = "//div[@id='exo-ide-autocomplete-panel']";

   private static final String ELEMENT_LOCATOR = PANEL + "//div[text()='%s']";

   private static final String SUBSTITUTE_ELEMENT = PANEL + "//div[text()='%s']";
   
   private static final String AUTOCOMPLETE_PROPOSAL = "//div[@class='gwt-HTML' and text()='%s']";

   /**
    * Xpath autocompletion input locator.
    */
   private static final String INPUT = "exo-ide-autocomplete-edit";

   private static final String JAVADOC_DIV = "exo-ide-autocomplete-doc-panel";

   private static final String IMPORT_PANEL_ID = "ideAssistImportDeclarationForm";

   @FindBy(xpath = PANEL)
   private WebElement panel;

   @FindBy(id = INPUT)
   private WebElement input;

   @FindBy(id = JAVADOC_DIV)
   private WebElement doc;

   @FindBy(id = IMPORT_PANEL_ID)
   private WebElement importPanel;

   /**
    * Type text to input field of autocompletion form.
    * 
    * @param text - text to type
    * @throws Exception
    */
   public void typeToInput(String text) throws Exception
   {
      IDE().INPUT.typeToElement(input, text);
   }

   /**
    * Type to input with cleaning previous value.
    * 
    * @param text text to type
    * @param clearInput if <code>true</code> - clear input befor type
    * @throws Exception
    */
   public void typeToInput(String text, boolean clearInput) throws Exception
   {
      if (clearInput)
         clearInput();
      typeToInput(text);
   }

   // /**
   // * Check, that element <code>elementTitle</code> is present in autocomplete panel.
   // *
   // * @param elementTitle - the title of element
   // */
   // @Deprecated
   // public void checkElementPresent(String elementTitle)
   // {
   //
   // assertTrue(selenium().isElementPresent(PANEL + "//div[text()='" + elementTitle + "']"));
   // }

   public boolean isElementPresent(String elementTitle)
   {
      try
      {
         WebElement element = driver().findElement(By.xpath(String.format(ELEMENT_LOCATOR, elementTitle)));
         return element != null && element.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   public String[] getFormProposalsText()
   {
      List<WebElement> elements =
         driver().findElements(By.cssSelector("div#exo-ide-autocomplete-panel div>div> table"));
      String[] texts = new String[elements.size()];
      for (int i = 0; i < elements.size(); i++)
      {
         WebElement el = elements.get(i);
         String elementText = el.getText();
         if (elementText.contains("\n"))
         {
            texts[i] = elementText.split("\n")[1];
         }
         else
            texts[i] = elementText;
      }

      return texts;
   }

   public void checkElementNotPresent(String elementTitle)
   {
      assertFalse(selenium().isElementPresent(PANEL + "//div[text()='" + elementTitle + "']"));
   }

   /**
    * Move cursor down
    * 
    * @param row Number of rows to move down
    * @throws InterruptedException
    */
   public void moveCursorDown(int row) throws InterruptedException
   {
      for (int i = 0; i < row; i++)
      {
         selectProposalPanel();
         new Actions(driver()).sendKeys(Keys.DOWN.toString()).build().perform();
         //input.sendKeys(Keys.DOWN.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }

   
   
   /**
    * Move cursor down
    * 
    * @param row Number of rows to move down
    * @throws InterruptedException
    */
   public void moveCursorUp(int row) throws InterruptedException
   {
      for (int i = 0; i < row; i++)
      {
         selectProposalPanel();
         new Actions(driver()).sendKeys(Keys.UP.toString()).build().perform();
         //input.sendKeys(Keys.DOWN.toString());
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
   }
   
   
   
   /**
    * Clear input field of Autocompletion form
    */
   public void clearInput()
   {
      input.clear();
   }

   /**
    * Close codeasstant by press Escape Button
    */
   public void closeForm()
   {
      selectProposalPanel();
      new Actions(driver()).sendKeys(Keys.ESCAPE).build().perform();
      (new WebDriverWait(driver(), 5)).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.id(PANEL_ID));
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
    * Click to the proposal panel
    */
   public void selectProposalPanel()
   {
      driver().findElement(By.id(PANEL_ID)).click();
   }

   /**
    * Press Enter key to close form and paste selected item in to the editor
    * 
    * @throws InterruptedException
    */
   public void insertSelectedItem() throws InterruptedException
   {
      // RETURN key is used instead of ENTER because
      // of issue http://code.google.com/p/selenium/issues/detail?id=2180
      new Actions(driver()).sendKeys(Keys.RETURN.toString()).build().perform();
      //input.sendKeys(Keys.RETURN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   

   
   /**
    * double click on selected item
    * in java autocomplete form
    * @param parameter
    * @throws InterruptedException
    */
   public void doudleClickSelectedItem(String parameter) throws InterruptedException
   {
      // RETURN key is used instead of ENTER because
      // of issue http://code.google.com/p/selenium/issues/detail?id=2180
      
      WebElement prop = driver().findElement(By.xpath(String.format(AUTOCOMPLETE_PROPOSAL, parameter)));
      new Actions (driver()).doubleClick(prop).build().perform();
      //input.sendKeys(Keys.RETURN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   
   
   
   /**
    * Open Autocompletion Form
    * 
    * @throws Exception
    */
   public void openForm() throws Exception
   {
      IDE().EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SPACE);
      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            return d.findElement(By.id(PANEL_ID)) != null;
         }
      });
   }

   public void waitForDocPanelOpened()
   {
      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver d)
         {
            return doc != null && doc.isDisplayed();
         }
      });

   }

   public String getDocPanelText()
   {
      return doc.getText();
   }

   public void checkDocFormPresent()
   {
      assertTrue(selenium().isElementPresent(JAVADOC_DIV));
   }

   public void clickOnLineNumer(int num)
   {
      driver().findElement(By.xpath("//div[@class='CodeMirror-line-numbers']/div[contains(text(), '" + num + "')]"))
         .click();
   }

   public void waitForImportAssistForOpened()
   {
      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            return importPanel != null && importPanel.isDisplayed();
         }
      });
   }

   public void waitForInput()
   {
      (new WebDriverWait(driver(), 5)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            return input != null && input.isDisplayed();
         }
      });
   }

   public void setFocusTInput()
   {
      if (input != null && input.isDisplayed())
         input.click();
   }

   /**
    * @param name
    * @throws InterruptedException
    */
   public void selectImportProposal(String name) throws InterruptedException
   {
      WebElement im = driver().findElement(By.xpath("//div[@class='gwt-Label' and contains(text(),'" + name + "')]"));
      Actions a = new Actions(driver());
      im.click();
      Action sel = a.doubleClick(im).build();
      sel.perform();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * @param name
    * @throws InterruptedException
    */
   public void selectProposal(String name, String afterDash) throws InterruptedException
   {
      WebElement im =
         driver().findElement(
            By.xpath("//div[@class='gwt-HTML' and contains(text(),'" + name + "')]/span[contains(text(),'" + afterDash
               + "')]"));
      Actions a = new Actions(driver());
      im.click();
      Action sel = a.doubleClick(im).build();
      sel.perform();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * 
    */
   public void waitForJavaToolingInitialized(String fileName)
   {
      final String title = "Initialize Java tooling for " + fileName;
      new WebDriverWait(driver(), 1000).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            WebElement element =
               driver.findElement(By.xpath("//div[@control-id='__request-notification-control' and @title='" + title
                  + "']"));
            return !element.isDisplayed();
         }
      });
   }
}
