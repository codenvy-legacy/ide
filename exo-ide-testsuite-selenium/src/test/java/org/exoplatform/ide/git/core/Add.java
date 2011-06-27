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
 * @version $Id:  Jun 23, 2011 6:00:40 PM anya $
 *
 */
public class Add extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "ideAddToIndexView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String ADD_BUTTON_ID = "ideAddToIndexViewAddButton";

      String CANCEL_BUTTON_ID = "ideAddToIndexViewCancelButton";

      String UPDATE_FIELD_ID = "ideAddToIndexViewUpdaterField";

      String MESSAGE_FIELD_ID = "ideAddToIndexViewMessageField";
   }

   public interface Messages
   {
      String ADD_FILE = "Add file %s to index.";

      String ADD_FOLDER = "Add content of folder %s to index.";

      String ADD_ALL_CHANGES = "Add all changes in repository to index.";
   }

   /**
    * Waits for Add to index view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Add to index view to be closed.
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
      return selenium().isElementPresent(Locators.UPDATE_FIELD_ID)
         && selenium().isElementPresent(Locators.MESSAGE_FIELD_ID)
         && selenium().isElementPresent(Locators.ADD_BUTTON_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Click Add button.
    */
   public void clickAddButton()
   {
      selenium().click(Locators.ADD_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Check Add button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isAddButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.ADD_BUTTON_ID + "\"]/@button-enabled");
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
    * Get message about what is gonna to be added to index.
    * 
    * @return {@link String} message
    */
   public String getAddMessage()
   {
      return selenium().getText(Locators.MESSAGE_FIELD_ID);
   }

   /**
    * Make the update field to be checked.
    */
   public void checkUpdateField()
   {
      selenium().check(Locators.UPDATE_FIELD_ID);
   }

   /**
    * Make the update field to be unchecked.
    */
   public void unCheckUpdateField()
   {
      selenium().uncheck(Locators.UPDATE_FIELD_ID);
   }

   /**
    * Get the checked value of update field.
    * 
    * @return {@link Boolean}
    */
   public boolean isUpdateFieldChecked()
   {
      return selenium().isChecked(Locators.UPDATE_FIELD_ID);
   }

   /**
    * Perform actions for adding to index.
    * 
    * @throws Exception
    */
   public void addToIndex() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.ADD);
      waitForViewOpened();
      clickAddButton();
      waitForViewClosed();
   }
}
