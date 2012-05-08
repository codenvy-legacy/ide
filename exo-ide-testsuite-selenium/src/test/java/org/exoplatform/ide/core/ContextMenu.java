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
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 7, 2012 12:31:41 PM anya $
 * 
 */
public class ContextMenu extends AbstractTestModule
{
   private interface Locators
   {
      String ID = "eXoIDEContextMenu";

      String LOCK_LAYER_CLASS = "exo-lockLayer";

      String COMMAND_TITLE_LOCATOR = "//td[contains(@class,'exo-popupMenuTitleField')]//nobr[text()='%s']";

      String COMMAND_LOCATOR = "//table[@class='exo-popupMenuTable']//tr[contains(., '%s')]";

      String ENABLED_ATTRIBUTE = "item-enabled";
   }

   @FindBy(id = Locators.ID)
   private WebElement contextMenu;

   @FindBy(className = Locators.LOCK_LAYER_CLASS)
   private WebElement lockLayer;

   /**
    * Wait context menu opened.
    */
   public void waitOpened()
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return driver.findElement(By.id(Locators.ID)) != null
                  && driver.findElement(By.id(Locators.ID)).isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait context menu closed.
    */
   public void waitClosed()
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return !lockLayer.isDisplayed() && !contextMenu.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Returns enabled state of the context menu command.
    * 
    * @param commandName command name
    * @return {@link Boolean} enabled state of the menu command
    * @throws Exception
    */
   public boolean isCommandEnabled(String commandName) throws Exception
   {
      try
      {
         WebElement command = driver().findElement(By.xpath(String.format(Locators.COMMAND_LOCATOR, commandName)));
         return Boolean.parseBoolean(command.getAttribute(Locators.ENABLED_ATTRIBUTE));
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   public void runCommand(String commandName)
   {
      WebElement command = driver().findElement(By.xpath(String.format(Locators.COMMAND_TITLE_LOCATOR, commandName)));
      command.click();
   }

   public void closeContextMenu()
   {
      lockLayer.click();
   }
}
