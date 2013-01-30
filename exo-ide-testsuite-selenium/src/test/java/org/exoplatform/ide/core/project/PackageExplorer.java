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
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 *
 */
public class PackageExplorer extends AbstractTestModule
{
   // TODO after improvement IDE-2200 change methods for package explorer as it done in project explorer 
   private interface Locators
   {
      String PACKAGE_EXPLORER_ID = "idePackageExplorerTreeGrid";

      String PACKAGE_EXPLORER_ITEM =
         "//div[@id='idePackageExplorerTreeGrid']//div[@class='ide-Tree-label' and text()='%s']";

      String CLOSE_PACKAGE_EXPLORER_BUTTON = "//div[@button-name='close-tab' and @tab-title='Package Explorer']";

      String PACKAGE_EXPLORER_SELECTED_ITEM =
         "//div[@id='idePackageExplorerTreeGrid']//div[@class='gwt-TreeItem gwt-TreeItem-selected']//div[@class='ide-Tree-label' and text()='%s']";

      String PACKAGE_EXPLORER_OPEN_ICON_WITH_SPEC_NAME =
         "//div[@id='idePackageExplorerTreeGrid']//div[@class='ide-Tree-label' and text()='%s']//ancestor::tbody/tr/td//img";

      String TREE_PREFIX = "navigation-";

      String NEW_PACKAGE_ID = "ideCreatePackageView-window";

      String NEW_PACKAGE_INPUT_FIELD = "//div[@id='ideCreatePackageView-window']//input[@type='text']";

      String NEW_PACKAGE_CREATE_BUTTON =
         "//div[@id='ideCreatePackageView-window']//td[@class='imageButtonText' and text()='Create']";

      String CONVENTION_WARNING_MESSAGE_ON_CREATE_NEW_PACKAGE_FORM =
         "(//div[@id='ideCreatePackageView-window']//div[contains(.,'convention')])[last()]";

      String EMPTY_WARNING_MESSAGE_ON_CREATE_NEW_PACKAGE_FORM =
         "(//div[@id='ideCreatePackageView-window']//div[contains(.,'must not be empty')])[last()]";

      String CONTEXT_MENU_ID = "eXoIDEContextMenu";

      String ELEMENT_IN_CONTEXT_MENU_BY_NAME = "//div[@id='eXoIDEContextMenu']//nobr[text()='%s']";

      String ELEMENT_IN_CONTEXT_MENU_BY_NAME_FOR_CHECKING_STATE =
         "//div[@id='eXoIDEContextMenu']//nobr[text()='%s']/../..";

      String ENABLED_ATTRIBUTE = "item-enabled";

      String LINK_WITH_EDITOR_BUTTON = "//div[@class='exoIconButtonPanel' and @title='Link with Editor']/img";

      String TAB_SCROLLER_RIGHT = "//div[@class='tabPanelScrollerRight']";
   }

   @FindBy(id = Locators.PACKAGE_EXPLORER_ID)
   private WebElement packageExplorerId;

   @FindBy(xpath = Locators.CLOSE_PACKAGE_EXPLORER_BUTTON)
   private WebElement closePackageExplorerButton;

   @FindBy(id = Locators.NEW_PACKAGE_ID)
   private WebElement newPackageId;

   @FindBy(xpath = Locators.NEW_PACKAGE_INPUT_FIELD)
   private WebElement newPackageInputField;

   @FindBy(xpath = Locators.NEW_PACKAGE_CREATE_BUTTON)
   private WebElement newPackageCreateButton;

   @FindBy(id = Locators.CONTEXT_MENU_ID)
   private WebElement contextMenuId;

   @FindBy(xpath = Locators.LINK_WITH_EDITOR_BUTTON)
   private WebElement linkWithEditorBtn;

   @FindBy(xpath = Locators.TAB_SCROLLER_RIGHT)
   private WebElement scrollerTabRight;

   /**
    * Wait and close Package Explorer
    * @throws Exception 
    */
   public void waitAndClosePackageExplorer() throws Exception
   {
      waitPackageExplorerOpened();
      IDE().LOADER.waitClosed();
      closePackageExplorer();
      IDE().PROGRESS_BAR.waitProgressBarControlClose();
   }

   /**
    * Close Package Explorer
    * 
    * if prj name too long clicks
    */
   public void closePackageExplorer() throws Exception
   {
      if (scrollerTabRight.isDisplayed() && scrollerTabRight != null)
      {
         scrollerTabRight.click();
         //sleep for animation
         Thread.sleep(2000);
         closePackageExplorerButton.click();
      }
      else
      {
         closePackageExplorerButton.click();
      }

   }

