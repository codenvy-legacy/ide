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

import org.exoplatform.ide.MenuCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Nov 8, 2011 4:05:33 PM anya $
 *
 */
public class Delete extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_LOCATOR = "//div[@view-id='ideDeleteItemsView']";

      String OK_BUTTON_ID = "ideDeleteItemFormOkButton";

      String NO_BUTTON_ID = "ideDeleteItemFormCancelButton";

      String LABEL_LOCATOR = VIEW_LOCATOR + "//div[@class='gwt-Label']";

   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(xpath = Locators.LABEL_LOCATOR)
   private WebElement label;

   @FindBy(id = Locators.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(id = Locators.NO_BUTTON_ID)
   private WebElement noButton;

   /**
    * Wait delete items view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed();
         }
      });
   }

   /**
    * Wait delete items view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
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
    * Click ok button.
    */
   public void clickOkButton()
   {
      okButton.click();
   }

   /**
    * Click no button.
    */
   public void clickNoButton()
   {
      noButton.click();
   }

   /**
    * Returns deletion question text.
    * 
    * @return {@link String} text displayed on deletion dialog
    */
   public String getDeletionText()
   {
      return (label != null) ? label.getText() : null;
   }

   /**
    * Performs deletion of selected items.
    * 
    * @throws Exception
    */
   public void deleteSelectedItems() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
      waitOpened();
      clickOkButton();
      waitClosed();
   }
}
