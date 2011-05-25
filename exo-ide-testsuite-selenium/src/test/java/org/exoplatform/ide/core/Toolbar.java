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

import org.exoplatform.ide.MenuCommands;
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
   public void runCommand(String buttonTitle) throws Exception
   {
      String locator = "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]//div[@title=\"" + buttonTitle + "\"]";
      selenium().click(locator);

      if ("New".equals(buttonTitle)) {
         waitForElementPresent("//div[@id='menu-lock-layer-id']//table[@class='exo-popupMenuTable']");
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

      String locator = "//table[@class='exo-popupMenuTable']//tbody//td//nobr[text()='" + menuItemName + "']";
      selenium().click(locator);

      if (menuItemName.equals(MenuCommands.New.PROJECT_TEMPLATE))
      {
         waitForElementPresent("ideCreateProjectTemplateForm");
      }
      else if (menuItemName.equals(MenuCommands.New.FOLDER))
      {
         waitForElementPresent("ideCreateFolderForm");
      }
      else if (menuItemName.equals(MenuCommands.New.PROJECT_FROM_TEMPLATE))
      {
         waitForElementPresent("//div[@view-id='ideCreateProjectFromTemplateView']");
      }
      else if (menuItemName.equals(MenuCommands.New.FILE_FROM_TEMPLATE))
      {
         waitForElementPresent("//div[@view-id='ideCreateFileFromTemplateForm']");
      }
      else if (menuItemName.equals(MenuCommands.New.GOOGLE_GADGET_FILE) ||
               menuItemName.equals(MenuCommands.New.REST_SERVICE_FILE) ||
               menuItemName.equals(MenuCommands.New.GROOVY_SCRIPT_FILE) ||
               menuItemName.equals(MenuCommands.New.CHROMATTIC) ||
               menuItemName.equals(MenuCommands.New.HTML_FILE) ||
               menuItemName.equals(MenuCommands.New.JAVASCRIPT_FILE) ||
               menuItemName.equals(MenuCommands.New.CSS_FILE) ||
               menuItemName.equals(MenuCommands.New.GROOVY_TEMPLATE_FILE) ||
               menuItemName.equals(MenuCommands.New.XML_FILE) ||
               menuItemName.equals(MenuCommands.New.TEXT_FILE) ||
               menuItemName.equals(MenuCommands.New.NETVIBES_WIDGET) ||
               menuItemName.equals(MenuCommands.New.JAVA_CLASS) ||
               menuItemName.equals(MenuCommands.New.JSP) ||
               menuItemName.equals(MenuCommands.New.RUBY) ||
               menuItemName.equals(MenuCommands.New.PHP)
               ) {
         IDE().EDITOR.waitEditorFileOpened();
      }
      else
      {
         waitForElementNotPresent("menu-lock-layer-id");
      }
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

   public void waitForButtonEnabled(String name, boolean enabled) throws Exception
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
      
      long startTime = System.currentTimeMillis();
      while (true) {
         if (selenium().isElementPresent(locator)) {
            break;
         }
         
         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT) {
            fail("timeout for element " + locator);
         }
         
         Thread.sleep(1000);
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
