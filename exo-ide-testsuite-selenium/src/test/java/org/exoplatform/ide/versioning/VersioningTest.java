/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.versioning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 18, 2010 $
 *
 */
public abstract class VersioningTest extends BaseTest
{
   /**
    * Checks whether version panel is opened or not.
    * 
    * @param isOpened 
    */
   protected void checkVersionPanelState(boolean isOpened)
   {
      if (isOpened)
      {
         assertTrue(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_Right' and @title='"
            + ToolbarCommands.View.HIDE_VERSION_HISTORY
            + "']/div[@class='exo-toolbar16Button-selected' and @elementenabled='true']"));
         assertTrue(selenium
            .isElementPresent("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[ID=ideVersionContentPanel]"));
         assertTrue(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
         // View version button
         IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION, true);
         IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_VERSION, true);
         //Restore button
         IDE.toolbar().checkButtonExistAtRight(MenuCommands.File.RESTORE_VERSION, true);
         IDE.toolbar().checkButtonEnabled(MenuCommands.File.RESTORE_VERSION, false);
         //Newer version button
         IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_NEWER_VERSION, true);
         IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
         //Older version button
         IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_OLDER_VERSION, true);
         IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_OLDER_VERSION, true);
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_Right' and @title='"
            + ToolbarCommands.View.HIDE_VERSION_HISTORY
            + "']/div[@class='exo-toolbar16Button-selected' and @elementenabled='true']"));
         IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, true);
         assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
         // View version button
         IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION, false);
         //Restore button
         IDE.toolbar().checkButtonExistAtRight(MenuCommands.File.RESTORE_VERSION, false);
         //Newer version button
         IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
         //Older version button
         IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_OLDER_VERSION, false);
      }
   }

   /**
    * Compares pointed content with content on version panel.
    * 
    * @param text text to compare
    * @throws Exception
    */
   protected void checkTextOnVersionPanel(String text) throws Exception
   {
      selenium.selectFrame("//div[@eventproxy='ideVersionContentForm']//iframe");
      String content = selenium.getText("//body[@class='editbox']");
      assertEquals(text, content);
      selectMainFrame();
   }

   /**
    * Closes version panel.
    */
   protected void closeVersionPanel()
   {
      selenium.click("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[ID=ideVersionContentPanel]/icon");
   }

   /**
    * Check whether "View Version History" button is present in top menu and toolbar.
    * 
    * @param isPresent button is present
    * @throws Exception
    */
   protected void checkViewVersionHistoryButtonPresent(boolean isPresent) throws Exception
   {
      IDE.menu().checkCommandVisibility(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, isPresent);
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.View.VIEW_VERSION_HISTORY, isPresent);
   }
   
   /**
    * Checks  "View Version History" button enabled/disabled state.
    * 
    * @param enabled button is enabled
    * @throws Exception
    */
   protected void checkViewVersionHistoryButtonState(boolean enabled) throws Exception
   {
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, enabled);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_VERSION_HISTORY, enabled);
   }

   /**
    * Checks  "View View Older Version" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   protected void checkOlderVersionButtonState(boolean enabled) throws Exception
   {
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.OLDER_VERSION, enabled);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_OLDER_VERSION, enabled);
   }

   /**
    * Checks  "View View Newer Version" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   protected void checkNewerVersionButtonState(boolean enabled) throws Exception
   {
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.NEWER_VERSION, enabled);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_NEWER_VERSION, enabled);
   }

   /**
    * Checks  "Restore To Version" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   protected void checkRestoreVersionButtonState(boolean enabled) throws Exception
   {
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RESTORE_VERSION, enabled);
      IDE.toolbar().checkButtonEnabled(MenuCommands.File.RESTORE_VERSION, enabled);
   }
   
   /**
    * Checks  "View Version List" button enabled/disabled state.
    * 
    * @param enabled
    * @throws Exception
    */
   protected void checkViewVersionListButtonState(boolean enabled) throws Exception
   {
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST, enabled);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.View.VIEW_VERSION, enabled);
   }

   /**
    * Checks the panel with version list is opened or closed.
    * 
    * @param isOpened
    */
   protected void checkViewVersionsListPanel(boolean isOpened)
   {
      if (isOpened)
      {
         assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideViewVersionsGrid\"]"));
         assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideViewVersionsForm\"]"));
         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideViewVersionsFormOpenVersionButton\"]"));
         assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideViewVersionsFormCloseButton\"]"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideViewVersionsGrid\"]"));
         assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideViewVersionsForm\"]"));
      }
   }

   /**
    * Checks  "Open" button enabled/disabled state on the panel with version list.
    * 
    * @param isEnabled
    */
   protected void checkOpenVersionButtonState(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Open']"));
      }
      else
      {
         assertTrue(selenium
            .isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Open']"));
      }
   }

   /**
    * Click on "Open" button on the panel with version list.
    */
   protected void clickOpenVersionButton()
   {
      selenium.click("scLocator=//IButton[ID=\"ideViewVersionsFormOpenVersionButton\"]");
   }

   /**
    * Click on "Close" button on the panel with version list.
    */
   protected void clickCloseVersionListPanelButton()
   {
      selenium.click("scLocator=//IButton[ID=\"ideViewVersionsFormCloseButton\"]");
   }

   /**
    * Compared versions count in the list with pointed size.
    * 
    * @param size versions count
    */
   protected void checkVersionListSize(int size)
   {
      for (int i = 0; i < size; i++)
      {
         assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideViewVersionsGrid\"]/body/row[" + i
            + "]/col[0]"));
      }
   }

   /**
    * Selects version in version list by its index.
    * 
    * @param index version index
    */
   protected void selectVersionInVersionList(int index)
   {
      selenium.click("scLocator=//ListGrid[ID=\"ideViewVersionsGrid\"]/body/row[" + index + "]/col[0]");
   }

   /**
    * Opens version from version list with pointed index and compares its content with pointed one.
    * 
    * @param index
    * @param versionContent
    * @throws Exception
    */
   protected void checkOpenVersion(int index, String versionContent) throws Exception
   {
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
//      Thread.sleep(TestConstants.SLEEP);
      
      checkViewVersionsListPanel(true);
      checkOpenVersionButtonState(false);
      selectVersionInVersionList(index);
      clickOpenVersionButton();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkViewVersionsListPanel(false);
      checkViewVersionListButtonState(true);
      checkTextOnVersionPanel(versionContent);
   }
}
