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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: $
*/
public class SelectWorkspace extends AbstractTestModule
{
   //!!!secondworkspace locator prescribe hardcode 
   private static String SELECTED_WORKSPACE_LOCATOR =
      "//div[@view-id='ideSelectWorkspaceView']//table[@id='ideEntryPointListGrid']/tbody/tr[1]";

   //TODO Method shold be refactor. After add in change in UI IDE and set attribute on  select element in Workspace Window tree
   public String getNonActiveWorkspaceName1() throws Exception
   {
      String secondWorkspaceUrl = null;
      IDE().MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
      Thread.sleep(TestConstants.SLEEP);
      selenium().click(SELECTED_WORKSPACE_LOCATOR);

      // click "UP" to go to previous workspace in the list
      //      selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_UP);
      //      selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_UP);
      //      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //      // test if "Ok" button is enabled
      if (selenium().isElementPresent(
         "//div[@eventproxy='ideSelectWorkspaceFormOkButton']//td[@class='buttonTitle' and text()='OK']"))
      {
         secondWorkspaceUrl = selenium().getText(SELECTED_WORKSPACE_LOCATOR);
      }
      else
      {
         // click "DOWN" to go to next workspace in the list
         selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_DOWN);
         selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.REDRAW_PERIOD);

         // test if "Ok" button is enabled
         if (selenium().isElementPresent(
            "//div[@eventproxy='ideSelectWorkspaceFormOkButton']//td[@class='buttonTitle' and text()='OK']"))
         {
            secondWorkspaceUrl = selenium().getText(SELECTED_WORKSPACE_LOCATOR);
         }
      }

      if ((secondWorkspaceUrl == null) || ("".equals(secondWorkspaceUrl)))
      {
         System.out.println("Error. It is impossible to recognise second workspace!");
      }

      // click the "Cancel" button
      selenium().click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]");

      // remove text before workspace name
      String secondWorkspaceName =
         secondWorkspaceUrl.toLowerCase().replace((BaseTest.ENTRY_POINT_URL).toLowerCase(), "");

      // remove ended '/'
      secondWorkspaceName = secondWorkspaceName.replace("/", "");

      return secondWorkspaceName;
   }

}
