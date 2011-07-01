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
 * @version $Id:  Jul 1, 2011 3:23:05 PM anya $
 *
 */
public class Pull extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "idePullView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String PULL_BUTTON_ID = "idePullViewPullButton";

      String CANCEL_BUTTON_ID = "idePullViewCancelButton";

      String REMOTE_FIELD_ID = "idePullViewRemoteField";

      String REMOTE_BRANCHES_FIELD_ID = "idePullViewRemoteBranchesField";

      String LOCAL_BRANCHES_FIELD_ID = "idePullViewLocalBranchesField";
   }

   /**
    * Waits for Pull view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Pull view to be closed.
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
      return selenium().isElementPresent(Locators.PULL_BUTTON_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID)
         && selenium().isElementPresent(Locators.REMOTE_FIELD_ID)
         && selenium().isElementPresent(Locators.LOCAL_BRANCHES_FIELD_ID)
         && selenium().isElementPresent(Locators.REMOTE_BRANCHES_FIELD_ID);
   }

   /**
    * Click Pull button.
    */
   public void clickPullButton()
   {
      selenium().click(Locators.PULL_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Check Pull button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isPullButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.PULL_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Check Cancel button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCancelButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.CANCEL_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Get displayed value of remote repository field.
    * 
    * @return {@link String} displayed value
    */
   public String getRemoteRepositoryValue()
   {
      return selenium().getText(Locators.REMOTE_FIELD_ID);
   }

   /**
    * Get displayed value of remote branch field.
    * 
    * @return {@link String} displayed value
    */
   public String getRemoteBranchValue()
   {
      return selenium().getValue(Locators.REMOTE_BRANCHES_FIELD_ID);
   }

   /**
    * Type pointed text to remote branch field.
    * 
    * @return {@link String} text to type
    */
   public void typeToRemoteBranch(String text)
   {
      selenium().type(Locators.REMOTE_BRANCHES_FIELD_ID, text);
   }
   
   /**
    * Type pointed text to local branch field.
    * 
    * @return {@link String} text to type
    */
   public void typeToLocalBranch(String text)
   {
      selenium().type(Locators.LOCAL_BRANCHES_FIELD_ID, text);
   }

   /**
    * Get displayed value of local branch field.
    * 
    * @return {@link String} displayed value
    */
   public String getLocalBranchValue()
   {
      return selenium().getValue(Locators.LOCAL_BRANCHES_FIELD_ID);
   }
}
