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
 * @version $Id:  Jun 27, 2011 11:23:17 AM anya $
 *
 */
public class Commit extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "ideCommitView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String COMMIT_BUTTON_ID = "ideCommitViewCommitButton";

      String CANCEL_BUTTON_ID = "ideCommitViewCancelButton";

      String MESSAGE_FIELD_ID = "ideCommitViewMessageField";

      String ALL_FIELD_ID = "ideCommitViewAllField";
   }

   /**
    * Waits for Commit view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Commit view to be closed.
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
      return selenium().isElementPresent(Locators.COMMIT_BUTTON_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID)
         && selenium().isElementPresent(Locators.MESSAGE_FIELD_ID)
         && selenium().isElementPresent(Locators.ALL_FIELD_ID);
   }

   /**
    * Click Commit button.
    */
   public void clickCommitButton()
   {
      selenium().click(Locators.COMMIT_BUTTON_ID);
   }

   /**
    * Click Cancel button.
    */
   public void clickCancelButton()
   {
      selenium().click(Locators.CANCEL_BUTTON_ID);
   }

   /**
    * Check Commit button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isCommitButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.COMMIT_BUTTON_ID + "\"]/@button-enabled");
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
    * Make the add field to be checked.
    */
   public void checkAddField()
   {
      selenium().check(Locators.ALL_FIELD_ID);
   }

   /**
    * Returns the checked state of add field. 
    * 
    * @return {@link Boolean} if <code>true</code> - checked
    */
   public boolean isAddFieldChecked()
   {
      return selenium().isChecked(Locators.ALL_FIELD_ID);
   }

   /**
    * Type message to message field.
    * 
    * @param message
    */
   public void typeToMessageField(String message)
   {
      selenium().type(Locators.MESSAGE_FIELD_ID, message);
   }

   /**
    * Perform actions to commit changes.
    * 
    * @param message commit message
    * @throws Exception
    */
   public void commit(String message) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.COMMIT);
      waitForViewOpened();
      typeToMessageField(message);
      clickCommitButton();
      waitForViewClosed();
   }
}
