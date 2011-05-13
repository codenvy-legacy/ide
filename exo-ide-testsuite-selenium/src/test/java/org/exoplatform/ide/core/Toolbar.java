/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.exoplatform.ide.TestConstants;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Toolbar extends AbstractTestModule
{

   /**
    * Performs click on toolbar button and makes pause after it.
    * @param buttonTitle toolbar button title
    */
   public void runCommand(String buttonTitle, boolean wait) throws Exception
   {
      String locator =
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]//div[@title=\"" + buttonTitle + "\"]";

      selenium().mouseOver(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().click(locator);
      //selenium.mouseUpAt(locator, "");
      if (wait)
      {
         Thread.sleep(TestConstants.REDRAW_PERIOD);
      }
      try
      {
         selenium().mouseOut(locator);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Performs click on toolbar button and makes pause after it.
    * @param buttonTitle toolbar button title
    */
   public void runCommand(String buttonTitle) throws Exception
   {
      runCommand(buttonTitle, true);
   }

   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * @param menuName
    */
   public void runCommandFromNewPopupMenu(String menuItemName) throws Exception
   {
      runCommand("New");

      String locator = "//table[@class='exo-popupMenuTable']//tbody//td//nobr[text()='" + menuItemName + "']";
      selenium().mouseOver(locator);
      //selenium.click(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().click(locator);
      //String hoverLocator =
      //   "//table[@class='exo-popupMenuTable']//tbody//td//nobr[text()='" + menuItemName + "']";

      //selenium.mouseUp(hoverLocator);

      //time to wait while gadget open new file
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * Check is button present on toolbar and is it enabled or disabled.
    * 
    * @param name button name
    * @param enabled boolean value
    */
   public void assertButtonEnabled(String name, boolean enabled)
   {
      if (enabled)
      {
         String locator =
            "//div[@id=\"exoIDEToolbar\" and @class=\"exoToolbarPanel\"]//div[@enabled=\"true\" and @title=\"" + name
               + "\"]";
         assertTrue(selenium().isElementPresent(locator));
      }
      else
      {
         String locator =
            "//div[@id=\"exoIDEToolbar\" and @class=\"exoToolbarPanel\"]//div[@enabled=\"false\" and @title=\"" + name
               + "\"]";
         assertTrue(selenium().isElementPresent(locator));
      }
   }

   public void waitForButtonEnabled(String name, boolean enabled, int waitPeriod)
   {
      String locator = null;
      if (enabled)
      {
         locator =
            "//div[@id=\"exoIDEToolbar\" and @class=\"exoToolbarPanel\"]//div[@enabled=\"true\" and @title=\"" + name
               + "\"]";
      }
      else
      {
         locator =
            "//div[@id=\"exoIDEToolbar\" and @class=\"exoToolbarPanel\"]//div[@enabled=\"false\" and @title=\"" + name
               + "\"]";
      }
      for (int second = 0;; second++)
      {
         if (second >= waitPeriod)
            fail("timeout for element " + locator);

         if (selenium().isElementPresent(locator))
            break;
      }
   }

   /**
    * Check is button present on toolbar
    * 
    * @param name button name (title in DOM)
    * @param isPresent is present
    */
   public void assertButtonExistAtLeft(String name, boolean exist)
   {
      String locator =
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]/div[@class=\"exoToolbarElementLeft\"]"
            + "/div[contains(@class, \"exoIconButtonPanel\") and @title=\"" + name + "\"]";
      if (exist)
      {
         assertTrue(selenium().isVisible(locator));
      }
      else
      {

         assertTrue(!selenium().isElementPresent(locator) || !selenium().isVisible(locator));

      }
   }

   /**
    * Check is button present on toolbar
    * 
    * @param name button name (title in DOM)
    * @param isPresent is present
    */
   public void checkButtonExistAtRight(String name, boolean exist)
   {
      String locator =
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]//div[@class=\"exoToolbarElementRight\"]//div[@class=\"exoIconButtonPanel\" and @title=\""
            + name + "\"]";

      if (exist)
      {
         assertTrue(selenium().isElementPresent(locator));
      }
      else
      {
         if (selenium().isElementPresent(locator))
            assertFalse(selenium().isVisible(locator));
      }
   }

   /**
    * Get the button's selected state.
    * 
    * @param name button's name
    * @return if <code>true</code>, then button is selected
    */
   public boolean isButtonSelected(String name)
   {
      return selenium().isElementPresent(
         "//div[@class='exo-toolbar16ButtonPanel_Right' and @title='" + name
            + "']/div[@class='exo-toolbar16Button-selected' and @elementenabled='true']");
   }

   public void assertButtonPresent(String name, boolean present)
   {
      //      String locator =
      //         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]//div[@class=\"exoIconButtonPanel\" and @title=\"" + name + "\"]";
      //
      //      if (present)
      //      {
      //         assertTrue(selenium.isElementPresent(locator));
      //      }
      //      else
      //      {
      //         assertFalse(selenium.isElementPresent(locator));
      //      }

   }

}
