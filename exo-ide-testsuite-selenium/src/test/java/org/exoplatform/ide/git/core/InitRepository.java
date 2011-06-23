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
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.AbstractTestModule;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 23, 2011 10:41:06 AM anya $
 *
 */
public class InitRepository extends AbstractTestModule
{
   public static interface Locators
   {
      String VIEW_ID = "ideInitRepositoryView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String INIT_BUTTON_ID = "ideInitRepositoryViewInitButton";

      String CANCEL_BUTTON_ID = "ideInitRepositoryViewCancelButton";

      String WORKDIR_FIELD_ID = "ideInitRepositoryViewWorkDirField";

      String BARE_FIELD_ID = "ideInitRepositoryViewBareField";
   }

   public static interface Titles
   {
      String VIEW_TITLE = "Initialize local repository";

      String INIT_BUTTON = "Init";

      String CANCEL_BUTTON = "Cancel";

      String WORKDIR_FIELD = "Work directory";

      String BARE_FIELD = "Bare repository";
   }

   /**
    * Waits for Init Git Repository view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Init Git Repository view to be closed.
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
      return selenium().isElementPresent(Locators.BARE_FIELD_ID)
         && selenium().isElementPresent(Locators.WORKDIR_FIELD_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID)
         && selenium().isElementPresent(Locators.INIT_BUTTON_ID);
   }

   /**
    * Click Init button.
    */
   public void clickInitButton()
   {
      selenium().click(Locators.INIT_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Check the field bare repository.
    */
   public void checkBareRepositoryField()
   {
      selenium().check(Locators.BARE_FIELD_ID);
   }

   /**
    * Check Init button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isInitButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.INIT_BUTTON_ID + "\"]/@button-enabled");
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
    * Get work directory field's value.
    * 
    * @return {@link String} fields value
    */
   public String getWorkDirectoryValue()
   {
      return selenium().getValue(Locators.WORKDIR_FIELD_ID);
   }

   /**
    * Get Init button's title.
    * 
    * @return {@link String} button's title
    */
   public String getInitButtonTitle()
   {
      return selenium().getText(Locators.INIT_BUTTON_ID);
   }

   /**
    * Get Cancel button's title.
    * 
    * @return {@link String} button's title
    */
   public String getCancelButtonTitle()
   {
      return selenium().getText(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Perform init Git repository.
    * 
    * @throws Exception
    */
   public void initRepository() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      waitForViewOpened();

      clickInitButton();
      waitForViewClosed();
   }
}
