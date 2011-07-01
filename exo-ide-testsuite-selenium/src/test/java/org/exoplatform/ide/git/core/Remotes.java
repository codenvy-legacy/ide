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
 * @version $Id:  Jun 30, 2011 12:38:07 PM anya $
 *
 */
public class Remotes extends AbstractTestModule
{
   public interface Locators
   {
      String VIEW_ID = "ideRemoteView";

      String VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(VIEW_ID);

      String ADD_BUTTON_ID = "ideRemoteViewAddButton";

      String DELETE_BUTTON_ID = "ideRemoteViewDeleteButton";

      String CLOSE_BUTTON_ID = "ideRemoteViewCloseButton";

      String REMOTE_GRID_ID = "ideRemoteGrid";

      String ADD_REMOTE_VIEW_ID = "ideAddRemoteRepositoryView";

      String ADD_REMOTE_VIEW_LOCATOR = IDE.getInstance().PERSPECTIVE.getViewLocator(ADD_REMOTE_VIEW_ID);

      String OK_BUTTON_ID = "ideAddRemoteRepositoryViewOkButton";

      String CANCEL_BUTTON_ID = "ideAddRemoteRepositoryViewCancelButton";

      String NAME_FIELD_ID = "ideAddRemoteRepositoryViewNameField";

      String URL_FIELD_ID = "ideAddRemoteRepositoryViewUrlField";

      String REMOTE_ROW_LOCATOR = "//table[@id=\"" + REMOTE_GRID_ID + "\"]//tr[contains(., \"%s\")]//div";
   }

   /**
    * Waits for Remotes view to be opened.
    * 
    * @throws Exception
    */
   public void waitForViewOpened() throws Exception
   {
      waitForElementPresent(Locators.VIEW_LOCATOR);
      waitForElementVisible(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Remotes view to be closed.
    * 
    * @throws Exception
    */
   public void waitForViewClosed() throws Exception
   {
      waitForElementNotPresent(Locators.VIEW_LOCATOR);
   }

   /**
    * Waits for Add remote repository view to be opened.
    * 
    * @throws Exception
    */
   public void waitForAddRemoteViewOpened() throws Exception
   {
      waitForElementPresent(Locators.ADD_REMOTE_VIEW_LOCATOR);
      waitForElementVisible(Locators.ADD_REMOTE_VIEW_LOCATOR);
   }

   /**
    * Waits for Add remote repository view to be closed.
    * 
    * @throws Exception
    */
   public void waitForAddRemoteViewClosed() throws Exception
   {
      waitForElementNotPresent(Locators.ADD_REMOTE_VIEW_LOCATOR);
   }

   /**
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isViewComponentsPresent()
   {
      return selenium().isElementPresent(Locators.REMOTE_GRID_ID)
         && selenium().isElementPresent(Locators.CLOSE_BUTTON_ID)
         && selenium().isElementPresent(Locators.ADD_BUTTON_ID)
         && selenium().isElementPresent(Locators.DELETE_BUTTON_ID);
   }

   /**
    * Checks view's components.
    * 
    * @return {@link Boolean} if <code>true</code> view's elements are present
    */
   public boolean isAddRepositoryViewComponentsPresent()
   {
      return selenium().isElementPresent(Locators.OK_BUTTON_ID)
         && selenium().isElementPresent(Locators.CANCEL_BUTTON_ID)
         && selenium().isElementPresent(Locators.NAME_FIELD_ID) && selenium().isElementPresent(Locators.URL_FIELD_ID);
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
    * Click Close button.
    */
   public void clickCloseButton()
   {
      selenium().click(Locators.CLOSE_BUTTON_ID);
   }

   /**
    * Click Delete button.
    */
   public void clickDeleteButton()
   {
      selenium().click(Locators.DELETE_BUTTON_ID);
   }

   /**
    * Click Ok button.
    */
   public void clickOkButton()
   {
      selenium().click(Locators.OK_BUTTON_ID);
   }

   /**
    * Type pointed text to repository's name field.
    * 
    * @param text
    */
   public void typeToNameField(String text)
   {
      selenium().type(Locators.NAME_FIELD_ID, text);
   }

   /**
    * Type pointed text to repository's URL field.
    * 
    * @param text
    */
   public void typeToUrlField(String text)
   {
      selenium().type(Locators.URL_FIELD_ID, text);
   }

   /**
    * Check Ok button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isOkButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.OK_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
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
    * Check Delete button is enabled.
    * 
    * @return {@link Boolean} <code>true</code> - button is enabled 
    */
   public boolean isDeleteButtonEnabled()
   {
      String attribute = selenium().getAttribute("//div[@id=\"" + Locators.DELETE_BUTTON_ID + "\"]/@button-enabled");
      return Boolean.parseBoolean(attribute);
   }

   /**
    * Get the number of remote repositories in grid.
    * 
    * @return count of remote repositories
    */
   public int getRemoteRepositoriesCount()
   {
      return selenium().getXpathCount("//table[@id=\"" + Locators.REMOTE_GRID_ID + "\"]/tbody[1]//tr").intValue();
   }

   public void waitForRemotesCount(int count) throws Exception
   {
      waitForElementPresent("//table[@id=\"" + Locators.REMOTE_GRID_ID + "\"]/tbody[1]//tr[" + count + "]");
   }

   /**
    * Select remote repository in grid by name.
    * 
    * @param remote name
    */
   public void selectRemoteByName(String remoteName)
   {
      selenium().click(String.format(Locators.REMOTE_ROW_LOCATOR, remoteName));
   }

   /**
    * Add new remote repository with pointed name and location.
    * 
    * @param name remote repository view
    * @param location remote repository location
    * @throws Exception 
    */
   public void addRemoteRepository(String name, String location) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      waitForViewOpened();

      //Add remote repository:
      clickAddButton();
      waitForAddRemoteViewOpened();

      typeToNameField(name);
      typeToUrlField(location);
      clickOkButton();
      waitForAddRemoteViewClosed();

      //Close Remotes view:
      clickCloseButton();
      waitForViewClosed();
   }
}
