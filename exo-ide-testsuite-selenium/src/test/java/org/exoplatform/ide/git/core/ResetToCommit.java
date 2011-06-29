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
 * @version $Id:  Jun 27, 2011 5:34:45 PM anya $
 *
 */
public class ResetToCommit extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "ideResetToCommitView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String REVERT_BUTTON_ID = "ideRevertToCommitViewRevertButton";

      String CANCEL_BUTTON_ID = "ideRevertToCommitViewCancelButton";

      String MODE_ID = "ideRevertToCommitViewMode";

      String REVISION_GRID_ID = "ideRevisionGrid";

      String REVISION_ROW_LOCATOR = "//table[@id=\"" + REVISION_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";
   }

   /**
    * Waits for Reset commit view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Reset commit view to be closed.
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
      return selenium().isElementPresent(Locators.REVISION_GRID_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID)
         && selenium().isElementPresent(Locators.REVERT_BUTTON_ID) && selenium().isElementPresent(Locators.MODE_ID);
   }

   /**
    * Click Revert button.
    */
   public void clickRevertButton()
   {
      selenium().click(Locators.REVERT_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Select "soft" mode of the revert operation.
    */
   public void selectSoftMode()
   {
      selenium().click("//label[contains(text(), \"soft\")]");
   }

   /**
    * Select "mixed" mode of the revert operation.
    */
   public void selectMixedMode()
   {
      selenium().click("//label[contains(text(), \"mixed\")]");
   }

   /**
    * Select "hard" mode of the revert operation.
    */
   public void selectHardMode()
   {
      selenium().click("//label[contains(text(), \"hard\")]");
   }

   /**
    * Select the row with revision by the pointed comment.
    * 
    * @param comment
    */
   public void selectRevisionByComment(String comment)
   {
      selenium().click(String.format(Locators.REVISION_ROW_LOCATOR, comment));
   }

   /**
    * Get the number of revisions in grid.
    * 
    * @return count of revisions
    */
   public int getRevisionsCount()
   {
      return selenium().getXpathCount("//table[@id=\"" + Locators.REVISION_GRID_ID + "\"]/tbody[1]//tr").intValue();
   }

}
