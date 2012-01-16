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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RESTService May 11, 2011 2:29:11 PM evgen $ FIXME
 */
public class CustomizeHotkeys extends AbstractTestModule
{
   private interface Locators
   {

      String ENABLED_BUTTON_PREFICS = "[button-enabled=true]";

      String DISABLED_BUTTON_PREFICS = "[button-enabled=false]";

      String CUSTOMIZE_HOTKEYS_FORM = "ideCustomizeHotKeysView-window";

      String BIND_BUTTON_ID = "ideCustomizeHotKeysViewBindButton";

      String IS_BIND_ENABLED_SELECTOR = "div#" + BIND_BUTTON_ID + ENABLED_BUTTON_PREFICS;

      String IS_BIND_DISABLED_SELECTOR = "div#" + BIND_BUTTON_ID + DISABLED_BUTTON_PREFICS;

      String UNBIND_BUTTON_ID = "ideCustomizeHotKeysViewUnbindButton";

      String IS_UNBIND_ENABLED_SELECTOR = "div#" + UNBIND_BUTTON_ID + ENABLED_BUTTON_PREFICS;

      String IS_UNBIND_DISABLED_SELECTOR = "div#" + UNBIND_BUTTON_ID + DISABLED_BUTTON_PREFICS;

      String OK_BUTTON_ID = "ideCustomizeHotKeysViewOkButton";

      String IS_OK_ENABLED_SELECTOR = "div#" + OK_BUTTON_ID + ENABLED_BUTTON_PREFICS;

      String IS_OK_DISABLED_SELECTOR = "div#" + OK_BUTTON_ID + DISABLED_BUTTON_PREFICS;

      String CANCEL_BUTTON_ID = "ideCustomizeHotKeysViewCancelButton";

      String LIST_GRID_FORM = "ideCustomizeHotKeysListGrid";

      String CUSTOMIZE_ROWS = "//table[@id='" + LIST_GRID_FORM + "']" + "/tbody//tr/td/div/span[contains(.,'%s')]";

      String MAXIMIZE = "img[title=Maximize]";


      
      // TODO After isuue IDE-; should be change
      String INPUT_FIELD = "div[view-id=ideCustomizeHotKeysView] input[type=text]";
   }

   // The basic webelements for customize
   @FindBy(id = Locators.CUSTOMIZE_HOTKEYS_FORM)
   private WebElement customizeHotkeyForm;

   @FindBy(id = Locators.BIND_BUTTON_ID)
   private WebElement bindButton;

   @FindBy(id = Locators.UNBIND_BUTTON_ID)
   private WebElement unbindButton;

   @FindBy(id = Locators.OK_BUTTON_ID)
   private WebElement okButton;

   @FindBy(css = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(css = Locators.IS_BIND_ENABLED_SELECTOR)
   private WebElement isBindEnabled;

   @FindBy(css = Locators.IS_BIND_DISABLED_SELECTOR)
   private WebElement isBindDisabled;

   @FindBy(css = Locators.IS_UNBIND_DISABLED_SELECTOR)
   private WebElement isUnBindDisabled;

   @FindBy(css = Locators.IS_UNBIND_ENABLED_SELECTOR)
   private WebElement isUnBindEnabled;

   @FindBy(css = Locators.IS_OK_ENABLED_SELECTOR)
   private WebElement isOkEnabled;

   @FindBy(css = Locators.IS_OK_DISABLED_SELECTOR)
   private WebElement isOkDisabled;

   @FindBy(css = Locators.INPUT_FIELD)
   private WebElement keyField;
   
   @FindBy(css = Locators.MAXIMIZE)
   private WebElement max;
  
   /**
    * Wait appearance Customize Hotkeys Form
    * 
    */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return customizeHotkeyForm != null && customizeHotkeyForm.isDisplayed();
         }
      });
   }

   /**
    * Waiting while button bind will enabled
    * 
    */
   public void waitBindEnabled() throws InterruptedException
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return isBindEnabled != null && isBindEnabled.isDisplayed();
         }
      });
   }

   /**
    * Waiting while button unbind will enabled
    * 
    */
   public void waitUnBindEnabled() throws InterruptedException
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return isUnBindEnabled != null && isUnBindEnabled.isDisplayed();
         }
      });
   }

   /**
    * Waiting while button ok will enabled
    * 
    */
   public void waitOkEnabled() throws InterruptedException
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return isOkEnabled != null && isOkEnabled.isDisplayed();
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
               input.findElement(By.id(Locators.CUSTOMIZE_HOTKEYS_FORM));
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
    * @return true if button enabled
    */
   public boolean isBindEnabled()
   {
      return isBindEnabled != null && isBindEnabled.isDisplayed();
   }

   /**
    * @return true if button disabled
    */
   public boolean isBindDisabled()
   {
      return isBindDisabled != null && isBindDisabled.isDisplayed();
   }

   /**
    * @return true if button disabled
    */
   public boolean isUnBindDisabled()
   {
      return isUnBindDisabled != null && isUnBindDisabled.isDisplayed();
   }

   /**
    * @return true if button enabled
    */
   public boolean isUnBindEnabled()
   {
      return isUnBindEnabled != null && isUnBindEnabled.isDisplayed();
   }

   /**
    * @return true if button enabled
    */
   public boolean isOkEnabled()
   {
      return isOkEnabled != null && isOkEnabled.isDisplayed();
   }

   /**
    * @return true if button disabled
    */
   public boolean isOkDisabled(boolean status)
   {
      return isOkDisabled != null && isOkDisabled.isDisplayed();
   }

   /**
    * click cancel button
    */
   public void cancelButtonClick()
   {
      cancelButton.click();
   }

   /**
    * click bind button button
    */
   public void bindlButtonClick()
   {
      bindButton.click();
   }

   /**
    * click bind button button
    */
   public void unbindlButtonClick()
   {
      unbindButton.click();
   }

   /**
    * click ok button button
    */
   public void okButtonClick()
   {
      okButton.click();
   }

   public void maximizeClick()
   {
      max.click();
   }
   
   /**
    * @return true if button disabled
    * @throws InterruptedException
    */
   public void typeKeys(String text) throws InterruptedException
   {
      IDE().INPUT.typeToElement(keyField, text);
   }

   /**
    * Selects an item on the commandlist by name
    * 
    * @param name
    */
   public void selectElementOnCommandlistbarByName(String name)
   {
      
      
      WebElement rowByName = driver().findElement(By.xpath(String.format(Locators.CUSTOMIZE_ROWS, name)));
      rowByName.click();
   }
   
   
   
   
   
   // /**
   // * @return false if REST service form is closed
   // * return true if REST service form is open
   // */
   // public boolean isFormOpened()
   // {
   //
   // // return (restServiceForm != null && restServiceForm.isDisplayed() && restServicePath != null
   // // && restServicePath.isDisplayed() && restServiceMethod != null && restServiceMethod.isDisplayed()
   // // && restServiceRequestMediaType != null && restServiceRequestMediaType.isDisplayed()
   // // && restServiceResponseMediaType != null && restServiceResponseMediaType.isDisplayed() && queryTable != null
   // // && getUrlButton != null && getUrlButton.isDisplayed() && sendButton != null && sendButton.isDisplayed()
   // // && cancelButton != null && cancelButton.isDisplayed());
   //
   // }

   public void maxClick()
   {
      // TODO
   }
}
