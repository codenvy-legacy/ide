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

import org.exoplatform.ide.IDE;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AskForValueDialog extends AbstractTestModule
{
   
   public interface Locator
   {
      
      String VIEW_ID = "ideAskForValueView";
      
      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);
      
      String OK_BUTTON_LOCATOR = "ideAskForValueViewYesButton";

      String NO_BUTTON_LOCATOR = "ideAskForValueViewNoButton";

      String CANCEL_BUTTON_LOCATOR = "ideAskForValueViewCancelButton";

      String TEXT_FIELD_LOCATOR = "ideAskForValueViewValueField";

   }


   /**
    * Determines whether the Dialog is open.
    * 
    * @return
    */
   public boolean isOpened()
   {
      return selenium().isElementPresent(Locator.VIEW_LOCATOR);
   }
   
   /**
    * Waits until AskForValue dialog will be present.
    * 
    * @throws Exception
    */
   public void waitForPresent() throws Exception {
      waitForElementPresent(Locator.VIEW_LOCATOR);
   }

   /**
    * Waits until AskForValue dialog closes.
    * 
    * @throws Exception
    */
   public void waitForAskDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(Locator.VIEW_LOCATOR);
   }
   
   /**
    * Closes AskForValue dialog.
    * 
    * @throws Exception
    */
   public void closeDialog() throws Exception {
      String locator = "//div[@id='ideAskForValueView-window']//img[@title='Close']";
      selenium().click(locator);
      waitForElementNotPresent(Locator.VIEW_LOCATOR);
   }

   /**
    * Clicks on "Ok" button.
    * 
    * @throws Exception
    */
   public void clickOkButton() throws Exception
   {
      selenium().click(Locator.OK_BUTTON_LOCATOR);
      waitForElementNotPresent(Locator.VIEW_LOCATOR);
   }

   /**
    * Clicks on "No" button.
    * 
    * @throws Exception
    */
   public void clickNoButton() throws Exception
   {
      selenium().click(Locator.NO_BUTTON_LOCATOR);
      waitForElementNotPresent(Locator.VIEW_LOCATOR);
   }

   /**
    * Determines whether the "No" button is visible.
    * 
    * @return
    */
   public boolean isNoButtonPresent()
   {
      return selenium().isElementPresent(Locator.NO_BUTTON_LOCATOR);
   }

   /**
    * Clicks on "Cancel" button.
    * 
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      selenium().click(Locator.CANCEL_BUTTON_LOCATOR);
      waitForElementNotPresent(Locator.VIEW_LOCATOR);
   }

   /**
    * Sets a new value of text field.
    * 
    * @param value
    * @throws Exception
    */
   public void setValue(String value) throws Exception
   {
      WebElement inputElement = driver().findElement(By.name(Locator.TEXT_FIELD_LOCATOR));
      inputElement.clear();
      inputElement.sendKeys(value);
   }

}
