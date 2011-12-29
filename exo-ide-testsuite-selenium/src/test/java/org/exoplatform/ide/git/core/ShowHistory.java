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

import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 29, 2011 2:04:17 PM anya $
 *
 */
public class ShowHistory extends AbstractTestModule
{
   private interface Locators
   {
      String VIEW_ID = "ideHistoryView";

      String VIEW_TITLE = "History";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String REVISION_GRID_ID = "ideRevisionGrid";

      String REVISION_ROW_LOCATOR = "//table[@id=\"" + REVISION_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";

      String CHANGES_IN_PROJECT_BUTTON_TITLE = "Show changes in project";

      String CHANGES_OF_RESOURCE_BUTTON_TITLE = "Show changes of selected resource";

      String DIFF_INDEX_BUTTON_TITLE = "Show diff with index";

      String DIFF_WORK_TREE_BUTTON_TITLE = "Show diff with working tree";

      String DIFF_PREV_REVISION_BUTTON_TITLE = "Show diff with previous version";

      String REFRESH_REVISION_LIST_BUTTON_TITLE = "Refresh revision list";

      String CHANGES_IN_PROJECT_BUTTON_SELECTOR = "div[title='" + CHANGES_IN_PROJECT_BUTTON_TITLE + "']";

      String CHANGES_OF_RESOURCE_BUTTON_SELECTOR = "div[title='" + CHANGES_OF_RESOURCE_BUTTON_TITLE + "']";

      String DIFF_INDEX_BUTTON_SELECTOR = "div[title='" + DIFF_INDEX_BUTTON_TITLE + "']";

      String DIFF_WORK_TREE_BUTTON_SELECTOR = "div[title='" + DIFF_WORK_TREE_BUTTON_TITLE + "']";

      String DIFF_PREV_REVISION_BUTTON_SELECTOR = "div[title='" + DIFF_PREV_REVISION_BUTTON_TITLE + "']";

      String REFRESH_REVISION_LIST_BUTTON_SELECTOR = "div[title='" + REFRESH_REVISION_LIST_BUTTON_TITLE + "']";

      String INDEX_STATE = "index state";

      String WORKING_TREE_STATE = "working tree state";

      String NOTHING_FOR_COMPARANCE = "nothing for comparance";

      String BUTTON_SELECTED = "exoIconButtonPanelSelected";

      String REVISION_ROW_SELECTOR = "table#" + REVISION_GRID_ID + ">tbody:first-of-type tr";

      String CONTENT_FRAME_LOCATOR = Locators.VIEW_LOCATOR + "//iframe";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = Locators.REVISION_GRID_ID)
   private WebElement revisionGrid;

   @FindBy(css = Locators.CHANGES_IN_PROJECT_BUTTON_SELECTOR)
   private WebElement projectChangesButton;

   @FindBy(css = Locators.CHANGES_OF_RESOURCE_BUTTON_SELECTOR)
   private WebElement resourceButton;

   @FindBy(css = Locators.DIFF_INDEX_BUTTON_SELECTOR)
   private WebElement diffIndexButton;

   @FindBy(css = Locators.DIFF_WORK_TREE_BUTTON_SELECTOR)
   private WebElement diffWorkTreeButton;

   @FindBy(css = Locators.DIFF_PREV_REVISION_BUTTON_SELECTOR)
   private WebElement diffPrevRevisionButton;

   @FindBy(css = Locators.REFRESH_REVISION_LIST_BUTTON_SELECTOR)
   private WebElement refreshButton;

   @FindBy(xpath = Locators.CONTENT_FRAME_LOCATOR)
   private WebElement contentFrame;

   /**
    * Waits for History view to be opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return isOpened();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Waits for History view to be closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isOpened()
   {
      return (view != null && view.isDisplayed() && revisionGrid != null && revisionGrid.isDisplayed()
         && projectChangesButton != null && projectChangesButton.isDisplayed() && resourceButton != null
         && resourceButton.isDisplayed() && diffIndexButton != null && diffIndexButton.isDisplayed()
         && diffPrevRevisionButton != null && diffPrevRevisionButton.isDisplayed() && diffWorkTreeButton != null
         && diffWorkTreeButton.isDisplayed() && refreshButton != null && refreshButton.isDisplayed());
   }

   public boolean isActive()
   {
      return IDE().PERSPECTIVE.isViewActive(view);
   }

   /**
    * Returns button's selected state.
    * 
    * @param button button's element
    * @return {@link Boolean} selected state of the button
    */
   private boolean isButtonSelected(WebElement button)
   {
      return button.getAttribute("class").contains(Locators.BUTTON_SELECTED);
   }

