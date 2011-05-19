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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.IDE;
import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class AbstractTestModule
{

   protected Selenium selenium()
   {
      return IDE.getInstance().getSelenium();
   }

   protected IDE IDE()
   {
      return IDE.getInstance();
   }

   /**
    * Wait while element present.
    * 
    * @param locator - element locator
    * @throws Exception
    */
   protected void waitForElementPresent(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();
      while (true)
      {
         if (selenium().isElementPresent(locator))
         {
            break;
         }
         
         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("Timeout waiting for element > " + locator);
         }

         Thread.sleep(1);
      }
      
      
//      int WAITING_MAX_SECONDS = 10;
//
//      for (int second = 0;; second++)
//      {
//         if (second >= WAITING_MAX_SECONDS * 10)
//         {
//            fail("timeout for element " + locator);
//         }
//
//         if (selenium().isElementPresent(locator))
//         {
//            break;
//         }
//
//         Thread.sleep(100);
//      }
   }

   protected void waitForElementTextIsNotEmpty(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();
      while (true)
      {
         String text = selenium().getText("debug-editor-active-file-url");
         if (text != null && !text.isEmpty())
         {
            break;
         }

         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("Timeout in waitEditorFileOpened");
         }

         Thread.sleep(1);
      }
   }

   /**
    * Wait while element is visible.
    * 
    * @param locator - element locator
    * @throws Exception
    */
   protected void waitForElementVisible(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();
      while (true)
      {
         if (selenium().isVisible(locator))
         {
            break;
         }
         
         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("Timeout for element > " + locator);
         }

         Thread.sleep(1);
      }      
      
//      int WAITING_MAX_SECONDS = 10;
//
//      for (int second = 0;; second++)
//      {
//         if (second >= WAITING_MAX_SECONDS * 10)
//         {
//            fail("timeout for element " + locator);
//         }
//
//         if (selenium().isVisible(locator))
//         {
//            break;
//         }
//
//         Thread.sleep(100);
//      }
   }

   /**
    * Wait while element not present.
    * 
    * @param locator - element locator
    * @throws Exception
    */
   protected void waitForElementNotPresent(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();
      while (true)
      {
         if (!selenium().isElementPresent(locator))
         {
            break;
         }
         
         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("Timeout for element > " + locator);
         }

         Thread.sleep(1);
      }      
      
//      int WAITING_MAX_SECONDS = 10;
//
//      for (int second = 0;; second++)
//      {
//         if (second >= WAITING_MAX_SECONDS * 10)
//         {
//            fail("timeout for element " + locator);
//         }
//
//         if (!selenium().isElementPresent(locator))
//         {
//            break;
//         }
//
//         Thread.sleep(100);
//      }
   }

   /**
    * Wait while text present.
    * 
    * @param text
    * @throws Exception
    */
   protected void waitForTextPresent(String text) throws Exception
   {
      long startTime = System.currentTimeMillis();
      while (true)
      {
         if (selenium().isTextPresent(text))
         {
            break;
         }
         
         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("Timeout for text > " + text);
         }

         Thread.sleep(1);
      }
      
//      int WAITING_MAX_SECONDS = 10;
//
//      for (int second = 0;; second++)
//      {
//         if (second >= WAITING_MAX_SECONDS * 10)
//         {
//            fail("timeout for text " + text);
//         }
//
//         if (selenium().isTextPresent(text))
//         {
//            break;
//         }
//
//         Thread.sleep(100);
//      }
   }

   /**
    * Check the state of button (enabled, disabled) by button id.
    * 
    * Use instead of this method <code>assertTrue(getButtonState(buttonId));</code>
    * 
    * @param buttonId - the id of button
    * @param isEnabled - is enabled
    */
   public void checkButtonState(String buttonId, boolean isEnabled)
   {
      assertTrue(selenium().isElementPresent(
         "//div[@id='" + buttonId + "' and @button-enabled='" + String.valueOf(isEnabled) + "']"));
   }
   
   /**
    * Get the state of button (enabled, disabled) by button id.
    * @param buttonId - the id of button
    * @return is button enabled or disabled
    * @throws InterruptedException 
    */
   public boolean getButtonState(String buttonId) throws Exception
   {
      if (selenium().isElementPresent("//div[@id='" + buttonId + "' and @button-enabled='true']"))
         return true;
      if (selenium().isElementPresent("//div[@id='" + buttonId + "' and @button-enabled='false']"))
         return false;
      throw new Exception("Can't determine is button enabled or disabled");
   }
   
   /**
    * To check the title of gwt dialog.
    * 
    * @param title - the title of gwt dialog
    * @return
    */
   public String getGwtDialogCaptionLocator(String title)
   {
      return "//div[@class='gwt-DialogBox']//div[@class='Caption']/span[text()='" + title + "']";
   }


}
