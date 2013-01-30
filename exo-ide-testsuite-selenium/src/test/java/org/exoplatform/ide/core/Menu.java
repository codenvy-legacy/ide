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
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Menu extends AbstractTestModule
{

   private interface Locators
   {
      String LOCK_LAYER_CLASS = "exo-lockLayer";

      String TOP_MENU_ITEM_LOCATOR = "//div[@class='exo-menuBar']//td[@class='exo-menuBarItem' and text()='%s']/.";

      String TOP_MENU_ITEM_DISABLED_LOCATOR = "//td[@class='exo-menuBarItemDisabled' and text()='%s']";

      String MENU_ITEM_LOCATOR = "//td[contains(@class,'exo-popupMenuTitleField')]//nobr[text()='%s']/..";

      String POPUP_SELECTOR = "//div[@class='exo-popupMenuMain']/table/tbody/tr[@item-enabled][last()]";

      String MENU_ITEM_ROW_LOCATOR = "//table[@class='exo-popupMenuTable']//tr[contains(., '%s')]";

      String ENABLED_ATTRIBUTE = "item-enabled";
   }

   @FindBy(className = Locators.LOCK_LAYER_CLASS)
   private WebElement lockLayer;

   /**
    * Run command from top menu.
    * 
    * @param topMenuName name of menu
    * @param commandName command name
    */
   public void runCommand(String topMenuName, String commandName) throws Exception
   {
      waitCommandEnabled(topMenuName, commandName);
      //Call top menu command:
      WebElement topMenuItem =
         driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, topMenuName)));
      topMenuItem.click();
      waitMenuPopUp();
      //Call command from menu popup:
      WebElement menuItem = driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, commandName)));
      menuItem.click();
      waitMenuPopUpClosed();
   }

   /**
    * Run command from top menu.
    * 
    * @param topMenuName name of menu
    * @param commandName command name
    */
   public void clickOnCommand(String topMenuName) throws Exception
   {
      //Call top menu command:
      WebElement topMenuItem =
         driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, topMenuName)));
      topMenuItem.click();
      waitMenuPopUp();
   }

   /**
    * Run command from sub menu.
    * 
    * @param menuName
    * @param commandName
    * @param subCommandName
    * @throws Exception
    */
   public void runCommand(String menuName, String commandName, String subCommandName) throws Exception
   {
      waitSubCommandEnabled(menuName, commandName, subCommandName);
      //Call top menu command:
      WebElement topMenuItem = driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, menuName)));
      topMenuItem.click();
      waitMenuPopUp();

      //Call command from menu popup:
      WebElement menuItem = driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, commandName)));
      menuItem.click();

      //Call sub menu command
      waitForMenuItemPresent(subCommandName);
      WebElement subMenuItem =
         driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, subCommandName)));
      subMenuItem.click();
      waitMenuPopUpClosed();
   }

   /**
    * Wait visibility state of the menu command.
    * 
    * @param topMenuName mane of menu
    * @param commandName command name
    * @throws Exception
    */

   public void waitCommandVisible(final String topMenuName, final String commandName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            WebElement topMenuItem =
               driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, topMenuName)));
            topMenuItem.click();
            waitMenuPopUp();

            try
            {
               return driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, commandName))) != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
            finally
            {
               topMenuItem.click();
            }
         }
      });
   }

   /**
    * Wait visibility state of the menu command.
    * 
    * @param topMenuName mane of menu
    * @param commandName command name
    * @throws Exception
    */

   public void waitCommandInvisible(final String topMenuName, final String commandName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            WebElement topMenuItem =
               driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, topMenuName)));
            topMenuItem.click();
            waitMenuPopUp();

            try
            {
               return driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, commandName))) != null;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
            finally
            {
               topMenuItem.click();
            }
         }
      });
   }

   /**
    * Wait for menu popup to draw.
    */
   protected void waitMenuPopUp()
   {
      new WebDriverWait(driver(), 10).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.POPUP_SELECTOR)));
   }

   /**
    * Wait for menu popup to close.
    */
   protected void waitMenuPopUpClosed()
   {
      new WebDriverWait(driver(), 10).until(ExpectedConditions.invisibilityOfElementLocated(By
         .xpath(Locators.POPUP_SELECTOR)));
   }

   /**
    * Wait for enabled state of the menu command. 
    * 
    * @param topMenuName top menu command name
    * @param commandName command name
    */
   public void waitCommandEnabled(final String topMenuName, final String commandName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            WebElement topMenuItem =
               driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, topMenuName)));
            topMenuItem.click();
            waitMenuPopUp();
            try
            {
               WebElement command =
                  driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_ROW_LOCATOR, commandName)));
               return Boolean.parseBoolean(command.getAttribute(Locators.ENABLED_ATTRIBUTE));
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
            finally
            {
               topMenuItem.click();
            }
         }
      });
   }

   /**
    * Wait for disabled state of the menu command. 
    * 
    * @param topMenuName top menu command name
    * @param commandName command name
    */
   public void waitCommandDisabled(final String topMenuName, final String commandName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            WebElement topMenuItem =
               driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, topMenuName)));
            topMenuItem.click();
            waitMenuPopUp();
            try
            {
               WebElement command =
                  driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_ROW_LOCATOR, commandName)));
               return !(Boolean.parseBoolean(command.getAttribute(Locators.ENABLED_ATTRIBUTE)));
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
            finally
            {
               topMenuItem.click();
            }
         }
      });
   }

   /**
    * Wait for disabled state of the menu sub command. 
    * 
    * @param topMenuName top menu command name
    * @param commandName command name
    */
   public void waitSubCommandDisabled(final String menuName, final String commandName, final String subCommandName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            WebElement topMenuItem =
               driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, menuName)));
            topMenuItem.click();
            waitMenuPopUp();
            //Call command from menu popup:
            WebElement menuItem =
               driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, commandName)));
            menuItem.click();
            try
            {
               WebElement command =
                  driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_ROW_LOCATOR, subCommandName)));
               return !(Boolean.parseBoolean(command.getAttribute(Locators.ENABLED_ATTRIBUTE)));
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
            finally
            {
               try
               {
                  Thread.sleep(10000);
               }
               catch (InterruptedException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               topMenuItem.click();
            }
         }
      });
   }

   /**
    * Wait for disabled state of the menu sub command. 
    * 
    * @param topMenuName top menu command name
    * @param commandName command name
    */
   public void waitSubCommandEnabled(final String menuName, final String commandName, final String subCommandName)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            WebElement topMenuItem =
               driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, menuName)));
            topMenuItem.click();
            waitMenuPopUp();
            //Call command from menu popup:
            WebElement menuItem =
               driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, commandName)));
            menuItem.click();
            try
            {
               WebElement command =
                  driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_ROW_LOCATOR, subCommandName)));
               return (Boolean.parseBoolean(command.getAttribute(Locators.ENABLED_ATTRIBUTE)));
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
            finally
            {
               try
               {
                  Thread.sleep(10000);
               }
               catch (InterruptedException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               topMenuItem.click();
            }
         }
      });
   }

   /**
    * check disabled state top menu
    */
   public void isTopMenuDisabled(String topMenuName) throws Exception
   {

      try
      {
         WebElement topMenuItem =
            driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_DISABLED_LOCATOR, topMenuName)));
      }
      catch (NoSuchElementException e)
      {

      }

   }

   /**
    * check enabled state top menu
    */
   public void isTopMenuEnabled(String topMenuName) throws Exception
   {

      try
      {
         WebElement topMenuItem =
            driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, topMenuName)));
      }
      catch (NoSuchElementException e)
      {

      }

   }

   /**
    * Wait for menu item to appear.
    * 
    * @param itemName name of item to wait
    * @throws Exception
    */
   protected void waitForMenuItemPresent(final String itemName) throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, itemName))) != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait for command from menu.
    * 
    * @param menuName name of the top menu
    * @param itemName name of the menu item to wait
    * @throws Exception
    */
   public void waitForMenuItemPresent(String menuName, String itemName) throws Exception
   {
      //Call top menu command:
      WebElement topMenuItem = driver().findElement(By.xpath(String.format(Locators.TOP_MENU_ITEM_LOCATOR, menuName)));
      topMenuItem.click();
      waitMenuPopUp();
      try
      {
         waitForMenuItemPresent(itemName);
      }
      finally
      {
         topMenuItem.click();
         waitMenuPopUpClosed();
      }
   }

   /**
    * click for onlock leayer
    */
   public void clickOnLockLayer()
   {
      lockLayer.click();
   }

   /**
    * Wait appearance Top menu
    * From eXoToolbar
    * @param menuName
    * @throws Exception
    */
   public void waitForTopMenuPresent(final WebElement menuName) throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return menuName != null && menuName.isDisplayed()
                  && menuName.getAttribute("class").contains("exo-menuBarItem");
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

   }

   /**
    * Click on item  in 'New' menu 
    * 
    * @param menuName
    */
   public void clickOnNewMenuItem(String menuName)
   {
      WebElement menuItem = driver().findElement(By.xpath(String.format(Locators.MENU_ITEM_LOCATOR, menuName)));
      menuItem.click();
      waitMenuPopUpClosed();
   }

}