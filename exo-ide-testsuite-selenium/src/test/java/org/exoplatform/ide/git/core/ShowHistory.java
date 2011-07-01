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
 * @version $Id:  Jun 29, 2011 2:04:17 PM anya $
 *
 */
public class ShowHistory extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "ideHistoryView";

      String VIEW_TITLE = "History";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String REVISION_GRID_ID = "ideRevisionGrid";

      String REVISION_ROW_LOCATOR = "//table[@id=\"" + REVISION_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";

      String BUTTON_LOCATOR = "//div[@title=\"%s\"]";

      String CHANGES_IN_PROJECT_BUTTON_TITLE = "Show changes in project";

      String CHANGES_OF_RESOURCE_BUTTON_TITLE = "Show changes of selected resource";

      String DIFF_INDEX_BUTTON_TITLE = "Show diff with index";

      String DIFF_WORK_TREE_BUTTON_TITLE = "Show diff with working tree";

      String DIFF_PREV_REVISION_BUTTON_TITLE = "Show diff with previous version";

      String REFRESH_REVISION_LIST_BUTTON_TITLE = "Refresh revision list";

      String CHANGES_IN_PROJECT_BUTTON = String.format(BUTTON_LOCATOR, CHANGES_IN_PROJECT_BUTTON_TITLE);

      String CHANGES_OF_RESOURCE_BUTTON = String.format(BUTTON_LOCATOR, CHANGES_OF_RESOURCE_BUTTON_TITLE);

      String DIFF_INDEX_BUTTON = String.format(BUTTON_LOCATOR, DIFF_INDEX_BUTTON_TITLE);

      String DIFF_WORK_TREE_BUTTON = String.format(BUTTON_LOCATOR, DIFF_WORK_TREE_BUTTON_TITLE);

      String DIFF_PREV_REVISION_BUTTON = String.format(BUTTON_LOCATOR, DIFF_PREV_REVISION_BUTTON_TITLE);

      String REFRESH_REVISION_LIST_BUTTON = String.format(BUTTON_LOCATOR, REFRESH_REVISION_LIST_BUTTON_TITLE);

      String INDEX_STATE = "index state";

      String WORKING_TREE_STATE = "working tree state";

      String NOTHING_FOR_COMPARANCE = "nothing for comparance";

      String BUTTON_SELECTED_LOCATOR = "css=div[class*=exoIconButtonPanelSelected][title*=\"%s\"]";
   }

   /**
    * Waits for History view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for History view to be closed.
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
         && selenium().isElementPresent(Locators.CHANGES_IN_PROJECT_BUTTON)
         && selenium().isElementPresent(Locators.CHANGES_OF_RESOURCE_BUTTON)
         && selenium().isElementPresent(Locators.DIFF_INDEX_BUTTON)
         && selenium().isElementPresent(Locators.DIFF_PREV_REVISION_BUTTON)
         && selenium().isElementPresent(Locators.DIFF_WORK_TREE_BUTTON)
         && selenium().isElementPresent(Locators.REFRESH_REVISION_LIST_BUTTON);
   }

   /**
    * Get the selection state of changes in project button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isChangesInProjectButtonSelected()
   {
      return selenium().isElementPresent(
         String.format(Locators.BUTTON_SELECTED_LOCATOR, Locators.CHANGES_IN_PROJECT_BUTTON_TITLE));
   }

   /**
    * Get the selection state of resource changes button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isChangesOfResourceButtonSelected()
   {
      return selenium().isElementPresent(
         String.format(Locators.BUTTON_SELECTED_LOCATOR, Locators.CHANGES_OF_RESOURCE_BUTTON_TITLE));
   }

   /**
    * Get the selection state of diff index button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isDiffIndexButtonSelected()
   {
      return selenium().isElementPresent(
         String.format(Locators.BUTTON_SELECTED_LOCATOR, Locators.DIFF_INDEX_BUTTON_TITLE));
   }

   /**
    * Get the selection state of diff working tree button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isDiffWorkTreeButtonSelected()
   {
      return selenium().isElementPresent(
         String.format(Locators.BUTTON_SELECTED_LOCATOR, Locators.DIFF_WORK_TREE_BUTTON_TITLE));
   }

   /**
    * Get the selection state of diff previous revision button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isDiffPrevRevisionButtonSelected()
   {
      return selenium().isElementPresent(
         String.format(Locators.BUTTON_SELECTED_LOCATOR, Locators.DIFF_PREV_REVISION_BUTTON_TITLE));
   }

   /**
    * Click changes in project button.
    */
   public void clickChangesInProjectButton()
   {
      selenium().click(Locators.CHANGES_IN_PROJECT_BUTTON);
   }

   /**
    * Click resource changes button.
    */
   public void clickChangesOfResourceButton()
   {
      selenium().click(Locators.CHANGES_OF_RESOURCE_BUTTON);
   }

   /**
    * Click diff with index button.
    */
   public void clickDiffIndexButton()
   {
      selenium().click(Locators.DIFF_INDEX_BUTTON);
   }

   /**
    * Click diff with work tree button.
    */
   public void clickDiffWorkTreeStatusButton()
   {
      selenium().click(Locators.DIFF_WORK_TREE_BUTTON);
   }

   /**
    * Click diff with previous version button.
    */
   public void clickDiffPrevRevisionButton()
   {
      selenium().click(Locators.DIFF_PREV_REVISION_BUTTON);
   }

   /**
    * Click refresh version list button.
    */
   public void clickRefreshRevisionListButton()
   {
      selenium().click(Locators.REFRESH_REVISION_LIST_BUTTON);
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

   /**
    * Closes the view.
    * 
    * @throws Exception
    */
   public void closeView() throws Exception
   {
      selenium().click("//div[@button-name='close-tab' and @tab-title='" + Locators.VIEW_TITLE + "']");
      waitForViewClosed();
   }

   /**
    * Wait for the pointed number of the commits.
    * 
    * @param count number of the commits
    * @throws Exception
    */
   public void waitForCommitsCount(int count) throws Exception
   {
      waitForElementPresent("//table[@id=\"" + Locators.REVISION_GRID_ID + "\"]/tbody[1]//tr[" + count + "]");
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
    * 
    * 
    * @return String diff text
    */
   public String getDiffText()
   {
      selenium().selectFrame(Locators.VIEW_LOCATOR + "//iframe");
      String text = selenium().getText("//body");
      IDE().selectMainFrame();
      return text;
   }
}
