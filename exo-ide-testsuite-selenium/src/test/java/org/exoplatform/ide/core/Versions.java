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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 11, 2011 9:45:54 AM anya $
 *
 */
/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class Versions extends AbstractTestModule
{
   private static final String VERSION_CONTENT_VIEW_ID = "ideVersionContentView";

   private static final String VERSION_CONTENT_VIEW_LOCATOR = "//div[@view-id=\"" + VERSION_CONTENT_VIEW_ID + "\"]";

   private static final String VERSIONS_LIST_GRID_ID = "ideViewVersionsGrid";

   private static final String VERSIONS_LIST_VIEW_ID = "ideViewVersionsForm";

   private static final String VERSIONS_LIST_VIEW_LOCATOR = "//div[@view-id='" + VERSIONS_LIST_VIEW_ID + "']";

   private final String OPEN_VERSION_BUTTON_ID = "ideViewVersionsFormOpenVersionButton";

   private final String ACTIVE_VERSIONS_LIST_VIEW = "//div[@view-id='ideVersionContentView' and @is-active='true']";
   
   private final String NOT_ACTIVE_VERSIONS_LIST_VIEW = "//div[@view-id='ideVersionContentView' and @is-active='false']";

   private final String CLOSE_BUTTON_ID = "ideViewVersionsFormCloseButton";

   /**
    * Wait till version content view is opened.
    * 
    * @throws Exception
    */
   public void waitVersionContentViewOpen() throws Exception
   {
      waitForElementPresent(VERSION_CONTENT_VIEW_LOCATOR);
      waitForElementVisible(VERSION_CONTENT_VIEW_LOCATOR);
      IDE().TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.VIEW_OLDER_VERSION, true);
   }

   /**
    * Wait till version content view is closed.
    * 
    * @throws Exception
    */
   public void waitVersionContentViewClosed() throws Exception
   {
      waitForElementNotPresent(VERSION_CONTENT_VIEW_LOCATOR);
   }

   /**
    * Checks whether version panel is opened or not.
    * 
    * @param isOpened 
    */
   public void checkVersionPanelState(boolean isOpened)
   {
      if (isOpened)
      {
         IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.HIDE_VERSION_HISTORY, true);
         assertTrue(selenium().isElementPresent(VERSION_CONTENT_VIEW_LOCATOR));

         // View version button
         IDE().TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION, true);
         IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_VERSION, true);
         //Restore button
         IDE().TOOLBAR.checkButtonExistAtRight(MenuCommands.File.RESTORE_VERSION, true);
         IDE().TOOLBAR.assertButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
         //Newer version button
         IDE().TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_NEWER_VERSION, true);
         IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
         //Older version button
         IDE().TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_OLDER_VERSION, true);
         IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_OLDER_VERSION, true);
      }
      else
      {
         assertFalse(IDE().TOOLBAR.isButtonSelected(ToolbarCommands.View.HIDE_VERSION_HISTORY));
         IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, true);
         assertFalse(selenium().isElementPresent(VERSION_CONTENT_VIEW_LOCATOR));
         // View version button
         IDE().TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION, false);
         //Restore button
         IDE().TOOLBAR.checkButtonExistAtRight(MenuCommands.File.RESTORE_VERSION, false);
         //Newer version button
         IDE().TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
         //Older version button
         IDE().TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_OLDER_VERSION, false);
      }
   }

   /**
    * Compares pointed content with content on version panel.
    * 
    * @param text text to compare
    * @throws Exception
    */
   public void checkTextOnVersionPanel(String text) throws Exception
   {
      selenium().selectFrame(VERSION_CONTENT_VIEW_LOCATOR + "//iframe");
      String content = selenium().getText(Locators.EDITOR_LOCATOR);
      assertEquals(text, content);
      IDE().selectMainFrame();
   }

   /**
    * Closes version panel.
    */
   public void closeVersionPanel()
   {
      int size = selenium().getXpathCount("//div[@panel-id=\"information\"]//div[@role='tab']").intValue();
      if (size <= 0)
         return;
      for (int i = 0; i < size; i++)
      {
         String locator = "//div[@panel-id=\"information\"]//td[@tab-bar-index='" + i + "']";
         String text = selenium().getText(locator);
         if (text.contains("Version"))
         {
            selenium().click(locator + "//div[@button-name=\"close-tab\"]");
            return;
         }
      }
   }

   /**
    * Check whether "View Version History" button is present in top menu and toolbar.
    * 
    * @param isPresent button is present
    * @throws Exception
    */
   public void checkViewVersionHistoryButtonPresent(boolean isPresent) throws Exception
   {
      IDE().MENU.checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, isPresent);
      IDE().TOOLBAR.checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION_HISTORY, isPresent);
   }

   /**
    * Checks  "View Version History" button enabled/disabled state.
    * 
    * @param enabled button is enabled
    * @throws Exception
    */
   public void checkViewVersionHistoryButtonState(boolean enabled) throws Exception
   {
      IDE().MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, enabled);
      IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, enabled);
   }

   /**
    * Checks  "View View Older Version" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   public void checkOlderVersionButtonState(boolean enabled) throws Exception
   {
      IDE().MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.OLDER_VERSION, enabled);
      IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_OLDER_VERSION, enabled);
   }

   /**
    * Checks  "View View Newer Version" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   public void checkNewerVersionButtonState(boolean enabled) throws Exception
   {
      IDE().MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.NEWER_VERSION, enabled);
      IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_NEWER_VERSION, enabled);
   }

   /**
    * Checks  "Restore To Version" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   public void checkRestoreVersionButtonState(boolean enabled) throws Exception
   {
      IDE().MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RESTORE_VERSION, enabled);
      IDE().TOOLBAR.assertButtonEnabled(MenuCommands.File.RESTORE_VERSION, enabled);
   }

   /**
    * Checks  "View Version List" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   public void checkViewVersionListButtonState(boolean enabled) throws Exception
   {
      IDE().MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST, enabled);
      IDE().TOOLBAR.assertButtonEnabled(ToolbarCommands.View.VIEW_VERSION, enabled);
   }

   /**
    * Wait till view with list of versions is opened.
    * 
    * @throws Exception
    */
   public void waitForViewVersionsListViewOpen() throws Exception
   {
      waitForElementPresent(VERSIONS_LIST_VIEW_LOCATOR);
   }

   /**
    * Wait till view with list of versions is closed.
    * 
    * @throws Exception
    */
   public void waitForViewVersionsListViewClose() throws Exception
   {
      waitForElementNotPresent(VERSIONS_LIST_VIEW_LOCATOR);
   }

   /**
    * Checks the panel with version list is opened or closed.
    * 
    * @param isOpened
    */
   public void checkViewVersionsListPanel(boolean isOpened)
   {
      if (isOpened)
      {
         assertTrue(selenium().isElementPresent(VERSIONS_LIST_VIEW_LOCATOR));
         assertTrue(selenium().isElementPresent(VERSIONS_LIST_GRID_ID));
         assertTrue(selenium().isElementPresent(OPEN_VERSION_BUTTON_ID));
         assertTrue(selenium().isElementPresent(CLOSE_BUTTON_ID));
      }
      else
      {
         assertFalse(selenium().isElementPresent(VERSIONS_LIST_VIEW_LOCATOR));
         assertFalse(selenium().isElementPresent(VERSIONS_LIST_GRID_ID));
      }
   }

   /**
    * Checks  "Open" button enabled/disabled state on the panel with version list.
    * 
    * @param isEnabled
    */
   public void checkOpenVersionButtonState(boolean isEnabled)
   {
      String attribute = selenium().getAttribute("//div[@id='" + OPEN_VERSION_BUTTON_ID + "']/@button-enabled");
      boolean buttonEnabled = Boolean.parseBoolean(attribute);
      assertEquals(isEnabled, buttonEnabled);
   }

   /**
    * Click on "Open" button on the panel with version list.
    */
   public void clickOpenVersionButton()
   {
      selenium().click(OPEN_VERSION_BUTTON_ID);
   }

   /**
    * Click on "Close" button on the panel with version list.
    */
   public void clickCloseVersionListPanelButton()
   {
      selenium().click(CLOSE_BUTTON_ID);
   }

   /**
    * Compared versions count in the list with pointed size.
    * 
    * @param size versions count
    */
   public void checkVersionListSize(int size)
   {
      int rows = selenium().getXpathCount("//table[@id=\"" + VERSIONS_LIST_GRID_ID + "\"]/tbody[1]/tr").intValue();
      assertEquals(size, rows);
   }

   /**
    * Selects version in version list by its index.
    * Index starts from 1.
    * 
    * @param index version index
    */
   public void selectVersionInVersionList(int index)
   {
      selenium().click("//table[@id=\"" + VERSIONS_LIST_GRID_ID + "\"]/tbody[1]/tr[" + index + "]//div");
   }

   /**
    * Opens version from version list with pointed index and compares its content with pointed one.
    * Index starts from 1.
    * 
    * @param index
    * @param versionContent
    * @throws Exception
    */
   public void checkOpenVersion(int index, String versionContent) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      waitForViewVersionsListViewOpen();

      checkViewVersionsListPanel(true);
      checkOpenVersionButtonState(false);
      selectVersionInVersionList(index);
      waitOpenVersionButtonEnabled();
      clickOpenVersionButton();
      waitForViewVersionsListViewClose();

      checkViewVersionsListPanel(false);
      checkViewVersionListButtonState(true);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkTextOnVersionPanel(versionContent);
   }

   /**
    * Wait open version button is enabled.
    * 
    * @throws Exception
    */
   public void waitOpenVersionButtonEnabled() throws Exception
   {
      waitForElementPresent("//div[@id='" + OPEN_VERSION_BUTTON_ID + "' and @button-enabled=\"true\"]");
   }

   /**
    * checks  selection version tab (if the tab on panel several) 
    * @throws Exception
    */
   public void checkVersionPanelIsActive() throws Exception
   {
      assertTrue(selenium().isElementPresent(ACTIVE_VERSIONS_LIST_VIEW));
   }
   
   /**
    *  checks  version tab is not active (if the tab on panel several) 
    * @throws Exception
    */
   public void checkVersionPanelIsNotActive() throws Exception
   {
      assertTrue(selenium().isElementPresent(NOT_ACTIVE_VERSIONS_LIST_VIEW));
   }
   
}
