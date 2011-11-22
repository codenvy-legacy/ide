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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Operations with or in code outline panel.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class Outline extends AbstractTestModule
{
   interface Locators
   {
      String VIEW_ID = "ideOutlineView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String TREE_ID = "ideOutlineTreeGrid";

      String TREE_PREFIX_ID = "outline-";

      String scrollTopLocator =
         "document.getElementById('ideOutlineTreeGrid').parentNode.parentNode.parentNode.scrollTop";

      String HIGHLIGHTER_SELECTOR = "div#" + TREE_ID + ">div.ide-Tree-item-selected";

      String ROW_BY_INDEX_SELECTOR = "div#" + TREE_ID + " div.gwt-Label:nth(%s)";

      String ROW_SELECTOR = "div#" + TREE_ID + " div.gwt-Label";

      String ROW_BY_TITLE_LOCATOR = "//div[@id='" + TREE_ID + "']//div[@class='gwt-Label' and text()='%s']";
   }

   public static final String VIEW_TITLE = "Outline";

   public static final int SELECT_OUTLINE_DELAY = 100; // msec

   private static final int LINE_HEIGHT = 31;

   private static final int OUTLINE_TOP_OFFSET_POSITION = 80;

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

   @FindBy(id = Locators.TREE_ID)
   private WebElement tree;

   @FindBy(css = Locators.HIGHLIGHTER_SELECTOR)
   private WebElement highlighter;

   /**
    * Wait Outline view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed();
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
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return view == null || !view.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Returns the label of the visible item in Outline tree.
    * Index starts from <code>1</code>.
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
    * Returns row element by pointed <b>visible</b> row number.
    * Row number starts from <code>1</code>. 
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
      return null;
   }

   /**
    * Double click the item at pointed row number.
    * Row number starts from <code>1</code>. 
    * 
    * @param rowNumber row number
    * @throws Exception
    */
   public void doubleClickItem(int rowNumber) throws Exception
   {
      WebElement row = getVisibleItem(rowNumber);
      row.click();
      new Actions(driver()).doubleClick(row).build().perform();
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
    * Select row in outline tree.
    * Row number starts from <code>1</code>.
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
    * Returns the visibility state of the Outline view.
    * 
    * @return {@link Boolean} if <code>true</code> then view is visible
    */
   public boolean isOutlineViewVisible()
   {
      try
      {
         return view != null && view.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Returns the outline's item selection state.
    * Row number starts from <code>1</code>.
    * 
    * @param rowNumber number of the row
    */
   public boolean isItemSelected(int rowNumber)
   {
      int linePositionTop = OUTLINE_TOP_OFFSET_POSITION + (rowNumber - 1) * LINE_HEIGHT;
      return linePositionTop == highlighter.getLocation().y;
   }

   /**
    * Returns whether item is present in Outline tree.
    * 
    * @param label item's title
    * @throws Exception
    */
   public boolean isItemPresent(String label) throws Exception
   {
      try
      {
         WebElement row = driver().findElement(By.xpath(String.format(Locators.ROW_BY_TITLE_LOCATOR, label)));
         return row != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
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
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return tree != null && tree.isDisplayed();
         }
      });
   }

   /**
    * Returns <code>true</code> if Outline tree is present.
    * 
    * @return {@link Boolean} present state of the Outline tree
    */
   public boolean isOutlineTreePresent()
   {
      try
      {
         return tree != null && tree.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Returns, whether element is present in outline tree.
    *  
    * @param id item's id
    * @return {@link Boolean} <code>true</code> if element is present
    */
   public boolean isItemPresentById(String id)
   {
      try
      {
         return driver().findElement(By.id(id)) != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    *  Method close Outline codehelper
    * @throws InterruptedException 
    */
   public void closeOutline() throws InterruptedException
   {
      IDE().PERSPECTIVE.getCloseViewButton(VIEW_TITLE).click();
   }

   /**
    * Returns the active state of outline view.
    * 
    * @return {@link Boolean} active state of outline view
    * @throws Exception
    */
   public boolean isActive() throws Exception
   {
      return IDE().PERSPECTIVE.isViewActive(view);
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
    * Type keys with Outline tree.
    * Is used for navigation (up, down, left or right).
    * 
    * @param keys keys to type
    */
   public void typeKeys(String keys)
   {
      new Actions(driver()).sendKeys(tree, keys).build().perform();
   }
}
