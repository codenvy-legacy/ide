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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

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

      String PANEL_MAXIMIZED_ATTRIBUTE = "panel-maximized";

      String PANEL_LOCATOR = "//div[@panel-id='%s']";

      String VIEW_LOCATOR = "//div[@view-id='%s']";

      String ACTIVE_VIEW_ATTRIBUTE = "is-active";

      String RESORE_BUTTON_ID = "%s-restore";

      String MAXIMIZE_BUTTON_ID = "%s-maximize";
   }

   /**
    * Maximize panel by click on maximize button.
    * 
    * @param panelId panel's id
    * @throws Exception
    */
   public void maximizePanel(String panelId) throws Exception
   {
      WebElement maximizeButton = driver().findElement(By.id(String.format(Locators.MAXIMIZE_BUTTON_ID, panelId)));
      maximizeButton.click();
      waitMaximized(panelId);
   }

   /**
    * Restore panel's size by clicking restore button.
    * 
    * @param panelId panel's id
    * @throws Exception
    */
   public void restorePanel(String panelId) throws Exception
   {
      WebElement restoreButton = driver().findElement(By.id(String.format(Locators.RESORE_BUTTON_ID, panelId)));
      restoreButton.click();
      waitRestored(panelId);
   }

   /**
    * Wait panel is maximized.
    * 
    * @param panelId panel's id
    */
   private void waitMaximized(final String panelId)
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            return isPanelMaximized(panelId);
         }
      });
   }

   /**
    * Wait panel is restored.
    * 
    * @param panelId panel's id
    */
   private void waitRestored(final String panelId)
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            return !isPanelMaximized(panelId);
         }
      });
   }

   /**
    * Returns whether panel is maximized.
    * 
    * @param panelId panel's id
    * @return {@link Boolean} <code>true</code> if panel is maximized
    */
   public boolean isPanelMaximized(String panelId)
   {
      WebElement panel = driver().findElement(By.xpath(String.format(Locators.PANEL_LOCATOR, panelId)));
      String attribute = panel.getAttribute(Locators.PANEL_MAXIMIZED_ATTRIBUTE);
      return panel.isDisplayed() && attribute != null && Boolean.parseBoolean(attribute);
   }

   /**
    * Returns the active state of the view.
    * 
    * @param view view
    * @return boolean view's active state 
    */
   public boolean isViewActive(WebElement view)
   {
      return (view != null) ? Boolean.parseBoolean(view.getAttribute(Locators.ACTIVE_VIEW_ATTRIBUTE)) : false;
   }

   /**
    * Forms view locator by its id.
    * 
    * @param viewId
    * @return {@link String} view's locator
    */
   public String getViewLocator(String viewId)
   {
      return String.format(Locators.VIEW_LOCATOR, viewId);
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
