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
package org.exoplatform.ide.git.core;

import org.exoplatform.ide.IDE;
import org.exoplatform.ide.core.AbstractTestModule;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 24, 2011 3:19:00 PM anya $
 *
 */
public class ResetFiles extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "ideResetFilesView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String RESET_BUTTON_ID = "ideResetFilesViewResetButton";

      String CANCEL_BUTTON_ID = "ideResetFilesViewCancelButton";

      String INDEX_FILES_GRID_ID = "ideIndexFilesGrid";

      String ITEM_CHECKBOX_LOCATOR = "//table[@id=\"" + INDEX_FILES_GRID_ID
         + "\"]//tr[contains(., \"%s\")]//input[@type=\"checkbox\"]";
   }

   /**
    * Waits for Reset files view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Reset files view to be closed.
    * 
    * @throws Exception
    */
   public void waitForViewClosed() throws Exception
   {
      waitForElementNotPresent(Locators.VIEW_LOCATOR);
   }

   /**
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isViewComponentsPresent()
   {
      return selenium().isElementPresent(Locators.INDEX_FILES_GRID_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID)
         && selenium().isElementPresent(Locators.RESET_BUTTON_ID);
   }

   /**
    * Click Reset button.
    */
   public void clickResetButton()
   {
      selenium().click(Locators.RESET_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Make item checked.
    * 
    * @param name item's name
    */
   public void checkFileByName(String name)
   {
      selenium().check(String.format(Locators.ITEM_CHECKBOX_LOCATOR, name));
   }

   /**
    * Make item unchecked.
    * 
    * @param name item's name
    */
   public void unCheckFileByName(String name)
   {
      selenium().uncheck(String.format(Locators.ITEM_CHECKBOX_LOCATOR, name));
   }

   /**
    * Get the number of files in index grid.
    * 
    * @return count of files
    */
   public int getFilesCount()
   {
      return selenium().getXpathCount("//table[@id=\"" + Locators.INDEX_FILES_GRID_ID + "\"]/tbody[1]//tr").intValue();
   }
}