   /**
    * Get the selection state of changes in project button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isChangesInProjectButtonSelected()
   {
      return isButtonSelected(projectChangesButton);
   }

   /**
    * Get the selection state of resource changes button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isChangesOfResourceButtonSelected()
   {
      return isButtonSelected(resourceButton);
   }

   /**
    * Get the selection state of diff index button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isDiffIndexButtonSelected()
   {
      return isButtonSelected(diffIndexButton);
   }

   /**
    * Get the selection state of diff working tree button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isDiffWorkTreeButtonSelected()
   {
      return isButtonSelected(diffWorkTreeButton);
   }

   /**
    * Get the selection state of diff previous revision button.
    * 
    * @return <code>true<code> if button is selected
    */
   public boolean isDiffPrevRevisionButtonSelected()
   {
      return isButtonSelected(diffPrevRevisionButton);
   }

   /**
    * Click changes in project button.
    */
   public void clickChangesInProjectButton()
   {
      projectChangesButton.click();
   }

   /**
    * Click resource changes button.
    */
   public void clickChangesOfResourceButton()
   {
      resourceButton.click();
   }

   /**
    * Click diff with index button.
    */
   public void clickDiffIndexButton()
   {
      diffIndexButton.click();
   }

   /**
    * Click diff with work tree button.
    */
   public void clickDiffWorkTreeStatusButton()
   {
      diffWorkTreeButton.click();
   }

   /**
    * Click diff with previous version button.
    */
   public void clickDiffPrevRevisionButton()
   {
      diffPrevRevisionButton.click();
   }

   /**
    * Click refresh version list button.
    */
   public void clickRefreshRevisionListButton()
   {
      refreshButton.click();
   }

   /**
    * Get the number of revisions in grid.
    * 
    * @return count of revisions
    */
   public int getRevisionsCount()
   {
      return driver().findElements(By.cssSelector(Locators.REVISION_ROW_SELECTOR)).size();
   }

   /**
    * Closes the view.
    * 
    * @throws Exception
    */
   public void closeView() throws Exception
   {
      IDE().PERSPECTIVE.getCloseViewButton(Locators.VIEW_TITLE).click();
   }

   /**
    * Wait for the pointed number of the commits.
    * 
    * @param count number of the commits
    * @throws Exception
    */
   public void waitForRevisionsCount(final int count) throws Exception
   {
      new WebDriverWait(driver(), 4).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return count == getRevisionsCount();
         }
      });
   }

   /**
    * Select the row with revision by the pointed comment.
    * 
    * @param comment
    */
   public void selectRevisionByComment(String comment)
   {
      WebElement revision = driver().findElement(By.xpath(String.format(Locators.REVISION_ROW_LOCATOR, comment)));
      revision.click();
   }

   /**
    * Get the content of diff operation.
    * 
    * @return String diff text
    */
   public String getDiffText()
   {
      WebElement body = driver().switchTo().frame(contentFrame).findElement(By.tagName("body"));
      String text = body.getText();
      IDE().selectMainFrame();
      return text;
   }

   /**
    * Returns whether it is nothing for comparison state.
    * 
    * @return {@link Boolean} 
    */
   public boolean isNothingForComparanceState()
   {
      return view.getText().contains(Locators.NOTHING_FOR_COMPARANCE);
   }

   /**
    * Returns whether it is compare with index state.
    * 
    * @return {@link Boolean} 
    */
   public boolean isCompareWithIndexState()
   {
      return view.getText().contains(Locators.INDEX_STATE);
   }

   /**
    * Returns whether it is compare with working tree state.
    * 
    * @return {@link Boolean} 
    */
   public boolean isCompareWithWorkingTree()
   {
      return view.getText().contains(Locators.WORKING_TREE_STATE);
   }
}
