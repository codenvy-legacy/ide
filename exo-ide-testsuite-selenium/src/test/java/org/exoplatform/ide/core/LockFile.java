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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Maxim Musienko</a>
 * @version $Id:  Feb 16, 2012 10:59:02 AM max $
 *
 */
public class LockFile extends AbstractTestModule
{
   private final String LOCK_LAYER_CLASS = "exo-lockLayer";

   private final String TOOLBAR_LOCK_LABEL_ACTIVE =
      "//table[@class='exo-popupMenuTable']/tbody//tr[@item-enabled='true']//td/nobr[text()='Lock File']";

   private final String TOOLBAR_LOCK_ICON_ACTIVE =
      "table.exo-popupMenuTable>tbody>tr[item-enabled=true]>td.exo-popupMenuIconField>img";

   private final String TOOLBAR_LOCK_ICON_DISABLED =
      "//div[@id='exoIDEToolbar']//div[@enabled='false' and @title='Lock File']";

   private final String TOOLBAR_UNLOCK_LABEL_ACTIVE = "//div[@id='exoIDEToolbar']//div[@title='Unlock File']";

   private final String CHEK_UNLOCK_IMAGE =
      "//table[@class='exo-popupMenuTable']/tbody//tr[@item-enabled='true']//td//img[@src='%s']";

   private final String EDIT_MENU_DISABLE_ICON =
      "//table[@class='exo-popupMenuTable']//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='Lock File']";

   private final String LOCK_ICON_ON_TAB = "//div[@tab-bar-index='%s']//span[@title]/img[@id='fileReadonly']";

   // Basic Webelements
   @FindBy(xpath = TOOLBAR_LOCK_LABEL_ACTIVE)
   private WebElement lockLabelIsActive;

   @FindBy(css = TOOLBAR_LOCK_ICON_ACTIVE)
   private WebElement lockIconIsActive;

   @FindBy(xpath = TOOLBAR_LOCK_ICON_DISABLED)
   private WebElement lockIconDisabled;

   @FindBy(xpath = TOOLBAR_UNLOCK_LABEL_ACTIVE)
   private WebElement unLockEnabled;

   @FindBy(xpath = EDIT_MENU_DISABLE_ICON)
   private WebElement disabledLockIconInEditMenu;

   /**
    * Wait  submenu lock active in menu Edit 
    */
   public void waitLockCommandActive() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return lockIconIsActive != null && lockIconIsActive.isDisplayed() && lockLabelIsActive != null
                  && lockLabelIsActive.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait submenu lock active in menu Edit 
    * @throws Exception 
    */
   public void waitLockIconViewOnFileInProjecrExplorer(final String path) throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement isElemLock = driver().findElement(By.id(IDE().PROJECT.EXPLORER.getItemId(path)));
               WebElement iconOnElem = isElemLock.findElement(By.id("resourceLocked"));
               return iconOnElem != null && iconOnElem.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * check present of the lock icon on tab
    * start with 0
    * @return
    */
   public void waitLockIconOnTabView(int index)
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format(
         LOCK_ICON_ON_TAB, index))));
   }

   /**
    * Wait  submenu lock not active in menu Edit
    */
   public void waitLockCommandNotActive()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(TOOLBAR_LOCK_ICON_DISABLED)));
   }

   //   // check is menu not present
   //   /**
   //    * retiurn true if submenu lock not present in Edit menu 
   //    * @return
   //    * 
   //    */
   //   public boolean isLockCommandsNotView()
   //   {
   //      try
   //      {
   //         return (isLockCommandNotActive() == false && isLockCommandActive() == false);
   //      }
   //      catch (Exception e)
   //      {
   //         return false;
   //      }
   //
   //   }

   /**
    * Wait active unlock submenu in Edit 
    */
   public void waitUnLockCommandActive()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(TOOLBAR_UNLOCK_LABEL_ACTIVE)));
   }

   /**
       * Waits for Loader to appear.
       * 
       * @throws Exception
       */
   public void waitLockActiveOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return lockIconDisabled != null && lockIconDisabled.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait disabled lock icon in menu edit
    * @throws Exception
    */
   public void waitDisabledLockIconInEditMenu() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return disabledLockIconInEditMenu != null && disabledLockIconInEditMenu.isDisplayed();
         }
      });
   }

}