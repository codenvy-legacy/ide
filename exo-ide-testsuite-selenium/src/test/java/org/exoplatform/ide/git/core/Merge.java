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
 * @version $Id:  Jul 22, 2011 12:31:53 PM anya $
 *
 */
public class Merge extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "MergeView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String MERGE_BUTTON_ID = "MergeViewMergeButton";

      String CANCEL_BUTTON_ID = "MergeViewCancelButton";

      String REF_TREE_ID = "MergeViewRefTree";
   }

   public interface Messages
   {
      String FAST_FORWARD = "Fast-forward";
      
      String CONFLICTING = "Conflicting";

      String MERGED_COMMITS = "Merged commits:";

      String NEW_HEAD_COMMIT = "New HEAD commit:";
      
      String CONFLICTS = "Conflicts:\n%s";
      
      String UP_TO_DATE = "Already up-to-date";
   }

   /**
    * Waits for Merge view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Merge view to be closed.
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
      return selenium().isElementPresent(Locators.MERGE_BUTTON_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID) && selenium().isElementPresent(Locators.REF_TREE_ID);
   }

   /**
    * Click Merge button.
    */
   public void clickMergeButton()
   {
      selenium().click(Locators.MERGE_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Check Merge button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isMergeButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.MERGE_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   public boolean isRererencePresent(String name)
   {
      return selenium().isElementPresent("//div[@id='" + Locators.REF_TREE_ID + "']//div[contains(*, '" + name + "')]");
   }

   public void waitRererenceVisible(String name) throws Exception
   {
      waitForElementVisible("//div[@id='" + Locators.REF_TREE_ID + "']//div[contains(*, '" + name + "')]");
   }

   public void selectReference(String name)
   {
      selenium().clickAt("//div[@id='" + Locators.REF_TREE_ID + "']//div[contains(text(), '" + name + "')]", "1,1");
   }

   public void doubleClickReference(String name)
   {
      selenium().doubleClick("//div[@id='" + Locators.REF_TREE_ID + "']//div[contains(text(), '" + name + "')]");
   }

}
