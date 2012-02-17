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

import org.exoplatform.ide.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
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

   private final String TOOLBAR_LOCK_LABEL_DISABLED =
      "//table[@class='exo-popupMenuTable']/tbody//tr[@item-enabled='false']//td/nobr[text()='Lock File']";

   private final String TOOLBAR_LOCK_ICON_DISABLED =
      "table.exo-popupMenuTable>tbody>tr[item-enabled=false]>td.exo-popupMenuIconFieldDisabled>img";

   private final String TOOLBAR_UNLOCK_LABEL_ACTIVE =
      "//table[@class='exo-popupMenuTable']/tbody//tr[@item-enabled='true']//td/nobr[text()='Unlock File']";

   private final String CHEK_UNLOCK_IMAGE =
      "//table[@class='exo-popupMenuTable']/tbody//tr[@item-enabled='true']//td//img[@src='%s']";

   // Basic Webelements
   @FindBy(xpath = TOOLBAR_LOCK_LABEL_ACTIVE)
   private WebElement lockLabelIsActive;

   @FindBy(css = TOOLBAR_LOCK_ICON_ACTIVE)
   private WebElement lockIconIsActive;

   @FindBy(xpath = TOOLBAR_LOCK_LABEL_DISABLED)
   private WebElement lockLabelDisabled;

   @FindBy(css = TOOLBAR_LOCK_ICON_DISABLED)
   private WebElement lockIconDisabled;

   @FindBy(xpath = TOOLBAR_UNLOCK_LABEL_ACTIVE)
   private WebElement unLockEnabled;

   /**
    * return true if submenu lock active in menu Edit 
    * @return
    */
   public boolean isLockCommandActive()
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

   /**
    * return true if submenu lock not active in menu Edit
    * @return
    */
   public boolean isLockCommandNotActive()
   {
      try
      {
         return lockIconDisabled != null && lockIconDisabled.isDisplayed() && lockLabelDisabled != null
            && lockLabelDisabled.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   // check is menu not present
   /**
    * retiurn true if submenu lock not present in Edit menu 
    * @return
    * 
    */
   public boolean isLockCommandsNotView()
   {
      try
      {
         return (isLockCommandNotActive() == false && isLockCommandActive() == false);
      }
      catch (Exception e)
      {
         return false;
      }

   }

   /**
    * check unlock submenu in Edit 
    * @return
    */
   public boolean isUnLockCommandActive()
   {
      try
      {
         System.out.print("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+BaseTest.BASE_URL + "IDE/ui/popup/check.gif");
         WebElement elem =
            driver().findElement(
               By.xpath(String.format(CHEK_UNLOCK_IMAGE, BaseTest.BASE_URL + "IDE/ui/popup/check.gif")));
         return elem != null && elem.isDisplayed() && unLockEnabled != null && unLockEnabled.isDisplayed()
            && lockIconIsActive != null && lockIconIsActive.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }

   }

   /**
       * Waits for Loader to appear.
       * 
       * @throws Exception
       */
   public void waitLockActiveOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return lockIconDisabled != null && lockIconDisabled.isDisplayed() && lockLabelDisabled != null
                  && lockLabelDisabled.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

}