   /**
    * Wait for Package Explorer
    * @throws Exception 
    */
   public void waitPackageExplorerOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOf(packageExplorerId));
      IDE().PROGRESS_BAR.waitProgressBarControlClose();
      IDE().LOADER.waitClosed();
   }

   /**
    * Wait for closing Package Explorer
    */
   public void waitPackageExplorerClosed()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.PACKAGE_EXPLORER_ID)));
   }

   /**
    * wait content in Package Explorer
    * 
    * @param path
    * @throws Exception
    */
   public void waitForItem(String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.id(getItemId(path))));
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
    * Wait item with specified name in  Package Explorer tree
    */
   public void waitItemInPackageExplorer(String item)
   {
      new WebDriverWait(driver(), 20).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.PACKAGE_EXPLORER_ITEM, item))));
   }

   /**
    * Wait item with specified name in  Package Explorer tree
    */
   public void waitItemInPackageIsSelected(String item)
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         Locators.PACKAGE_EXPLORER_SELECTED_ITEM, item))));
   }

   /**
    * open item with specified name by double click
    * @param item
    */
   public void openItemWithDoubleClick(String item)
   {
      WebElement node = driver().findElement(By.xpath(String.format(Locators.PACKAGE_EXPLORER_ITEM, item)));
      node.click();
      waitItemInPackageIsSelected(item);
      new Actions(driver()).doubleClick(node).build().perform();
   }

   /**
    * click on icon '+' with specified name in tree
    * @param item
    */
   public void openItemWithClickOnOpenIcon(String item)
   {
      driver().findElement(By.xpath(String.format(Locators.PACKAGE_EXPLORER_OPEN_ICON_WITH_SPEC_NAME, item))).click();
   }

   /**
    * select item with specified name
    * @param item
    */
   public void selectItemInPackageExplorer(String item)
   {
      WebElement node = driver().findElement(By.xpath(String.format(Locators.PACKAGE_EXPLORER_ITEM, item)));
      node.click();
      waitItemInPackageIsSelected(item);
   }

   /**
    * Wait item with specified name in  Package Explorer is not present
    */
   public void waitItemInPackageExplorerIsNotPresent(String item)
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(String.format(
         Locators.PACKAGE_EXPLORER_ITEM, item))));
   }

   /**
    * Wait create new package form
    */
   public void waitCreateNewPackageForm()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .id(Locators.NEW_PACKAGE_ID)));
   }

   /**
    * New package input
    */
   public void typeNewPackageName(String packageName)
   {
      newPackageInputField.sendKeys(packageName);
   }

   /**
    * New package create button
    * @throws Exception 
    */
   public void clickCreateNewPackageButton() throws Exception
   {
      newPackageCreateButton.click();
      IDE().LOADER.waitClosed();
   }

   /**
    * wait for convention warning.
    */
   public void waitConventionWarningInCreatePackageForm()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.CONVENTION_WARNING_MESSAGE_ON_CREATE_NEW_PACKAGE_FORM)));
   }

   /**
    * wait for empty name field warning.
    */
   public void waitEmptyNameFieldWarningInCreatePackageForm()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.EMPTY_WARNING_MESSAGE_ON_CREATE_NEW_PACKAGE_FORM)));
   }

   /**
    * context menu on item with specified name
    * @param item
    */
   public void openContextMenuOnSelectedItemInPackageExplorer(String item)
   {
      WebElement node = driver().findElement(By.xpath(String.format(Locators.PACKAGE_EXPLORER_ITEM, item)));
      Actions act = new Actions(driver());
      Action rClick = act.contextClick(node).build();
      rClick.perform();
      waitContextMenu();
   }

   /**
    * wait for context menu.
    */
   public void waitContextMenu()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .id(Locators.CONTEXT_MENU_ID)));
   }

   /**
    * wait for context menu disappear.
    */
   public void waitContextMenuDisappear()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By
         .id(Locators.CONTEXT_MENU_ID)));
   }

   /**
    * click on element from context menu by name
    *
    * @param item
    */
   public void clickOnItemInContextMenu(String item)
   {
      driver().findElement(By.xpath(String.format(Locators.ELEMENT_IN_CONTEXT_MENU_BY_NAME, item))).click();
   }

   /**
    * Wait for enabled state of the context menu command. 
    * 
    * @param itemName command name
    */
   public void waitElementInContextMenuEnabled(final String itemName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement command =
                  driver().findElement(
                     By.xpath(String.format(Locators.ELEMENT_IN_CONTEXT_MENU_BY_NAME_FOR_CHECKING_STATE, itemName)));
               return Boolean.parseBoolean(command.getAttribute(Locators.ENABLED_ATTRIBUTE));
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait for disabled state of the context menu command. 
    * 
    * @param itemName command name
    */
   public void waitElementInContextMenuDisabled(final String itemName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement command =
                  driver().findElement(
                     By.xpath(String.format(Locators.ELEMENT_IN_CONTEXT_MENU_BY_NAME_FOR_CHECKING_STATE, itemName)));
               return !(Boolean.parseBoolean(command.getAttribute(Locators.ENABLED_ATTRIBUTE)));
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Type keys to tree grid
    * @param keys
    */

   public void typeKeys(String keys)
   {
      new Actions(driver()).sendKeys(packageExplorerId, keys).build().perform();
   }

   /**
    * click on Link with Editor button
    */
   public void clickOnLinkWithEditorButton()
   {
      linkWithEditorBtn.click();
   }
}