/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import java.util.List;

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Operations with or in code outline panel.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 22, 2010 $
 * 
 */
public class Outline extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideOutlineView";

      String JAVA_VIEW_ID = "exoJavaOutlineView";

      String JAVA_VIEW_LOCATOR = "//div[@view-id='" + JAVA_VIEW_ID + "']";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String TREE_ID = "ideOutlineTreeGrid";

      String TREE_PREFIX_ID = "outline-";

      String scrollTopLocator =
         "document.getElementById('ideOutlineTreeGrid').parentNode.parentNode.parentNode.scrollTop";

      String HIGHLIGHTER_SELECTOR_OLD = "div#" + TREE_ID + ">div.ide-Tree-item-selected";

      String EXPAND_SELECTOR = "div.cellTreeSelectedItem>div>div>img";

      String HIGHLIGHTER_SELECTOR = "div.cellTreeSelectedItem";

      String ROW_SELECTOR = "div#" + TREE_ID + " div>span";

      String ROW_BY_TITLE_LOCATOR = "//div[@id='" + TREE_ID + "']//div[@class]//span[text()='%s']";

      String BORDER_PREFIX = "//div[@component='Border' and contains(@style, '182, 204, 232')]";

      String HIGHLITER_BORDER = VIEW_LOCATOR + BORDER_PREFIX;

      String HIGHLITER_JAVAOUTLINE_BORDER = JAVA_VIEW_LOCATOR + BORDER_PREFIX;

      String SELECTED_ELEMENT_CSS = "div.cellTreeSelectedItem>div>div>span";

      String INFORMATION_FORM = "//div[@id='information']/ancestor::div[1][contains(@style, 'width: 300')]";

   }

   public static final String NOT_AVAILABLE_TITLE = "An outline is not available.";

   public static final String VIEW_TITLE = "Outline";

   public static final int SELECT_OUTLINE_DELAY = 100; // msec

   private static int LINE_HEIGHT = 26;

   private int OUTLINE_TOP_OFFSET_POSITION = 76;

   public Outline()
   {
      if (driver() instanceof ChromeDriver)
      {
         OUTLINE_TOP_OFFSET_POSITION = 85;
         LINE_HEIGHT = 26;
      }
   }

   public enum TokenType {
      CLASS, METHOD, FIELD, ANNOTATION, INTERFACE, ARRAY, ENUM, CONSTRUCTOR, KEYWORD, TEMPLATE, VARIABLE, FUNCTION,
      /** Property type for JSON */
      PROPERTY,

      /**
       * HTML or XML tag.
       */
      TAG,

      /**
       * HTML or XML attribute;
       */
      ATTRIBUTE, CDATA,

      /** Property type for JavaScript */
      BLOCK,

      /** Property type for Groovy code */
      GROOVY_TAG, PACKAGE, IMPORT, PARAMETER, TYPE,

      /** Property type for Java code */
      JSP_TAG,

      /** Property type for Ruby code **/
      ROOT, MODULE, LOCAL_VARIABLE, GLOBAL_VARIABLE, CLASS_VARIABLE, INSTANCE_VARIABLE, CONSTANT,

      /** Propperty type for Php code **/
      PHP_TAG, CLASS_CONSTANT, NAMESPACE;
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(xpath = Locators.JAVA_VIEW_LOCATOR)
   private WebElement javaView;

   @FindBy(id = Locators.TREE_ID)
   private WebElement tree;

   @FindBy(css = Locators.HIGHLIGHTER_SELECTOR)
   private WebElement highlighter;

   @FindBy(css = Locators.EXPAND_SELECTOR)
   private WebElement expand;

   @FindBy(css = Locators.SELECTED_ELEMENT_CSS)
   private WebElement selectElementSelector;

   @FindBy(xpath = Locators.INFORMATION_FORM)
   private WebElement informationForm;

   /**
    * Wait Outline view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return (informationForm != null && informationForm.isDisplayed() && view != null && view.isDisplayed());
         }
      });
   }

   /**
    * Wait Outline view opened.
    * 
    * @throws Exception
    */
   public void waitHighliterRedraw() throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .cssSelector(Locators.HIGHLIGHTER_SELECTOR)));
   }

   /**
    * Wait while node with sub appearance in outline tree
    * for example: if we open default groovy file in IDE in first node we have:'@ HelloWorld'. 
    * This name in DOM consists of two parts '@' and  'HelloWorld'. 
    * This method may check only one part of the name, only '@' or "Helloworld".
    * @param nameNode
    * @throws Exception
    */
   public void waitNodeWithSubNamePresent(final String nameNode) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.ROW_BY_TITLE_LOCATOR, nameNode))));
   }

   /**
    * Wait while node on  outline tree  is selected
    * 
    * @throws Exception
    */
   public void waitElementIsSelect(final String node) throws Exception
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return selectElementSelector.getText().equals(node);
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Wait Outline view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By
         .xpath(Locators.VIEW_LOCATOR)));
   }

   /**
    * Returns the label of the visible item in Outline tree. Index starts from <code>1</code>.
    * 
    * @param rowNumber
    * @return
    */
   public String getItemLabel(int rowNumber)
   {
      WebElement row = getVisibleItem(rowNumber);
      return (row != null) ? row.getText() : null;
   }

   /**
    * Returns row element by pointed <b>visible</b> row number. Row number starts from <code>1</code>.
    * 
    * @param rowNumber
    * @return
    */
   private WebElement getVisibleItem(int rowNumber)
   {
      List<WebElement> rows = driver().findElements(By.cssSelector(Locators.ROW_SELECTOR));
      if (rows == null || rows.size() <= 0)
         return null;

      int index = 0;

      try
      {
         for (WebElement row : rows)
         {
            if (row.isDisplayed())
            {
               index++;
            }

            if (index == rowNumber)
            {
               return row;
            }
         }
      }
      catch (StaleElementReferenceException se)
      {
         return getVisibleItem(rowNumber);
      }
      return null;
   }

   /**
    * Click on expand icon in outline three
    * @param rowNumber row number
    * @throws Exception
    */
   public void expandSelectItem(int rowNumber) throws Exception
   {

      WebElement exp = driver().findElement(By.cssSelector(Locators.EXPAND_SELECTOR));
      exp.click();
   }

   /**
    * Double click the item at pointed row number. Row number starts from <code>1</code>.
    * 
    * @param rowNumber row number
    * @throws Exception
    */
   public void doubleClickItem(int rowNumber) throws Exception
   {
      WebElement row = getVisibleItem(rowNumber);
      row.findElement(By.xpath("//img[@onload='this.__gwtLastUnhandledEvent=\"load\";']")).click();
      new Actions(driver()).moveToElement(row, 1, 1).doubleClick().build().perform();
   }

   /**
    * Double click the item with pointed label.
    * 
    * @param label
    * @throws Exception
    */
   public void doubleClickItem(String label) throws Exception
   {
      WebElement row = driver().findElement(By.xpath(String.format(Locators.ROW_BY_TITLE_LOCATOR, label)));
      row.click();
      new Actions(driver()).doubleClick(row).build().perform();
   }

   /**
    * Select row in outline tree. Row number starts from <code>1</code>.
    * 
    * @param row - number of row (from 1).
    * @throws Exception
    */
   public void selectRow(int rowNumber) throws Exception
   {
      WebElement row = getVisibleItem(rowNumber);
      row.click();
   }

   /**
    * Wait the visibility state of the Outline view.
    * @throws InterruptedException 
    * 
    */
   public void waitOutlineViewVisible() throws InterruptedException
   {
      //for animation in google chrome
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.VIEW_LOCATOR)));
   }

   /**
    * Wait the invisibility state of the Outline view.
    * 
    */
   public void waitOutlineViewInvisible()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By
         .xpath(Locators.VIEW_LOCATOR)));
   }

   public String getSelectedText() throws Exception
   {
      //need for redra
      Thread.sleep(200);
      return selectElementSelector.getText();
   }

   /**
    * Returns whether item is present in Outline tree.
    * 
    * @param label item's title
    * @throws Exception
    */
   public void waitItemPresent(String label) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.ROW_BY_TITLE_LOCATOR, label))));
   }

   /**
    * Select item in tree.
    * 
    * @param label item's label
    */
   public void selectItem(String label) throws Exception
   {
      WebElement item = driver().findElement(By.xpath(String.format(Locators.ROW_BY_TITLE_LOCATOR, label)));
      item.click();
   }

   /**
    * Wait for Outline tree visibility.
    * 
    * @throws Exception
    */
   public void waitOutlineTreeVisible() throws Exception
   {
      //for animation in google chrome
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.id(Locators.TREE_ID)));
   }

   /**
    * Returns, whether element is present in outline tree.
    * 
    * @param id item's id
    * @return {@link Boolean} <code>true</code> if element is present
    */
   public void waitItemPresentById(String id)
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
   }

   /**
    * Method close Outline codehelper
    * 
    * @throws InterruptedException
    */
   public void closeOutline() throws InterruptedException
   {
      IDE().PERSPECTIVE.getCloseViewButton(VIEW_TITLE).click();
   }

   /**
    * Wait the active state of outline view.
    * 
    * @throws Exception
    */
   public void waitActive() throws Exception
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return IDE().PERSPECTIVE.isViewActive(view);
         }
      });
   }

   /**
    * Wait the not active state of outline view.
    * 
    * @throws Exception
    */
   public void waitNotActive() throws Exception
   {
      new WebDriverWait(driver(), 10).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return !(IDE().PERSPECTIVE.isViewActive(view));
         }
      });
   }

   public enum LabelType {
      NAME("item-name"), TYPE("item-type"), PARAMETER("item-parameter"), MODIFIER("item-modifier");

      private String className;

      LabelType(String className)
      {
         this.className = className;
      }

      @Override
      public String toString()
      {
         return this.className;
      }
   }

   /**
    * Type keys with Outline tree. Is used for navigation (up, down, left or right).
    * 
    * @param keys keys to type
    */
   public void typeKeys(String keys)
   {
      new Actions(driver()).sendKeys(tree, keys).build().perform();
   }

   /**
    * Wait for item to appear at specified position (row number).
    * 
    * @param item item's title
    * @param position position
    */
   public void waitItemAtPosition(final String item, final int position)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return item.equals(getItemLabel(position));
         }
      });
   }

   /**
    * Wait Outline is not available.
    * 
    * @throws Exception
    */
   public void waitNotAvailable() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return NOT_AVAILABLE_TITLE.equals(view.getText().trim());
         }
      });
   }

   /**
    * Returns true if highlight border present
    * 
    */
   public void waitHiglightBorderPresent()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.HIGHLITER_BORDER)));
   }

   /**
    * Returns the visibility state of the Outline view.
    * Only for Java files 
    * 
    */
   public void waitJavaOutlineViewVisible()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.JAVA_VIEW_LOCATOR)));
   }

   /**
    * Returns true if highlight border present
    * 
    */
   public void waitHiglightBorderJavaOutlinePresent()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.HIGHLITER_JAVAOUTLINE_BORDER)));
   }

   /**
    * wait Outline Tree Not Visible
    */
   public void waitOutlineTreeNotVisible()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.id(Locators.TREE_ID)));
   }

}
