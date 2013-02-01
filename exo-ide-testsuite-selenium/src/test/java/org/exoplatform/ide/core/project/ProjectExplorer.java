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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 4, 2011 10:38:30 AM anya $
 * 
 */
public class ProjectExplorer extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_LOCATOR = "//div[@view-id='ideTinyProjectExplorerView']";

      String TREE_PREFIX = "navigation-";

      String TREE_GRID_ID = "ideProjectExplorerItemTreeGrid";

      String PROJECTS_LIST_GRID_ID = "ideProjectExplorerProjectsListGrid";

      String ROOT_ITEM_SELECTOR = "div#" + TREE_GRID_ID + " div.ide-Tree-label:first-of-type";

      String PROJECT_ROW_LOCATOR = "//table[@id=\"" + PROJECTS_LIST_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";

      String PROJECT_ROW_SELECTOR = "table#" + PROJECTS_LIST_GRID_ID + ">tbody:first-of-type tr";

      String OPEN_CLOSE_BUTTON_LOCATOR = "//div[@id='%s']/table/tbody/tr/td[1]/img";

      String PROJECT_LIST_GRID_ITEM = "//table[@id='ideProjectExplorerProjectsListGrid']//div[@style and text()='%s']";

      String CLOSE_EXPLORER_BUTTON = "//div[@button-name='close-tab' and @tab-title='%s']";

      String BORDER_PREFIX = "//div[@component='Border' and contains(@style, '182, 204, 232')]";

      String HIGHLITER_BORDER = VIEW_LOCATOR + BORDER_PREFIX;

   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.TREE_GRID_ID)
   private WebElement treeGrid;

   @FindBy(id = Locators.PROJECTS_LIST_GRID_ID)
   private WebElement projectsListGrid;

   @FindBy(css = Locators.ROOT_ITEM_SELECTOR)
   private WebElement rootItem;

   /**
    * @throws InterruptedException
    */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 160).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.VIEW_LOCATOR)));
   }

   /**
    * Returns the active state of the view.
    * 
    * @return {@link Boolean} view's active state
    */
   public boolean isActive()
   {
      return IDE().PERSPECTIVE.isViewActive(view);
   }

   /**
    * Generate item id
    * 
    * @param path
    *            item's name
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
    * wait content in Project tree
    * 
    * @param path
    * @throws Exception
    */
   public void waitForItem(final String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.id(getItemId(path))));
      IDE().PROGRESS_BAR.waitProgressBarControlClose();
   }

   /**
    * wait content in project (name file or folder)
    * 
    * @param gridItem
    * @throws Exception
    */
   public void waitForItemInProjectList(final String gridItem) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.PROJECT_LIST_GRID_ITEM, gridItem))));
   }

   /**
    * wait disappear item in project tree
    * 
    * @param path
    * @throws Exception
    */
   public void waitForItemNotPresent(final String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.id(getItemId(path))));
   }

   /**
    * wait disappear progressor image on select folder
    * 
    * @param path
    * @throws Exception
    */
   public void waitUpdateContentInFolder(final String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               String stateImgProgessor = getImageAttributeFromContent(path);
               return stateImgProgessor.startsWith("url(\"data:image/png;");
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   public void waitForItemNotVisible(final String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.id(getItemId(path))));
   }

   /**
    * Select item in project explorer view.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void selectItem(String path) throws Exception
   {

      driver().findElement(By.xpath("//div[@id='" + getItemId(path) + "']//div[@class='ide-Tree-label']")).click();
      waitHiglightBorderPresent();
   }

   /**
    * Select item in project explorer view by right mouse click.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void selectItemByRightClick(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      new Actions(driver()).contextClick(item).perform();
   }

   /**
    * Open item (make double click) in Project explorer tree.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void openItem(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      item.click();
      new Actions(driver()).doubleClick(item).build().perform();
   }

   /**
    *wait item present in project explorer tree.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void waitItemPresent(String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.id(getItemId(path))));
   }

   /**
    *wait item not present in project explorer tree.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void waitItemNotPresent(String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.id(getItemId(path))));
   }

   /**
    * wait item visibility state in project explorer tree.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void waitItemVisible(String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.id(getItemId(path))));
   }

   /**
    * wait item invisibility state in project explorer tree.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void waitItemNotVisible(String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.id(getItemId(path))));
   }

   /**
    * Press right arrow for expand item
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void expandItem(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      item.click();
      item.sendKeys(Keys.ARROW_RIGHT);
   }

   /**
    * Click open/close(+/-) button of the pointed item.
    * 
    * @param path
    *            item's path
    * @throws Exception
    */
   public void clickOpenCloseButton(String path) throws Exception
   {
      WebElement button =
         driver().findElement(By.xpath(String.format(Locators.OPEN_CLOSE_BUTTON_LOCATOR, getItemId(path))));
      button.click();
   }

   /**
    * Returns current folder's name (root node in Project Explorer).
    * 
    * @return {@link String} name of the current project
    */
   public String getCurrentProject()
   {
      if (treeGrid == null || !treeGrid.isDisplayed() || rootItem == null || !rootItem.isDisplayed())
      {
         return null;
      }
      return rootItem.getText();
   }

   public void typeKeys(String keys)
   {
      new Actions(driver()).sendKeys(treeGrid, keys).build().perform();
   }

   /**
    * send your keys commands to item in project explorer
    * 
    * @param keys
    * @param item
    * @throws Exception
    */
   public void typeKeysToItem(String item, String keys) throws Exception
   {
      WebElement elem =
         driver().findElement(By.xpath("//*[@id='" + getItemId(item) + "']//div[@class='ide-Tree-label']"));
      elem.sendKeys(keys);
   }

   /**
    * click on tab with name of the project
    * 
    * @param nameProject
    */
   public void selectProjectTab(String nameProject)
   {
      IDE().PERSPECTIVE.selectTabsOnExplorer(nameProject);
   }

   /**
    * wait the visibility state of the projects list grid.
    * 
    */
   public void waitProjectsListGridVisible()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .id(Locators.PROJECTS_LIST_GRID_ID)));
   }

   /**
    * wait the invisibility state of the projects list grid.
    * 
    */
   public void waitProjectsListGridNotVisible()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.PROJECTS_LIST_GRID_ID)));
   }

   /**
    * Get the number of projects in grid.
    * 
    * @return count of projects
    */
   public int getProjectsCountInProjectsListGrid()
   {
      return driver().findElements(By.cssSelector(Locators.PROJECT_ROW_SELECTOR)).size();
   }

   /**
    * Select the row with project by the pointed name.
    * 
    * @param name
    *            project name
    */
   public void selectProjectByNameInProjectsListGrid(String name)
   {
      WebElement projectRow = driver().findElement(By.xpath(String.format(Locators.PROJECT_ROW_LOCATOR, name)));
      projectRow.click();
   }

   /**
    * Open context menu.
    */
   public void openContextMenu()
   {
      new Actions(driver()).contextClick(view);
   }

   /**
    * get image attribute and return string from current folder
    * 
    * @throws Exception
    */
   public String getImageAttributeFromContent(String path) throws Exception
   {
      WebElement imgElem =
         driver().findElement(By.xpath("//div[@id='" + getItemId(path) + "']" + "/table/tbody/tr/td[2]//img"));
      return imgElem.getCssValue("background");
   }

   /**
    * close ProjectExplorer
    * 
    * @param projectName
    * @throws Exception
    */
   public void clickCloseProjectExplorer(String projectName) throws Exception
   {
      WebElement closeBtn = driver().findElement(By.xpath(String.format(Locators.CLOSE_EXPLORER_BUTTON, projectName)));
      closeBtn.click();
   }

   /**
    * Wait true if highlight border present
    * 
    */
   public void waitHiglightBorderPresent()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.HIGHLITER_BORDER)));
   }

}
