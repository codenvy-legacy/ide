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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class Toolbar extends AbstractTestModule
{

   private interface Locators
   {
      String TOOLBAR_ID = "exoIDEToolbar";

      String BUTTON_SELECTOR = "div[title='%s']";

      String POPUP_PANEL_LOCATOR = "//table[@class='exo-popupMenuTable']";

      String ROW_FROM_NEW_POPUP_LOCATOR = POPUP_PANEL_LOCATOR + "//tr[contains(., '%s')]";

      String BUTTON_FROM_NEW_POPUP_LOCATOR = ROW_FROM_NEW_POPUP_LOCATOR + "//nobr";

      String LOCKLAYER_CLASS = "exo-lockLayer";

      String POPUP_SELECTOR = "table.exo-popupMenuTable";

      String SELECTED_BUTTON_SELECTOR = "div#" + TOOLBAR_ID + " div.exoIconButtonPanelSelected[title='%s']";

      String RIGHT_SIDE_BUTTON_SELECTOR = "div#" + TOOLBAR_ID + " div.exoToolbarElementRight div[title='%s']";

      String LEFT_SIDE_BUTTON_SELECTOR = "div#" + TOOLBAR_ID + " div.exoToolbarElementLeft div[title='%s']";
   }

   @FindBy(className = Locators.LOCKLAYER_CLASS)
   WebElement lockLayer;

   @FindBy(id = Locators.TOOLBAR_ID)
   WebElement toolbar;

   /**
    * Performs click on toolbar button.
    * 
    * @param buttonTitle button's title
    */
   public void runCommand(String buttonTitle) throws Exception
   {
      waitButtonPresentAtLeft(buttonTitle);
      WebElement button = toolbar.findElement(By.cssSelector(String.format(Locators.BUTTON_SELECTOR, buttonTitle)));
      button.click();
   }

   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * 
    * @param commandName command's name from New popup
    */
   public void runCommandFromNewPopupMenu(final String commandName) throws Exception
   {
      runCommand(MenuCommands.New.NEW);
      waitMenuPopUp();
      try
      {
         WebElement button =
            driver().findElement(By.xpath(String.format(Locators.BUTTON_FROM_NEW_POPUP_LOCATOR, commandName)));
         button.click();
      }
      finally
      {
         try
         {
            if (lockLayer != null && lockLayer.isDisplayed())
            {
               lockLayer.click();
            }
         }
         catch (NoSuchElementException e)
         {
         }
      }
   }

   /**
    * Wait for popup to draw.
    */
   protected void waitMenuPopUp()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return driver.findElement(By.cssSelector(Locators.POPUP_SELECTOR)) != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Check is button present on toolbar and is it enabled or disabled.
    * 
    * @param name button name
    * @param enabled boolean value
    * 
    * Use {@link Toolbar.#isButtonEnabled(String)}
    */
   @Deprecated
   public void assertButtonEnabled(String name, boolean enabled)
   {
      if (enabled)
      {
         String locator =
            "//div[@id=\"exoIDEToolbar\" and @class=\"exoToolbarPanel\"]//div[@enabled=\"true\" and @title=\"" + name
               + "\"]";
         assertTrue(selenium().isElementPresent(locator));
      }
      else
      {
         String locator =
            "//div[@id=\"exoIDEToolbar\" and @class=\"exoToolbarPanel\"]//div[@enabled=\"false\" and @title=\"" + name
               + "\"]";
         assertTrue(selenium().isElementPresent(locator));
      }
   }

   /**
    * Returns the enabled state of the Toolbar button.
    * 
    * @param name button's title
    * @return enabled state of the button
    */
   public boolean isButtonEnabled(String name)
   {
      try
      {
         WebElement button = driver().findElement(By.cssSelector(String.format(Locators.BUTTON_SELECTOR, name)));
         return Boolean.parseBoolean(button.getAttribute("enabled"));
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Returns the enabled state of button from new popup.
    * 
    * @param name button's name
    * @return {@link Boolean} enabled state
    * @throws Exception
    */
   public boolean isButtonFromNewPopupMenuEnabled(String name) throws Exception
   {
      runCommand(MenuCommands.New.NEW);

      try
      {
         WebElement button = driver().findElement(By.xpath(String.format(Locators.ROW_FROM_NEW_POPUP_LOCATOR, name)));
         return Boolean.parseBoolean(button.getAttribute("item-enabled"));
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
      finally
      {
         if (lockLayer != null)
         {
            lockLayer.click();
         }
      }
   }

   /**
    * Wait for button change the enabled state.
    * 
    * @param name button's name
    * @param enabled <code>true</code> if wait for enabled state, otherwise - for disabled
    * @throws Exception
    */
   public void waitForButtonEnabled(final String name, boolean enabled) throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            return isButtonEnabled(name);
         }
      });
   }

   /**
    * Check is button present on toolbar
    * 
    * @param name button name (title in DOM)
    * @param isPresent is present
    * use {@link #isButtonPresentAtLeft(String)}
    */
   @Deprecated
   public void assertButtonExistAtLeft(String name, boolean exist)
   {
      String locator =
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]/div[@class=\"exoToolbarElementLeft\"]"
            + "/div[contains(@class, \"exoIconButtonPanel\") and @title=\"" + name + "\"]";
      if (exist)
      {
         assertTrue(selenium().isVisible(locator));
      }
      else
      {

         assertTrue(!selenium().isElementPresent(locator) || !selenium().isVisible(locator));

      }
   }

   /**
    * Check is button present on right part of Toolbar.
    * 
    * @param name button name (title in DOM)
    */
   public boolean isButtonPresentAtRight(String name)
   {
      try
      {
         return driver().findElement(By.cssSelector(String.format(Locators.RIGHT_SIDE_BUTTON_SELECTOR, name))) != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
   * Check is button present on left part of  Toolbar.
   * 
   * @param name button name (title in DOM)
   */
   public boolean isButtonPresentAtLeft(String name)
   {
      try
      {
         WebElement button =
            driver().findElement(By.cssSelector(String.format(Locators.LEFT_SIDE_BUTTON_SELECTOR, name)));
         return button != null && button.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   public void waitButtonNotPresentAtLeft(final String name)
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            return !isButtonPresentAtLeft(name);
         }
      });
   }

   public void waitButtonPresentAtLeft(final String name)
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            return isButtonPresentAtLeft(name);
         }
      });
   }

   /**
    * Get the button's selected state.
    * 
    * @param name button's name
    * @return if <code>true</code>, then button is selected
    */
   public boolean isButtonSelected(String name)
   {
      try
      {
         WebElement button =
            driver().findElement(By.cssSelector(String.format(Locators.SELECTED_BUTTON_SELECTOR, name)));
         return button != null && button.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }
}
