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

import org.exoplatform.ide.TestConstants;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Menu extends AbstractTestModule
{

   /**
    * Open command from top menu.
    * 
    * @param topMenuName name of menu
    * @param commandName command name
    */
   public void runCommand(String topMenuName, String commandName) throws Exception
   {
      selenium().mouseDown("//td[@class='exo-menuBarItem' and text()='" + topMenuName + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().click("//td[@class='exo-popupMenuTitleField']/nobr[text()='" + commandName + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   public void runCommand(String menuName, String commandName, String subCommandName) throws Exception {
      selenium().click("//td[@class='exo-menuBarItem' and text()='" + menuName + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().click("//td[@class='exo-popupMenuTitleField']/nobr[text()='" + commandName + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      
      selenium().click("//td[@class='exo-popupMenuTitleField']/nobr[text()='" + subCommandName + "']");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Check is command in top menu visible or hidden.
    * 
    * @param topMenuName mane of menu
    * @param commandName command name
    * @param isPresent boolean value
    */
   public void checkCommandVisibility(String topMenuName, String commandName, boolean visible) throws Exception
   {
      selenium().mouseDownAt("//td[@class='exo-menuBarItem' and text()='" + topMenuName + "']", "");

      if (visible)
      {
         assertTrue(selenium().isElementPresent("//td/nobr[text()='" + commandName + "']"));
      }
      else
      {
         assertFalse(selenium().isElementPresent("//td/nobr[text()='" + commandName + "']"));
      }
      selenium().mouseDown("//div[@class='exo-lockLayer']/");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Check is command in top menu enabled or disabled.
    * 
    * @param topMenuName mane of menu
    * @param commandName command name
    * @param enabled boolean value
    */
   public void checkCommandEnabled(String topMenuName, String commandName, boolean enabled) throws Exception
   {
      selenium().mouseDownAt("//td[@class='exo-menuBarItem' and text()='" + topMenuName + "']", "");

      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      if (enabled)
      {
         assertTrue(selenium().isElementPresent("//table[@class=\"exo-popupMenuTable\"]//td[@class=\"exo-popupMenuTitleFieldOver\"]"));
      }
      else
      {
         assertTrue(selenium().isElementPresent("//table[@class=\"exo-popupMenuTable\"]//td[@class=\"exo-popupMenuTitleFieldDisabled\"]/nobr[text()='"
            + commandName + "']"));
      }
      selenium().mouseDown("//div[@class='exo-lockLayer']/");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   /**
    * Get the XPATH locator for top menu command.
    * 
    * @param title - the title of top menu command.
    * 
    * @return {@link String}
    */
   public String getMenuLocator(String title)
   {
      return "//td[@class='exo-menuBarItem' and text()='" + title + "']";
   }
   
   public void waitForMenuItemPresent(String itemName) throws Exception {
      waitForElementPresent(getMenuLocator(itemName));
   }

}
