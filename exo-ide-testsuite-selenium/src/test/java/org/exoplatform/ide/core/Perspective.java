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

import org.junit.Assert;

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
   
   
   
   public boolean isViewActive(String viewId)
   {
      fail();
      return false;
   }

   public void activateView(String viewId)
   {
      fail();
   }

}
