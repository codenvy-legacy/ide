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
         checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_VERSION, true);
         checkToolbarButtonState(ToolbarCommands.View.VIEW_VERSION, true);
         //Restore button
         checkToolbarButtonPresentOnRightSide(MenuCommands.File.RESTORE_VERSION, true);
         checkToolbarButtonState(MenuCommands.File.RESTORE_VERSION, false);
         //Newer version button
         checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_NEWER_VERSION, true);
         checkToolbarButtonState(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
         //Older version button
         checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_OLDER_VERSION, true);
         checkToolbarButtonState(ToolbarCommands.View.VIEW_OLDER_VERSION, true);
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='exo-toolbar16ButtonPanel_Right' and @title='"
            + ToolbarCommands.View.HIDE_VERSION_HISTORY
            + "']/div[@class='exo-toolbar16Button-selected' and @elementenabled='true']"));
         checkToolbarButtonState(ToolbarCommands.View.VIEW_VERSION_HISTORY, true);
         assertFalse(selenium.isElementPresent("scLocator=//Layout[ID=\"ideVersionContentForm\"]"));
         // View version button
         checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_VERSION, false);
         //Restore button
         checkToolbarButtonPresentOnRightSide(MenuCommands.File.RESTORE_VERSION, false);
         //Newer version button
         checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_NEWER_VERSION, false);
         //Older version button
         checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_OLDER_VERSION, false);
      }
   }

   protected void checkTextOnVersionPanel(String text) throws Exception
   {
      selenium.selectFrame("//div[@eventproxy='ideVersionContentForm']//iframe");
      String content = selenium.getText("//body[@class='editbox']");
      assertEquals(text, content);
      selectMainFrame();
   }

   protected void closeVersionPanel()
   {
      selenium.click("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[ID=ideVersionContentPanel]/icon");
   }

   protected void checkViewVersionHistoryButtonPresent() throws Exception
   {
      checkMenuCommandPresent(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, true);
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.VERSION_HISTORY, true);
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.View.VIEW_VERSION_HISTORY, true);
   }

   protected void checkOlderVersionButtonState(boolean enabled) throws Exception
   {
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.OLDER_VERSION, enabled);
      checkToolbarButtonState(ToolbarCommands.View.VIEW_OLDER_VERSION, enabled);
   }

   protected void checkNewerVersionButtonState(boolean enabled) throws Exception
   {
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.NEWER_VERSION, enabled);
      checkToolbarButtonState(ToolbarCommands.View.VIEW_NEWER_VERSION, enabled);
   }

   protected void checkRestoreVersionButtonState(boolean enabled) throws Exception
   {
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RESTORE_VERSION, enabled);
      checkToolbarButtonState(MenuCommands.File.RESTORE_VERSION, enabled);
   }
   
   protected void checkViewVersionListButtonState(boolean enabled) throws Exception
   {
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST, enabled);
      checkToolbarButtonState(ToolbarCommands.View.VIEW_VERSION, enabled);
   }

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

   protected void clickOpenVersionButton()
   {
      selenium.click("scLocator=//IButton[ID=\"ideViewVersionsFormOpenVersionButton\"]");
   }

   protected void clickCloseVersionListPanelButton()
   {
      selenium.click("scLocator=//IButton[ID=\"ideViewVersionsFormCloseButton\"]");
   }

   protected void checkVersionListSize(int size)
   {
      for (int i = 0; i < size; i++)
      {
         assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideViewVersionsGrid\"]/body/row[" + i
            + "]/col[0]"));
      }
   }

   protected void selectVersionInVersionList(int index)
   {
      selenium.click("scLocator=//ListGrid[ID=\"ideViewVersionsGrid\"]/body/row[" + index + "]/col[0]");
   }

   protected void checkOpenVersion(int index, String versionContent) throws Exception
   {
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.VERSION_LIST);
      Thread.sleep(TestConstants.SLEEP);
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
