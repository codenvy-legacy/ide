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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowKeyboardShortcuts.java May 10, 2012 4:37:42 PM azatsarynnyy $
 *
 */
public class ShowKeyboardShortcuts extends AbstractTestModule
{
   private interface Locators
   {

      String ENABLED_BUTTON_PREFICS = "[button-enabled=true]";

      String SHOW_KEYBOARD_SHORCUTS_VIEW = "ideShowHotKeysView-window";

      String CLOSE_BUTTON_ID = "ideShowHotKeysViewCloseButton";

      String IS_CANCEL_ENABLED_SELECTOR = "div#" + CLOSE_BUTTON_ID + ENABLED_BUTTON_PREFICS;

      String LIST_GRID_FORM = "ideShowHotKeysListGrid";

      String ROW_SELECTOR = "//table[@id='" + LIST_GRID_FORM + "']" + "/tbody//tr/td/div[contains(.,'%s')]";

      String CLOSE_TITLE = "img[title=Close]";

   }

   @FindBy(id = Locators.SHOW_KEYBOARD_SHORCUTS_VIEW)
   private WebElement showKeyboardShortcutsForm;

   @FindBy(id = Locators.CLOSE_BUTTON_ID)
   private WebElement closeButton;

   @FindBy(css = Locators.IS_CANCEL_ENABLED_SELECTOR)
   private WebElement isCancelEnabled;

   @FindBy(css = Locators.CLOSE_TITLE)
   private WebElement closeTitle;

   /**
    * Wait appearance Show Keyboard Shortcuts view
    * 
    */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return showKeyboardShortcutsForm != null && showKeyboardShortcutsForm.isDisplayed();
         }
      });
   }

   /**
    * Wait disappearance Customize Hotkeys Form
    * 
    * @throws InterruptedException
    */
   public void waitClosed() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.id(Locators.SHOW_KEYBOARD_SHORCUTS_VIEW));
               return false;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   /**
    * @return true if close button enabled
    */
   public boolean isCloseButtonEnabled()
   {
      return isCancelEnabled != null && isCancelEnabled.isDisplayed();
   }

   /**
    * Click close button.
    */
   public void closeButtonClick()
   {
      closeButton.click();
   }

   /**
    * Returns true if shortcut is present in list grid.
    * 
    * @param shortcut keyboard shortcut
    * @return true if shortcut is present in list grid
    */
   public boolean isShortcutPresent(String shortcut)
   {
      try
      {
         driver().findElement(By.xpath(String.format(Locators.ROW_SELECTOR, shortcut)));
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
      return true;
   }

   /**
    * Close form using the method 
    * of click on close label in form
    */
   public void closeClick()
   {
      closeTitle.click();
   }

}
