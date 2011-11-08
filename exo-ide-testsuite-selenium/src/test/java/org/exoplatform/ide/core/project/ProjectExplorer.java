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
package org.exoplatform.ide.core.project;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Nov 4, 2011 10:38:30 AM anya $
 *
 */
public class ProjectExplorer extends AbstractTestModule
{
   interface Locators
   {
      String VIEW_LOCATOR = "//div[@view-id='ideTinyProjectExplorerView']";

      String TREE_PREFIX = "navigation-";

      String TREE_GRID_ID = "ideProjectExplorerItemTreeGrid";

      String ROOT_ITEM_LOCATOR = "xpath=(//div[@id='" + TREE_GRID_ID
         + "']//div[@class='ide-Tree-label'])[position()=1]";
   }

   @FindBy(how = How.XPATH, using = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.TREE_GRID_ID)
   private WebElement treeGrid;

   @FindBy(how = How.XPATH, using = Locators.ROOT_ITEM_LOCATOR)
   private WebElement rootItem;

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
               e.printStackTrace();
               return false;
            }
         }
      });
   }

   /**
    * Generate item id 
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
               e.printStackTrace();
               return false;
            }
         }
      });
   }

   public void selectItem(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      item.click();
   }

   public void openItem(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      item.click();

      Actions actions = new Actions(driver());
      actions.doubleClick(item).build().perform();
   }

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
    * Returns current folder's name (root node in Project Explorer).
    * 
    * @return {@link String} name of the current project
    */
   public String getCurrentProject()
   {
      if (treeGrid == null || !treeGrid.isDisplayed() || rootItem != null || !rootItem.isDisplayed())
      {
         return null;
      }
      return rootItem.getText();
   }
}
