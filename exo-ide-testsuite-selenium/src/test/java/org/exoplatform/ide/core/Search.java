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

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Search component.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Nov 22, 2011 4:56:23 PM anya $
 *
 */
public class Search extends AbstractTestModule
{
   private static final String TREE_PREFIX_ID = "search-";

   private interface Locators
   {
      String VIEW_ID = "ideSearchView";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String SEARCH_RESULTS_VIEW_ID = "ideSearchResultView";

      String SEARCH_RESULTS_VIEW_LOCATOR = "//div[@view-id='" + SEARCH_RESULTS_VIEW_ID + "']";

      String PATH_FIELD_ID = "ideSearchFormPathField";

      String CONTAINING_TEXT_FIELD_ID = "ideSearchFormContentField";

      String MIME_TYPE_FIELD_ID = "ideSearchFormMimeTypeField";

      String SEARCH_BUTTON_ID = "ideSearchFormSearchButton";

      String CANCEL_BUTTON_ID = "ideSearchFormCancelButton";

      String SEARCH_RESULT_TREE = "ideSearchResultItemTreeGrid";

      String SEARCH_RESULT_SELECTOR = "div#" + SEARCH_RESULT_TREE + " div[id^=" + TREE_PREFIX_ID + "]";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement performSearchView;

   @FindBy(xpath = Locators.SEARCH_RESULTS_VIEW_LOCATOR)
   private WebElement searchResultsView;

   @FindBy(name = Locators.PATH_FIELD_ID)
   private WebElement pathField;

   @FindBy(name = Locators.CONTAINING_TEXT_FIELD_ID)
   private WebElement containingTextField;

   @FindBy(name = Locators.MIME_TYPE_FIELD_ID)
   private WebElement mimeTypeField;

   @FindBy(id = Locators.SEARCH_BUTTON_ID)
   private WebElement searchButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = Locators.SEARCH_RESULT_TREE)
   private WebElement resultTree;

   /**
    * Wait Perform search view opened.
    * 
    * @throws Exception
    */
   public void waitPerformSearchOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement view = input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return (view != null && view.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait Perform search view closed.
    * 
    * @throws Exception
    */
   public void waitPerformSearchClosed() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.VIEW_LOCATOR));
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
    * Returns opened state of the perform search view.
    * 
    * @return {@link Boolean} opened state
    * @throws Exception
    */
   public boolean isPerformSearchOpened() throws Exception
   {
      try
      {
         return performSearchView != null && performSearchView.isDisplayed() && pathField != null
            && pathField.isDisplayed() && containingTextField != null && containingTextField.isDisplayed()
            && mimeTypeField != null && mimeTypeField.isDisplayed() && searchButton != null
            && searchButton.isDisplayed() && cancelButton != null && cancelButton.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Get the value(content) of path field.
    * 
    * @return String path field's value
    */
   public String getPathValue()
   {
      return IDE().INPUT.getValue(pathField);
   }

   /**
    * Get the value(content) of containing text field.
    * 
    * @return String containing text field's value
    */
   public String getContainingTextValue()
   {
      return IDE().INPUT.getValue(containingTextField);
   }

   /**
    * Get the value(content) of Mime type field.
    * 
    * @return String Mime type field's value
    */
   public String getMimeTypeValue()
   {
      return IDE().INPUT.getValue(mimeTypeField);
   }

   /**
    * Type text into path field.
    * 
    * @param value value to type
    * @throws InterruptedException 
    */
   public void setPathValue(String value) throws InterruptedException
   {
      IDE().INPUT.typeToElement(pathField, value, true);
   }

   /**
    * Type text into containing text field.
    * 
    * @param value value to type
    * @throws InterruptedException 
    */
   public void setContainingTextValue(String value) throws InterruptedException
   {
      IDE().INPUT.typeToElement(containingTextField, value, true);
   }

   /**
    * Type text in Mime type field.
    * 
    * @param value value to type
    * @throws InterruptedException 
    */
   public void setMimeTypeValue(String value) throws InterruptedException
   {
      IDE().INPUT.typeToElement(mimeTypeField, value, true);
   }

   /**
    * Click on Search button.
    */
   public void clickSearchButton()
   {
      searchButton.click();
   }

   /**
    * Click on Cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Performs search from clicking the control to showing the results panel.
    * 
    * @param checkPath check path of the search
    * @param text text to search
    * @param mimeType Mime type
    * @throws Exception
    */
   public void performSearch(String checkPath, String text, String mimeType) throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.File.SEARCH);
      waitPerformSearchOpened();

      assertEquals(checkPath, getPathValue());
      setContainingTextValue(text);
      setMimeTypeValue(mimeType);
      clickSearchButton();

      waitPerformSearchClosed();
   }

   /**
    * Wait Search results view opened.
    * 
    * @throws Exception
    */
   public void waitSearchResultsOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return (searchResultsView != null && searchResultsView.isDisplayed() && resultTree != null && resultTree.isDisplayed());
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait Search results view closed.
    * 
    * @throws Exception
    */
   public void waitSearchResultsClosed() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.SEARCH_RESULTS_VIEW_LOCATOR));
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
    * Get the number of found results.
    * 
    * @return number of the found results
    */
   public int getResultsCount()
   {
      return driver().findElements(By.cssSelector(Locators.SEARCH_RESULT_SELECTOR)).size();
   }

   /**
    * Generate item id in search tree 
    * @param href of item 
    * @return id of item
    */
   public String getItemId(String href) throws Exception
   {
      return TREE_PREFIX_ID + Utils.md5(href);
   }

   public void doubleClickOnFile(String fileURL) throws Exception
   {
      String locator = "//div[@id='" + getItemId(fileURL) + "']/div/table/tbody/tr/td[2]";

      selenium().mouseDown(locator);
      selenium().mouseUp(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().doubleClick(locator);
      IDE().EDITOR.waitTabPresent(0);
   }
}
