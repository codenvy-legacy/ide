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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Perspective extends AbstractTestModule
{

   public interface Panel
   {

      String NAVIGATION = "navigation";

      String EDITOR = "editor";

      String INFORMATION = "information";

      String OPERATION = "operation";

   }

   interface Locators
   {
      String CLOSE_BUTTON_SELECTOR = "div.tabTitleCloseButton[tab-title=%s]";
   }

   /**
    * Maximize panel with specified ID
    * 
    * @param panelId ID of panel
    */
   public void maximizePanel(String panelId) throws Exception
   {
      String locator = "//div[@id='" + panelId + "-maximize']";
      selenium().click(locator);
      String panelLocator = "//div[@panel-id='" + panelId + "']";
      selenium().waitForCondition(
         "var value = selenium.browserbot.findElementOrNull(\"" + panelLocator
            + "\"); value.getAttribute('panel-maximized') == 'true' ", "5000");
   }

   public void restorePanel(String panelId) throws Exception
   {
      String locator = "//div[@id='" + panelId + "-restore']";
      selenium().click(locator);
      String panelLocator = "//div[@panel-id='" + panelId + "']";
      selenium().waitForCondition(
         "var value = selenium.browserbot.findElementOrNull(\"" + panelLocator
            + "\"); value.getAttribute('panel-maximized') == 'false' ", "5000");
   }

   public void checkPanelIsMaximized(String panelId, boolean isMaximized)
   {
      String panelLocator = "//div[@panel-id='" + panelId + "' and @panel-maximized='" + isMaximized + "']";
      selenium().isVisible(panelLocator);

      String maximizeButtonLocator = "//div[@id='" + panelId + "-maximize']";
      String restoreButtonLocator = "//div[@id='" + panelId + "-restore']";

      if (isMaximized)
      {
         assertFalse(selenium().isVisible(maximizeButtonLocator));
         assertTrue(selenium().isVisible(restoreButtonLocator));
      }
      else
      {
         assertTrue(selenium().isVisible(maximizeButtonLocator));
         assertFalse(selenium().isVisible(restoreButtonLocator));
      }
   }

   public void checkViewIsActive(String viewId) throws Exception
   {
      assertTrue(selenium().isElementPresent("//div[@view-id='" + viewId + "'" + "and @is-active='true']"));
   }

   public void checkViewIsNotActive(String viewId) throws Exception
   {
      assertFalse(selenium().isElementPresent("//div[@view-id='" + viewId + "'" + "and @is-active='true']"));
      assertTrue(selenium().isElementPresent("//div[@view-id='" + viewId + "'" + "and @is-active='false']"));
   }

   public void checkViewIsNotPresent(String viewId) throws Exception
   {
      assertFalse(selenium().isElementPresent("//div[@view-id='" + viewId + "'" + "and @is-active='true']"));
      assertFalse(selenium().isElementPresent("//div[@view-id='" + viewId + "'" + "and @is-active='false']"));
   }

   /**
    * Locator in this method can be used to select the other menus 
    * (change index in this part for select next tab.Start index value of 2 
    * (mark *)//table[@id='operation-panel-switcher']/tbody/tr/td/table/tbody/tr/td[*])
    * @param viewId
    * @throws Exception
    */
   public void clickOnIconPropertiesTab(String viewId) throws Exception
   {
      selenium()
         .click(
            "//div[@panel-id='operation']//table/tbody/tr/td/table/tbody/tr/td[2]//div[@class='tabMiddleCenterInner']/div/div/table/tbody/tr/td[1]/img");
      //selenium().click("//table[@id='operation-panel-switcher']/tbody/tr/td/table/tbody/tr/td[2]//div[@class='tabMiddleCenterInner']/div/div/table/tbody/tr/td[1]/img");
   }

   /**
    * Returns the active state of the view.
    * 
    * @param view view
    * @return boolean view's active state 
    */
   public boolean isViewActive(WebElement view)
   {
      return (view != null) ? Boolean.parseBoolean(view.getAttribute("is-active")) : false;
   }

   public void activateView(String viewId)
   {
      fail();
   }

   /**
    * Forms view locator by its id.
    * 
    * @param viewId
    * @return {@link String} view's locator
    */
   public String getViewLocator(String viewId)
   {
      return "//div[@view-id=\"" + viewId + "\"]";
   }

   /**
    * Get close button of the view.
    * 
    * @param viewTitle view's title
    * @return {@link WebElement} close button
    */
   public WebElement getCloseViewButton(String viewTitle)
   {
      return driver().findElement(By.cssSelector(String.format(Locators.CLOSE_BUTTON_SELECTOR, viewTitle)));
   }
}
