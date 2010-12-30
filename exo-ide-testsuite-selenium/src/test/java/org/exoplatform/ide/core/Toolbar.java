/**
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
 *
 */

package org.exoplatform.ide.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Toolbar
{

   private Selenium selenium;

   public Toolbar(Selenium selenium)
   {
      this.selenium = selenium;
   }

   /**
    * Performs click on toolbar button and makes pause after it.
    * @param buttonTitle toolbar button title
    */
   public void runCommand(String buttonTitle) throws Exception
   {
      String locator =
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]//div[@title=\"" + buttonTitle + "\"]";

      selenium.mouseOver(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      String hoverLocator =
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]//div[@title=\"" + buttonTitle + "\"]";
//      selenium.mouseDownAt(hoverLocator, "");
//      selenium.mouseUpAt(hoverLocator, "");
      selenium.clickAt(hoverLocator, "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      try
      {
         selenium.mouseOut(hoverLocator);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * @param menuName
    */
   public void runCommandFromNewPopupMenu(String menuItemName) throws Exception
   {
      runCommand("New");

      String locator =
         "//table[@class=\"exo-popupMenuTable\"]//td[@class=\"exo-popupMenuTitleField\"]//nobr[text()=\""
            + menuItemName + "\"]";
      selenium.mouseOver(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      String hoverLocator =
         "//table[@class=\"exo-popupMenuTable\"]//td[@class=\"exo-popupMenuTitleFieldOver\"]//nobr[text()=\""
            + menuItemName + "\"]";
      selenium.mouseDownAt(hoverLocator, "");
      //time to wait while gadget open new file
      Thread.sleep(TestConstants.REDRAW_PERIOD);
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
         assertTrue(selenium.isElementPresent(locator));
      }
      else
      {
         String locator =
            "//div[@id=\"exoIDEToolbar\" and @class=\"exoToolbarPanel\"]//div[@enabled=\"false\" and @title=\"" + name
               + "\"]";
         assertTrue(selenium.isElementPresent(locator));
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
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]/div[@class=\"exoToolbarElementLeft\"]/div[@class=\"exoIconButtonPanel\" and @title=\""
         + name + "\"]";
      if (exist)
      {
         assertTrue(selenium.isVisible(locator));
      }
      else
      {
         
         assertFalse(selenium.isVisible(locator));

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
         assertTrue(selenium.isElementPresent(locator));
      }
      else
      {
         if(selenium.isElementPresent(locator))
          assertFalse(selenium.isVisible(locator));
      }
   }
   
   public void assertButtonPresent(String name, boolean present) {
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
