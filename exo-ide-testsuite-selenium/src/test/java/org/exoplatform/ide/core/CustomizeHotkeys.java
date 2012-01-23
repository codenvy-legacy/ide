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

import static org.junit.Assert.assertEquals;
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

      String IS_CANCEL_ENABLED_SELECTOR = "div#" + CANCEL_BUTTON_ID + ENABLED_BUTTON_PREFICS;

      String IS_CANCEL_DISABLED_SELECTOR = "div#" + CANCEL_BUTTON_ID + DISABLED_BUTTON_PREFICS;

      String LIST_GRID_FORM = "ideCustomizeHotKeysListGrid";

      String CUSTOMIZE_ROWS = "//table[@id='" + LIST_GRID_FORM + "']" + "/tbody//tr/td/div/span[contains(.,'%s')]";

      String BINDING_ROWS = "//table[@id='" + LIST_GRID_FORM + "']" + "/tbody//tr/td/div/span[contains(.,'%s')]";

      String MAXIMIZE_TITLE = "img[title=Maximize]";

      String CLOSE_TITLE = "img[title=Close]";

      // TODO After isuue IDE-; should be change
      String INPUT_FIELD = "div[view-id=ideCustomizeHotKeysView] input[type=text]";

      String INPUT_FIELD_DISABLED = "div[view-id=ideCustomizeHotKeysView] input[type=text][disabled='']";

      String INPUT_FIELD_ENABLED = "div[view-id=ideCustomizeHotKeysView] input[class=gwt-TextBox][type=text]";

      String LABEL_MESSAGE_HOTKEY_ID = "ideCustomizeHotKeysMessageLabel";

      String FIRST_KEY_MESSAGE = "First key should be Ctrl or Alt";

      String ALREDY_TO_ANORHER_COMM_MESSAGE = "Such hotkey already bound to another command";

      String ALREDY_TO_THIS_COMM_MESSAGE = "Such hotkey already bound to this command";
      
      String HOLT_MESSAGE = "Holt Ctrl or Alt, then press key";

      String HOKEY_USED_MESSAGE = "This hotkey is used by Code or WYSIWYG Editors";

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

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = Locators.LABEL_MESSAGE_HOTKEY_ID)
   private WebElement hotkeyMessage;

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

   @FindBy(css = Locators.IS_CANCEL_DISABLED_SELECTOR)
   private WebElement isCancelDisabled;

   @FindBy(css = Locators.IS_CANCEL_ENABLED_SELECTOR)
   private WebElement isCancelEnabled;

   @FindBy(css = Locators.INPUT_FIELD)
   private WebElement keyField;

   @FindBy(css = Locators.MAXIMIZE_TITLE)
   private WebElement max;

   @FindBy(css = Locators.INPUT_FIELD_DISABLED)
   private WebElement isKeyFieldDisabled;

   @FindBy(css = Locators.INPUT_FIELD_ENABLED)
   private WebElement isKeyFieldEnabled;

   @FindBy(css = Locators.CLOSE_TITLE)
   private WebElement closeTitle;

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
   public boolean isOkDisabled()
   {
      return isOkDisabled != null && isOkDisabled.isDisplayed();
   }

   /**
    * @return true if cancel button enabled
    */
   public boolean isCancelEnabled()
   {
      return isCancelEnabled != null && isCancelEnabled.isDisplayed();
   }

   /**
    * @return true if cancel button disabled
    */
   public boolean isCancelDisabled()
   {
      return isCancelDisabled != null && isCancelDisabled.isDisplayed();
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
    * Return current text from textfield
    * @return
    */
   public String getTextTypeKeys()
   {
      return IDE().INPUT.getValue(keyField);
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

   /**
    * Close form using the method 
    * of click on close label in form
    */
   public void closeClick()
   {
      closeTitle.click();
   }

   /**
    * get message from Hot Keys Message Label
    * 
    */
   public String getMessage()
   {
      return hotkeyMessage.getText();
   }

   /**
    * Checking  active key field
    * @param name
    */
   public boolean isKeyFieldActive(boolean isActive)
   {
      if (isActive)
      {
         return isKeyFieldEnabled != null && isKeyFieldEnabled.isDisplayed();
      }
      else
         return isKeyFieldDisabled != null && isKeyFieldDisabled.isDisplayed();
   }

   /**
    * Checking label with message is empty
    */

   public void isAlredyNotView()
   {
      assertEquals("", getMessage());
   }

   /**
    * Check present already message
    * 
    */
   public void isAlreadyMessageView()
   {
      assertEquals(Locators.ALREDY_TO_ANORHER_COMM_MESSAGE, getMessage());
   }

   /**
    * Check present already to this command message
    * 
    */
   public void isAlreadyToThisCommandMessView()
   {
      assertEquals(Locators.ALREDY_TO_THIS_COMM_MESSAGE, getMessage());
   }
   
   
   /**
    * Checking label with "First key should be Ctrl or Alt" is present
    */

   public void isFirstKeyMessageView()
   {
      assertEquals(Locators.FIRST_KEY_MESSAGE, getMessage());
   }

   /**
    * Checking Holt Message is view
    */

   public void isHoltMessageView()
   {
      assertEquals(Locators.HOLT_MESSAGE, getMessage());
   }

   /**
    * Checking "This hotkey is used by Code or WYSIWYG Editors" Message is view
    */

   public void isHotKeyUsedMessageView()
   {
      assertEquals(Locators.HOKEY_USED_MESSAGE, getMessage());
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

}
