/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 2, 2012 5:24:53 PM anya $
 * 
 */
public class SearchResult extends AbstractTestModule
{
   private final class Locators
   {
      public static final String VIEW_LOCATOR = "//div[@view-id='ideSearchResultView']";

      public static final String TREE_PREFIX = "search-";

      public static final String SEARCH_RESULT_TREE = "ideSearchResultItemTreeGrid";

      public static final String TREE_GRID_ID = "ideSearchResultItemTreeGrid";

      public static final String SEARCH_RESULT_SELECTOR = "div#" + SEARCH_RESULT_TREE + " div[id^=" + TREE_PREFIX + "]";
   }

   private final String VIEW_TITLE = "Search";

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.TREE_GRID_ID)
   private WebElement treeGrid;

   /**
    * @throws InterruptedException
    */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
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
      });
   }

   /**
    * Wait Search results view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
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
    * Wait Search results view closed.
    * 
    * @throws Exception
    */
   public void waitItemIsSelected(final WebElement elem) throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
              return elem.findElement(By.cssSelector("div.gwt-TreeItem-selected")).isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public void close()
   {
      IDE().PERSPECTIVE.getCloseViewButton(VIEW_TITLE).click();
   }

   /**
    * Is item present in search results tree.
    * 
    * @param path item's path
    * @return <code>true</code> if item is present.
    * @throws Exception
    */
   public boolean isItemPresent(String path) throws Exception
   {
      try
      {
         return driver().findElement(By.id(getItemId(path))) != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Is item visible in search results tree.
    * 
    * @param path item's path
    * @return <code>true</code> if item is present.
    * @throws Exception
    */
   public boolean isItemVisible(String path) throws Exception
   {
      try
      {
         WebElement item = driver().findElement(By.id(getItemId(path)));
         return (item != null && item.isDisplayed());
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Open item (make double click) in Search results tree.
    * 
    * @param path item's path
    * @throws Exception
    */
   public void openItem(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      item.click();
      new Actions(driver()).doubleClick(item).build().perform();
   }
   
   
   /**
    * Open item (make double click) in Search results tree.
    * 
    * @param path item's path
    * @throws Exception
    */
   public void expandItem(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      item.click();
      new Actions(driver()).doubleClick(item).build().perform();
   }

   /**
    * Select item in Search results tree.
    * 
    * @param path item's path
    * @throws Exception
    */
   public void selectItem(String path) throws Exception
   {
      driver().findElement(By.id(getItemId(path))).click();
   }

   public void typeKeys(String keys)
   {
      new Actions(driver()).sendKeys(treeGrid, keys).build().perform();
   }

   public void typeKeysToItem(String path, String keys) throws Exception
   {
      WebElement elem = driver().findElement(By.id(getItemId(path)));
      elem.sendKeys(keys);
   }

   /**
    * Generate item id
    * 
    * @param path item's name
    * @return id of item
    */
   public String getItemId(String path) throws Exception
   {
      path = (path.startsWith(BaseTest.WS_URL)) ? path.replace(BaseTest.WS_URL, "") : path;
      String itemId = (path.startsWith("/")) ? path : "/" + path;
      itemId = Utils.md5(itemId);
      return Locators.TREE_PREFIX + itemId;
   }
   
   

   /**
    * Generate item id
    * 
    * @param path item's name
    * @return id of item
    */
   public WebElement getWebElem(String path) throws Exception
   {
      path = (path.startsWith(BaseTest.WS_URL)) ? path.replace(BaseTest.WS_URL, "") : path;
      String itemId = (path.startsWith("/")) ? path : "/" + path;
      itemId = Utils.md5(itemId);
      WebElement elem = driver().findElement(By.id(Locators.TREE_PREFIX + itemId));
      return elem;
   }
   

   public void waitForItem(final String path) throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement item = driver().findElement(By.id(getItemId(path)));
               return item != null && item.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   public int getResultCount()
   {
      return driver().findElements(By.cssSelector(Locators.SEARCH_RESULT_SELECTOR)).size();
   }
}
