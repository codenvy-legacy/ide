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
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 17, 2011 2:27:36 PM vereshchaka $
 *
 */
public class CodeAssistant extends AbstractTestModule
{

   public interface Locators
   {
      /**
       * XPath autocompletion panel locator.
       */
      public static final String PANEL_ID = "exo-ide-autocomplete-panel";

      public static final String PANEL = "//table[@id='exo-ide-autocomplete-panel']";

      /**
       * Xpath autocompletion input locator.
       */
      public static final String INPUT = "exo-ide-autocomplete-edit";

      public static final String JAVADOC_DIV = "exo-ide-autocomplete-doc-panel";
   }

   @FindBy(id = Locators.INPUT)
   private WebElement input;

   @FindBy(id = Locators.JAVADOC_DIV)
   private WebElement doc;

   /**
    * Type text to input field of autocompletion form.
    * 
    * @param text - text to type
    * @throws Exception
    */
   public void typeToInput(String text) throws Exception
   {
      input.sendKeys(text);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
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

   /**
    * Check, that element <code>elementTitle</code> is present in autocomplete panel.
    *  
    * @param elementTitle - the title of element
    */
   public void checkElementPresent(String elementTitle)
   {
      assertTrue(selenium().isElementPresent(Locators.PANEL + "//div[text()='" + elementTitle + "']"));
   }

   public void checkElementNotPresent(String elementTitle)
   {
      assertFalse(selenium().isElementPresent(Locators.PANEL + "//div[text()='" + elementTitle + "']"));
   }

   /**
    * Move cursor down
    * @param row Number of rows to move down
    * @throws InterruptedException
    */
   public void moveCursorDown(int row) throws InterruptedException
   {
      for (int i = 0; i < row; i++)
      {
         input.sendKeys(Keys.DOWN.toString());
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
      input.sendKeys(Keys.ESCAPE);
      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.id(Locators.PANEL_ID));
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
    *Press Enter key to close form and paste selected item in to the editor 
    * @throws InterruptedException 
    */
   public void insertSelectedItem() throws InterruptedException
   {
      //RETURN key is used instead of ENTER because 
      //of issue http://code.google.com/p/selenium/issues/detail?id=2180
      input.sendKeys(Keys.RETURN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Open Autocompletion Form
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
            return d.findElement(By.id(Locators.PANEL_ID)) != null;
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
      assertTrue(selenium().isElementPresent(Locators.JAVADOC_DIV));
   }
}
